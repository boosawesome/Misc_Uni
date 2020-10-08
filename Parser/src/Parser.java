import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;

/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */
public class Parser {

	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement
														// this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");

	// Actions
	static Pattern ACTIONS = Pattern.compile("move|takeFuel|turnL|turnR|wait|turnAround|shieldOn|shieldOff");
	static Pattern MOVE = Pattern.compile("move");
	static Pattern TAKEFUEL = Pattern.compile("takeFuel");
	static Pattern TURNL = Pattern.compile("turnL");
	static Pattern TURNR = Pattern.compile("turnR");
	static Pattern WAIT = Pattern.compile("wait");
	static Pattern TURNAROUND = Pattern.compile("turnAround");
	static Pattern SHIELDON = Pattern.compile("shieldOn");
	static Pattern SHIELDOFF = Pattern.compile("shieldOff");

	// Statement patterns
	public static final Pattern VARIABLE = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
	public static final Pattern DESIG = Pattern.compile("\\=");
	public static final Pattern LOOP = Pattern.compile("loop");
	public static final Pattern IF = Pattern.compile("if");
	public static final Pattern WHILE = Pattern.compile("while");
	public static final Pattern ELIF = Pattern.compile("elif");
	public static final Pattern ELSE = Pattern.compile("else");

	// Conditionals
	static Pattern CONDITIONS = Pattern.compile("and|or|not|lt|gt|eq");
	static Pattern AND = Pattern.compile("and");
	static Pattern OR = Pattern.compile("or");
	static Pattern NOT = Pattern.compile("not");
	static Pattern LESSTHAN = Pattern.compile("lt");
	static Pattern GREATERTHAN = Pattern.compile("gt");
	static Pattern EQUAL = Pattern.compile("eq");

	// Values
	static Pattern OPERATIONS = Pattern.compile("add|sub|mul|div");
	static Pattern ADD = Pattern.compile("add");
	static Pattern SUB = Pattern.compile("sub");
	static Pattern MUL = Pattern.compile("mul");
	static Pattern DIV = Pattern.compile("div");

	// Sensors
	static Pattern SENSORS = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");
	static Pattern FUELLEFT = Pattern.compile("fuelLeft");
	static Pattern OPPLR = Pattern.compile("oppLR");
	static Pattern OPPFB = Pattern.compile("oppFB");
	static Pattern NUMBARRELS = Pattern.compile("numBarrels");
	static Pattern BARRELLR = Pattern.compile("barrelLR");
	static Pattern BARRELFB = Pattern.compile("barrelFB");
	static Pattern WALLDIST = Pattern.compile("wallDist");

	public static HashMap<String, VarNode> variables = new HashMap<>();

	/**
	 * Adds statements to the program
	 * 
	 * PROG ::= STMT+
	 */
	static RobotProgramNode parseProgram(Scanner s) {
		ProgNode progNode = new ProgNode();

		while (s.hasNext()) {
			StmtNode statement = parseStatement(s);
			progNode.addStmtNode(statement);
		}

		return progNode;
	}
	
	/**
	 *determines which course of action to take
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseStatement(Scanner s) {
		if (s.hasNext(ACTIONS)) {
			// ACT ;
			return parseAction(s);
		} else if (s.hasNext(LOOP)) {
			// LOOP
			return parseLoop(s);
		} else if (s.hasNext(IF)) {
			// IF
			return parseIf(s);
		} else if (s.hasNext(WHILE)) {
			// WHILE
			return parseWhile(s);
		} else if (s.hasNext(VARIABLE)) {
			// DESIGNATE ;
			return parseDesig(s);
		} else {
			fail("Unkown statement", s);
			return null; // dead code
		}
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseAction(Scanner s) {

		String lable = require(ACTIONS, "Unknown Action command", s);
		StmtNode statement;

		switch (lable) {
		case "move":
			ExpNode expA = null;
			if (checkFor(OPENPAREN, s)) {
				expA = parseExpression(s);
				require(CLOSEPAREN, "Needed a close parenthesis after the argument", s);
			}
			statement = new MoveNode(expA);
			break;
		case "takeFuel":
			statement = new TakeFuelNode();
			break;
		case "turnL":
			statement = new TurnLNode();
			break;
		case "turnR":
			statement = new TurnRNode();
			break;
		case "wait":
			ExpNode expB = null;
			if (checkFor(OPENPAREN, s)) {
				expB = parseExpression(s);
				require(CLOSEPAREN, "Needed a close parenthesis after the argument", s);
			}
			statement = new WaitNode(expB);
			break;
		/*
		 * case "turnAround": statement = new TurnAroundNode(); break; case
		 * "shieldOn": statement = new ShieldOnNode(); break; case "shieldOff":
		 * statement = new ShieldOffNode(); break;
		 */default:
			fail("Unknown Action command", s);
			statement = null; // dead code
			break;
		}

		require(";", "All ACT has to end with a \";\"", s);

		return statement;
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseLoop(Scanner s) {
		require(LOOP, "LOOP has to start with \"loop\"", s);
		BlockNode blockNode = parseBlock(s);
		return new LoopNode(blockNode);
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseIf(Scanner s) {

		// if ( COND ) BLOCK
		require(IF, "IF has to start with \"if\"", s);
		require(OPENPAREN, "Needed an open parenthesis after \"if\"", s);
		CondNode condition = parseConditional(s);
		require(CLOSEPAREN, "Needed a close parenthesis after the conditional expression", s);
		BlockNode ifBlock = parseBlock(s);

		// [elif ( COND ) BLOCK]*
		ArrayList<CondNode> elifConditions = new ArrayList<>();
		ArrayList<BlockNode> elifBlocks = new ArrayList<>();
		while (checkFor(ELIF, s)) {
			require(OPENPAREN, "Needed an open parenthesis after \"elif\"", s);
			CondNode elifCondition = parseConditional(s);
			require(CLOSEPAREN, "Needed a close parenthesis after the conditional expression", s);
			BlockNode elifBlock = parseBlock(s);
			elifConditions.add(elifCondition);
			elifBlocks.add(elifBlock);
		}

		// [else BLOCK]
		BlockNode elseBlock = null;
		if (checkFor(ELSE, s)) {
			elseBlock = parseBlock(s);
		}

		return new IfNode(condition, ifBlock, elifConditions, elifBlocks, elseBlock);
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseWhile(Scanner s) {

		require(WHILE, "WHILE has to start with \"while\"", s);
		require(OPENPAREN, "Needed an open parenthesis after \"while\"", s);

		CondNode condition = parseConditional(s);

		require(CLOSEPAREN, "Needed a close parenthesis after the conditional expression", s);

		BlockNode block = parseBlock(s);

		return new WhileNode(block, condition);
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return StmtNode (Statement node)
	 */
	private static StmtNode parseDesig(Scanner s) {
		String varName = require(VARIABLE, "No valid variable found", s);

		VarNode varNode = variables.get(varName);
		if (varNode == null) {
			varNode = new VarNode(varName, 0);
			variables.put(varName, varNode);
		}

		require("=", "Needed a \"=\" after the variable name in an assignment", s);

		ExpNode expNode = parseExpression(s);

		require(";", "All assignments has to end with a \";\"", s);

		return new DesigNode(varNode, expNode);
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return BlockNode (block of nodes)
	 */
	private static BlockNode parseBlock(Scanner s) {

		require(OPENBRACE, "BLOCK has to start with \"{\"", s);

		BlockNode blockNode = new BlockNode();

		while (s.hasNext() && !s.hasNext(CLOSEBRACE)) {
			StmtNode statement = parseStatement(s);
			blockNode.addStmtNode(statement);
		}

		if (blockNode.isEmpty()) {
			fail("BLOCK cannot be empty", s);
		}

		require(CLOSEBRACE, "BLOCK has to end with a \"}\"", s);

		return blockNode;
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return ExpNode (Expression node)
	 */
	private static ExpNode parseExpression(Scanner s) {

		if (s.hasNext(NUMPAT)) {
			// NUM
			return new NumNode(requireInt(NUMPAT, "Needed an integer", s));
		} else if (s.hasNext(SENSORS)) {
			// SEN
			return parseSensor(s);
		} else if (s.hasNext(VARIABLE)) {
			// VAR
			String varName = require(VARIABLE, "Invalid variable name", s);
			VarNode var = variables.get(varName);
			if (var == null) {
				// fail("This variable \"" + varName + "\" has not been
				// initialised", s);
				var = new VarNode(varName, 0);
				variables.put(varName, var);
			}
			return var;
		} else if (s.hasNext(OPERATIONS)) {
			// OP
			return parseOperation(s);
		} else {
			fail("Unkown EXP statement", s);
			return null; // dead code
		}
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return SensNode (Sensor node)
	 */
	private static SensNode parseSensor(Scanner s) {

		String lable = require(SENSORS, "Unknown Sensor label", s);

		switch (lable) {
		case "fuelLeft":
			return new FuelLeftNode();
		case "oppLR":
			return new OppLRNode();
		case "oppFB":
			return new OppFBNode();
		case "numBarrels":
			return new NumBarrelNode();
		case "barrelLR":
			ExpNode expA = null;
			if (checkFor(OPENPAREN, s)) {
				expA = parseExpression(s);
				require(CLOSEPAREN, "Needed a close parenthesis after the argument", s);
			}
			return new BarrelLRNode(expA);
		case "barrelFB":
			ExpNode expB = null;
			if (checkFor(OPENPAREN, s)) {
				expB = parseExpression(s);
				require(CLOSEPAREN, "Needed a close parenthesis after the argument", s);
			}
			return new BarrelFBNode(expB);
		case "wallDist":
			return new WallDistNode();
		default:
			fail("Unknown Sensor label", s);
			return null; // dead code
		}
	}
	
	/**
	 * 
	 * @param Scannenr s
	 * @return OpNode (Operation node)
	 */
	private static OpNode parseOperation(Scanner s) {

		String label = require(OPERATIONS, "Unknown Operation label", s);
		require(OPENPAREN, "Needed an open parenthesis before two arguments", s);
		ExpNode exp1 = parseExpression(s);
		require(",", "Needed a \",\" between two arguments", s);
		ExpNode exp2 = parseExpression(s);
		require(CLOSEPAREN, "Needed a close parenthesis after two arguments", s);

		switch (label) {
		case "add":
			return new AddNode(exp1, exp2);
		case "sub":
			return new SubNode(exp1, exp2);
		case "mul":
			return new MulNode(exp1, exp2);
		case "div":
			return new DivNode(exp1, exp2);
		default:
			fail("Unknown Operation label", s);
			return null; // dead code
		}
	}
	
	/**
	 * 
	 * @param Scanner s
	 * @return CondNode
	 */
	@SuppressWarnings("unused")
	private static CondNode parseConditional(Scanner s) {

		String label = require(CONDITIONS, "Unknown Condition label", s);
		require(OPENPAREN, "Needed an open parenthesis before two arguments", s);

		CondNode condArgument1 = null;
		CondNode condArgument2 = null;
		ExpNode expArgument1 = null;
		ExpNode expArgument2 = null;

		switch (label) {
		case "lt":
		case "gt":
		case "eq":
			expArgument1 = parseExpression(s);
			require(",", "Needed a \",\" between two arguments", s);
			expArgument2 = parseExpression(s);
			break;
		case "and":
		case "or":
			condArgument1 = parseConditional(s);
			require(",", "Needed a \",\" between two arguments", s);
			condArgument2 = parseConditional(s);
			break;
		case "not":
			condArgument1 = parseConditional(s);
			break;
		default:
			break;
		}

		require(CLOSEPAREN, "Needed a close parenthesis after two arguments", s);

		switch (label) {
		case "lt":
			return new LTNode(expArgument1, expArgument2);
		case "gt":
			return new GTNode(expArgument1, expArgument2);
		case "eq":
			return new EqNode(expArgument1, expArgument2);
		case "and":
			/*
			 * return new AndNode(condArgument1, condArgument2); case "or":
			 * return new OrNode(condArgument1, condArgument2); case "not":
			 * return new NotNode(condArgument1);
			 */ default:
			return null; // dead code
		}
	}

	// utility methods for the parser

	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * Requires that the next token matches a pattern if it matches, it consumes
	 * and returns the token, if not, it throws an exception with an error
	 * message
	 */
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	 * Requires that the next token matches a pattern (which should only match a
	 * number) if it matches, it consumes and returns the token as an integer if
	 * not, it throws an exception with an error message
	 */
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	 * Checks whether the next token in the scanner matches the specified
	 * pattern, if so, consumes the token and return true. Otherwise returns
	 * false without consuming anything.
	 */
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// program nodes
abstract class StmtNode implements RobotProgramNode {

}

class ProgNode implements RobotProgramNode {
	private ArrayList<StmtNode> statements;

	public ProgNode() {
		statements = new ArrayList<>();
	}

	@Override
	public void execute(Robot robot) {
		for (StmtNode state : statements) {
			state.execute(robot);
		}
	}

	public void addStmtNode(StmtNode node) {
		statements.add(node);
	}

	@Override
	public String toString() {
		String s = "";
		for (StmtNode n : statements) {
			s += n.toString() + "\n";
		}
		return s;
	}
}

class BlockNode implements RobotProgramNode {

	private ArrayList<StmtNode> statements;

	public BlockNode() {
		statements = new ArrayList<>();
	}

	@Override
	public void execute(Robot robot) {
		for (StmtNode state : statements) {
			state.execute(robot);
		}
	}

	public void addStmtNode(StmtNode node) {
		statements.add(node);
	}

	public boolean isEmpty() {
		return statements.isEmpty();
	}

	@Override
	public String toString() {
		String s = "";
		for (StmtNode n : statements) {
			s += "\t" + n.toString() + "\n";
		}
		return s + "}";
	}

}

//Operations
interface OpNode extends ExpNode {
	
	@Override
	public int assess(Robot robot);
	
	@Override
	public String toString();
	
}

class AddNode implements OpNode{
	
	private ExpNode exp1;
	private ExpNode exp2;
	
	public AddNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public int assess(Robot robot) {
		return exp1.assess(robot) + exp2.assess(robot);
	}
	
	@Override
	public String toString(){
		return "add(" + exp1.toString() + "," + exp2.toString() + ")";
	}
}

class SubNode implements OpNode{
	
	private ExpNode exp1;
	private ExpNode exp2;
	
	public SubNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public int assess(Robot robot) {
		return exp1.assess(robot) - exp2.assess(robot);
	}
	
	@Override
	public String toString(){
		return "sub(" + exp1.toString() + "," + exp2.toString() + ")";
	}
}

class MulNode implements OpNode{
	
	private ExpNode exp1;
	private ExpNode exp2;
	
	public MulNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public int assess(Robot robot) {
		return exp1.assess(robot) * exp2.assess(robot);
	}
	
	@Override
	public String toString(){
		return "mul(" + exp1.toString() + "," + exp2.toString() + ")";
	}
}

class DivNode implements OpNode{
	
	private ExpNode exp1;
	private ExpNode exp2;
	
	public DivNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public int assess(Robot robot) {
		return exp1.assess(robot) / exp2.assess(robot);
	}
	
	@Override
	public String toString(){
		return "div(" + exp1.toString() + "," + exp2.toString() + ")";
	}
}

class VarNode implements ExpNode {

	private final String name;
	private int value;

	public VarNode(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int assess(Robot robot) {
		return value;
	}

	@Override
	public String toString() {
		return name;
	}
}

class LoopNode extends StmtNode {

	private BlockNode blockNode;

	public LoopNode(BlockNode blockNode) {
		this.blockNode = blockNode;
	}

	@Override
	public void execute(Robot robot) {
		while (true) {
			blockNode.execute(robot);
		}
	}

	@Override
	public String toString() {
		return "loop " + blockNode.toString();
	}

}

//Expressions
interface ExpNode {

	public int assess(Robot robot);

	@Override
	String toString();
}

//Sensors
interface SensNode extends ExpNode {

	@Override
	public int assess(Robot robot);

	@Override
	public String toString();

}

class FuelLeftNode implements SensNode {

	@Override
	public int assess(Robot robot) {
		return robot.getFuel();
	}

}

class OppLRNode implements SensNode {

	@Override
	public int assess(Robot robot) {
		return robot.getOpponentLR();
	}

}

class OppFBNode implements SensNode {

	@Override
	public int assess(Robot robot) {
		return robot.getOpponentFB();
	}

}

class NumBarrelNode implements SensNode {

	@Override
	public int assess(Robot robot) {
		return robot.numBarrels();
	}

}

class BarrelLRNode implements SensNode {

	private ExpNode exp;

	public BarrelLRNode(ExpNode exp) {
		this.exp = exp;
	}

	@Override
	public int assess(Robot robot) {
		int arg = exp != null ? exp.assess(robot) : 0;
		return robot.getBarrelLR(arg);
	}

	@Override
	public String toString() {
		String s = "barrelLR";
		if (exp != null) {
			s = s + "(" + exp.toString() + ")";
		}
		return s;
	}
}

class BarrelFBNode implements SensNode {

	private ExpNode exp;

	public BarrelFBNode(ExpNode exp) {
		this.exp = exp;
	}

	@Override
	public int assess(Robot robot) {
		int arg = exp != null ? exp.assess(robot) : 0;
		return robot.getBarrelFB(arg);
	}

	@Override
	public String toString() {
		String s = "barrelFB";
		if (exp != null) {
			s = s + "(" + exp.toString() + ")";
		}
		return s;
	}
}

class WallDistNode implements SensNode {

	@Override
	public int assess(Robot robot) {
		return robot.getDistanceToWall();
	}

	@Override
	public String toString() {
		return "wallDist";
	}

}

class NumNode implements ExpNode {

	private final int n;

	public NumNode(int n) {
		this.n = n;
	}

	@Override
	public int assess(Robot robot) {
		return this.n;
	}

}

// conditional nodes
class IfNode extends StmtNode {
	private CondNode con;
	private BlockNode block;
	private ArrayList<CondNode> elseifCons = null;
	private ArrayList<BlockNode> elseifBlocks = null;
	private BlockNode elseBlock;

	public IfNode(CondNode con, BlockNode block, ArrayList<CondNode> elseifCons, ArrayList<BlockNode> elseifBlocks,
			BlockNode elseBlock) {
		this.con = con;
		this.block = block;
		this.elseifCons = elseifCons;
		this.elseifBlocks = elseifBlocks;
		this.elseBlock = elseBlock;
	}

	@Override
	public void execute(Robot robot) {
		if (con.assess(robot)) {
			block.execute(robot);
		} else {
			int i = 0;
			while (!elseifCons.isEmpty() && elseifBlocks.isEmpty()) {
				if (elseifCons.get(i).assess(robot)) {
					elseifBlocks.get(i).execute(robot);
					i++;
				}
				if (elseBlock != null) {
					elseBlock.execute(robot);
				}
			}
		}
	}

	@Override
	public String toString() {
		String s = "if (" + con.toString() + ") " + block.toString();

		if (!elseifCons.isEmpty() && !elseifBlocks.isEmpty()) {
			for (int i = 0; i < elseifCons.size(); i++) {
				s = s + "elif (" + elseifCons.get(i).toString() + ") " + elseifBlocks.get(i).toString();
			}
		}

		if (elseBlock != null) {
			s = s + "else " + elseBlock.toString();
		}
		return s;
	}

}

class WhileNode extends StmtNode {

	private CondNode con;
	private BlockNode block;

	public WhileNode(BlockNode block, CondNode con) {
		this.block = block;
		this.con = con;
	}

	@Override
	public void execute(Robot robot) {
		while (con.assess(robot)) {
			block.execute(robot);
		}
	}
}

class LTNode implements CondNode {

	private ExpNode exp1;
	private ExpNode exp2;

	public LTNode(ExpNode exp1, ExpNode exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public boolean assess(Robot robot) {
		return exp1.assess(robot) < exp2.assess(robot);
	}

	@Override
	public String toString() {
		return "lt(" + exp1.toString() + ", " + exp2.toString() + ")";
	}
}

class GTNode implements CondNode {

	private ExpNode exp1;
	private ExpNode exp2;

	public GTNode(ExpNode exp1, ExpNode exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public boolean assess(Robot robot) {
		return exp1.assess(robot) > exp2.assess(robot);
	}

	@Override
	public String toString() {
		return "gt(" + exp1.toString() + ", " + exp2.toString() + ")";
	}
}

class EqNode implements CondNode {

	private ExpNode exp1;
	private ExpNode exp2;

	public EqNode(ExpNode exp1, ExpNode exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public boolean assess(Robot robot) {
		return exp1.assess(robot) == exp2.assess(robot);
	}

	@Override
	public String toString() {
		return "eq(" + exp1.toString() + ", " + exp2.toString() + ")";
	}
}

//Conditionals
interface CondNode {

	public boolean assess(Robot robot);

	@Override
	public String toString();
}

// action nodes
class TurnLNode extends StmtNode {

	@Override
	public void execute(Robot robot) {
		robot.turnLeft();
	}

	@Override
	public String toString() {
		return "turnL";
	}
}

class TurnRNode extends StmtNode {

	@Override
	public void execute(Robot robot) {
		robot.turnRight();
	}

	@Override
	public String toString() {
		return "turnR";
	}
}

class MoveNode extends StmtNode {

	private ExpNode exp;

	public MoveNode(ExpNode exp) {
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		int argument = exp != null ? exp.assess(robot) : 1;
		for (int i = 0; i < argument; i++) {
			robot.move();
		}
	}

	@Override
	public String toString() {
		String s = "move";
		if (exp != null) {
			s = s + "(" + exp.toString() + ")";
		}
		return s + ";";
	}
}

class WaitNode extends StmtNode {

	private ExpNode exp;

	public WaitNode(ExpNode exp) {
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		int argument = exp != null ? exp.assess(robot) : 1;
		for (int i = 0; i < argument; i++) {
			robot.idleWait();
		}
	}

	@Override
	public String toString() {
		return "wait";
	}
}

class TakeFuelNode extends StmtNode {

	@Override
	public void execute(Robot robot) {
		robot.takeFuel();
	}

	@Override
	public String toString() {
		return "takeFuel;";
	}

}
//Designate
class DesigNode extends StmtNode {

	private VarNode var;
	private ExpNode exp;

	public DesigNode(VarNode var, ExpNode exp) {
		this.var = var;
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		var.setValue(exp.assess(robot));
	}

	@Override
	public String toString() {
		return var.toString() + " = " + exp.toString() + ";";
	}
}

// You could add the node classes here, as long as they are not declared public
// (or private)

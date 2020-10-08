import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */
public class HuffmanCoding {
	private static final int R = 256;
	private Map<Character, String> map;
	private Map<String, Character> reverseMap;
	private String text;
	private Node rootNode;

	private static class Node implements Comparable<Node> {
		Node leftNode;
		Node rightNode;
		int weight;
		char c;

		public Node(Node leftNode, Node rightNode, int weight, char c) {
			this.leftNode = leftNode;
			this.rightNode = rightNode;
			this.weight = weight;
			this.c = c;
		}

		private boolean isLeaf() {
			assert ((leftNode == null) && (rightNode == null)) || ((leftNode != null) && (rightNode != null));
			return (leftNode == null) && (rightNode == null);
		}

		@Override
		public int compareTo(Node o) {
			return this.weight - o.weight;
		}
	}

	/**
	 * This would be a good place to compute and store the tree.
	 */
	public HuffmanCoding(String text) {
		rootNode = buildTrie(text);
		map = new HashMap<Character, String>();
		reverseMap = new HashMap<String, Character>();
		this.text = text;
		
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		char input[] = text.toCharArray();
		ArrayList<String> e = new ArrayList<String>();
		buildDictionary(rootNode, "");
		for (char c : input){
			e.add(map.get(c));
		}
		String toReturn = null;
		for(String s : e){
			 toReturn = toReturn + s;
			 //System.out.println(s);
		}
		return toReturn;
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		char output[] = encoded.toCharArray();
		ArrayList<Character> decode = new ArrayList<Character>();
		ArrayList<Character> string = new ArrayList<Character>();
		
		for(char c : output){
			decode.add(c);
			if(map.containsValue(decode.toString()));
			string.add(reverseMap.get(decode.toString()));
		}
		return string.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}

	private static Node buildTrie(String text) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>();
		Map<Character, Integer> freq = new HashMap<>();
		for (char c: text.toCharArray()){
			freq.put(c, freq.getOrDefault(c, 0) + 1);
		}
		for(HashMap.Entry<Character, Integer> entry : freq.entrySet()){
			pq.add(new Node(null, null, entry.getValue(), entry.getKey()));
		}
		for(Entry<Character, Integer> i : freq.entrySet()){
			System.out.println(i. getKey() + " : " + i.getValue());
		}

		// merge two smallest trees
		while (pq.size() > 1) {
			Node left = pq.poll();
			Node right = pq.poll();
			Node parent = new Node(left, right, left.weight + right.weight, '\0');
			pq.add(parent);
		}
		return pq.poll();
	}
	
	private void buildDictionary(Node node, String code){
		if (node.leftNode == null && node.rightNode == null){
			map.put(node.c, code);
			reverseMap.put(code, node.c);
			char[] temp = new char[1];
			temp[0] = node.c;
			String check = new String(temp);
			System.out.println(check + " : " + code);
		}
		else {
			buildDictionary(node.leftNode, code + "0");
			buildDictionary(node.rightNode, code + "1");
		}
	}
}

package code;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 *
 * @author tony
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;
	
	Set<Node> ArtPoints;

	List<Node> highlightedNodes = new ArrayList<>();
	Collection<Road> highlightedRoads = new HashSet<>();
	List<Segment> highlightedSegs = new LinkedList<>();
	Queue<Node>open;
	Queue<Node>close;

	Comparator<Node>comp = new Comparator<Node>(){
		//override compare method
		public int compare(Node i, Node j){
			if(i.getF() > j.getF()){
				return 1;
			}

			else if (i.getF() < j.getF()){
				return -1;
			}

			else{
				return 0;
			}
		}

	};
	


	public Graph(File nodes, File roads, File segments, File polygons) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;

		// draw all the segments.
		g2.setColor(Mapper.SEGMENT_COLOUR);
		for (Segment s : segments)
			s.draw(g2, origin, scale);

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}

		//draw all the highlighted segments
		for(Segment seg : highlightedSegs){
			seg.draw(g2, origin, scale);
		}

		// draw all the nodes.
		g2.setColor(Mapper.NODE_COLOUR);
		for (Node n : nodes.values())
			n.draw(g2, screen, origin, scale);

		// draw the highlighted node, if it exists.
		for (Node n : highlightedNodes) {
			g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			n.draw(g2, screen, origin, scale);
		}
	}

	public void setHighlight(Node node) {
		this.highlightedNodes.add(node);
	}

	public void setHighlight(Collection<Road> roads) {
		this.highlightedRoads = roads;
	}

	public void sethighlight(List<Segment> segs){
		this.highlightedSegs = segs;
	}

	protected void constructPath(Node node, Queue<Node> order) {
		LinkedList<Node> path = new LinkedList<Node>();
		Double length = 0.0;
		while (node.parent != null) {
			path.addFirst(node);
			node = node.parent;
		}
		for(Node n : path){
			for(Segment s : segments){
				if((s.start == n && s.end == n.parent) || (s.end == n && s.start == n.parent )){
					highlightedSegs.add(s);
					length = length + s.length;
				}	
			}
		}
		System.out.println(length);
	}

	public void route(Node source, Node goal){
		close = new LinkedList<Node>();
		open = new PriorityQueue<Node>(11, comp);

		//start cost
		source.g = 0;
		source.h = source.getH(goal);
		source.parent = null;

		open.add(source);


		while((!open.isEmpty())){
			Node current = open.poll();
			if(current.visited == false){current.visited = true;}
			if(current == goal){constructPath(goal, open);}

			Set<Node> nodes = new HashSet<>();

			for(Segment s : current.segments){
				if(s.end.visited == false){nodes.add(s.end); s.end.visited = true;}
				if(s.start.visited == false){nodes.add(s.start); s.start.visited = true;}
			}

			open.remove(current);
			close.add(current);

			for(Node n : nodes){
				if(close.contains(n)){}
				//look here
				Double tentG = current.g + current.getDist(n);
				if(!open.contains(n)){
					n.parent = current;
					n.g = tentG;
					n.h = n.getH(goal);
					n.f = n.getF();
					open.add(n);
				}
				else {
					if(tentG >= n.g){
						n.parent = current;
						n.g = current.g;
					}
				}
			}
			close.add(current);
		}
	}
}

// code for COMP261 assignments
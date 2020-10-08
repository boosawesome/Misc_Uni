package code;

import java.util.*;

public class Articulation {
	int CountSubTrees;
    Node n;
	Graph g;
	Set<Node> ArtPoints;
    
	public Articulation(Graph g, Node n) {
		this.g = g;
		this.n=n;
	}

	public Set<Node> APS(List<Node> NodesUnsearched) {
		for (Node nodes : NodesUnsearched)
			nodes.count = Integer.MAX_VALUE;
		ArtPoints = new HashSet<>();
		while (!NodesUnsearched.isEmpty()) {
			Node beginning = NodesUnsearched.get(0);
			beginning.count = 0;
			CountSubTrees = 0;
			List<Node> neighbours = new ArrayList<>();
			for (Segment s : beginning.segments) {
				if (s.start == beginning) {
					neighbours.add(s.end);
				} else if (s.end == beginning) {
					neighbours.add(s.start);
				}
			}

			for (Node nodes : neighbours) {
				if (nodes.count == Integer.MAX_VALUE) {
					iterArtPts(nodes, 1, beginning, NodesUnsearched);
					CountSubTrees++;
				}
			}
			if (CountSubTrees > 1) {
				ArtPoints.add(beginning);
			}
			NodesUnsearched.remove(beginning);
		}
		return ArtPoints;
	}

	public void iterArtPts(Node beginning, int count, Node root, List<Node> artPoints) {
		Stack<ArtPoint> stack = new Stack<>();
		stack.push(new ArtPoint(beginning, 1, new ArtPoint(root, 0, null)));

		while (!stack.isEmpty()) {
			ArtPoint current = stack.peek();
			Node node = current.getNode();
			if (node.count == Integer.MAX_VALUE) {
				current.setReachBack(current.getCount());
				node.count = current.getCount();
				
				List<Node>neighbours = new ArrayList<>();
				for (Segment seg : node.segments) {
					if (seg.start == node) {
						neighbours.add(seg.end);
					} else if (seg.end == node) {
						neighbours.add(seg.start);
					}
				}
				current.children = new ArrayList<>();
				for (Node n : neighbours) {
					if (n != current.getParent().getNode()) {
						current.children.add(n);
					}
				}

			} else if (!current.children.isEmpty()) {
				Node decent = current.children.remove(0);
				if (decent.count < Integer.MAX_VALUE) {
					current.setReachBack(Math.min(current.getReachBack(), decent.count));
				} else {
					stack.push(new ArtPoint(decent, node.count + 1, current));
				}

			} else {
				if (node.nodeID != beginning.nodeID) {
					if (current.getReachBack() >= current.getParent().getCount()) {
						ArtPoints.add(current.getParent().getNode());
					}
					current.getParent().setReachBack(Math.min(current.getParent().getReachBack(), current.getReachBack()));
				}
				stack.pop();
				artPoints.remove(current.getNode());
			}
		}
	}
}
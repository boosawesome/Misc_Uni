package code;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MapEngine extends GUI {
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 1600;

	Node selectedNode;

	Graph graph;

	public MapEngine() {
	}

	@Override
	protected void redraw(Graphics g) {
		if (graph == null) {
		} else
			graph.render(g);
	}
	
	/* 
	 * Point mousePoint = e.getPoint();		
		Location mouseLocation = Location.newFromPoint(mousePoint, origin, scale);
		double closest = Double.MAX_VALUE;
		Node n = null;
		
		for(Map.Entry<Integer, Node> entry : Node_Map.entrySet()){	
			n = entry.getValue();
			double dist = mouseLocation.distance(n.getLoc());		
			if(dist < closest){										
				closest = dist;
				Select_Node = n;
	 */
	
	@Override
	protected void onClick(MouseEvent e) {
		Location mouseLocation = Location.newFromPoint(e.getPoint(), Graph.origin, Graph.SCALE_LAT);
		double closest = Double.MAX_VALUE;
		Node n = null;
		selectedNode = null;
		
		for (Map.Entry<Integer, Node> entry : graph.nodes.entrySet()) {
			n = entry.getValue();

			double distance = mouseLocation.distance(n.getLoc());

			if (distance < closest) {
				closest = distance;
				selectedNode = n;
			}
		}
		for(Map.Entry<Integer, Node> entry : graph.nodes.entrySet()){
			entry.getValue().Select(false);
		}
		graph.nodes.get(selectedNode.nodeID).Select(true);
		getTextOutputArea().setText(Integer.toString(selectedNode.nodeID));
	}

	@Override
	protected void onSearch() {

		String query = getSearchBox().getText();

		for (Segment segment : graph.segments) {
			segment.Highlight(false);
		}
		if (graph.trie.contains(query) == null) {
			System.out.println("dang");
		}
		Set<Road> roads = graph.trie.contains(query);
		if (roads != null) {
			ArrayList<String> str = new ArrayList<>();
			str.add("\"" + query + "\"" + " HAS MATCHES:" + "  ");

			for (Road road : roads) {
				str.add(road.getLabel());

				getTextOutputArea().setText(str.toString());

				for (Segment segment : graph.segments) {
					if (segment.getRoad() == road) {
						segment.Highlight(true);
					}
				}
			}
		}

	}

	@Override
	protected void onMove(Move m) {
		switch (m) {

		case ZOOM_IN:
			Graph.zoom = Graph.zoom + 10;
			break;

		case ZOOM_OUT:
			if (Graph.zoom > 0) {
				Graph.zoom = Graph.zoom - 10;
			}
			break;

		case NORTH:
			Graph.CENTRE_LON = Graph.CENTRE_LON - 0.01;
			break;

		case EAST:
			Graph.CENTRE_LAT = Graph.CENTRE_LAT + 0.01;
			break;

		case SOUTH:
			Graph.CENTRE_LON = Graph.CENTRE_LON + 0.01;
			break;

		case WEST:
			Graph.CENTRE_LAT = Graph.CENTRE_LAT - 0.01;
			break;
		}

	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		try {
			this.graph = new Graph(nodes, roads, segments, polygons);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new MapEngine();

	}
}

package code;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph {

	HashMap<Integer, Node> nodes;
	Set<Segment> segments;
	HashMap<Integer, Road> roads;
	private Set<Polygon> polygons;

	Trie trie;

	public static double SCALE_LAT = 111.0;
	public static double SCALE_LON = 88.649;

	public static double CENTRE_LAT = -36.868816;
	public static double CENTRE_LON = 174.744800;

	public static Location origin = new Location(CENTRE_LAT, CENTRE_LON);

	public static double zoom = 200;

	//Loads data structures with values read in from files
	public Graph(File nodeFile, File roadFile, File segmentFile, File polygonFile) throws Exception {
		try {
			nodes = FileSort.ProcessNodes(nodeFile);
			roads = FileSort.ProcessRoads(roadFile);
			segments = FileSort.ProcessSegments(segmentFile, nodes, roads);
			//polygons = FileSort.ProcessPolygons(polygonFile);

			trie = new Trie();
			for (Map.Entry<Integer, Road> entry : roads.entrySet()) {
				Road road = entry.getValue();
				trie.addString(road.getLabel(), road);
				
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
	}

	//Draws the graph
	public void render(Graphics g) {
		/*for (Polygon p : polygons) {
			if (p.getType() == 0x28)
				g.setColor(Color.BLUE);
			else if (p.getType() == 0x17)
				g.setColor(Color.GREEN);
			else if (p.getType() == 0x0e)
				g.setColor(Color.GRAY);
			else if (p.getType() == 0x07)
				g.setColor(Color.LIGHT_GRAY);
			else
				g.setColor(Color.WHITE);
			p.draw(g);
		}*/
	 

		// Calls the draw method for each node
		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
			Node node = entry.getValue();
			node.draw(g);
		}

		// Draws each segment on the map
		for (Segment s : segments) {
			s.draw(g);
		}
	}


}

package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class FileSort {

	public static HashMap<Integer, Road> ProcessRoads(File directory) throws IOException {
		HashMap<Integer, Road> roads = new HashMap<Integer, Road>();
		// Read file line by line
		@SuppressWarnings("resource")
		BufferedReader rData = new BufferedReader(new FileReader(directory));

		@SuppressWarnings("unused")
		String trashLine = rData.readLine();

		for (String rLine = rData.readLine(); rLine != null; rLine = rData.readLine()) {
			// Process each line using split method
			String[] rValues = rLine.split("\t");
			int roadID = Integer.parseInt(rValues[0]);
			int type = Integer.parseInt(rValues[1]);
			String label = rValues[2];
			String city = rValues[3];
			int oneway = Integer.parseInt(rValues[4]);
			int speed = Integer.parseInt(rValues[5]);

			// Create Road object and place in map
			roads.put(roadID, new Road(roadID, type, label, city, oneway, speed));
		}

		return roads;
	}

	public static HashMap<Integer, Node> ProcessNodes(File directory) throws IOException {
		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		// Read file line by line
		File nodeFile = directory;
		@SuppressWarnings("resource")
		BufferedReader nData = new BufferedReader(new FileReader(nodeFile));

		@SuppressWarnings("unused")
		String trashLine = nData.readLine();

		for (String nLine = nData.readLine(); nLine != null; nLine = nData.readLine()) {
			// Process each line using split method
			String[] nValues = nLine.split("\t");
			int nodeID = Integer.parseInt(nValues[0]);
			double lat = Double.parseDouble(nValues[1]);
			double lon = Double.parseDouble(nValues[2]);

			// Create Node object and place in map
			nodes.put(nodeID, new Node(nodeID, lat, lon));
		}

		return nodes;
	}

	public static HashSet<Segment> ProcessSegments(File directory, HashMap<Integer, Node> nodes,
			HashMap<Integer, Road> roads) throws IOException {
		HashSet<Segment> segments = new HashSet<Segment>();
		// Read file line by line
		File segFile = directory;
		@SuppressWarnings("resource")
		BufferedReader sData = new BufferedReader(new FileReader(segFile));

		@SuppressWarnings("unused")
		String trashLine = sData.readLine();

		for (String sLine = sData.readLine(); sLine != null; sLine = sData.readLine()) {
			// Process each line using split method
			String[] sValues = sLine.split("\t");
			int sRoadID = Integer.parseInt(sValues[0]);
			Road road = roads.get(sRoadID);
			double length = Double.parseDouble(sValues[1]);
			Node node1 = nodes.get(Integer.parseInt(sValues[2]));
			Node node2 = nodes.get(Integer.parseInt(sValues[3]));

			ArrayList<Location> coords = new ArrayList<Location>();

			for (int i = 4; i < sValues.length; i += 2) {
				double x = Double.parseDouble(sValues[i]);
				double y = Double.parseDouble(sValues[i + 1]);
				coords.add(new Location(x, y));
			}

			// Create Segment object and place in collection
			Segment seg = new Segment(node1, node2, length, road, coords);
			segments.add(seg);
			/*
			 * nodes.get(node1.getID()).setSegments(seg);
			 * nodes.get(node2.getID()).setSegments(seg);
			 * roads.get(road.ID).setSegments(seg);
			 */
		}

		return segments;
	}

	public static HashSet<Polygon> ProcessPolygons(File directory) throws IOException {
		HashSet<Polygon> polygons = new HashSet<Polygon>();
		// Read file line by line
		File polyFile = directory;
		BufferedReader pData = new BufferedReader(new FileReader(polyFile));

		pData.readLine();

		// Process each block of data representing a polygon
		String pLine;
		boolean inPoly = false;
		int type = 0;
		ArrayList<Location> locations = new ArrayList<>();
		while ((pLine = pData.readLine()) != null) {
			if (pLine.contains("[POLYGON]")) {
				inPoly = true;
				type = 0;
				locations = new ArrayList<>();
			} else if (pLine.contains("[END]")) {
				inPoly = false;
				Polygon polygon = new Polygon(type, locations);
				polygons.add(polygon);
			}

			if (inPoly) {
				if (pLine.startsWith("Type=")) {
					pLine = pLine.substring(7);
					type = Integer.parseInt(pLine, 16);
				}

				if (pLine.startsWith("Data0=")) {
					pLine = pLine.substring(6);
					pLine = pLine.replace("(", "");
					pLine = pLine.replace(")", "");
					pLine = pLine.replace(",", " ");

					Scanner sc = new Scanner(pLine);
					while (sc.hasNextDouble()) {
						Location loc = new Location(sc.nextDouble(), sc.nextDouble());
						locations.add(loc);
					}
					sc.close();
				}
			}
		}

		pData.close();

		return polygons;
	}
}

package code;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;
/**
 * This is the class which defines the node objects.
 * 
 * @author Ryan van den Eykel 300335200
 *
 */
public class Node {

	int nodeID;

	private Set<Segment> segments = new HashSet<Segment>();

	double lat;
	double lon;
	
	Location location;
	
	boolean selected = false;
	boolean view = true;
	
	/**
	 * 
	 * @param nodeID - integer identifying node
	 * @param lat - latitude of node
	 * @param lon - longitude of node
	 */
	public Node(int nodeID, double lat, double lon) {
		this.nodeID = nodeID;
		this.lat = lat;
		this.lon = lon;
		this.location = new Location(lat, lon);
	}
	int x = (int) ((lat - Graph.CENTRE_LAT) * Graph.SCALE_LAT * Graph.zoom);
	int y = (int) ((lon - Graph.CENTRE_LON) * Graph.SCALE_LON * Graph.zoom);
	
	/**
	 * Draws the node object on the canvas
	 * @param g - the graphics
	 */
	public void draw(Graphics g) {

		if (selected) {
			g.setColor(Color.RED);
			for (Segment segment : segments) {
				segment.draw(g);
			}
		}
		
		//Adjust values of latitude and longitude to pixel position and draw
		int dx = (int) ((lat - Graph.CENTRE_LAT) * Graph.SCALE_LAT * Graph.zoom);
		int dy = (int) ((lon - Graph.CENTRE_LON) * Graph.SCALE_LON * Graph.zoom);
		int x = (int) (dx* (Graph.SCALE_LAT / 100) + Graph.CENTRE_LAT);
		int y = (int) (dy* (Graph.SCALE_LON / 100) + Graph.CENTRE_LON);
		
		if(selected) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		g.fillRect((int) x, (int) y, 2, 2);
	}


	public int getID() {
		return nodeID;
	}

	public void setSegments(Segment segments) {
		this.segments.add(segments);
	}

	public Set<Segment> getSegments() {
		return segments;
	}
	
	public Location getLoc(){
		return location;
	}
	public double getLocX() {
		return (x * (Graph.SCALE_LAT / 100) + Graph.CENTRE_LAT);
	}

	public double getLocY() {
		return (y * (Graph.SCALE_LON / 100) + Graph.CENTRE_LON);
	}

	public void Select(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

}

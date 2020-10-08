package code;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
/**
 * This is the class which defines the segment objects.
 * 
 * @author Ryan van den Eykel 300335200
 *
 */
public class Segment {

	public Node node1;
	public Node node2;

	public double length;

	public Road road;

	public ArrayList<Location> coords;

	public boolean highlighted;
	
	/**
	 * 
	 * @param node1 - node of the road segment is a part of
	 * @param node2 - node of the road segment is a part of
	 * @param length - length of segment
	 * @param road - road the segment is a part of
	 * @param coords - coordinates of the segment ends in the road
	 */

	public Segment(Node node1, Node node2, double length, Road road, ArrayList<Location> coords){
		this.node1 = node1;
		this.node2 = node2;
		this.length = length;
		this.road = road;
		this.coords = coords;
	}
	/**
	 * Draw the segments
	 * @param g - the graphics
	 */
	public void draw(Graphics g){
		
		//Adjust coordinates of each segment end for pixel position and draw
		for(int i = 0; i < coords.size() - 1; i++){
			Location loc1 = coords.get(i);
			double dx1 =  ((loc1.x - Graph.CENTRE_LAT) * Graph.SCALE_LAT * Graph.zoom);
			double dy1 =  ((loc1.y - Graph.CENTRE_LON) * Graph.SCALE_LON * Graph.zoom);

			int x1 = (int) (dx1* (Graph.SCALE_LAT / 100) + Graph.CENTRE_LAT);
			int y1 = (int) (dy1* (Graph.SCALE_LON / 100) + Graph.CENTRE_LON);


			Location loc2 = coords.get(i + 1);
			double dx2 = ((loc2.x - Graph.CENTRE_LAT) * Graph.SCALE_LAT * Graph.zoom);
			double dy2 = ((loc2.y - Graph.CENTRE_LON) * Graph.SCALE_LON * Graph.zoom);

			int x2 = (int) (dx2* (Graph.SCALE_LAT / 100) + Graph.CENTRE_LAT);
			int y2 = (int) (dy2* (Graph.SCALE_LON / 100) + Graph.CENTRE_LON);
			
			if(highlighted) g.setColor(Color.RED);
			else g.setColor(Color.BLUE);
			g.drawLine(x1, y1, x2, y2);
		}

}


	public Node getNode1(){
		return node1;
	}

	public Node getNode2(){
		return node2;
	}

	public double getLength(){
		return length;
	}

	public Road getRoad(){
		return road;
	}

	public ArrayList<Location> getCoords(){
		return coords;
	}

	public void Highlight(boolean highlighted) {
		this.highlighted = highlighted;
	}
}

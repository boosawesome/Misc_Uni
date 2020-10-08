package code;
/**
 * This is the class which defines the polygon objects.
 * 
 * @author Ryan van den Eykel 300335200
 *
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
	private int type;
	private List<Location> locations = new ArrayList<>();

	public Color color;

	public Polygon(int type, ArrayList<Location> locations) {
		this.type = type;
		this.locations = locations;
	}
	
	/**
	 * Draws the polygon object on the canvas
	 * @param g - the graphics
	 */
	public void draw(Graphics g) {
		int[] xPoints = new int[locations.size()];
		int[] yPoints = new int[locations.size()];
		
		//Fill int arrays with the coordinates, adjusted to pixel position, and draw
		for (int i = 0; i < locations.size(); i++) {
			Location loc =  locations.get(i);
			xPoints[i] = (int) (loc.x * (Graph.SCALE_LAT / 100) + Graph.CENTRE_LAT);
			yPoints[i] = (int) (loc.y * (Graph.SCALE_LON / 100) + Graph.CENTRE_LON);
		}
		
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}

	public int getType() {
		return type;
	}
}

package renderer;

import java.util.HashMap;
import java.util.Map;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {

	private int y1;
	private int y2;
	public Map<Integer, ListRow> edges = new HashMap<Integer, ListRow>();

	public EdgeList(int startY, int endY) {
		this.y1 = startY;
		this.y2 = endY;
		for (int i = startY; i <= endY; i++) {
			edges.put(i, new ListRow());
		}
	}

	public int getStartY() {
		return y1;
	}

	public int getEndY() {
		return y2;
	}

	public float getLeftX(int i) {
		return (float) edges.get(i).leftX;
	}

	public float getRightX(int i) {
		return (float) edges.get(i).rightX;
	}

	public float getLeftZ(int i) {
		return (float) edges.get(i).leftZ;
	}

	public float getRightZ(int i) {
		return (float) edges.get(i).rightZ;
	}

	public void setLeftZ(int i, float z) {
		edges.get(i).leftZ = z;
	}

	public void setRightZ(int i, float z) {
		edges.get(i).rightZ = z;
	}

	public void setLeftX(int i, float x) {
		edges.get(i).leftX = x;
	}

	public void setRightX(int i, float x) {
		edges.get(i).rightX = x;
	}

	public static class ListRow {
		private float leftX;
		private float rightX;
		private float leftZ;
		private float rightZ;
	}
}

// code for comp261 assignments
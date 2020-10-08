package renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		// check whether cross product is positive
		Vector3D[] v = poly.getVertices();
		Vector3D a = v[2].minus(v[1]);
		Vector3D b = v[1].minus(v[0]);
		Vector3D normal = b.crossProduct(a);
		return (normal.z > 0f);
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		// from lecture slides
		int r = 255;
		int g = 255;
		int b = 255;
		float red, green, blue;
		Vector3D e1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
		Vector3D e2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
		Vector3D unitNormal = e1.crossProduct(e2).unitVector();
		float cos = unitNormal.cosTheta(lightDirection);

		if (cos < 0) {
			lightColor = new Color(0, 0, 0);
		}
		// ascertain red value
		red = ((((1 / (float) 255) * ambientLight.getRed()) + (1 / (float) 255) * lightColor.getRed() * cos)
				* (1 / (float) 255) * poly.getReflectance().getRed());
		if (red * 255 < 255)
			r = (int) (red * 255);
		// ascertain green value
		green = ((((1 / (float) 255) * ambientLight.getGreen()) + (1 / (float) 255) * lightColor.getGreen() * cos)
				* (1 / (float) 255) * poly.getReflectance().getGreen());
		if (green * 255 < 255)
			g = (int) (green * 255);
		// ascertain blue value
		blue = ((((1 / (float) 255) * ambientLight.getBlue()) + (1 / (float) 255) * lightColor.getBlue() * cos)
				* (1 / (float) 255) * poly.getReflectance().getBlue());
		if (blue * 255 < 255)
			b = (int) (blue * 255);

		return new Color(r, g, b);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		List<Polygon> rotation = new ArrayList<Polygon>();
		Vector3D rotatedLight;
		// create transformation matrix, multiple it by light vector
		rotatedLight = Transform.newYRotation(yRot).multiply(Transform.newXRotation(xRot).multiply(scene.getLight()));
		Vector3D[] vertices;
		// create transformation matrix, multiple it by current vector, for each
		// vector of each polygon
		for (Polygon p : scene.getPolygons()) {
			vertices = new Vector3D[4];
			int i = 1;
			for (Vector3D point : p.getVertices()) {
				point = Transform.newYRotation(yRot).multiply(Transform.newXRotation(xRot).multiply(point));
				vertices[i] = point;
				i++;
			}
			Color c = p.getReflectance();
			rotation.add(new Polygon(vertices[1], vertices[2], vertices[3], c));
		}
		return new Scene(rotation, rotatedLight);
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */

	public static Scene translateScene(Scene scene) {
		// Centre scene
		List<Polygon> newPolygons = new ArrayList<Polygon>();
		float minY = Float.POSITIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float minX = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;

		for (Polygon poly : scene.getPolygons()) {
			for (Vector3D point : poly.getVertices()) {
				if (minY > point.y)
					minY = point.y;
				if (maxY < point.y)
					maxY = point.y;
				if (minX > point.x)
					minX = point.x;
				if (maxX < point.x)
					maxX = point.x;
			}
		}
		return translateScene(scene, newPolygons, maxY, minY, maxX, minX);

	}

	public static Scene translateScene(Scene scene, List<Polygon> newPolygons, float maxY, float minY, float maxX,
			float minX) {
		// create transformation matrix, multiple it by current vector, for each
		// vector of each polygon
		for (Polygon poly : scene.getPolygons()) {
			Vector3D[] vertices = new Vector3D[3];
			int i = 0;
			for (Vector3D p : poly.getVertices()) {
				vertices[i] = Transform.newTranslation(-1 * minX + (GUI.CANVAS_WIDTH - (maxX - minX)) / 2,
						-1 * minY + (GUI.CANVAS_HEIGHT - (maxY - minY)) / 2, 0).multiply(p);
				i++;
			}
			newPolygons.add(new Polygon(vertices[0], vertices[1], vertices[2], poly.getReflectance()));
		}
		return new Scene(newPolygons, scene.lightPos);
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// scales scene to fit canvas
		List<Polygon> newPolygons = new ArrayList<Polygon>();
		float minY = Float.POSITIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float minX = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;

		for (Polygon poly : scene.getPolygons()) {
			for (Vector3D point : poly.getVertices()) {
				if (minX > point.x)
					minX = point.x;
				if (maxX < point.x)
					maxX = point.x;
				if (minY > point.y)
					minY = point.y;
				if (maxY < point.y)
					maxY = point.y;

			}
		}
		return scaleScene(minX, minY, maxX, maxY, newPolygons, scene);
	}

	public static Scene scaleScene(float minX, float minY, float maxX, float maxY, List<Polygon> newPolygons, Scene scene) {
		float s;
		if (((GUI.CANVAS_HEIGHT - 350) / (maxY - minY)) > ((GUI.CANVAS_WIDTH - 350) / (maxX - minX)))
			s = ((GUI.CANVAS_WIDTH - 350) / (maxX - minX));
		else
			s = ((GUI.CANVAS_HEIGHT - 350) / (maxY - minY));
		// create transformation matrix, multiple it by current vector, for each
		// vector of each polygon
		for (Polygon p : scene.getPolygons()) {
			Vector3D[] vertices = new Vector3D[3];
			int i = 0;
			for (Vector3D v : p.getVertices()) {
				vertices[i] = Transform.newScale(s, s, s).multiply(v);
				;
				i++;
			}
			newPolygons.add(new Polygon(vertices[0], vertices[1], vertices[2], p.getReflectance()));
		}
		return new Scene(newPolygons, scene.lightPos);
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		EdgeList edgeList = new EdgeList(
				(int) (Math.min(poly.vertices[1].y, Math.min(poly.vertices[0].y, poly.vertices[2].y))),
				(int) (Math.max(poly.vertices[1].y, Math.max(poly.vertices[0].y, poly.vertices[2].y))));
		Vector3D[][] edges = new Vector3D[][] { new Vector3D[] { poly.vertices[0], poly.vertices[1] },
				new Vector3D[] { poly.vertices[1], poly.vertices[2] },
				new Vector3D[] { poly.vertices[2], poly.vertices[0] } };

		float x;
		float slope;
		int y;
		for (Vector3D[] e : edges) {
			slope = (e[1].x - e[0].x) / (e[1].y - e[0].y);
			y = (int) (e[0].y);
			x = e[0].x;
			if (e[0].y < e[1].y) {
				while (y <= (int) (e[1].y)) {
					edgeList.setLeftX(y, x);
					x = x + slope;
					y++;
				}
			} else {
				while (y >= (int) (e[1].y)) {
					edgeList.setRightX(y, x);
					x = x - slope;
					y--;
				}
			}
		}
		float z;
		for (Vector3D[] e : edges) {
			slope = (e[1].z - e[0].z) / (e[1].y - e[0].y);
			z = e[0].z;
			y = (int) (e[0].y);
			if (e[0].y < e[1].y) {
				while (y <= (e[1].y)) {
					edgeList.setLeftZ(y, z);
					z = z + slope;
					y++;
				}
			} else {
				while (y >= (e[1].y)) {
					edgeList.setRightZ(y, z);
					z = slope;
					y--;
				}
			}
		}
		return edgeList;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {

		float slope, z, x;

		for (int y = polyEdgeList.getStartY(); y <= polyEdgeList.getEndY(); y++) {
			slope = (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y))
					/ (float) (polyEdgeList.getRightX(y) - polyEdgeList.getLeftX(y));
			z = (float) Math.floor(polyEdgeList.getLeftZ(y));
			x = (float) Math.floor(polyEdgeList.getLeftX(y));

			while (x <= Math.floor(polyEdgeList.getRightX(y))) {
				if ((int) x >= 0 && y >= 0 && (int) x < zbuffer.length && y < zbuffer[(int) x].length
						&& z < zdepth[(int) x][y]) {
					zbuffer[(int) x][y] = polyColor;
					zdepth[(int) x][y] = z;
				}

				z = z + slope;
				x++;
			}
		}
	}
}
// code for comp261 assignments

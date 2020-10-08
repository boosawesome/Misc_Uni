package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.event.ChangeEvent;

import renderer.Scene.Polygon;

public class Renderer extends GUI {

	protected Scene scene = null;
	protected Color ambientLight = Color.white;
	protected float xRot = 0;
	protected float yRot = 0;
	protected boolean scaled = false;

	@Override
	protected void onLoad(File file) {
		//set values to default
		xRot = 0;
		yRot = 0;
		scaled = false;
		
		Scanner scan = null;
		Scanner sc = null;
		
		//scan in polygons file
		try {
			scan = new Scanner(file);
			String lightStr;
			String[] values;
			Vector3D light;
			List<Polygon> polygons = new ArrayList<Polygon>();
			lightStr = scan.nextLine();
			values = lightStr.split(" ");
			light = new Vector3D(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
			
			//set each 3Dimensional vector
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				sc = new Scanner(line);
				Vector3D v1 = new Vector3D(sc.nextFloat(), sc.nextFloat(), sc.nextFloat());
				Vector3D v2 = new Vector3D(sc.nextFloat(), sc.nextFloat(), sc.nextFloat());
				Vector3D v3 = new Vector3D(sc.nextFloat(), sc.nextFloat(), sc.nextFloat());
				Color c = new Color(sc.nextInt(), sc.nextInt(), sc.nextInt());
				polygons.add(new Polygon(v1, v2, v3, c));
			}
			this.scene = new Scene(polygons, light);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
			sc.close();
		}
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_LEFT) {
			yRot = (float) 0.1;
			xRot = 0;
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			yRot = 0;
			xRot = (float) -0.1;
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			yRot = 0;
			xRot = (float) 0.1;
		} else if (ev.getKeyCode() == KeyEvent.VK_RIGHT) {
			yRot = (float) -0.1;
			xRot = 0;
		} else if (Character.toUpperCase(ev.getKeyChar()) == 'U') {
		}
	}

	/**
	 * Triggered by moving a slider in the GUI
	 * Does not do anything as of yet
	 */
	protected void onLightChange(ChangeEvent e) {
		int[] Color = getAmbientLight();
		ambientLight = new Color(Color[0], Color[1], Color[2]);
		render();
	}

	@Override
	protected BufferedImage render() {
		if (scene == null)
			return null;
		if (!scaled) {
			scene = Pipeline.scaleScene(scene);
			scaled = true;
		}
		scene = Pipeline.translateScene(Pipeline.rotateScene(scene, xRot, yRot));
		
		//initalise zbuffer and zdepth
		Color[][] zbuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		for (int i = 0; i < zbuffer.length; i++) {
			for (int j = 0; j < zbuffer[0].length; j++) {
				zbuffer[i][j] = new Color(200, 200, 200);
				zdepth[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		int[] color = getAmbientLight();
		Color col = new Color(color[0], color[1], color[2]);
		for (Polygon poly : scene.getPolygons()) {
			Pipeline.computeZBuffer(zbuffer, zdepth, Pipeline.computeEdgeList(poly),
					Pipeline.getShading(poly, scene.getLight(), new Color(200, 200, 200), col));
		}
		return convertBitmapToImage(zbuffer);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}
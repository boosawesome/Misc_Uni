package renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.server.SocketSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends GUI {
    
    // the 3D model
    private Scene scene;
    private Scene centralisedScene;
    // the viewing angle for rotation
    private float xRot = 0f, yRot = 0f;
    // the viewing position for translation
    private Vector3D viewerPosition = new Vector3D(0f, 0f, 0f);
    // the scale for zooming in or out
    private float currentScale = 1.0f;
    private static final float ZOOM_FACTOR = 1.3f;
    private static final float MIN_ZOOM = 0.5f, MAX_ZOOM = 5.0f;
    // the angle that is manually rotated by 
    private float rotationAngle = 0.2f;
    // the distance of manual translation
    private float translationAmount = 2.0f;
    // a flag to switch between dragging to rotate and to move
    private boolean isRotating = true;
    // a position of where the dragging started
    private Point dragStart;
    
	@Override
	protected void onLoad(File file) {
	    
	    // set to default value;
	    xRot = yRot = 0f;
	    viewerPosition = new Vector3D(0f, 0f, 0f);
	    currentScale = 1.0f;
	    isRotating = true;
	    centralisedScene = null;
	    
	    BufferedReader bfReader;
	    List<Polygon> polygons = new ArrayList<>();
	    Vector3D lightPos;
	    
	    try {
            bfReader = new BufferedReader(new FileReader(file));
            String line = bfReader.readLine();
            
            if (line == null) {
                System.out.println("Empty source file.");
                bfReader.close();
                return;
            }
            
            String[] values = line.split(" ");
            
            // parse the light source
            float x = Float.parseFloat(values[0]);
            float y = Float.parseFloat(values[1]);
            float z = Float.parseFloat(values[2]);
            lightPos = new Vector3D(x, y, z);
            
            line = bfReader.readLine();
            
            // parse polygons one by one
            while (line != null) {
                values = line.split(" ");
                
                // 9 coordinates for the polygon
                float a_x = Float.parseFloat(values[0]);
                float a_y = Float.parseFloat(values[1]);
                float a_z = Float.parseFloat(values[2]);
                float b_x = Float.parseFloat(values[3]);
                float b_y = Float.parseFloat(values[4]);
                float b_z = Float.parseFloat(values[5]);
                float c_x = Float.parseFloat(values[6]);
                float c_y = Float.parseFloat(values[7]);
                float c_z = Float.parseFloat(values[8]);
                float[] points = new float[]{a_x, a_y, a_z, b_x, b_y, b_z, c_x, c_y, c_z};
                
                // (r, g, b) for the surface color of the polygon
                int r = Integer.parseInt(values[9]);
                int g = Integer.parseInt(values[10]);
                int b = Integer.parseInt(values[11]);
                int[] color = new int[]{r, g, b};
                
                polygons.add(new Polygon(points, color));
                
                line = bfReader.readLine();
            }
            bfReader.close();
            this.scene = new Scene(polygons, lightPos);
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException");
        } catch (IOException e) {
            System.err.println("IOException");
        }
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
	    char cha = ev.getKeyChar();
	    
	    // Rotation
	    if (ev.getKeyCode() == KeyEvent.VK_UP) {
            rotateX(-rotationAngle);
        } else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
            rotateX(rotationAngle);
        } else if (ev.getKeyCode() == KeyEvent.VK_LEFT) {
            rotateY(rotationAngle);
        } else if (ev.getKeyCode() == KeyEvent.VK_RIGHT) {
            rotateY(-rotationAngle);
            
        // Translation
        } else if (cha == 'w' || cha == 'W') {
            moveDown(-translationAmount);
        } else if (cha == 's' || cha == 'S') {
            moveDown(translationAmount);
        } else if (cha == 'a' || cha == 'A') {
            moveRight(-translationAmount);
        } else if (cha == 'd' || cha == 'D') {
            moveRight(translationAmount);
        } else if (cha == 'q' || cha == 'Q') {
            zoom(1.0f / ZOOM_FACTOR);
        } else if (cha == 'e' || cha == 'E') {
            zoom(ZOOM_FACTOR);
        }
	}
	
	private void moveDown(float amount) {
	    viewerPosition = viewerPosition.plus(new Vector3D(0f, amount, 0f));
	}

    private void moveRight(float amount) {
        viewerPosition = viewerPosition.plus(new Vector3D(amount, 0f, 0f));
    }

    private void zoom(float scale) {
        currentScale *= scale;
        if (currentScale < MIN_ZOOM) {
            currentScale = MIN_ZOOM;
        } else if (currentScale > MAX_ZOOM) {
            currentScale = MAX_ZOOM;
        }
    }

    private void rotateX(float amount) {
	    xRot += amount;
	}
	
	private void rotateY(float amount) {
	    yRot += amount;
	}
    
    @Override
    protected void onScroll(MouseWheelEvent e) {
        int i = e.getWheelRotation();
        if (i > 0) {
            zoom(1.0f / ZOOM_FACTOR);
        } else {
            zoom(ZOOM_FACTOR);
        }
    }
    
    @Override
    protected void onPressed(MouseEvent e) {
        dragStart = e.getPoint();
    }

    @Override
    protected void onReleased(MouseEvent e) {
        Point dragEnd = e.getPoint();
        int mx = dragEnd.x - dragStart.x;
        int my = dragEnd.y - dragStart.y;
        
        if (isRotating) {
            rotateY(-mx / 100.0f);
            rotateX(my / 100.0f);
        } else {
            moveRight(mx);
            moveDown(my);
        }
    }
    
    @Override
    protected void switchMoveRotation() {
        isRotating = !isRotating;
    }
    

    @Override
    protected void onDefault() {
        xRot = 0f;
        yRot = 0f;
        viewerPosition = new Vector3D(0f, 0f, 0f);
        currentScale = 1.0f;
    }

	@Override
	protected BufferedImage render() {

	    Color[][] zbuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
	    float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
	    
	    // initialise all pixels as default color
	    Color backgroundColor = new Color(200, 200, 200);  // light grey
	    for (int i = 0; i < zbuffer.length; i++) {
	        for (int j = 0; j < zbuffer[i].length; j++) {
	            zbuffer[i][j] = backgroundColor;
	        }
	    }
	    
	    // before loading in anything, display a background
	    if (scene == null) {
	        return convertBitmapToImage(zbuffer);
	    }
	    
	    Dimension dimension = getDrawingDimension();
	    if (centralisedScene == null) {
	        // scale the scene to fit in the canvas
	        float[] boundary = scene.getBoundary();
	        centralisedScene = Pipeline.autoScaleAndTranslate(scene, boundary, dimension);
	    }
	    
	    // rotate the camera
	    Scene rotatedScene = Pipeline.rotateScene(centralisedScene, xRot, yRot);
	    
	    // scale the scene
	    Scene scaledScene = Pipeline.scaleScene(rotatedScene, currentScale, currentScale, currentScale);
	    
	    // re-centralise the scene again
	    float[] newBoundary = scaledScene.getBoundary();
	    Scene reCenteredScene = Pipeline.autoTranslate(scaledScene, newBoundary, dimension);
	    
	    // translate the scene towards the viewer position
	    Scene translatedScene = Pipeline.translateScene(reCenteredScene,
	            viewerPosition.x, viewerPosition.y, viewerPosition.z);

	    // initialise the depth of all pixels
	    for (int i = 0; i < zdepth.length; i++) {
            for (int j = 0; j < zdepth[i].length; j++) {
                zdepth[i][j] = Float.POSITIVE_INFINITY;
            }
        }
	    
	    // update colors in zbuffer
	    Color lightColor = getDirectLight();
	    Color ambientColor = getAmbientLight();
	    Vector3D lightVector = translatedScene.getLight();
	    List<Polygon> polygons = translatedScene.getPolygons();
	    for (Polygon p : polygons) {
	        if (Pipeline.isHidden(p)) {
	            continue;
	        }
	        
	        Color shading = Pipeline.getShading(p, lightVector, lightColor, ambientColor);
	        EdgeList edgeList = Pipeline.computeEdgeList(p);
	        Pipeline.computeZBuffer(zbuffer, zdepth, edgeList, shading);
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

// code for comp261 assignments

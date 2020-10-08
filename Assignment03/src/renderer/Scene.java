package renderer;
import java.util.List;

/**
 * The Scene class is where we store data about a 3D model and light source
 * inside our renderer. 
 * 
 * ** (Polygon class has been moved into a separated class) **
 * It also contains a static inner class that represents one single polygon.
 * 
 * Method stubs have been provided, but you'll need to fill them in.
 * 
 * If you were to implement more fancy rendering, e.g. Phong shading, you'd want
 * to store more information in this class.
 */
public class Scene {

    // the collection of polygons that construct into this 3D model
    private final List<Polygon> polygons;
    // the Vector that represents the direction of the light source
    private final Vector3D lightPos;

    public Scene(List<Polygon> polygons, Vector3D lightPos) {
        this.polygons = polygons;
        this.lightPos = lightPos;
    }

    public Vector3D getLight() {
        return lightPos;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }
    
    /**
     * Get the bounding box of this scene, i.e. the leftmost, rightmost,
     * upmost, downmost, closest, and farthest vertex of it.
     * @return    --- an 6-element array of float containing left, right,
     *                up, down, clost, far boundary
     */
    public float[] getBoundary(){
        float left = Float.POSITIVE_INFINITY;
        float right = Float.NEGATIVE_INFINITY;
        float up = Float.POSITIVE_INFINITY;
        float down = Float.NEGATIVE_INFINITY;
        float close = Float.POSITIVE_INFINITY;
        float far = Float.NEGATIVE_INFINITY;
        
        for (Polygon p : polygons) {
            for (Vector3D vec: p.vertices) {
                float x = vec.x;
                float y = vec.y;
                float z = vec.z;
                
                if (x < left) {
                    left = x;
                }
                if (x > right) {
                    right = x;
                }
                if (y < up) {
                    up = y;
                }
                if (y > down) {
                    down = y;
                }
                if (z < close) {
                    close = z;
                }
                if (z > far) {
                    far = z;
                }
            }
        }
        return new float[] {left, right, up, down, close, far};
    }

}

// code for COMP261 assignments

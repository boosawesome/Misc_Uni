package code;

import java.util.ArrayList;
import java.util.List;

public class Road {
	public int ID;
	public int type;
	public String label;
	public String city;
	public int oneway;
	public int speed;

	List<Segment> segments = new ArrayList<Segment>();

	public Road(int ID, int type, String label, String city, int oneway, int speed){
		this.ID = ID;
		this.type = type;
		this.label = label;
		this.city = city;
		this.oneway = oneway;
		this.speed = speed;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setSegments(Segment segments) {
		this.segments.add(segments);
	}
	
}

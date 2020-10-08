package code;

import java.util.*;

public class ArtPoint {
	private ArtPoint Ancestor;
	private Node node;
	private int c;
	private int reachBack;
	List<Node> children;

	public ArtPoint(Node n, int count, ArtPoint p) {
		this.node = n;
		this.c = count;
		this.Ancestor = p;
		this.reachBack = Integer.MAX_VALUE;
		this.children = null;
	}
	public int getCount(){
		return c;
	}
	
	public int getReachBack(){
		return reachBack;
	}
	
	public Node getNode(){
		return node;
	}
	
	public ArtPoint getParent(){
		return Ancestor;
	}
	public void setReachBack(int i){
		reachBack=i;
	}
}

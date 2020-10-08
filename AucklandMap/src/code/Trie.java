package code;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class Trie {
	private Map<Character, Trie> children;
	private Set<Road> matches, potentialMatches;
	private char c;
	
	public Trie() {
		children = new HashMap<>();
		matches = new HashSet<>();
		potentialMatches = new HashSet<>();
	}

	public Trie(char c) {
		this.c = c;
		children = new HashMap<>();
		matches = new HashSet<>();
		potentialMatches = new HashSet<>();
	}

	public char getChar() {
		return c;
	}

	public void addString(String str, Road road) {
		Trie child;
		str = str.toLowerCase();
		char c = str.toCharArray()[0];
		str = str.substring(1);

		if (children.containsKey(c)) {
			child = children.get(c);
		} else {
			child = new Trie(c);
			children.put(c, child);
		}
		
		child.getPotentialMatches().add(road);
		
		if (!str.isEmpty()) {
			child.addString(str, road);
		} else {
			matches.add(road);
		}
	}

	public Set<Road> contains(String str) {
		str = str.toLowerCase();
		char c = str.toCharArray()[0];
		str = str.substring(1);
		
		if (children.containsKey(c)) {
			Trie child = children.get(c);
			if (!str.isEmpty()) {
				return child.contains(str);
			} else {
				return child.getPotentialMatches();
			}
		}

		return null;
	}
	
	public Set<Road> getPotentialMatches() {
		return potentialMatches;
	}
	
	public Set<Road> getMatches() {
		return matches;
	}
}
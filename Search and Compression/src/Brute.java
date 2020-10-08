
public class Brute {
	private String text;
	private String search;

	public Brute(String pattern, String text) {
		this.text = text;
		this.search = pattern;
	}

	public int search(String pattern, String text) {
		int t = 0;
		int s = 0;

		while (t + s < text.length()) {
			if (search.charAt(s) == text.charAt(s + t)) {
				s++;
				if (s == pattern.length()) {
					return t;
				}
			} 
			else {
				t++;
				s = 0;
			}
		}
		return -1;
	}
}

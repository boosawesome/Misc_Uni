/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {
	private String text;
	private String search;
	private int match[];

	public KMP(String pattern, String text) {
		this.text = text;
		this.search = pattern;

		match = matchTable(pattern);
	}

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public int search(String pattern, String text) {
		int s = 0;
		int t = 0;
		
		while (t + s < text.length() - 1) {
			if (search.charAt(s) == text.charAt(s + t)) {
				s++;
				if (s == pattern.length()) {
					return t;
				}
			} else if (match[s] > -1) {
				t = t + s - match[s];
				s = match[s];
			} else {
				t = t + s + 1;
				s = 0;
			}
		}
		return -1;
	}

	private int[] matchTable(String word) {
		int[] matchTable = new int[word.length() + 1];

		matchTable[0] = -1;
		matchTable[1] = 0;
		int tablePos = 1;
		int charPos = 0;

		while (tablePos < word.length()) {
			if (word.charAt(tablePos) == word.charAt(charPos)) {
				matchTable[tablePos] = matchTable[charPos];
				tablePos++;
				charPos++;
			} else {
				matchTable[tablePos] = charPos;
				charPos = matchTable[charPos];
				while (charPos >= 0 && (word.charAt(tablePos) != word.charAt(charPos))) {
					charPos = matchTable[charPos];
				}
				tablePos++;
				charPos++;
			}
		}
		matchTable[tablePos] = charPos;

		return matchTable;
	}
}

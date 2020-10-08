/**
 * A new instance of LempelZiv is created for every run.
 */
public class LempelZiv {

	/**
	 * Take uncompressed input as a text string, compress it, and return it as a
	 * text string.
	 */
	public String compress(String input) {

		StringBuilder output = new StringBuilder();

		int cursor = 0;
		int windowSize = 50;
		while (cursor < input.length()) {
			int lookahead = 1;
			int prevMatch = -1;
			while (true) {
				String textWindow = input.substring((cursor < windowSize) ? 0 : (cursor - windowSize), cursor);
				int match = textWindow.indexOf(input.substring(cursor, cursor + lookahead));
				if (match > -1 && (cursor + lookahead) < input.length()) {
					prevMatch = match;
					lookahead++;
				} else {
					String extra = "[" + ((prevMatch > -1) ? (textWindow.length() - prevMatch) : 0) + ","
							+ (lookahead - 1) + "," + input.charAt(cursor + lookahead - 1) + "]";
					output.append(extra);
					cursor += lookahead;
					break;
				}
			}
		}

		return output.toString();
	}

	/**
	 * Take compressed input as a text string, decompress it, and return it as a
	 * text string.
	 */
	public String decompress(String compressed) {

		String output = "";

		java.util.Scanner s = new java.util.Scanner(compressed);
		s.useDelimiter(""); // One char at a time.
		while (s.hasNext()) {
			s.next(); // "["
			s.useDelimiter(",");
			int offset = s.nextInt();
			s.useDelimiter("");
			s.next(); // ","
			s.useDelimiter(",");
			int length = s.nextInt();
			s.useDelimiter("");
			s.next(); // ","
			String c = s.next();
			s.next(); // "]"
			String extra = output.substring(output.length() - offset, output.length() - offset + length);
			output += extra + c;
		}

		return output;
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't want to. It is called on every run and its return
	 * value is displayed on-screen. You can use this to print out any relevant
	 * information from your compression.
	 */
	public String getInformation() {
		return "";
	}
}

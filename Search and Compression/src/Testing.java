import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Test;

public class Testing {

	private final String shortTest = "asfnbkdlhanbdlnabhd cjdmkjm k ldj las asd";
	private final String pattern = "asd";
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private String tolstoy;
	private final String tolstoyPattern = "unreal immobility in space and to recognize a motion we did not feel;";

	public Testing() {
		File file = new File("data/war_and_peace.txt");
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			tolstoy = new String(encoded, CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Test

	public void testSearchBruteShort() {
		Brute b = new Brute(pattern, shortTest);
		int num = b.search(pattern, shortTest);

		assertEquals(38, num);
	}

	@Test

	public void testSearchKMPShort() {
		KMP kmp = new KMP(pattern, shortTest);
		int num = kmp.search(pattern, shortTest);
		assertEquals(38, num);
	}

	@Test

	public void testSearchBruteLong() {
		Brute b = new Brute(tolstoyPattern, tolstoy);
		b.search(tolstoyPattern, tolstoy);
	}

	@Test

	public void testSearchKMPLong() {
		KMP kmp = new KMP(tolstoyPattern, tolstoy);
		kmp.search(tolstoyPattern, tolstoy);

	}
	
	@Test
	
	public void testHuffmanShort(){
		HuffmanCoding huff = new HuffmanCoding(shortTest);
		String encoded = huff.encode(shortTest);
		String decoded = huff.decode(encoded);
		
		assertEquals(shortTest, decoded);
	}
	
	@Test
	
	public void testHuffmanLong(){
		HuffmanCoding huff = new HuffmanCoding(tolstoy);
		String encoded = huff.encode(tolstoy);
		String decoded = huff.decode(encoded);
		
		assertEquals(tolstoy, decoded);
	}
	
	@Test
	
	public void testLempelZivShort(){
		
	}
	
	@Test
	
	public void testLempelZivLong(){
		
	}
}

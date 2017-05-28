import Huffman.*;

public class Test {
	public static void main(String[] args) throws InvalidFormatException {
		String compressedStr = Huffman.compress("this is a test string!!!");
		String decompressedStr = Huffman.decompress(compressedStr);

		System.out.println(compressedStr);
		System.out.println(decompressedStr);
	}
}

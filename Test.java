import Huffman.*;

public class Test {
	public static void main(String[] args) {
		String compressedStr = Huffman.compress(args[0]);
		System.out.println(compressedStr);

		String decompressedStr;
		try {
			decompressedStr = Huffman.decompress(compressedStr);
			System.out.println(decompressedStr);
		}
		catch (InvalidFormatException e) {
			System.out.println(e.getMessage());
		}
	}
}

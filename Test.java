import Huffman.*;

public class Test {
	public static void main(String[] args) {
		// to compress a string
		String compressedStr = Huffman.compress(args[0]);
		System.out.println("str after compressed : " + compressedStr);

		// to decompress a compressed string
		String decompressedStr;
		try {
			decompressedStr = Huffman.decompress(compressedStr);
			System.out.println("after decompressed   : " + decompressedStr);
		}
		catch (InvalidFormatException e) {
			System.out.println(e.getMessage());
		}

		// check size
		System.out.println("");
		System.out.println("original str length  : " + args[0].length());
		System.out.println("after compressed     : " + compressedStr.length());
	}
}

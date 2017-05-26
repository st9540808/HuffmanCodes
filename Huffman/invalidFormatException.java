package Huffman;

public class invalidFormatException extends Exception {
	public invalidFormatException() {
		super("the str to be decompressed has invalid format");
	}

	public invalidFormatException(String msg) {
		super(msg);
	}
}

package Huffman;

public class InvalidFormatException extends Exception {
	public InvalidFormatException() {
		super("the str to be decompressed has invalid format");
	}

	public InvalidFormatException(String msg) {
		super(msg);
	}
}

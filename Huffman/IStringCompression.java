package Huffman;
import Huffman.invalidFormatException;

public interface IStringCompression {
	public String compress(String str);
	public String decompress(String str) throws invalidFormatException;
}

# Huffman Codes

## Description
This project implement the Huffman codes compression algorithm in java.

## Usage
the ```compress``` and ```decompress``` method is defined as follow:
'''java
public class Huffman {
	public static String compress(String str)
	{...}

	public static String decompress(String str) throws InvalidFormatException
	{...}
}
'''
<br>

To use the method, input any string expect char '\u0000'(this serves a special
meaning in the compressed string). Follow is an example:
'''java
String compressedStr = Huffman.compress("any string to be compressed");
try {
	decompressedStr = Huffman.decompress(compressedStr);	
} 
catch (InvalidFormatException e)
{...}
'''
Remember to use a try catch clause with the ```decompress``` method

import Huffman.*;
import java.nio.file.*;
import java.io.*;

public class TestFile {
	public static void main(String[] args) {
		compress(args[0]);

//		/*		
		String content = null;
		try {
			content = new String(Files.readAllBytes(Paths.get(args[0])));
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("invalid file path");
			System.exit(1);
		}
//		StringTraverse.fieldTraverse(content);
		StringTraverse.charAtTraverse(content);
//		*/
	}

	public static void compress(String filepath) {
		String content = null;
		try {
			content = new String(Files.readAllBytes(Paths.get(filepath)));
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("invalid file path");
			System.exit(1);
		}

		PrintWriter outputFile = null;
		try {
			outputFile = new PrintWriter(
			            new FileOutputStream(
			            filepath.substring(0, filepath.lastIndexOf('.')) + ".hmc"));
		}
		catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

		outputFile.print(Huffman.compress(content));
		outputFile.close();
	}

	public static void decompress(String filepath) {
		String content = null;
		try {
			content = new String(Files.readAllBytes(
			              Paths.get(filepath.substring(0,
			                        filepath.lastIndexOf('.')) + ".hmc")));
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("invalid file path");
			System.exit(1);
		}
	
		try {
			Huffman.decompress(content);
		}
		catch (InvalidFormatException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	public static void compressAndDecompress(String content) {
		String compressedContent = Huffman.compress(content);
		String decompressedContent = null;
		try {
			decompressedContent = Huffman.decompress(compressedContent);
		}
		catch (InvalidFormatException ex) {
			System.out.println(ex.getMessage());
			System.out.println("there is something wrong");
			System.exit(1);
		}
		System.out.println(decompressedContent.equals(content));
	}
}

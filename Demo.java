import Huffman.*;
import java.nio.file.*;
import java.io.*;

public class Demo {
	public static void main(String[] args) {
		String content = null;
		try {
			content = new String(Files.readAllBytes(Paths.get(args[0])));
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("invalid file path");
			System.exit(1);
		}
		verify(content, args[0]);
	}

	public static void verify(String content, String filepath) {
		String compressedContent = Huffman.compress(content);
		String decompressedContent = null;
		try {
			decompressedContent = Huffman.decompress(compressedContent);
		} catch (InvalidFormatException ex) {
			System.out.println(ex.getMessage());
			System.out.println("there is something wrong");
			System.exit(1);
		}
		
		// here verify original content and decompressed Content
		System.out.println(decompressedContent.equals(content));

		PrintWriter outputFile = null;
		try {
			outputFile = new PrintWriter(
			             new FileOutputStream(
			             filepath.substring(0, filepath.lastIndexOf('.')) + ".hmc"));
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

		outputFile.print(Huffman.compress(content));
		outputFile.close();
	}
}

import Huffman.*;
import java.nio.file.*;
import java.io.*;

public class Verify {
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
		
		TestFile.compressAndDecompress(content);
	}
}

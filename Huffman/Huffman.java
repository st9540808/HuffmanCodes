package Huffman;
import Huffman.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.AbstractMap;
import java.util.BitSet;
import java.util.Arrays;
import java.lang.reflect.*;
import java.lang.StringBuilder;

public class Huffman {
	private static final int hashMapSize = 100;
		
	public static String compress(String str) {
		final HashMap<Character, Integer> freq = countFreq(str);
		final HashMap<Character, String> code = generateCode(freq);
		final HashMap<Character, boolean[]> codeboolArray
			= convertValueStringTobooleanArray(code);

		final int totalBits = countTotalBits(str, freq, code);
		return generateDecodeString(totalBits, code) + "\u0000\u0000"
			 + generateCompressedString(str, codeboolArray, totalBits);
	}

	public static String decompress(String str) throws InvalidFormatException {
		final int totalBitsIndex = str.indexOf('\u0000');
		if (totalBitsIndex == -1) {
			throw new InvalidFormatException();
		}

		final int totalBits;
		try {
			totalBits = Integer.parseInt(str.substring(0, totalBitsIndex));
		} catch (NumberFormatException e) {
			throw new InvalidFormatException();
		}
		
		final int compressedStringSeperatorIndex = str.indexOf("\u0000\u0000");
		if (compressedStringSeperatorIndex == -1) {
			throw new InvalidFormatException();
		}
		final HashMap<String, Character> decompressedCode
			= getDecompressedCode(str.substring(totalBitsIndex + 1, compressedStringSeperatorIndex));
		
		// decompress section
		final String compressedString = str.substring(compressedStringSeperatorIndex + 2);
		byte[] decompressedCodeArray = new byte[compressedString.length() * 2];
		for (int i = 0, arrayIndex = 0; i < compressedString.length(); ++i) {
			char compressedChar = compressedString.charAt(i);
			decompressedCodeArray[arrayIndex++] = (byte)(compressedChar >>> 8);
			decompressedCodeArray[arrayIndex++] = (byte)(compressedChar & 0xff);
		}

		// reconstructing BitSet
		final BitSet decompressedBitSet = new BitSet();
		for (int i = 0; i < decompressedCodeArray.length * 8; ++i) {
			if ((decompressedCodeArray[i / 8] & (1 << (i % 8))) > 0) {
				decompressedBitSet.set(i);
			}
		}

		return reconstructString(decompressedCode, decompressedBitSet, totalBits);
	}

	///////////////////////////////////////////////////////////////////////////////////
	// for compress
	///////////////////////////////////////////////////////////////////////////////////
	private static HashMap<Character, Integer> countFreq(String str) {
		HashMap<Character, Integer> freq = new HashMap<>(hashMapSize);

		if (str.length() < 512) {
			final int length = str.length();
			for (int i = 0; i < length; ++i) {
				int count = freq.getOrDefault(str.charAt(i), 0);
				freq.put(str.charAt(i), count + 1);
			}
		} else { // str.length() >= 512
			Field field = null;
			try {
				field = String.class.getDeclaredField("value");
			} catch(NoSuchFieldException ex) {
				System.exit(1);
			}
			field.setAccessible(true);
	
			try {
				final char[] chars = (char[]) field.get(str);
				final int length = chars.length;
				for (int i = 0; i < length; ++i) {
					int count = freq.getOrDefault(chars[i], 0);
					freq.put(chars[i], count + 1);
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				System.out.println("error occurs in countFreq()");
				System.exit(1);
			}
		} // end case of str.length() >= 512

		return freq;
	}

	private static HashMap<Character, String> generateCode(HashMap<Character, Integer> freq) {
		// create min-heap
		PriorityQueue<MinHeapNode> queue =
			new PriorityQueue<>(Comparator.comparing((MinHeapNode node) -> node.entry.getValue()));

		for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
			MinHeapNode newNode = new MinHeapNode();
			newNode.setEntry(new AbstractMap.SimpleEntry<Character, Integer>(entry));
			queue.add(newNode);
		}
		if (queue.size() == 1) { // for special case of a single kind of character
			HashMap<Character, String> code = new HashMap<>(1);
			code.put(queue.poll().entry.getKey(), "0");
			return code;
		}

		// constructing Huffman codes
		final int queueSize = queue.size() - 1;
		for (int i = 0; i < queueSize; ++i) {
			MinHeapNode x = queue.poll();
			MinHeapNode y = queue.poll();

			MinHeapNode newNode = new MinHeapNode();
			newNode.left = x;
			newNode.right = y;
			newNode.setEntry(new AbstractMap.SimpleEntry
			                 <Character, Integer>('\u0000', x.entry.getValue() + y.entry.getValue()));
			queue.add(newNode);
		}
		
		// storing final result
		HashMap<Character, String> code = new HashMap<>(hashMapSize);
		printCode(queue.poll(), "", code);
		return code;
	}

	private static void printCode(MinHeapNode root, String str, HashMap<Character, String> code) {
		if (root.entry.getKey() != '\u0000') {
			code.put(root.entry.getKey(), str);
			return;
		}

		printCode(root.left,  str + "0", code);
		printCode(root.right, str + "1", code);
	}

	private static HashMap<Character, boolean[]> convertValueStringTobooleanArray
				  (HashMap<Character, String> code) {
		HashMap<Character, boolean[]> codeboolArray = new HashMap<>(hashMapSize);
		
		for (Map.Entry<Character, String> entry : code.entrySet()) {
			String entryString = entry.getValue();
			boolean[] boolArray = new boolean[entryString.length()];
			for (int i = 0; i < boolArray.length; ++i) {
				if (entryString.charAt(i) == '1') {
					boolArray[i] = true;
				}
			}
			codeboolArray.put(entry.getKey(), boolArray);
		}
		return codeboolArray;
	}

	private static String generateCompressedString
		(String str, HashMap<Character, boolean[]> code, int totalBits) {
		// create bitset
		BitSet codeBitSet = new BitSet();

		Field field = null;
		try {
			field = String.class.getDeclaredField("value");
		} catch(NoSuchFieldException ex) {
			System.exit(1);
		}
		field.setAccessible(true);

		try {
			final char[] chars = (char[]) field.get(str);
			final int length = chars.length;
			/* code here */
			for (int i = 0, bitIndex = 0; i < length; ++i) {
				final boolean[] entryboolArray = code.get(chars[i]);

				for (int j = 0; j < entryboolArray.length; ++j, ++bitIndex) {
					if (entryboolArray[j] == true) {
						codeBitSet.set(bitIndex);
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("error occurs in countFreq()");
			System.exit(1);
		}

		byte[] codeByteArray = codeBitSet.toByteArray();
		StringBuilder compressedString = new StringBuilder(totalBits / 16 + 1);
		for (int i = 0, arrayIndex = 0; i < codeByteArray.length / 2; ++i) {
			int upperByte = (codeByteArray[arrayIndex++] << 8) & 0x0000ff00;
			int lowerByte = codeByteArray[arrayIndex++] & 0x000000ff;
			char compressedChar = (char)(upperByte | lowerByte);
			compressedString.append(compressedChar);
		}
		if (codeByteArray.length % 2 == 1) {
			char upperByte = (char)codeByteArray[codeByteArray.length - 1];
			char compressedChar = (char)(upperByte << 8);
			compressedString.append(compressedChar);
		}

		return compressedString.toString();
	}

	private static int countTotalBits(String str,
			HashMap<Character, Integer> freq, HashMap<Character, String> code) {
		// calculate how many bits to use
		int totalBits = 0;
		for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
			totalBits += entry.getValue() * code.get(entry.getKey()).length();
		}
		return totalBits;
	}

	private static String generateDecodeString(int totalBits, HashMap<Character, String> code) {
		String decodeString = "" + totalBits;
		for (Map.Entry<Character, String> entry : code.entrySet()) {
			decodeString = decodeString + "\u0000" + entry.getKey() + entry.getValue();
		}
		return decodeString;
	}

	///////////////////////////////////////////////////////////////////////////////////
	// for decompress
	///////////////////////////////////////////////////////////////////////////////////
	private static HashMap<String, Character> getDecompressedCode(String str) {
		HashMap<String, Character> decompressedCode = new HashMap<>(hashMapSize);
		
		String[] parts = str.split("\u0000");
		for (String part : parts) {
			decompressedCode.put(part.substring(1), part.charAt(0));
		}

		return decompressedCode;
	}

	private static String reconstructString(HashMap<String, Character> decompressedCode,
		 BitSet decompressedBitSet, int totalBits) {
		// get max
		int maxStringLength = 0;
		for (Map.Entry<String, Character> entry : decompressedCode.entrySet()) {
			if (entry.getKey().length() > maxStringLength) { 
				maxStringLength = entry.getKey().length();
			}
		}
		
		// reconstructing string
		StringBuilder decompressedStr = new StringBuilder();
		StringBuilder matchStr = new StringBuilder(maxStringLength);
		
		for (int i = 0; i < totalBits; ++i) {
			if (decompressedBitSet.get(i) == true) {
				matchStr.append('1');
			} else {
				matchStr.append('0');
			}

			if (decompressedCode.containsKey(matchStr.toString())) {
				char matchChar = decompressedCode.get(matchStr.toString());
				decompressedStr.append(matchChar);
				matchStr.setLength(0);
			}
		}

		return decompressedStr.toString();
	}
	
	private static class MinHeapNode {
		private Map.Entry<Character, Integer> entry; 
		private MinHeapNode left;
		private MinHeapNode right;

		public MinHeapNode() {
			entry = null;
			right = left = null;
		}

		public void setEntry(Map.Entry<Character, Integer> newEntry) {
			entry = newEntry;
		}
	}
}

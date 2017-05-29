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

public class Huffman {
	private static final int hashMapSize = 20;
		
	public static String compress(String str) {
		final HashMap<Character, Integer> freq = countFreq(str);
		final HashMap<Character, String> code = generateCode(freq);
		final int totalBits = countTotalBits(str, freq, code);
		return generateDecodeString(totalBits, code) + "\u0000\u0000"
			 + generateCompressedString(str, code);
	}

	public static String decompress(String str) throws InvalidFormatException {
		final int totalBitsIndex = str.indexOf('\u0000');
		if (totalBitsIndex == -1) {
			throw new InvalidFormatException();
		}

		final int totalBits;
		try {
			totalBits = Integer.parseInt(str.substring(0, totalBitsIndex));
		}
		catch (NumberFormatException e) {
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

	private static HashMap<Character, Integer> countFreq(String str) {
		HashMap<Character, Integer> freq = new HashMap<>(hashMapSize);

		for (int i = 0; i < str.length(); ++i) {
			Integer count = freq.get(str.charAt(i));
			if (count == null) {
				freq.put(str.charAt(i), 1);
			}
			else {
				freq.put(str.charAt(i), count + 1);
			}
		}

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

		// constructing Huffman codes
		HashMap<Character, String> code = new HashMap<>(hashMapSize);
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

	private static String generateCompressedString(String str, HashMap<Character, String> code) {
		// create bitset
		BitSet codeBitSet = new BitSet();
		for (int i = 0, bitIndex = 0; i < str.length(); ++i) {
			String codeString = code.get(str.charAt(i));
			for (int j = 0; j < codeString.length(); ++j) {
				if (codeString.charAt(j) == '1') {
					codeBitSet.set(bitIndex);
				}
				bitIndex++;
			}
		}
		
		byte[] codeByteArray = codeBitSet.toByteArray();
		String compressedString = "";
		for (int i = 0, arrayIndex = 0; i < codeByteArray.length / 2; ++i) {
			int upperByte = (codeByteArray[arrayIndex++] << 8) & 0x0000ff00;
			int lowerByte = codeByteArray[arrayIndex++] & 0x000000ff;
			char compressedChar = (char)(upperByte | lowerByte);
			compressedString = compressedString + compressedChar;
		}
		if (codeByteArray.length % 2 == 1) {
			char upperByte = (char)codeByteArray[codeByteArray.length - 1];
			char compressedChar = (char)(upperByte << 8);
			compressedString = compressedString + compressedChar;
		}

		return compressedString;
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
		// reconstructing string
		String decompressedStr = "";
		String matchStr = "";
		for (int i = 0; i < totalBits; ++i) {
			if (decompressedBitSet.get(i) == true) {
				matchStr = matchStr + "1";
			}
			else {
				matchStr = matchStr + "0";
			}

			if (decompressedCode.containsKey(matchStr)) {
				char matchChar = decompressedCode.get(matchStr);
				decompressedStr = decompressedStr + matchChar;
				matchStr = "";
			}
		}

		return decompressedStr;
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

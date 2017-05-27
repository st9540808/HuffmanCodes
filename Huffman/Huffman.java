package Huffman;
import Huffman.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.AbstractMap;
import java.util.BitSet;

public class Huffman {
	public static void main(String[] args) {
		String compressedString = compress("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbccccccccccccddddddddddddddddfffffeeeeeeeee");
		System.out.println(compressedString);
	}

	public static String compress(String str) {
		HashMap<Character, Integer> freq = countFreq(str);
		HashMap<Character, String> code = generateCode(freq);
		
		/* test Huffman codes */
		for (Map.Entry entry : code.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		return generateCompressedString(str, freq, code);
	}

	public static String decompress(String str) throws invalidFormatException {

		return "str";
	}

	private static HashMap<Character, Integer> countFreq(String str) {
		HashMap<Character, Integer> freq = new HashMap<>(20);

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
		HashMap<Character, String> code = new HashMap<>(20);
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

	private static String generateCompressedString(String str,
			HashMap<Character, Integer> freq, HashMap<Character, String> code) {
		// calculate how many bits to use
		int totalBits = 0;
		for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
			totalBits += entry.getValue() * code.get(entry.getKey()).length();
		}

		BitSet codeBitSet = new BitSet(totalBits);
		for (int i = 0, bitIndex = 0; i < str.length(); ++i) {
			String codeString = code.get(str.charAt(i));
			for (int j = 0; j < codeString.length(); ++j) {
				if (codeString.charAt(j) == '1') {
					codeBitSet.set(bitIndex, true);
				}
				else {
					codeBitSet.set(bitIndex, false);
				}
				bitIndex++;
			}
		}

		byte[] codeByteArray = codeBitSet.toByteArray();
		System.out.println(codeByteArray.length);
		String compressedString = "";
		for (int i = 0, arrayIndex = 0; i < codeByteArray.length / 2; ++i) {
			char upperByte = (char)codeByteArray[arrayIndex++];
			char lowerByte = (char)codeByteArray[arrayIndex++];
			char compressedChar = (char)((char)(upperByte << 8) | lowerByte);
			compressedString = compressedString + compressedChar;
		}
		if (codeByteArray.length % 2 == 1) {
			char upperByte = (char)codeByteArray[codeByteArray.length - 1];
			char compressedChar = (char)(upperByte << 8);
			compressedString = compressedString + compressedChar;
		}

		return compressedString;
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

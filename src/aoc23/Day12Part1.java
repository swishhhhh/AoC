package aoc23;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/12">Advent of Code 2023 Day 12</a>
 */
public class Day12Part1 {

	static class TreeNode {
		String value;
		String fullPath;
		int depth; //depth in tree
		List<TreeNode> children = new ArrayList<>();

		public TreeNode(String value, String fullPath, int depth) {
			this.value = value;
			this.fullPath = fullPath;
			this.depth = depth;
		}

		@Override
		public String toString() {
			return "TreeNode{" +
					"value='" + value + '\'' +
					", fullPath='" + fullPath + '\'' +
					", depth=" + depth +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {

		List<String> lines = ResourceLoader.readStrings("aoc23/Day12_input.txt");

		long sum = 0;
		for (String line: lines) {
			sum+= getNumArrangementsForLine(line);
		}

		System.out.printf("Sum = %s%n", sum);

		long expected = 7541;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static long getNumArrangementsForLine(String line) {
		String[] ary = line.split(" ");
		String pattern = ary[0];
		String expectedSignature = ary[1];

		Map<Integer, List<TreeNode>> levelsToNodesMap = new HashMap<>();
		TreeNode root = new TreeNode("root", "", 0);
		levelsToNodesMap.put(0, List.of(root));

		char[] tokens = pattern.toCharArray();
		for (int i = 0; i < tokens.length; i++) {
			List<TreeNode> parentNodes = levelsToNodesMap.get(i);

			int level = i + 1;
			boolean lastLevel = level == tokens.length;
			List<TreeNode> newNodesThisLevel = new ArrayList<>();
			levelsToNodesMap.put(level, newNodesThisLevel);

			char token = tokens[i];

			if (token == '#' || token == '?') {
				for (TreeNode parent : parentNodes) {
					String candidatePath = parent.fullPath + "#";
					if (canAddToTree(candidatePath, expectedSignature, lastLevel)) {
						TreeNode node = new TreeNode("#", candidatePath, level);
						parent.children.add(node);
						newNodesThisLevel.add(node);
					}
				}
			}

			if (token == '.' || token == '?') {
				for (TreeNode parent : parentNodes) {
					String candidatePath = parent.fullPath + ".";
					if (canAddToTree(candidatePath, expectedSignature, lastLevel)) {
						TreeNode node = new TreeNode(".", candidatePath, level);
						parent.children.add(node);
						newNodesThisLevel.add(node);
					}
				}
			}
		}

		List<TreeNode> lowestNodes = levelsToNodesMap.get(tokens.length);
		long num = lowestNodes == null ? 0 : lowestNodes.size();
//		System.out.printf("%s = %s%n", line, num);
		return num;

	}

	private static boolean canAddToTree(String input, String expectedSignature, boolean mustFullyMatch) {
		String inputSignature = getSignature(input);

		if (mustFullyMatch) {
			return inputSignature.equals(expectedSignature);
		}

		//if !mustFullyMatch then split inputSignature (e.g. 1,1,3) into 2 parts:
		// 	part 1 (e.g. "1,1,") = everything up until and including the last comma (if no comma, then empty string)
		// 	part 2 (e.g. "3") = everything after the last comma (if no comma, then full inputSignature)
		int lastCommaIdx = inputSignature.lastIndexOf(',');
		String sigPart1 = lastCommaIdx == -1 ? "" : inputSignature.substring(0, lastCommaIdx + 1);

		//sigPart1 has to be complete prefix match
		if (!expectedSignature.startsWith(sigPart1)) {
			return false;
		}

		//sigPart2 has to be less-than-or-equal-to the corresponding part/token in the expected signature
		// (e.g. for inputSignature 1,1,5, sigPart = 5 (3rd token), if expectedSignature is 1,1,7,5 - we have a match
		// since 5 is <= 7 (3rd token in expectedSignature)
		String[] expectedSigTokens = expectedSignature.split(",");
		String[] inputSigTokens = inputSignature.split(",");
		if (inputSigTokens.length > expectedSigTokens.length) {
			return false;
		}

		int tokensToCompareIdx = inputSigTokens.length - 1; //last token in input
		int expectedTokenToCompareTo = Integer.parseInt(expectedSigTokens[tokensToCompareIdx]);
		int lastInputToken =
				inputSigTokens[tokensToCompareIdx].isBlank() ? 0 : Integer.parseInt(inputSigTokens[tokensToCompareIdx]);
		return lastInputToken <= expectedTokenToCompareTo;
	}

	private static String getSignature(String input) {
		//e.g. "##..###.#" -> 2,3,1
		int groupLen = 0;
		List<Integer> groupLengths = new ArrayList<>();
		for (char c : input.toCharArray()) {
			if (c == '.') {
				if (groupLen > 0) {
					groupLengths.add(groupLen);
				}
				groupLen = 0;
			} else if (c == '#') {
				groupLen++;
			} else {
				throw new RuntimeException("Invalid char " + c);
			}
		}
		if (groupLen > 0) { //add last group
			groupLengths.add(groupLen);
		}

		return groupLengths.toString().replace("[", "").replace("]", "").replace(" ", "");
	}
}

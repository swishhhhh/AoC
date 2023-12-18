package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/12">Advent of Code 2023 Day 12</a>
 */
public class Day12Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day12_input.txt");

		long sum = 0;
		for (String line: lines) {
			line = unfoldLine(line);
			sum+= getNumArrangementsForLine(line);
		}

		System.out.printf("Sum = %s%n", sum);

		long expected = 17485169859432L;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static String unfoldLine(String line) {
		String[] ary = line.split(" ");
		String pattern = ary[0];
		String unfoldedPattern = (pattern + "?").repeat(5);
		unfoldedPattern = unfoldedPattern.substring(0, unfoldedPattern.length() - 1);

		String expectedSignature = ary[1];
		String unfoldedSig = (expectedSignature + ",").repeat(5);
		unfoldedSig = unfoldedSig.substring(0, unfoldedSig.length() - 1);

		return unfoldedPattern + " " + unfoldedSig;
	}

	private static long getNumArrangementsForLine(String line) {
		String[] ary = line.split(" ");
		String pattern = ary[0];
		char[] tokens = pattern.toCharArray();
		String expectedSignature = ary[1];
		Map<String, Long> cache = new HashMap<>();

		long n = dfs(tokens, "", 0, cache, expectedSignature);
//		System.out.printf("%s = %s%n", line, n);
		return n;
	}

	private static long dfs(char[] inputPatternTokens, String pathSoFar, int idx, Map<String, Long> cache,
							String expectedSignature) {
		//cache lookup using key comprised of: {idx}|{count(#)}|{pathSoFar.endsWith(".") ? 0 : sig.get(lastElement)}
		List<Integer> sig = getSignatureAsList(pathSoFar);
		String key = null;
		if (sig.size() > 0) {
			key =
				idx
				+ "|" + Helper.countChars(pathSoFar, '#')
				+ "|" + (pathSoFar.endsWith(".") ? "0" : sig.get(sig.size() - 1));

			if (cache.containsKey(key)) {
				long value = cache.get(key);
//				System.out.printf("Arrangement (from cache) using key=value (%s=%s): %s%n", key, value, pathSoFar);
				return value;
			}
		}

		boolean isLastToken = idx == inputPatternTokens.length;
		if (isCandidateSignatureValid(pathSoFar, expectedSignature, isLastToken)) {
			if (idx == inputPatternTokens.length) { //end of the line
//				System.out.printf("Arrangement: %s%n", pathSoFar);
				return 1;
			}
		} else {
			return 0;
		}

		char token = inputPatternTokens[idx];
		long sum = 0;
		for (char c : List.of('#', '.')) {
			if (token != '?' && token != c) {
				continue;
			}

			String candidatePath = pathSoFar + c;
			sum+= dfs(inputPatternTokens, candidatePath, idx + 1, cache, expectedSignature);
		}

		//add to cache
		if (key != null) {
//			System.out.printf("Cache put: %s: %s=%s%n", pathSoFar, key, sum);
			cache.put(key, sum);
		}

		return sum;
	}

	private static boolean isCandidateSignatureValid(String candidatePath, String expectedSignature,
													 boolean isLastToken) {
		String inputSignature = getSignature(candidatePath);

		if (isLastToken) { //signature must fully match
			return inputSignature.equals(expectedSignature);
		}

		if (inputSignature.isBlank()) {
			return true;
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
		int lastInputToken = Integer.parseInt(inputSigTokens[tokensToCompareIdx]);
		return candidatePath.endsWith(".")
				? lastInputToken == expectedTokenToCompareTo  //last block of #s complete, number must match
				: lastInputToken <= expectedTokenToCompareTo; //last block might still be in progress...
	}

	private static String getSignature(String input) {
		//e.g. "##..###.#" -> 2,3,1
		return getSignatureAsList(input)
				.toString()
				.replace("[", "")
				.replace("]", "")
				.replace(" ", "");
	}

	private static List<Integer> getSignatureAsList(String input) {
		//e.g. "##..###.#" -> List.of(2, 3, 1)
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

		return groupLengths;
	}
}

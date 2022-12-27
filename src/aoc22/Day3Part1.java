package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2022/day/3">Advent of Code 2022 Day 3</a>
 */
public class Day3Part1 {
	public static final int VALUE_OF_LOWERCASE_A = 1;
	public static final int VALUE_OF_UPPERCASE_A = 27;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day3_input.txt");

		int total = 0;

		for (String line: lines) {
			int len = line.length();
			String firstHalf = line.substring(0, len / 2);

			char[] lineChars = firstHalf.toCharArray();
			Set<Character> charSet = new HashSet<>();
			for (char c: lineChars) {
				charSet.add(c);
			}

			//2nd half
			char foundChar = '0';
			for (int i = len / 2; i < len; i++) {
				char c = line.charAt(i);
				if (charSet.contains(c)) {
					foundChar = c;
					break;
				}
			}

			if (Character.isUpperCase(foundChar)) {
				total+= (foundChar - ('A' - VALUE_OF_UPPERCASE_A));
			} else {
				total+= (foundChar - ('a' - VALUE_OF_LOWERCASE_A));
			}
		}

		System.out.printf("Total = %s%n", total);
	}
}

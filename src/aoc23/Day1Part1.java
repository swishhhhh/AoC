package aoc23;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/1">Advent of Code 2023 Day 1</a>
 */
public class Day1Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day1_input.txt");

		long sum = 0;
		for (String line: lines) {
			StringBuilder sb = new StringBuilder(getFirstDigitInLine(line));
			sb.append(getLastDigitInLine(line));
			sum+= Long.parseLong(sb.toString());
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static String getFirstDigitInLine(String line) {
		char[] ary = line.toCharArray();
		for (char c : ary) {
			if (c >= '0' && c <= '9') {
				return String.valueOf(c);
			}
		}
		return "";
	}

	private static String getLastDigitInLine(String line) {
		char[] ary = line.toCharArray();
		for (int i = ary.length - 1; i >= 0; i--) {
			if (ary[i] >= '0' && ary[i] <= '9') {
				return String.valueOf(ary[i]);
			}
		}
		return "";
	}
}

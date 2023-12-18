package aoc23;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/15">Advent of Code 2023 Day 15</a>
 */
public class Day15Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day15_input.txt");

		long sum = 0;

		String[] tokens = lines.get(0).split(",");
		for (String token : tokens) {
			sum+= hash(token);
		}

		System.out.printf("Sum = %s%n", sum);

		long expected = 506891;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static long hash(String token) {
		long value = 0;

		for (char c : token.toCharArray()) {
			value+= c;
			value*= 17;
			value%= 256;
		}

		return value;
	}
}

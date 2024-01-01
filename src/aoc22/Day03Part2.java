package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.Helper.unboxCharArray;

/**
 * <a href="https://adventofcode.com/2022/day/3">Advent of Code 2022 Day 3</a>
 */
public class Day03Part2 {

	public static final int VALUE_OF_LOWERCASE_A = 1;
	public static final int VALUE_OF_UPPERCASE_A = 27;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day3_input.txt");

		int total = 0;

		for (int i = 0; i < lines.size(); i += 3) {
			Set<Character> charSet =
				new HashSet<>(List.of(unboxCharArray(lines.get(i).toCharArray())));   //1st line
			charSet.retainAll(List.of(unboxCharArray(lines.get(i+1).toCharArray()))); //2nd line
			charSet.retainAll(List.of(unboxCharArray(lines.get(i+2).toCharArray()))); //3rd line

			Character foundChar = (Character) charSet.toArray()[0];
			if (Character.isUpperCase(foundChar)) {
				total += (foundChar - ('A' - VALUE_OF_UPPERCASE_A));
			} else {
				total += (foundChar - ('a' - VALUE_OF_LOWERCASE_A));
			}
		}

		long answer = total;
		System.out.printf("Total = %s%n", total);

		long expected = 2760;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

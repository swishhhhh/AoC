package aoc21;

import utils.ResourceLoader;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/1">Advent of Code 2021 Day 1</a>
 */
public class Day01Part1 {

	public static void main(String[] args) throws Exception {
		List<Integer> numbers = ResourceLoader.readInts("aoc21/Day1_input.txt");

		int numberOfIncreases = 0;
		boolean first = true;
		int prevNumber = -1;
		for (Integer number: numbers) {
			if (first) {
				prevNumber = number;
				first = false;
				continue;
			}

			if (number > prevNumber) {
				numberOfIncreases++;
			}
			prevNumber = number;
		}

		long answer = numberOfIncreases;
		System.out.println("Number of increases = " + numberOfIncreases);

		long expected = 1482;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

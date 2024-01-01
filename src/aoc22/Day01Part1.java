package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/1">Advent of Code 2022 Day 1</a>
 */
public class Day01Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day1_input.txt");

		int currentSum = 0;
		int highestSum = 0;
		int elfNumber = 1;
		int highestElf = 0;

		for (String line: lines) {
			if (line.trim().length() == 0) {
				currentSum = 0;
				elfNumber++;
				continue;
			}

			int value = Integer.parseInt(line);
			currentSum+= value;
			if (currentSum >= highestSum) {
				highestSum = currentSum;
				highestElf = elfNumber;
			}
		}

		long answer = highestSum;
		System.out.printf("Total elves %s, Highest Elf number %s, highestSum = %s%n", elfNumber, highestElf, highestSum);

		long expected = 75501;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

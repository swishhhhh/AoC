package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/1">Advent of Code 2022 Day 1</a>
 */
public class Day01Part2 {

	private static int highestSum1 = 0, highestSum2 = 0, highestSum3 = 0;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day1_input.txt");

		int currentSum = 0;

		for (String line: lines) {
			if (line.trim().length() == 0) {
				checkForTop3Sum(currentSum);

				currentSum = 0; //reset for next elf
				continue;
			}

			int value = Integer.parseInt(line);
			currentSum+= value;
		}

		checkForTop3Sum(currentSum); //for last elf (since there's no trailing new-line in the file

		long answer = highestSum1 + highestSum2 + highestSum3;
		System.out.printf("Total of top 3 = %s%n", answer);

		long expected = 215594;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static void checkForTop3Sum(int currentSum) {
		if (currentSum > highestSum1) {
			highestSum3 = highestSum2;
			highestSum2 = highestSum1;
			highestSum1 = currentSum;
		} else if (currentSum > highestSum2) {
			highestSum3 = highestSum2;
			highestSum2 = currentSum;
		} else if (currentSum > highestSum3) {
			highestSum3 = currentSum;
		}
	}
}

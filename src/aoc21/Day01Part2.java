package aoc21;

import static utils.ResourceLoader.*;

/**
 * <a href="https://adventofcode.com/2021/day/1">Advent of Code 2021 Day 1</a>
 */
public class Day01Part2 {
	public static void main(String... args) throws Exception {
		Integer[] numbers = readInts("aoc21/Day1_input.txt").toArray(new Integer[]{});

		int numberOfIncreases = 0;
		int prevSum = getSumOfLast3Numbers(numbers, 2);

		for (int i = 3; i < numbers.length; i++) {
			int sum = getSumOfLast3Numbers(numbers, i);
			if (sum > prevSum) {
				numberOfIncreases++;
			}
			prevSum = sum;
		}

		long answer = numberOfIncreases;
		System.out.println("Number of increases = " + numberOfIncreases);

		long expected = 1518;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	static int getSumOfLast3Numbers(Integer[] numbers, int lookBackFromNum) {
		int sum = 0;
		for (int i = lookBackFromNum - 2; i <= lookBackFromNum; i++) {
			sum+= numbers[i];
		}
		return sum;
	}
}

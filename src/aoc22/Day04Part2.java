package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/4">Advent of Code 2022 Day 4</a>
 */
public class Day04Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day4_input.txt");

		int ctr = 0;
		int min1, max1, min2, max2;

		//check for overlapping pairs
		for (String line: lines) {
			String[] pairAry = line.split(",");

			String[] rangeAry = pairAry[0].split("-");
			min1 = Integer.parseInt(rangeAry[0]);
			max1 = Integer.parseInt(rangeAry[1]);

			rangeAry = pairAry[1].split("-");
			min2 = Integer.parseInt(rangeAry[0]);
			max2 = Integer.parseInt(rangeAry[1]);

			if (max1 >= min2 && max1 <= max2) {
				ctr++;
			} else if (max2 >= min1 && max2 <= max1) {
				ctr++;
			}
		}

		long answer = ctr;
		System.out.printf("Ctr = %s%n", ctr);

		long expected = 936;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

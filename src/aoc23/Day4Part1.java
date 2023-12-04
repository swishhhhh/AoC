package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2023/day/4">Advent of Code 2023 Day 4</a>
 */
public class Day4Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day4_input.txt");
		long sum = 0;

		for (String line: lines) {
			//sample line -> Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53

			String s = line.split(":")[1]; //sample -> 41 48 83 86 17 | 83 86  6 31 17  9 48 53

			List<Integer> winningNums = Helper.extractIntsFromText(s.split("\\|")[0]);
			Set<Integer> winningNumsSet = new HashSet<>(winningNums);

			List<Integer> myNums = Helper.extractIntsFromText(s.split("\\|")[1]);
			boolean matchFound = false;
			int cardPoints = 0;
			for (Integer num: myNums) {
				if (winningNumsSet.contains(num)) {
					if (!matchFound) {
						cardPoints = 1;
						matchFound = true;
					} else {
						cardPoints = cardPoints * 2;
					}
				}
			}
			sum+= cardPoints;
		}

		System.out.printf("Sum = %s%n", sum);
	}
}

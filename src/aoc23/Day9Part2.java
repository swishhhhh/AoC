package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/9">Advent of Code 2023 Day 9</a>
 */
public class Day9Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day9_input.txt");

		long sum = 0;
		int lineCtr = 0;
		for (String line: lines) {
			lineCtr++;
			List<Long> history = Helper.extractLongsFromText(line, true);
			List<Long> firstNumsOfHistory = new ArrayList<>();
			long prevValue = 0;

			while (true) {
				//save first num of history list
				firstNumsOfHistory.add(history.get(0));

				List<Long> nextHistory = new ArrayList<>();
				boolean allZeros = true;
				for (int i = 0; i < history.size() - 1; i++) {
					long diff = history.get(i + 1) - history.get(i);
					nextHistory.add(diff);
					allZeros = allZeros && diff == 0;
				}
				if (allZeros) {
					break;
				}

				history = nextHistory;
			}

			for (int i = firstNumsOfHistory.size() - 1; i >= 0; i--) {
				prevValue = firstNumsOfHistory.get(i) - prevValue;
			}

			sum+= prevValue;
			System.out.printf("Previous Value for line %s = %s%n", lineCtr, prevValue);
		}

		System.out.printf("Sum = %s%n", sum);
	}
}

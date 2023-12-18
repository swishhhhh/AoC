package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/2">Advent of Code 2023 Day 2</a>
 */
public class Day02Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day2_input.txt");

		long sum = 0;
		for (String line: lines) {
			sum+= getPower(line);
		}

		System.out.printf("Sum = %s%n", sum);

		long expected = 72227;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static int getPower(String line) {
		//line sample -> Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
		String trimmed = line.split(":")[1].trim();
		//trimmed sample -> 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green

		int minReds = 0, minGreens = 0, minBlues = 0;

		String[] sets = trimmed.split(";");
		for (String set : sets) {
			//set sample -> 3 blue, 4 red
			String[] balls = set.split(",");
			for (String ball : balls) {
				//ball sample -> 3 blue
				int qty = Helper.extractIntsFromText(ball).get(0);
				if (ball.contains("red")) {
					minReds = Math.max(minReds, qty);
				} else if (ball.contains("green")) {
					minGreens = Math.max(minGreens, qty);
				} else if (ball.contains("blue")) {
					minBlues = Math.max(minBlues, qty);
				}
			}
		}

		return minReds * minGreens * minBlues;
	}
}

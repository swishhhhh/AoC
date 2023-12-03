package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/2">Advent of Code 2023 Day 2</a>
 */
public class Day2Part1 {
	public static final int MAX_RED = 12;
	public static final int MAX_GREEN = 13;
	public static final int MAX_BLUE = 14;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day2_input.txt");

		int lineN = 0;
		int sum = 0;
		for (String line: lines) {
			lineN++;
			if (isValid(line)) {
				sum+= lineN;
			}
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static boolean isValid(String line) {
		//line sample -> Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
		String trimmed = line.split(":")[1].trim();
		//trimmed sample -> 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green

		String[] sets = trimmed.split(";");
		for (String set : sets) {
			//set sample -> 3 blue, 4 red
			String[] balls = set.split(",");
			for (String ball : balls) {
				//ball sample -> 3 blue
				int qty = Helper.extractIntsFromText(ball).get(0);
				if ((ball.contains("red")) && qty > MAX_RED) {
					return false;
				} else if ((ball.contains("green")) && qty > MAX_GREEN) {
					return false;
				} else if ((ball.contains("blue")) && qty > MAX_BLUE) {
					return false;
				}
			}
		}

		return true;
	}
}

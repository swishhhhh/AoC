package aoc22;

import utils.ResourceLoader;
import java.util.List;

import static aoc22.Day02Part1.Hand.*;

/**
 * <a href="https://adventofcode.com/2022/day/2">Advent of Code 2022 Day 2</a>
 */
public class Day02Part1 {

	enum Hand {ROCK, PAPER, SCISSORS}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day2_input.txt");

		int score = 0;
		Hand opponentHand = null, myHand = null;

		for (String line: lines) {
			String[] ary = line.split(" ");
			switch (ary[0]) {
				case "A" -> opponentHand = ROCK;
				case "B" -> opponentHand = PAPER;
				case "C" -> opponentHand = SCISSORS;
			}

			switch (ary[1]) {
				case "X" -> {
					myHand = ROCK;
					score += 1;
				}
				case "Y" -> {
					myHand = PAPER;
					score += 2;
				}
				case "Z" -> {
					myHand = SCISSORS;
					score += 3;
				}
			}

			if (opponentHand == myHand) {
				score+= 3;
			} else if (opponentHand == ROCK && myHand == PAPER) {
				score+= 6;
			} else if (opponentHand == SCISSORS && myHand == ROCK) {
				score+= 6;
			} else if (opponentHand == PAPER && myHand == SCISSORS) {
				score+= 6;
			}

		}

		long answer = score;
		System.out.printf("Score = %s%n", answer);

		long expected = 9651;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

package aoc22;

import utils.ResourceLoader;
import java.util.List;

import static aoc22.Day02Part2.Hand.*;

/**
 * <a href="https://adventofcode.com/2022/day/2">Advent of Code 2022 Day 2</a>
 */
public class Day02Part2 {

	enum Hand {ROCK, PAPER, SCISSORS}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day2_input.txt");

		long score = 0;
		Hand opponentHand = null, myHand = null;

		for (String line: lines) {
			String[] ary = line.split(" ");
			switch (ary[0]) {
				case "A" -> opponentHand = ROCK;
				case "B" -> opponentHand = PAPER;
				case "C" -> opponentHand = SCISSORS;
			}

			switch (ary[1]) {
				case "X" -> myHand = losingResponse(opponentHand);  //make sure to lose
				case "Y" -> myHand = opponentHand; 					//make sure to draw
				case "Z" -> myHand = winningResponse(opponentHand); //make sure to win
			}

			score+= scoreForMyHand(myHand);

			if (opponentHand == myHand) {
				score+= 3;
			} else if (opponentHand  == ROCK && myHand  == PAPER) {
				score+= 6;
			} else if (opponentHand  == SCISSORS && myHand  == ROCK) {
				score+= 6;
			} else if (opponentHand  == PAPER && myHand  == SCISSORS) {
				score+= 6;
			}

		}

		long answer = score;
		System.out.printf("Score = %s%n", score);

		long expected = 10560;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Hand winningResponse(Hand opHand) {
		if (opHand == ROCK) return  PAPER;
		if (opHand == SCISSORS) return  ROCK;
		if (opHand == PAPER) return  SCISSORS;
		return null;
	}

	private static Hand losingResponse(Hand opHand) {
		if (opHand == ROCK) return  SCISSORS;
		if (opHand == SCISSORS) return  PAPER;
		if (opHand == PAPER) return  ROCK;
		return null;
	}

	private static int scoreForMyHand(Hand hand) {
		if (hand == ROCK) return 1;
		if (hand == PAPER) return 2;
		if (hand == SCISSORS) return 3;

		return 0;
	}
}

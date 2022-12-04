package aoc22;

import utils.ResourceLoader;

import java.util.List;

public class Day2Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day2_input.txt");

		int score = 0;
		String opponentHand = "", myHand = "";

		for (String line: lines) {
			String[] ary = line.split(" ");
			switch (ary[0]) {
				case "A" -> opponentHand = "rock";
				case "B" -> opponentHand = "paper";
				case "C" -> opponentHand = "scissors";
			}

			switch (ary[1]) {
				case "X" -> {
					myHand = "rock";
					score += 1;
				}
				case "Y" -> {
					myHand = "paper";
					score += 2;
				}
				case "Z" -> {
					myHand = "scissors";
					score += 3;
				}
			}

			if (opponentHand.equals(myHand)) {
				score+= 3;
			} else if (opponentHand.equals("rock") && myHand.equals("paper")) {
				score+= 6;
			} else if (opponentHand.equals("scissors") && myHand.equals("rock")) {
				score+= 6;
			} else if (opponentHand.equals("paper") && myHand.equals("scissors")) {
				score+= 6;
			}

		}

		System.out.printf("Score = %s%n", score);
	}
}

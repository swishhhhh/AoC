package aoc22;

import utils.ResourceLoader;

import java.util.List;

public class Day2Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day2_input.txt");

		long score = 0;
		String opponentHand = "", myHand = "";

		for (String line: lines) {
			String[] ary = line.split(" ");
			switch (ary[0]) {
				case "A" -> opponentHand = "rock";
				case "B" -> opponentHand = "paper";
				case "C" -> opponentHand = "scissors";
			}

			switch (ary[1]) {
				case "X" -> myHand = losingResponse(opponentHand);  //make sure to lose
				case "Y" -> myHand = opponentHand; 					//make sure to draw
				case "Z" -> myHand = winningResponse(opponentHand); //make sure to win
			}

			score+= scoreForMyHand(myHand);

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

	private static String winningResponse(String opHand) {
		if (opHand.equals("rock")) return "paper";
		if (opHand.equals("scissors")) return "rock";
		if (opHand.equals("paper")) return "scissors";
		return null;
	}

	private static String losingResponse(String opHand) {
		if (opHand.equals("rock")) return "scissors";
		if (opHand.equals("scissors")) return "paper";
		if (opHand.equals("paper")) return "rock";
		return null;
	}

	private static int scoreForMyHand(String hand) {
		if (hand.equals("rock")) return 1;
		if (hand.equals("paper")) return 2;
		if (hand.equals("scissors")) return 3;

		return 0;
	}
}

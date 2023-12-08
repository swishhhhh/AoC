package aoc23;

import utils.ResourceLoader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2023/day/7">Advent of Code 2023 Day 7</a>
 */
public class Day7Part1 {

	static class Hand {
		List<String> cardLabels;
		List<String> cardStrengths;
		String typeSignature;
		int bid;

		public Hand(List<String> cardLabels, List<String> cardStrengths, String typeSignature, int bid) {
			this.cardLabels = cardLabels;
			this.cardStrengths = cardStrengths;
			this.typeSignature = typeSignature;
			this.bid = bid;
		}

		@Override
		public String toString() {
			return "Hand{" +
					"cardLabels=" + cardLabels +
					", cardStrengths=" + cardStrengths +
					", typeSignature='" + typeSignature + '\'' +
					", bid=" + bid +
					'}';
		}
	}

	final static Map<String, String> cardStrengths;
	static { //initializer
		cardStrengths = new HashMap<>();
		cardStrengths.putAll(Map.of("2", "02", "3", "03", "4", "04", "5", "05", "6", "06", "7", "07", "8", "08", "9", "09"));
		cardStrengths.putAll(Map.of("T", "10", "J", "11", "Q", "12", "K", "13", "A", "14"));
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day7_input.txt");

		List<Hand> hands = new ArrayList<>();
		var strengths = cardStrengths;
		for (String line: lines) {
			List<String> cardLabels = List.of(line.split(" ")[0].split(""));
			List<String> cardStrengths =
					cardLabels.stream().map(strengths::get).collect(Collectors.toList());
			hands.add(new Hand(cardLabels, cardStrengths, getHandTypeSignature(cardLabels),
					Integer.parseInt(line.split(" ")[1])));
		}

		hands.sort((hand1, hand2) -> {
			if (hand1.typeSignature.equals(hand2.typeSignature)) {
				//hands of same type, so proceed to compare cardStrengths
				return hand1.cardStrengths.toString().compareTo(hand2.cardStrengths.toString());
			} else {
				//hands of different types
				return hand1.typeSignature.compareTo(hand2.typeSignature);
			}
		});

		//add up the bids
		long sum = 0;
		int ctr = 0;
		for (Hand hand : hands) {
			ctr++;
			sum+= hand.bid * ctr;
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static String getHandTypeSignature(List<String> cardLabels) {
		//produces frequency signature of elements in carLabels list:
		// 	e.g. {"A", "A", "A", "B", "C"} produces: "[3, 1, 1]"
		//	     {"A", "A", "A", "A", "A"} produces: "[5]"
		ArrayList<Long> values =
				new ArrayList<>(
						cardLabels.stream().collect(
										Collectors.groupingBy(
												Function.identity(), Collectors.counting()))
								.values());
		values.sort(Collections.reverseOrder());
		return values.toString();
	}
}

package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2023/day/4">Advent of Code 2023 Day 4</a>
 */
public class Day04Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day4_input.txt");
		long sum = 0;
		Map<String, Integer> cardCopiesMap = new HashMap<>();
		
		//initialize map
		for (int i = 1; i <= lines.size(); i++) {
			cardCopiesMap.put("Card " + i, 1);
		}

		int cardN = 0;
		for (String line: lines) {
			cardN++;

			//sample line -> Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
			String s = line.split(":")[1];
			Set<Integer> winningNumsSet = new HashSet<>(Helper.extractIntsFromText(s.split("\\|")[0]));

			List<Integer> myNums = Helper.extractIntsFromText(s.split("\\|")[1]);
			int numMatches = 0;
			for (Integer num: myNums) {
				if (winningNumsSet.contains(num)) {
					numMatches++;
				}
			}

			int numCopiesThisCard = cardCopiesMap.get("Card " + cardN);

			//add "numCopiesThisCard" copies for each of the next "numMatches" cards
			for (int i = cardN + 1; i <= cardN + numMatches; i++) {
				int numCopiesNextCard = cardCopiesMap.get("Card " + i);
				numCopiesNextCard+= numCopiesThisCard;
				cardCopiesMap.put("Card " + i, numCopiesNextCard);
			}
		}

		//total up all the copies
		for (Integer copies: cardCopiesMap.values()) {
			sum+= copies;
		}

		System.out.printf("Sum = %s%n", sum);

		long expected = 13768818;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}
}

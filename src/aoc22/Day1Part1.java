package aoc22;

import utils.ResourceLoader;

import java.util.List;

public class Day1Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day1_input.txt");

		int currentSum = 0;
		int highestSum = 0;
		int elfNumber = 1;
		int highestElf = 0;

		for (String line: lines) {
			if (line.trim().length() == 0) {
				currentSum = 0;
				elfNumber++;
				continue;
			}

			int value = Integer.parseInt(line);
			currentSum+= value;
			if (currentSum >= highestSum) {
				highestSum = currentSum;
				highestElf = elfNumber;
			}
		}

		System.out.printf("Total elves %s, Highest Elf number %s, highestSum = %s%n", elfNumber, highestElf, highestSum);
	}
}

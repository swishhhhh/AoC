package aoc22;

import aoc22.day19.Blueprint;
import aoc22.day19.State;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 *  <a href="https://adventofcode.com/2022/day/19">Advent of Code 2022 Day 19</a>
 */
public class Day19Part1 {

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day19_input.txt");

		int sum = 0;
		int minutesToCollect = 24;

		for (String line: lines) {
			Blueprint bp = parseBlueprint(line, minutesToCollect);
			State maxState = bp.getStateWithMaxGeodes();
			int qualityLevel = bp.getID() * maxState.getTotalGeode();
			if (DEBUG) {
				System.out.printf("Max Geodes for Blueprint %s = %s, Quality Level=%s%n",
						bp.getID(), maxState.getTotalGeode(), qualityLevel);
			}
			sum+= qualityLevel;
		}

		long answer = sum;
		System.out.printf("Sum Quality Level = %s%n", sum);

		long expected = 1589;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Blueprint parseBlueprint(String line, int minutesToCollect) {
		List<Integer> nums = Helper.extractIntsFromText(line);
		return new Blueprint(nums.get(0), minutesToCollect, nums.get(1), nums.get(2), nums.get(3), nums.get(4),
				nums.get(5), nums.get(6), DEBUG);
	}
}

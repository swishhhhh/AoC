package aoc22;

import aoc22.day19.Blueprint;
import aoc22.day19.State;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

public class Day19Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day19_input.txt");

		int sum = 0;
		int minutesToCollect = 24;

		for (String line: lines) {
			Blueprint bp = parseBlueprint(line, minutesToCollect);
			State maxState = bp.getStateWithMaxGeodes();
			int qualityLevel = bp.getID() * maxState.getTotalGeode();
			System.out.printf("Max Geodes for Blueprint %s = %s, Quality Level=%s%n",
					bp.getID(), maxState.getTotalGeode(), qualityLevel);
			sum+= qualityLevel;
		}

		System.out.printf("Sum Quality Level = %s%n", sum);
	}

	private static Blueprint parseBlueprint(String line, int minutesToCollect) {
		List<Integer> nums = Helper.extractIntsFromText(line);
		return new Blueprint(nums.get(0), minutesToCollect, nums.get(1), nums.get(2), nums.get(3), nums.get(4),
				nums.get(5), nums.get(6));
	}
}

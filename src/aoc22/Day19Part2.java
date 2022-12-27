package aoc22;

import aoc22.day19.Blueprint;
import aoc22.day19.State;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

public class Day19Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day19_input.txt");

		long product = 1;
		int minutesToCollect = 32;

		for (int i = 0; i < 3; i++) {
			Blueprint bp = parseBlueprint(lines.get(i), minutesToCollect);
			State maxState = bp.getStateWithMaxGeodes();
			product*= maxState.getTotalGeode();
			System.out.printf("Max Geodes for Blueprint %s = %s, Product=%s%n",
					bp.getID(), maxState.getTotalGeode(), product);
		}
	}

	private static Blueprint parseBlueprint(String line, int minutesToCollect) {
		List<Integer> nums = Helper.extractIntsFromText(line);
		return new Blueprint(nums.get(0), minutesToCollect, nums.get(1), nums.get(2), nums.get(3), nums.get(4),
				nums.get(5), nums.get(6));
	}
}

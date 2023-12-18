package aoc23;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/8">Advent of Code 2023 Day 8</a>
 */
public class Day08Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day8_input.txt");

		char[] leftRightAry = lines.get(0).toCharArray();
		Map<String, String[]> nodes = new HashMap<>();

		for (int i = 2; i < lines.size(); i++) {
			String line = lines.get(i);
			String key = line.split("=")[0].trim();
			String values = line.split("=")[1].trim();
			values = values.replace("(", "").replace(")", "");
			String[] valsAry = values.split(",");
			valsAry[1] = valsAry[1].trim();
			nodes.put(key, valsAry);
		}

		int lrPtr = 0;
		String nodePtr = "AAA";
		long steps = 0;
		while (true) {
			if (nodePtr.equals("ZZZ")) {
				break;
			}

			String[] values = nodes.get(nodePtr);
			if (leftRightAry[lrPtr] == 'L') {
				nodePtr = values[0];
			} else {
				nodePtr = values[1];
			}

			steps++;
			lrPtr++;
			if (lrPtr >= leftRightAry.length) {
				lrPtr = 0;
			}
		}


		System.out.printf("Steps = %s%n", steps);

		long expected = 14681;
		if (steps != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", steps, expected));
		}
	}

}

package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2023/day/8">Advent of Code 2023 Day 8</a>
 */
public class Day08Part2 {

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

		//find all start and end nodes
		Set<String> startNodes = new HashSet<>();
		for (String node : nodes.keySet()) {
			if (node.endsWith("A")) {
				startNodes.add(node);
			}
		}

		long steps = executeCycles(leftRightAry, nodes, startNodes);
		System.out.printf("Steps = %s%n", steps);

		long expected = 14321394058031L;
		if (steps != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", steps, expected));
		}
	}

	private static long executeCycles(char[] leftRightAry, Map<String, String[]> nodes, Set<String> startNodes) {
		int lrPtr = 0;
		String[] nodePtrs = startNodes.toArray(new String[]{});

		//keep track of cycle lengths for each path
		Long[] cycleLengths = new Long[startNodes.size()];
		long cyclesFoundCtr = 0;

		long steps = 0;

		while (true) {
			if (allPtrsAtEnd(nodePtrs)) {
				break;
			}

			for (int i = 0; i < nodePtrs.length; i++) {
				String nodePtr = nodePtrs[i];

				if (nodePtr.endsWith("Z") && cycleLengths[i] == null) {
					cycleLengths[i] = steps;
					cyclesFoundCtr++;
				}

				//if cycle length found (found node ending with "Z") for every pointer/path, then short-circuit by
				// getting lowest common multiplier of all cycle lengths which will == the total number of steps needed
				if (cyclesFoundCtr == startNodes.size()) {
					return Helper.lowestCommonMultiplier(cycleLengths);
				}

				String[] values = nodes.get(nodePtr);
				if (leftRightAry[lrPtr] == 'L') {
					nodePtrs[i] = values[0];
				} else {
					nodePtrs[i] = values[1];
				}
			}

			steps++;
			lrPtr++;
			if (lrPtr >= leftRightAry.length) {
				lrPtr = 0;
			}
		}

		return steps;
	}

	private static boolean allPtrsAtEnd(String[] nodePtrs) {
		for (String node : nodePtrs) {
			if (!node.endsWith("Z")) {
				return false;
			}
		}
		return true;
	}
}

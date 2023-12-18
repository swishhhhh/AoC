package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/6">Advent of Code 2023 Day 6</a>
 */
public class Day06Part1 {


	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day6_input.txt");

		List<Integer> raceTimes = Helper.extractIntsFromText(lines.get(0));
		List<Integer> distanceRecords = Helper.extractIntsFromText(lines.get(1));
		Map<String, Integer> waysPerRaceMap = new HashMap<>();

		for (int i = 0; i < raceTimes.size(); i++) {
			int time = raceTimes.get(i);
			int distRecord = distanceRecords.get(i);

			int numWaysToBeatRecord = 0;
			long prevDistance = 0;
			for (int j = 1; j < time; j++) {
				long distance = (time - j) * j;

				//optimization: once the distance goes down from previous AND is < record, no need to continue
				if (distance < prevDistance && distance < distRecord) {
					break;
				}

				if (distance > distRecord) {
					numWaysToBeatRecord++;
				}

				prevDistance = distance;
			}

			waysPerRaceMap.put("Race " + i, numWaysToBeatRecord);
		}

		long waysProduct = 1;
		for (Integer ways : waysPerRaceMap.values()) {
			if (ways > 0) {
				waysProduct *= ways;
			}
		}

		System.out.printf("Product of number of ways to beat record = %s%n", waysProduct);

		long expected = 861300;
		if (waysProduct != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", waysProduct, expected));
		}
	}
}

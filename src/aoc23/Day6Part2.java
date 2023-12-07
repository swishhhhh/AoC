package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/6">Advent of Code 2023 Day 6</a>
 */
public class Day6Part2 {


	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day6_input.txt");

		List<Integer> raceTimes = Helper.extractIntsFromText(lines.get(0));
		StringBuilder sb = new StringBuilder();
		for (Integer raceTime: raceTimes) {
			sb.append(raceTime);
		}
		long raceTime = Long.parseLong(sb.toString());

		List<Integer> distanceRecords = Helper.extractIntsFromText(lines.get(1));
		sb = new StringBuilder();
		for (Integer record: distanceRecords) {
			sb.append(record);
		}
		long record = Long.parseLong(sb.toString());

		long numWaysToBeatRecord = 0;
		long prevDistance = 0;
		for (long i = 1; i < raceTime; i++) {
			long distance = (raceTime - i) * i;

			//optimization: once the distance goes down from previous AND is < record, no need to continue
			if (distance < prevDistance && distance < record) {
				break;
			}

			if (distance > record) {
				numWaysToBeatRecord++;
			}

			prevDistance = distance;
		}

		System.out.printf("Number of ways to beat record = %s%n", numWaysToBeatRecord);
	}
}

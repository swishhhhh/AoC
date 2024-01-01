package aoc22;

import datastructs.Coordinates;
import datastructs.Diamond;
import utils.Helper;
import utils.ResourceLoader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2022/day/15">Advent of Code 2022 Day 15</a>
 */
public class Day15Part1 {
	static Set<Coordinates> selectedCoords = new LinkedHashSet<>();
	static Set<Coordinates> beacons = new LinkedHashSet<>();
	static Set<Diamond> diamonds = new LinkedHashSet<>();

	private final static int targetRow = 2_000_000;
	private final static boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day15_input.txt");

		int ctr = 0;

		int sensorX, sensorY, beaconX, beaconY;
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		for (String line: lines) {
			ctr++;
			List<Integer> ints = Helper.extractIntsFromText(line, true);
			sensorX = ints.get(0);
			sensorY = ints.get(1);
			beaconX = ints.get(2);
			beaconY = ints.get(3);
			beacons.add(new Coordinates(beaconX, beaconY));
			int distance = Math.abs(sensorX - beaconX) + Math.abs(sensorY - beaconY);
			diamonds.add(new Diamond(sensorX, sensorY, distance + 1));
			minX = Math.min(minX, sensorX - distance);
			maxX = Math.max(maxX, sensorX + distance);

			if (DEBUG) {
				System.out.printf("line %s: %s, distance: %s%n", ctr, line, distance);
			}
		}

		for (int i = minX; i <= maxX; i++) {
			for (Diamond d: diamonds) {
				if (d.containsPoint(i, targetRow)) {
					selectedCoords.add(new Coordinates(i, targetRow));
				}
			}
		}

		selectedCoords.removeAll(beacons);

		long answer = selectedCoords.size();
		System.out.printf("Selected Coordinates = %s%n", selectedCoords.size());

		long expected = 4827924;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Diamond;
import utils.Helper;
import utils.ResourceLoader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day15Part1 {
	static Set<Coordinates> selectedCoords = new LinkedHashSet<>();
	static Set<Coordinates> beacons = new LinkedHashSet<>();
	static Set<Diamond> diamonds = new LinkedHashSet<>();

	static int targetRow = 2000000;

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
			Coordinates beacon = new Coordinates(beaconX, beaconY);
			beacons.add(beacon);
			int distance = Math.abs(sensorX - beaconX) + Math.abs(sensorY - beaconY);
			Diamond diamond = new Diamond(sensorX, sensorY, distance + 1);
			diamonds.add(diamond);
			minX = Math.min(minX, sensorX - distance);
			maxX = Math.max(maxX, sensorX + distance);

			System.out.printf("line %s: %s, distance: %s%n", ctr, line, distance);
		}

		for (int i = minX; i <= maxX; i++) {
			for (Diamond d: diamonds) {
				if (d.containsPoint(i, targetRow)) {
					selectedCoords.add(new Coordinates(i, targetRow));
				}
			}
		}

		selectedCoords.removeAll(beacons);

		System.out.printf("Selected Coordinates = %s%n", selectedCoords.size());
	}
}

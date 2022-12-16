package aoc22;

import aoc22.datastructs.Coordinates;
import utils.Helper;
import utils.ResourceLoader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day15Part1 {
	static Set<Coordinates> selectedCoords = new LinkedHashSet<>();
	static Set<Coordinates> beacons = new LinkedHashSet<>();
	static int targetRow = 2000000;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day15_input.txt");

		int ctr = 0;

		int sensorX, sensorY, beaconX, beaconY;
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
			int verticalDistanceToTargetRow = Math.abs(targetRow - sensorY);
			int adjDistance = distance - verticalDistanceToTargetRow + 1; //only count between rows

			System.out.printf("line %s: %s, distance: %s, adjusted distance: %s%n", ctr, line, distance, adjDistance);

			for (int i = 0; i < adjDistance; i++) {
				Coordinates coord = new Coordinates(sensorX - i, targetRow); //left coord (or center when i == 0)
				if (!coord.equals(beacon)) {
					selectedCoords.add(coord);
				}

				if (i > 0) {
					coord = new Coordinates(sensorX + i, targetRow); //right coord
					if (!coord.equals(beacon)) {
						selectedCoords.add(coord);
					}
				}
			}
		}

		System.out.printf("Selected Coordinates = %s%n", selectedCoords.size());
	}
}

package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Diamond;
import utils.Helper;
import utils.ResourceLoader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2022/day/15">Advent of Code 2022 Day 15</a>
 */
public class Day15Part2 {
	static Set<Coordinates> beacons = new LinkedHashSet<>();
	static Set<Diamond> diamonds = new LinkedHashSet<>();

	static final int gridSearchStart = 0;
	static final int getGridSearchEnd = 4_000_000;

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

			System.out.printf("line %s: %s, distance: %s%n", ctr, line, distance);
		}

		Coordinates missingBeacon = null;
		for (int yCursor = gridSearchStart; yCursor <= getGridSearchEnd; yCursor++) {
			int xCursor = gridSearchStart;
			while (xCursor <= getGridSearchEnd) {
				/*
				 * "searchDiamonds" returns -1 if coordinate is not in any of the diamonds,
				 * if it is found in any diamond, it returns the diamond with the greatest
				 * number of steps that can be taken/skipped to the right and still be inside
				 * that diamond (0 if only found on the rightmost edge of any diamond but not inside)
				 */
				int steps = searchDiamonds(xCursor, yCursor, diamonds);
				if (steps == -1) {
					missingBeacon = new Coordinates(xCursor, yCursor);
					break;
				}

				xCursor+= steps; //number of steps to skip to the right
				xCursor++;
			}
			if (missingBeacon != null) {
				break;
			}
		}

		if (missingBeacon == null) {
			System.out.println("Houston we got a problem! Couldn't locate the beacon.");
		} else {
			long tuningFreq = (4_000_000L * missingBeacon.x()) + missingBeacon.y();
			System.out.printf("Missing Beacon = %s, Tuning Frequency = %s%n", missingBeacon, tuningFreq);
		}
	}

	private static int searchDiamonds(int xCursor, int yCursor, Set<Diamond> diamonds) {
		int maxSteps = -1;
		for (Diamond diamond: diamonds) {
			int steps = diamond.getNumStepsToRightStillInbound(xCursor, yCursor);
			maxSteps = Math.max(maxSteps, steps);
		}

		return maxSteps;
	}
}

package aoc22;

import aoc22.datastructs.Coordinates;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/14">Advent of Code 2022 Day 14</a>
 */
public class Day14Part2 {

	static int maxXcoord = 0;
	static int minXcoord = Integer.MAX_VALUE;
	static int maxYcoord = 0;
	static int entryXCoord = 500;
	static int entryYCoord = 0;
	static Coordinates entryPoint = new Coordinates(entryXCoord, entryYCoord);
	static String[][] grid;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day14_input.txt");

		List<List<Coordinates>> rockTraces = new ArrayList();

		for (String line: lines) {
			List<Coordinates> rockTrace = new ArrayList<>();
			List<Integer> ints = Helper.extractIntsFromText(line);
			for (int i = 0; i < ints.size(); i+=2) {
				int x = ints.get(i);
				int y = ints.get(i+1);

				maxXcoord = Math.max(maxXcoord, x);
				minXcoord = Math.min(minXcoord, x);
				maxYcoord = Math.max(maxYcoord, y);

				Coordinates coord = new Coordinates(x, y);
				rockTrace.add(coord);
			}
			rockTraces.add(rockTrace);
		}

		//add one more trace for the "floor" at + 2 from the lowest trace
		maxYcoord = maxYcoord + 2;
		maxXcoord = entryXCoord + maxYcoord; //the farthest right it can fall
		minXcoord = entryXCoord - maxYcoord; //the farthest left it can fall
		List<Coordinates> rockTrace = new ArrayList<>();
		for (int i = minXcoord; i <= maxXcoord; i++) {
			Coordinates coord = new Coordinates(i, maxYcoord);
			rockTrace.add(coord);
		}
		rockTraces.add(rockTrace);

		//setup grid
		grid = new String[maxYcoord + 1][maxXcoord + 1];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = ".";
			}
		}
		grid[0][entryXCoord] = "+";

		for (List<Coordinates> rockTrace2: rockTraces) {
			int x1 = 0, y1 = 0, x2, y2, xLen, yLen, xDirection, yDirection;
			boolean firstCoordOnTrace = true;
			for (Coordinates coord: rockTrace2) {
				if (firstCoordOnTrace) {
					x1 = coord.x();
					y1 = coord.y();
					firstCoordOnTrace = false;
					continue;
				}

				x2 = coord.x();
				y2 = coord.y();
				xLen = Math.abs(x1 - x2) + 1;
				yLen = Math.abs(y1 - y2) + 1;
				xDirection = Integer.compare(x2, x1);
				yDirection = Integer.compare(y2, y1);

				//fill in grid
				int len = Math.max(xLen, yLen);
				for (int i = 0; i < len; i++) {
					grid[y1 + (i*yDirection)][x1 + (i*xDirection)] = "#";
				}

				x1 = x2;
				y1 = y2;
			}
		}

		//simulate sand dropping
		int steps = 0, unitsOfSand = 1; //initialize first unit-of-sand
		Coordinates cursor = new Coordinates(entryXCoord, entryYCoord);
		Coordinates target;
		while (true) {
			steps++;

			//S: if target OOB -> done!, else if empty, update cursor to target, continue
			target = new Coordinates(cursor.x(), cursor.y() + 1);
			if (outOfBounds(target)) {
				unitsOfSand--; //don't count last one!
				break;
			}
			if (isEmpty(target)) {
				cursor = new Coordinates(target.x(), target.y());
				continue;
			}

			//SW: ...
			target = new Coordinates(cursor.x() - 1, cursor.y() + 1);
			if (outOfBounds(target)) {
				unitsOfSand--; //don't count last one!
				break;
			}
			if (isEmpty(target)) {
				cursor = new Coordinates(target.x(), target.y());
				continue;
			}

			//SE: ...
			target = new Coordinates(cursor.x() + 1, cursor.y() + 1);
			if (outOfBounds(target)) {
				unitsOfSand--; //don't count last one!
				break;
			}
			if (isEmpty(target)) {
				cursor = new Coordinates(target.x(), target.y());
				continue;
			}

			//if all 3 full, set grain down in cursor position (update grid), increment unitsOfSand, reset cursor, continue
			target = new Coordinates(cursor.x(), cursor.y());
			grid[target.y()][target.x()] = "O";

			//new check: if target == entryPoint -> done!
			if (cursor.equals(entryPoint)) {
				System.out.println("We're full!");
				break;
			}

			//init next unit-of-sand
			unitsOfSand++;
			steps++; //since movement into entryPoint (of next grain of sand) should also count as a step...
			cursor = new Coordinates(entryXCoord, entryYCoord);

			if (steps == Integer.MAX_VALUE) {
				System.err.println("Max number of steps reached, something is wrong!");
				break;
			}
		}

		printGrid();

		System.out.printf("Total steps = %s, units of sand = %s%n", steps, unitsOfSand);
	}

	private static boolean outOfBounds(Coordinates target) {
		return target.x() < minXcoord || target.x() > maxXcoord || target.y() > maxYcoord;
	}

	private static boolean isEmpty(Coordinates target) {
		return grid[target.y()][target.x()].equals(".") || grid[target.y()][target.x()].equals("+");
	}

	private static void printGrid() {
		for (int i = 0; i < grid.length; i++) {
			System.out.printf("%03d", i);
			for (int j = minXcoord - 1; j < grid[0].length; j++) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
}

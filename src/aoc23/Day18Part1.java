package aoc23;

import aoc23.datastructs.Coordinates;
import aoc23.datastructs.Direction;
import utils.ResourceLoader;

import java.util.List;

import static aoc23.datastructs.Direction.*;
import static java.lang.Math.*;
import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/18">Advent of Code 2023 Day 18</a>
 */
public class Day18Part1 {
	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day18_input.txt");

		long count = process(lines);
		System.out.printf("Trench tiles count: %s%n", count);

		long expected = 47045;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long process(List<String> lines) {
		//figure out grid coords
		int x = 0, y = 0;
		int minX = 0, maxX = 0, minY = 0, maxY = 0;

		for (String line : lines) {
			String[] ary = line.split(" ");
			String direction = ary[0];
			int steps = Integer.parseInt(ary[1]);
			switch (direction) {
				case "R" -> x+= steps;
				case "L" -> x-= steps;
				case "U" -> y-= steps;
				case "D" -> y+= steps;
			}
			minX = min(minX, x);
			maxX = max(maxX, x);
			minY = min(minY, y);
			maxY = max(maxY, y);
		}

		int width = maxX - minX + 1;
		int height = maxY - minY + 1;

		//initialize grid
		char[][] grid = new char[height][width];
		fillGrid(grid, '.');

		//mark grid (dig trenches)
		Coordinates cursor = new Coordinates(abs(minX), abs(minY)); //move cursor off origin by as many as min x and y went negative

		//first tile
		grid[cursor.y()][cursor.x()] = '#';

		for (String line : lines) {
			String[] ary = line.split(" ");
			String leftRightUpDown = ary[0];
			int steps = Integer.parseInt(ary[1]);
			cursor = markGrid(grid, cursor, leftRightUpDown, steps);
		}

		if (DEBUG) {
			printGrid(grid);
		}

		long count = fillAndCount(grid);

		if (DEBUG) {
			System.out.println();
			printGrid(grid);
		}

		return count;
	}

	private static long fillAndCount(char[][] grid) {
		/*
		 * The algo in short is for every cell see how many vertical barriers there are to the left of it
		 * and for even numbers consider the cell outside the shape, for odd inside.
		 * The complexity is when there are more than 1 hashes consecutively (indicating the presence
		 * of a horizontal border in addition to the possible vertical one). In this case we only want
		 * to count it as vertical border if the vertical pair of hashes book-ending the horizontal line (string
		 * of hashes) continue on vertically in opposite directions (in the rows above and below it).
		 * The string of horizontal hashes will always end with a vertical 90 degree turn at the end, the only
		 * question is whether they will go in the same direction or opposite each other. If the former (same
		 * direction)they cancel each other out, if the latter (opposite directions) they count as a vertical
		 * line (barrier).
		 *
		 * E.g.   #    #
		 *     -> ######
		 *
		 * The above (->) horizontal line would not count as a vertical barrier given that the hashes in the row
		 * above it are both on top of it (indicating they cancel each other out).
		 *
		 * As opposed to:  #
		 *              -> ######
		 *                      #
		 * Here the horizontal line above (->) where the hashes at the beginning and end of the horizontal line
		 * continue on vertically in opposite directions (indicating a vertical barrier that should be counted).
		 */

		long count = 0;
		for (int row = 0; row < grid.length; row++) {
			int barrierCtr = 0;
			boolean pendingHashAbove = false, pendingHashBelow = false;
			for (int col = 0; col < grid[row].length; col++) {
				char c = grid[row][col];
				if (c == '#') {
					count++;
					//if there are hashes on both directly above and below it, then definitely a barrier
					boolean hasHashAbove = hasHashAbove(grid, row, col);
					boolean hasHashBelow = hasHashBelow(grid, row, col);
					if (hasHashAbove && hasHashBelow) {
						barrierCtr++;
					} else if (hasHashAbove) {
						if (pendingHashBelow) { //from earlier
							barrierCtr++;
							pendingHashBelow = false;
						} else if (pendingHashAbove) {
							//the pair of hashes above cancel each other out, don't count barrier
							pendingHashAbove = false; //reset  for future
						} else {
							pendingHashAbove = true;
						}
					} else if (hasHashBelow) {
						if (pendingHashAbove) { //from earlier
							barrierCtr++;
							pendingHashAbove = false;
						} else if (pendingHashBelow) {
							//the pair of hashes above cancel each other out, don't count barrier
							pendingHashBelow = false; //reset  for future
						} else {
							pendingHashBelow = true;
						}
					}
				} else if (c == '.') {
					if (barrierCtr % 2 == 0) {
						grid[row][col] = 'O';
					} else {
						grid[row][col] = 'I';
						count++;
					}
				}
			}
		}

		return count;
	}

	private static boolean hasHashAbove(char[][] grid, int row, int col) {
		return !isCellOutOfBounds(grid, col, row - 1) && grid[row - 1][col] == '#';
	}

	private static boolean hasHashBelow(char[][] grid, int row, int col) {
		return !isCellOutOfBounds(grid, col, row + 1) && grid[row + 1][col] == '#';
	}

	private static Coordinates markGrid(char[][] grid, Coordinates cursor, String leftRightUpDown, int steps) {
		Direction direction = getDirectionFromOrientation(leftRightUpDown);
		for (int i = 0; i < steps; i++) {
			cursor = getNextCoord(cursor, direction);
			grid[cursor.y()][cursor.x()] = '#';
		}
		return cursor;
	}

	private static Coordinates getNextCoord(Coordinates coord, Direction direction) {
		int x = coord.x(), y = coord.y();
		switch (direction) {
			case NORTH -> y = y - 1;
			case SOUTH -> y = y + 1;
			case WEST  -> x = x - 1;
			case EAST  -> x = x + 1;
		}

		return new Coordinates(x, y);
	}

	private static Direction getDirectionFromOrientation(String leftRightUpDown) {
		Direction dir = null;
		switch (leftRightUpDown) {
			case "L" -> dir = WEST;
			case "R" -> dir = EAST;
			case "U" -> dir = NORTH;
			case "D" -> dir = SOUTH;
		}
		return dir;
	}
}

package aoc23;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/14">Advent of Code 2023 Day 14</a>
 */
public class Day14Part2 {
	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day14_input.txt");

		char[][] grid = new char[lines.size()][];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		//warm up first with 1000 cycles (can probably be much lower)
		long warmupCycles = 1000;
		for (int i = 0; i < warmupCycles; i++) {
			tiltCycle(grid);
		}
		long cycle = warmupCycles;

		//capture initial grid signature
		String initialSignature = getGridSignature(grid);

		long MAX_CYCLES = 1_000_000_000;
		while (cycle < MAX_CYCLES) {
			tiltCycle(grid);
			cycle++;
			if (getGridSignature(grid).equals(initialSignature)) {
				System.out.printf("Grid signature matches initial signature after %s cycles%n", cycle);
				break;
			}
		}

		long cycleRepeatsIncrement = cycle - warmupCycles;
		long cyclesLeftToComplete = (MAX_CYCLES - cycle) % cycleRepeatsIncrement;

		for (int i = 0; i < cyclesLeftToComplete; i++) {
			tiltCycle(grid);
		}

		long sum = calculateColumnLoads(grid);
		System.out.printf("Sum = %s%n", sum);

		long expected = 102829;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static void tiltCycle(char[][] grid) {
		tiltGridNorth(grid);
		tiltGridWest(grid);
		tiltGridSouth(grid);
		tiltGridEast(grid);
	}

	private static void tiltGridNorth(char[][] grid) {
		for (int col = 0; col < grid[0].length; col++) {
			int limit = 0;

			for (int row = 0; row < grid.length; row++) {
				char c = grid[row][col];
				switch (c) {
					case '.' -> limit = Math.min(limit, row);
					case '#' -> limit = row + 1;
					case 'O' -> {
						if (limit < row) { //swap current cell with limit
							grid[row][col] = '.';
							grid[limit][col] = 'O';
						}
						limit++;
					}
				}
			}
		}
	}

	private static void tiltGridWest(char[][] grid) {
		for (int row = 0; row < grid.length; row++) {
			int limit = 0;

			for (int col = 0; col < grid[0].length; col++) {
				char c = grid[row][col];
				switch (c) {
					case '.' -> limit = Math.min(limit, col);
					case '#' -> limit = col + 1;
					case 'O' -> {
						if (limit < col) { //swap current cell with limit
							grid[row][col] = '.';
							grid[row][limit] = 'O';
						}
						limit++;
					}
				}
			}
		}
	}

	private static void tiltGridSouth(char[][] grid) {
		for (int col = 0; col < grid[0].length; col++) {
			int limit = grid.length - 1;

			for (int row = grid.length - 1; row >= 0; row--) {
				char c = grid[row][col];
				switch (c) {
					case '.' -> limit = Math.max(limit, row);
					case '#' -> limit = row - 1;
					case 'O' -> {
						if (limit > row) { //swap current cell with limit
							grid[row][col] = '.';
							grid[limit][col] = 'O';
						}
						limit--;
					}
				}
			}
		}
	}

	private static void tiltGridEast(char[][] grid) {
		for (int row = 0; row < grid.length; row++) {
			int limit = grid[0].length - 1;

			for (int col = grid[0].length - 1; col >= 0; col--) {
				char c = grid[row][col];
				switch (c) {
					case '.' -> limit = Math.max(limit, col);
					case '#' -> limit = col - 1;
					case 'O' -> {
						if (limit > col) { //swap current cell with limit
							grid[row][col] = '.';
							grid[row][limit] = 'O';
						}
						limit--;
					}
				}
			}
		}
	}

	private static long calculateColumnLoads(char[][] grid) {
		int totalLoad = 0;

		for (int col = 0; col < grid[0].length; col++) {
			long colLoad = 0;
			for (int row = 0; row < grid.length; row++) {
				if (grid[row][col] == 'O') {
					colLoad+= (grid.length - row);
				}
			}
			totalLoad+= colLoad;
		}

		return totalLoad;
	}

	private static String getGridSignature(char[][] grid) {
		StringBuilder sb = new StringBuilder();
		for (char[] c : grid) {
			sb.append(c);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}

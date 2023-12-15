package aoc23;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/14">Advent of Code 2023 Day 14</a>
 */
public class Day14Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day14_input.txt");

		char[][] grid = new char[lines.size()][];
		long sum = 0;

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		for (int col = 0; col < grid[0].length; col++) {
			sum+= calculateColumnLoad(grid, col);
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static long calculateColumnLoad(char[][] grid, int col) {
		int ceiling = 0;
		int totalLoad = 0;

		for (int row = 0; row < grid.length; row++) {
			char c = grid[row][col];
			switch (c) {
				case '.' -> ceiling = Math.min(ceiling, row);
				case '#' -> ceiling = row + 1;
				case 'O' -> {
					if (ceiling < row) {
						//swap current cell with ceiling
						grid[row][col] = '.';
						grid[ceiling][col] = 'O';
						totalLoad += (grid.length - ceiling);
					} else {
						totalLoad += (grid.length - row);
					}
					ceiling++;
				}
			}
		}

		return totalLoad;
	}
}

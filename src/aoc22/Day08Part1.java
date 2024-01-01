package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/8">Advent of Code 2022 Day 8</a>
 */
public class Day08Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day8_input.txt");

		int ctr = 0;
		int dimension = lines.get(0).length();
		int[][] grid = new int[dimension][dimension];

		//load grid
		int row = 0;
		for (String line : lines) {
			for (int j = 0; j < dimension; j++) {
				grid[row][j] = Integer.parseInt(line.substring(j, j + 1));
			}
			row++;
		}

		for (row = 0; row < dimension; row++) {
			for (int col = 0; col < dimension; col++) {
				if (row == 0 || col == 0 || row == dimension - 1 || col == dimension - 1) {
					ctr++;
					continue;
				}

				int cell = grid[row][col];
				boolean good = false;

				//look up
				for (int i = 0; i < row; i++) {
					good = true;
					if (cell <= grid[i][col]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look down
				for (int i = row + 1; i < dimension; i++) {
					good = true;
					if (cell <= grid[i][col]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look left
				for (int i = 0; i < col; i++) {
					good = true;
					if (cell <= grid[row][i]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look right
				for (int i = col + 1; i < dimension; i++) {
					good = true;
					if (cell <= grid[row][i]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}
			}
		}

		long answer = ctr;
		System.out.printf("Total = %s%n", ctr);

		long expected = 1798;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}


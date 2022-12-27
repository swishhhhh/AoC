package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/8">Advent of Code 2022 Day 8</a>
 */
public class Day8Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day8_input.txt");

		long hiScore = 0;
		int dimension = lines.get(0).length();
		int[][] grid = new int[dimension][dimension];

		//load grid
		int row = 0;
		for (String line : lines) {
			for (int col = 0; col < dimension; col++) {
				grid[row][col] = Integer.parseInt(line.substring(col, col + 1));
			}
			row++;
		}

		for (row = 0; row < dimension; row++) {
			for (int col = 0; col < dimension; col++) {
				int cell = grid[row][col];

				//look up
				int visibleTreesUp = 0;
				if (row > 0) {
					for (int i = row - 1; i >= 0; i--) {
						visibleTreesUp++;
						if (cell <= grid[i][col]) {
							break;
						}
					}
				}

				//look down
				int visibleTreesDown = 0;
				if (row < dimension - 1) {
					for (int i = row + 1; i < dimension; i++) {
						visibleTreesDown++;
						if (cell <= grid[i][col]) {
							break;
						}
					}
				}

				//look left
				int visibleTreesLeft = 0;
				if (col > 0) {
					for (int i = col - 1; i >= 0; i--) {
						visibleTreesLeft++;
						if (cell <= grid[row][i]) {
							break;
						}
					}
				}

				//look right
				int visibleTreesRight = 0;
				if (col < dimension - 1) {
					for (int i = col + 1; i < dimension; i++) {
						visibleTreesRight++;
						if (cell <= grid[row][i]) {
							break;
						}
					}
				}

				long score = (long) visibleTreesDown * visibleTreesUp * visibleTreesRight * visibleTreesLeft;
				if (score > hiScore) {
					hiScore = score;
				}

			}
		}

		System.out.printf("HighScore = %s%n", hiScore);
	}
}


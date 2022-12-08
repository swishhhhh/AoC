package aoc22;

import utils.ResourceLoader;

import java.util.List;

public class Day8Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day8_input.txt");

		long hiScore = 0;
		int dimension = 99;
		int[][] grid = new int[dimension][dimension];

		int i = 0;
		for (String line : lines) {
			for (int j = 0; j < dimension; j++) {
				grid[i][j] = Integer.parseInt(line.substring(j, j + 1));
			}
			i++;
		}

		for (i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				int cell = grid[i][j];

				//look left
				int visTreesLeft = 0;
				if (i > 0) {
					for (int k = i - 1; k >= 0; k--) {
						visTreesLeft++;
						if (cell <= grid[k][j]) {
							break;
						}
					}
				}

				//look right
				int visTreesRight = 0;
				if (i < dimension - 1) {
					for (int k = i + 1; k < dimension; k++) {
						visTreesRight++;
						if (cell <= grid[k][j]) {
							break;
						}
					}
				}

				//look up
				int visTreesUp = 0;
				if (j > 0) {
					for (int k = j - 1; k >= 0; k--) {
						visTreesUp++;
						if (cell <= grid[i][k]) {
							break;
						}
					}
				}

				//look down
				int visTreesDown = 0;
				if (j < dimension - 1) {
					for (int k = j + 1; k < dimension; k++) {
						visTreesDown++;
						if (cell <= grid[i][k]) {
							break;
						}
					}
				}

				long score = visTreesRight * visTreesLeft * visTreesDown * visTreesUp;
				if (score > hiScore) {
					hiScore = score;
				}

			}
		}

		System.out.printf("HighScore = %s%n", hiScore);
	}
}


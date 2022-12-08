package aoc22;

import utils.ResourceLoader;

import java.util.List;

public class Day8Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day8_input.txt");

		int ctr = 0;
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
				if (i == 0 || j == 0 || i == dimension - 1 || j == dimension - 1) {
					ctr++;
					continue;
				}

				int cell = grid[i][j];
				boolean good = false;

				//look left
				for (int k = 0; k < i; k++) {
					good = true;
					if (cell <= grid[k][j]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look right
				for (int k = i + 1; k < dimension; k++) {
					good = true;
					if (cell <= grid[k][j]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look up
				for (int k = 0; k < j; k++) {
					good = true;
					if (cell <= grid[i][k]) {
						good = false;
						break;
					}
				}
				if (good) {
					ctr++;
					continue;
				}

				//look down
				for (int k = j + 1; k < dimension; k++) {
					good = true;
					if (cell <= grid[i][k]) {
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

		System.out.printf("Total = %s%n", ctr);
	}
}


package aoc23;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/3">Advent of Code 2023 Day 3</a>
 */
public class Day3Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day3_input.txt");

		char[][] grid = new char[lines.size()][];
		long sum = 0;

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				if (grid[row][col] == '*') {
					List<Integer> adjNumbers = getAdjacentNumbers(grid, row, col);
					if (adjNumbers.size() == 2) {
						sum += (adjNumbers.get(0) * adjNumbers.get(1));
						System.out.printf("Gear found [%s, %s], adjNumbers = %s%n", row, col, adjNumbers);
					}
				}
			}
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static List<Integer> getAdjacentNumbers(char[][] grid, int row, int col) {
		List<Integer> adjNumbers = new ArrayList<>();

		//check above
		if (row > 0) {
			//4 scenarios: number directly above (in which case diagonals not possible),
			//  		   diagonal number NW, diagonal number NE, diagonal numbers both NW and NE

			if (isDigit(grid[row - 1][col])) { //number directly above
				adjNumbers.add(extractNumber(grid, row - 1, col));
			} else {
				if (col > 0 && isDigit(grid[row - 1][col - 1])) {
					adjNumbers.add(extractNumber(grid, row - 1, col - 1));
				}
				if (col < grid[row].length - 1 && isDigit(grid[row - 1][col + 1])) {
					adjNumbers.add(extractNumber(grid, row - 1, col + 1));
				}
			}
		}

		//check below
		if (row < grid.length - 1) {
			//4 scenarios: number directly below (in which case diagonals not possible),
			//  		   diagonal number SW, diagonal number SE, diagonal numbers both SW and SE

			if (isDigit(grid[row + 1][col])) { //number directly below
				adjNumbers.add(extractNumber(grid, row + 1, col));
			} else {
				if (col > 0 && isDigit(grid[row + 1][col - 1])) {
					adjNumbers.add(extractNumber(grid, row + 1, col - 1));
				}
				if (col < grid[row].length - 1 && isDigit(grid[row + 1][col + 1])) {
					adjNumbers.add(extractNumber(grid, row + 1, col + 1));
				}
			}
		}

		//check left
		if (col > 0) {
			if (isDigit(grid[row][col - 1])) {
				adjNumbers.add(extractNumber(grid, row, col - 1));
			}
		}

		//check right
		if (col < grid[row].length - 1) {
			if (isDigit(grid[row][col + 1])) {
				adjNumbers.add(extractNumber(grid, row, col + 1));
			}
		}

		return adjNumbers;
	}

	private static Integer extractNumber(char[][] grid, int row, int col) {
		//find beginning of number to the left
		int startCol = col;
		while (true) {
			if (startCol == 0) {
				break;
			}

			startCol--;
			if (!isDigit(grid[row][startCol])) {
				startCol++;
				break;
			}
		}

		//read forward/right until end of number
		StringBuilder sb = new StringBuilder();
		int xCol = startCol;
		while (true) {
			sb.append(grid[row][xCol]);
			xCol++;
			if (xCol >= grid[row].length || !isDigit(grid[row][xCol])) {
				break;
			}
		}

		return Integer.parseInt(sb.toString());
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
}

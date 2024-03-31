package aoc23;

import utils.ResourceLoader;

import java.util.List;

import static utils.Helper.isDigit;

/**
 * <a href="https://adventofcode.com/2023/day/3">Advent of Code 2023 Day 3</a>
 */
public class Day03Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day3_input.txt");

		char[][] grid = new char[lines.size()][];
		long sum = 0;

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		boolean midWord = false;
		StringBuilder sb = null;
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				if (isDigit(grid[row][col])) {
					if (midWord) {
						sb.append(grid[row][col]);
					} else {
						sb = new StringBuilder();
						sb.append(grid[row][col]);
						midWord = true;
					}
				} else {
					if (midWord) { //just completed a word
//						System.out.printf("Val %s found [%s, %s]", sb, row, col - sb.length());
						if (isAdjacentToSymbol(grid, row, col - sb.length(), col - 1)) {
							sum+= Integer.parseInt(sb.toString());
//							System.out.print(" *");
						}
//						System.out.println();
						midWord = false;
					}
				}
			}

			//end of row logic
			if (midWord) {
//				System.out.printf("Val %s found [%s, %s]", sb, row, grid[row].length - 1 - sb.length());
				if (isAdjacentToSymbol(grid, row, grid[row].length - sb.length(), grid[row].length - 1)) {
					sum+= Integer.parseInt(sb.toString());
//					System.out.print(" *");
				}
//				System.out.println();
				midWord = false;
			}
		}


		System.out.printf("Sum = %s%n", sum);

		long expected = 539637;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static boolean isAdjacentToSymbol(char[][] grid, int row, int startCol, int endCol) {
		//check above
		if (row > 0) {
			for (int i = Math.max(0, startCol - 1); i <= Math.min(grid[row - 1].length - 1, endCol + 1); i++) {
				char cell = grid[row - 1][i];
				if (cell != '.' && !isDigit(cell)) {
					return true;
				}
			}
		}

		//check below
		if (row < grid.length - 1) {
			for (int i = Math.max(0, startCol - 1); i <= Math.min(grid[row + 1].length - 1, endCol + 1); i++) {
				char cell = grid[row + 1][i];
				if (cell != '.' && !isDigit(cell)) {
					return true;
				}
			}
		}

		//check left
		if (startCol > 0) {
			char cell = grid[row][startCol - 1];
			if (cell != '.' && !isDigit(cell)) {
				return true;
			}
		}

		//check right
		if (endCol < grid[row].length - 1) {
			char cell = grid[row][endCol + 1];
			return cell != '.' && !isDigit(cell);
		}

		return false;
	}

}

package aoc23;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/13">Advent of Code 2023 Day 13</a>
 */
public class Day13Part2 {

	private static final List<char[][]> grids = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day13_input.txt");

		initGrids(lines);
		long sum = 0;

		for (char[][] grid : grids) {
			sum+= scoreGrid(grid);
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static void initGrids(List<String> lines) {
		int lineN = -1, gridStartLineN = 0;
		for (String line: lines) {
			lineN++;
			if (line.isEmpty()) {
				addGrid(lines, lineN, gridStartLineN);
				gridStartLineN = lineN + 1;
			}
		}

		//add last grid
		lineN++;
		addGrid(lines, lineN, gridStartLineN);
	}

	private static void addGrid(List<String> lines, int lineN, int gridStartLineN) {
		char[][] grid = new char[lineN - gridStartLineN][lines.get(gridStartLineN).length()];
		int row = -1;
		for (int i = gridStartLineN; i < lineN; i++) {
			row++;
			grid[row] = lines.get(gridStartLineN + row).toCharArray();
		}
		grids.add(grid);
	}

	private static long scoreGrid(char[][] grid) {
		//iterate rows
		for (int row = 0; row < grid.length - 1; row++) {
			String thisRow = joinRow(grid[row]);
			String nextRow = joinRow(grid[row + 1]);

			if (thisRow.equals(nextRow) || atMostOneCharMismatch(thisRow, nextRow)) {
				if (isHorizontalMirrorCut(grid, row)) {
					return (row + 1) * 100L;
				}
			}
		}

		//iterate columns
		for (int col = 0; col < grid[0].length; col++) {
			String thisCol = joinCol(grid, col);
			String nextCol = joinCol(grid, col + 1);

			if (thisCol.equals(nextCol) || atMostOneCharMismatch(thisCol, nextCol)) {
				if (isVerticalMirrorCut(grid, col)) {
					return col + 1;
				}
			}
		}

		throw new RuntimeException("No mirrors in grid " + Arrays.deepToString(grid));
	}

	private static String joinRow(char[] row) {
		StringBuilder sb = new StringBuilder();
		for (char c: row) {
			sb.append(c);
		}
		return sb.toString();
	}

	private static String joinCol(char[][] grid, int col) {
		StringBuilder sb = new StringBuilder();
		for (char[] row : grid) {
			sb.append(row[col]);
		}
		return sb.toString();
	}

	/**
	 * return true if s1 matches s2 with at most 1 char mismatch (full equality (0 mismatches) returns true as well)
	 */
	private static boolean atMostOneCharMismatch(String s1, String s2) {
		if (s1.length() != s2.length()) { //should not happen
			return false;
		}

		int mismatchCnt = 0;
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				mismatchCnt++;
			}
			if (mismatchCnt > 1) {
				return false;
			}
		}

		return true;
	}

	private static boolean isHorizontalMirrorCut(char[][] grid, int row) {
		int numSmudges = 0;
		for (int i = 0; i < Math.min(row + 1, grid.length - row - 1); i++) {
			String topHalfRowToCompare = joinRow(grid[row - i]);
			String bottomHalfRowToCompare = joinRow(grid[row + 1 + i]);

			if (topHalfRowToCompare.equals(bottomHalfRowToCompare)) {
				continue;
			}

			if (numSmudges == 0 && atMostOneCharMismatch(topHalfRowToCompare, bottomHalfRowToCompare)) {
				numSmudges++;
			} else { //more than 1 smudge needed
				return false;
			}
		}
		return numSmudges == 1;
	}

	private static boolean isVerticalMirrorCut(char[][] grid, int col) {
		int numSmudges = 0;
		for (int i = 0; i < Math.min(col + 1, grid[0].length - col - 1); i++) {
			String leftHalfColToCompare = joinCol(grid, col - i);
			String rightHalfColToCompare = joinCol(grid, col + 1 + i);

			if (leftHalfColToCompare.equals(rightHalfColToCompare)) {
				continue;
			}

			if (numSmudges == 0 && atMostOneCharMismatch(leftHalfColToCompare, rightHalfColToCompare)) {
				numSmudges++;
			} else { //more than 1 smudge needed
				return false;
			}
		}
		return numSmudges == 1;
	}
}

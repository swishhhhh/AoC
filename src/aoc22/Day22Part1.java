package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static aoc22.datastructs.Direction.*;

/**
 * <a href="https://adventofcode.com/2022/day/22">Advent of Code 2022 Day 22</a>
 */
public class Day22Part1 {
	private static char[][] grid;
	private static int[] leftEdgePositions;
	private static int[] rightEdgePositions;
	private static int[] topEdgePositions;
	private static int[] bottomEdgePositions;
	private static final List<Direction> DIRECTIONS = List.of(NORTH, EAST, SOUTH, WEST);

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day22_input.txt");

		Direction direction = EAST;
		String instructionLine = lines.get(lines.size() - 1);
		List<String> instructions = new ArrayList<>();

		//parse instruction line into steps
		StringBuilder sb = new StringBuilder();
		for (char c: instructionLine.toCharArray()) {
			if (c >= '0' && c <= '9') {
				sb.append(c);
			} else if (c == 'R' || c == 'L'){
				//add prev number
				instructions.add(sb.toString());
				instructions.add(String.valueOf(c));
				sb = new StringBuilder(); //clear it for next number
			} else {
				throw new Exception("Unexpected char in instructions line: " + c);
			}
		}
		instructions.add(sb.toString()); //last number on line

		//----------
		lines.remove(lines.size() - 1); //remove instructions line (last one)
		lines.remove(lines.size() - 1); //remove blank line (2nd to last one)

		int numRows = lines.size();
		int numCols = -1;
		//zip thru to get max line len
		for (String line: lines) {
			numCols = Math.max(numCols, line.length());
		}

		//set up perimeter outline
		initPerimeter(numRows, numCols);

		//load up grid and perimeter values
		loadGridAndPerimeter(lines, numRows, numCols);

		//find initial position of cursor (leftmost open cell of top row)
		Coordinates cursor = getInitialCursor();

		//loop
		for (String instruction: instructions) {
			if (Helper.isNumeric(instruction)) {
				cursor = moveCursor(Integer.parseInt(instruction), cursor, direction);
			} else {
				direction = rotateDirection(instruction, direction);
			}
		}

		//final answer
		int directionValue = -1;
		switch (direction) {
			case EAST -> directionValue = 0;
			case SOUTH -> directionValue = 1;
			case WEST -> directionValue = 2;
			case NORTH -> directionValue = 3;
		}
		long answer = (1000L * (cursor.y() + 1)) + (4L * (cursor.x() + 1)) + directionValue;

		System.out.printf("Result = %s%n", answer);

		long expected = 146092;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Coordinates moveCursor(int numSteps, Coordinates cursor, Direction direction) {
		int targetRow = cursor.y();
		int targetCol = cursor.x();
		int tmpRow, tmpCol;

		for (int i = 0; i < numSteps; i++) {
			switch (direction) {
				case EAST, WEST -> {
					tmpCol = targetCol;
					if (direction == EAST) targetCol++;
					if (direction == WEST) targetCol--;
					if (isOutOfBounds(targetRow, targetCol)) {
						targetCol = wrapCol(direction, targetRow);
					}
					if (grid[targetRow][targetCol] == '#') {
						targetCol = tmpCol; //hit a wall, restore previous cell
						return new Coordinates(targetCol, targetRow);
					}
				}
				case NORTH, SOUTH -> {
					tmpRow = targetRow;
					if (direction == NORTH) targetRow--;
					if (direction == SOUTH) targetRow++;
					if (isOutOfBounds(targetRow, targetCol)) {
						targetRow = wrapRow(direction, targetCol);
					}
					if (grid[targetRow][targetCol] == '#') {
						targetRow = tmpRow; //hit a wall, restore previous cell
						return new Coordinates(targetCol, targetRow);
					}
				}
			}
		}

		return new Coordinates(targetCol, targetRow);
	}

	private static boolean isOutOfBounds(int targetRow, int targetCol) {
		return     targetRow < 0 || targetRow >= grid.length
				|| targetCol < 0 || targetCol >= grid[targetRow].length
				|| targetRow < topEdgePositions[targetCol]
				|| targetRow > bottomEdgePositions[targetCol]
				|| targetCol < leftEdgePositions[targetRow]
				|| targetCol > rightEdgePositions[targetRow];
	}

	private static int wrapCol(Direction direction, int row) {
		int col;
		switch (direction) {
			case EAST -> col = leftEdgePositions[row];
			case WEST -> col = rightEdgePositions[row];
			default -> throw new IllegalArgumentException("wrapCol only supports EAST or WEST");
		}
		return col;
	}

	private static int wrapRow(Direction direction, int col) {
		int row;
		switch (direction) {
			case NORTH -> row = bottomEdgePositions[col];
			case SOUTH -> row = topEdgePositions[col];
			default -> throw new IllegalArgumentException("wrapRow only supports NORTH or SOUTH");
		}
		return row;
	}

	private static Direction rotateDirection(String instruction, Direction fromDirection) {
		int idx = DIRECTIONS.indexOf(fromDirection);
		switch (instruction) {
			case "R" -> {
				idx++;
				if (idx >= DIRECTIONS.size()) idx = 0;
			}
			case "L" -> {
				idx--;
				if (idx < 0) idx = DIRECTIONS.size() - 1;
			}
		}
		return DIRECTIONS.get(idx);
	}

	private static void initPerimeter(int numRows, int numCols) {
		leftEdgePositions = new int[numRows];
		rightEdgePositions = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			leftEdgePositions[i] = -1;
			rightEdgePositions[i] = -1;
		}
		topEdgePositions = new int[numCols];
		bottomEdgePositions = new int[numCols];
		for (int i = 0; i < numCols; i++) {
			topEdgePositions[i] = -1;
			bottomEdgePositions[i] = -1;
		}
	}

	private static Coordinates getInitialCursor() {
		for (int i = 0; i < grid[0].length; i++) {
			if (grid[0][i] == '.') {
				return new Coordinates(i, 0);
			}
		}

		throw new IllegalStateException("Should never get here with valid input");
	}

	private static void loadGridAndPerimeter(List<String> lines, int numRows, int numCols) {
		grid = new char[numRows][numCols];
		int row = -1;
		for (String line: lines) {
			row++;
			char[] chars = line.toCharArray();
			int col = 0;
			for (; col < chars.length; col++) {
				grid[row][col] = chars[col];

				if (grid[row][col] != ' ') {
					if (leftEdgePositions[row] == -1) {
						leftEdgePositions[row] = col;
					}
					if (col > rightEdgePositions[row]) {
						rightEdgePositions[row] = col;
					}
					if (topEdgePositions[col] == -1) {
						topEdgePositions[col] = row;
					}
					if (row > bottomEdgePositions[col]) {
						bottomEdgePositions[col] = row;
					}
				}
			}

			//pad rest of row with spaces
			for (; col < numCols; col++) {
				grid[row][col] = ' ';
			}
		}
	}
}

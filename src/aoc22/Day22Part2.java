package aoc22;

import datastructs.Coordinates;
import datastructs.Direction;
import aoc22.day22.CubeWrapper;
import aoc22.day22.CubeWrapperMyInput;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static datastructs.Direction.*;

/**
 * <a href="https://adventofcode.com/2022/day/22">Advent of Code 2022 Day 22</a>
 */
public class Day22Part2 {
	private static char[][] grid;
	private static int[] leftEdgePositions;
	private static int[] rightEdgePositions;
	private static int[] topEdgePositions;
	private static int[] bottomEdgePositions;
	private static Direction direction;
	private static final List<Direction> DIRECTIONS = List.of(NORTH, EAST, SOUTH, WEST);

	static CubeWrapper getCubeWrapper(Coordinates cursor, Direction direction) {
//		return new CubeWrapperSample1(cursor, direction);
		return new CubeWrapperMyInput(cursor, direction);
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day22_input.txt");

		direction = EAST;
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
				sb = new StringBuilder(); //clear it for next numbers
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
				cursor = moveCursorAndDirection(Integer.parseInt(instruction), cursor);
			} else {
				rotateDirection(instruction);
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

		System.out.printf("Row = %s, Col = %s, Direction = %s, Result = %s%n",
				cursor.y() + 1, cursor.x() + 1, direction, answer);

		long expected = 110342;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Coordinates moveCursorAndDirection(int numSteps, Coordinates cursor) {
		int targetRow = cursor.y();
		int targetCol = cursor.x();
		Coordinates targetCursor = null;

		for (int i = 0; i < numSteps; i++) {
			//save tmpCursor in case you need to back out of the move (i.e. if you hit a wall)
			Coordinates tmpCursor = new Coordinates(targetCol, targetRow);
			Direction tmpDirection = direction;

			switch (direction) {
				case EAST -> targetCol++;
				case WEST -> targetCol--;
				case NORTH -> targetRow--;
				case SOUTH -> targetRow++;
			}
			targetCursor = new Coordinates(targetCol, targetRow);

			if (isOutOfBounds(targetRow, targetCol)) {
				CubeWrapper cubeWrapper = getCubeWrapper(tmpCursor, direction);
				cubeWrapper.wrap();
				targetCursor = cubeWrapper.getCursor();
				targetCol = targetCursor.x();
				targetRow = targetCursor.y();
				direction = cubeWrapper.getDirection();
			}

			if (grid[targetCursor.y()][targetCursor.x()] == '#') {
				//hit a wall, restore previous cell and direction
				direction = tmpDirection;
				return tmpCursor;
			}
		}

		return targetCursor;
	}

	private static boolean isOutOfBounds(int targetRow, int targetCol) {
		return     targetRow < 0 || targetRow >= grid.length
				|| targetCol < 0 || targetCol >= grid[targetRow].length
				|| targetRow < topEdgePositions[targetCol]
				|| targetRow > bottomEdgePositions[targetCol]
				|| targetCol < leftEdgePositions[targetRow]
				|| targetCol > rightEdgePositions[targetRow];
	}

	private static void rotateDirection(String instruction) {
		int idx = DIRECTIONS.indexOf(direction);
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
		direction = DIRECTIONS.get(idx);
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

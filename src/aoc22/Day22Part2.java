package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;
import aoc22.day22.CubeWrapper;
import aoc22.day22.CubeWrapperMyInput;
import aoc22.day22.CubeWrapperSample1;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static aoc22.datastructs.Direction.*;

public class Day22Part2 {
	static char[][] grid;
	static int[] leftEdgePositions;
	static int[] rightEdgePositions;
	static int[] topEdgePositions;
	static int[] bottomEdgePositions;
	static Direction direction;

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
		long result = (1000L * (cursor.getY() + 1)) + (4L * (cursor.getX() + 1)) + directionValue;

		System.out.printf("Result = %s%n", result);
	}

	private static Coordinates moveCursorAndDirection(int numSteps, Coordinates cursor) {
		int targetRow = cursor.getY();
		int targetCol = cursor.getX();
		Coordinates targetCursor = null; // = new Coordinates(cursor.getX(), cursor.getY());

		for (int i = 0; i < numSteps; i++) {
			//save tmpCursor in case you need to back out of the move (i.e. if hit a wall)
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
				targetCol = targetCursor.getX();
				targetRow = targetCursor.getY();
				direction = cubeWrapper.getDirection();
			}

			if (grid[targetCursor.getY()][targetCursor.getX()] == '#') {
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
		switch (instruction) {
			case "R" -> {
				switch (direction) {
					case EAST -> direction = SOUTH;
					case SOUTH -> direction = WEST;
					case WEST -> direction = NORTH;
					case NORTH -> direction = EAST;
				}
			}
			case "L" -> {
				switch (direction) {
					case EAST -> direction = NORTH;
					case NORTH -> direction = WEST;
					case WEST -> direction = SOUTH;
					case SOUTH -> direction = EAST;
				}
			}
		}
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

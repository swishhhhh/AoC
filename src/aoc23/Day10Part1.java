package aoc23;

import aoc23.datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2023/day/10">Advent of Code 2023 Day 10</a>
 */
public class Day10Part1 {

	private enum PipeType {
		VERTICAL('|'), HORIZONTAL('-'), F('F'), S7('7'), J('J'), L('L');

		private final char symbol;

		PipeType(char symbol) {
			this.symbol = symbol;
		}

		static PipeType getFromSymbol(char input) {
			for (PipeType type : PipeType.values()) {
				if (type.symbol == input) {
					return type;
				}
			}
			throw new IllegalStateException("Unexpected value: " + input);
		}
		@Override
		public String toString() {
			return "PipeType{" +
					"symbol='" + symbol + '\'' +
					'}';
		}
	}

	private static char[][] grid;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day10_input.txt");
		Coordinates startLocation = null;

		//setup grid and start locations, cursor
		grid = new char[lines.size()][lines.get(0).length()];
		for (int row = 0; row < grid.length; row++) {
			grid[row] = lines.get(row).toCharArray();
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col] == 'S') {
					startLocation = new Coordinates(col, row);
				}
			}
		}

		//replace starting cell's symbol with correct pipe-type
		grid[startLocation.y()][startLocation.x()] = getStartingPipe(startLocation).symbol;

		List<Coordinates> adjacentCells = getReachableAdjacentCells(startLocation);
		//pick 1 and take first step
		Coordinates cursor = adjacentCells.get(0);
		long steps = 1;
		Coordinates prevCursor = startLocation;

		while (!cursor.equals(startLocation)) {
			steps++;
			adjacentCells = getReachableAdjacentCells(cursor);
			if (adjacentCells.size() != 2) {
				throw new RuntimeException(String.format("Cursor at %s and adjacent cells count != 2 (%s)%n",
						cursor, adjacentCells));
			}

			Coordinates temp = cursor;
			cursor = adjacentCells.get(0).equals(prevCursor) ? adjacentCells.get(1) : adjacentCells.get(0);
			prevCursor = temp;
		}

		long farthest = (long) Math.ceil(steps / 2);
		System.out.printf("Steps = %s, farthest = %s%n", steps, farthest);

		long expected = 6886;
		if (farthest != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", farthest, expected));
		}
	}

	private static PipeType getStartingPipe(Coordinates startLocation) {
		//figure out which 2 of the surrounding 4 sides have adjacent pipes (can't use getReachableAdjacentCells
		// method here because we don't yet know the pipe-symbol for this starting cell)
		boolean adjacentNorth = false, adjacentEast = false, adjacentSouth = false, adjacentWest = false;
		if (startLocation.y() > 0) {
			char c = grid[startLocation.y() - 1][startLocation.x()];
			adjacentNorth = Set.of('|', 'F', '7').contains(c);
		}
		if (startLocation.y() < grid.length - 1) {
			char c = grid[startLocation.y() + 1][startLocation.x()];
			adjacentSouth = Set.of('|', 'J', 'L').contains(c);
		}
		if (startLocation.x() > 0) {
			char c = grid[startLocation.y()][startLocation.x() - 1];
			adjacentWest = Set.of('-', 'F', 'L').contains(c);
		}
		if (startLocation.x() < grid[0].length - 1) {
			char c = grid[startLocation.y()][startLocation.x() + 1];
			adjacentEast = Set.of('-', 'J', '7').contains(c);
		}

		if (adjacentNorth && adjacentSouth)	return PipeType.VERTICAL;
		if (adjacentEast && adjacentWest) 	return PipeType.HORIZONTAL;
		if (adjacentNorth && adjacentEast) 	return PipeType.L;
		if (adjacentNorth && adjacentWest) 	return PipeType.J;
		if (adjacentSouth && adjacentEast) 	return PipeType.F;
		if (adjacentSouth && adjacentWest) 	return PipeType.S7;

		throw new RuntimeException("Unable to determine starting pipe");
	}

	private static List<Coordinates> getReachableAdjacentCells(Coordinates cursor) {
		char symbol = grid[cursor.y()][cursor.x()];
		PipeType pipeType = PipeType.getFromSymbol(symbol);
		switch (pipeType) {
			case VERTICAL -> {
				return List.of(new Coordinates(cursor.x(), cursor.y() - 1), new Coordinates(cursor.x(), cursor.y() + 1));
			}
			case HORIZONTAL -> {
				return List.of(new Coordinates(cursor.x() - 1, cursor.y()), new Coordinates(cursor.x() + 1, cursor.y()));
			}
			case F -> {
				return List.of(new Coordinates(cursor.x() + 1, cursor.y()), new Coordinates(cursor.x() , cursor.y() + 1));
			}
			case S7 -> {
				return List.of(new Coordinates(cursor.x() - 1, cursor.y()), new Coordinates(cursor.x(), cursor.y() + 1));
			}
			case J -> {
				return List.of(new Coordinates(cursor.x(), cursor.y() - 1), new Coordinates(cursor.x() - 1, cursor.y()));
			}
			case L -> {
				return List.of(new Coordinates(cursor.x(), cursor.y() - 1), new Coordinates(cursor.x() + 1, cursor.y()));
			}

			default -> throw new IllegalStateException("Unexpected value: " + cursor);
		}
	}
}

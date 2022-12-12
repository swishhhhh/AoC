package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Day12Part2 {

	static class Coordinates {
		int row;
		int col;
		char label;
		int stepsFromStartCnt;

		public Coordinates(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public String toString() {
			return "Coordinate{" +
					"row=" + row +
					", col=" + col +
					", label=" + label +
					", stepsFromStartCnt=" + stepsFromStartCnt +
					'}';
		}
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Coordinates that = (Coordinates) o;

			if (row != that.row) return false;
			return col == that.col;
		}
		@Override
		public int hashCode() {
			int result = row;
			result = 31 * result + col;
			return result;
		}
	}

	static char[][] grid;
	static boolean[][] visitedGrid;
//	static Coordinates startCoords;
	static Coordinates targetCoords;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day12_input.txt");

		//setup arrays
		String firstLine = lines.get(0);
		grid = new char[lines.size()][firstLine.length()];
		visitedGrid = new boolean[lines.size()][firstLine.length()];

		List<Coordinates> startCoordsA = new ArrayList<>();

		for (int row = 0; row < lines.size(); row++) {
			String line = lines.get(row);

			List<Character> list = Helper.charArrayToList(line.toCharArray());
			for (int col = 0; col < list.size(); col++) {
				char cell = list.get(col);

				switch (cell) {
					case 'S', 'a' -> {
						Coordinates c = new Coordinates(row, col);
						c.label = 'S';
						startCoordsA.add(c);
						grid[row][col] = 'a';
					}
					case 'E' -> {
						targetCoords = new Coordinates(row, col);
						targetCoords.label = 'E';
						grid[row][col] = 'z';
					}
					default -> grid[row][col] = cell;
				}
			}
		}

		//-----------
		int lowestCount = Integer.MAX_VALUE;
		for (Coordinates startCoords: startCoordsA) {
			int stepsFromStart = bfsSearchGrid(startCoords);
			if (stepsFromStart < lowestCount) {
				lowestCount = stepsFromStart;
			}
		}

		System.out.println("Lowest number of steps: " + lowestCount);
	}

	private static int bfsSearchGrid(Coordinates startCoords) {
		// Create a queue for BFS
		LinkedList<Coordinates> queue = new LinkedList<>();

		startCoords.stepsFromStartCnt = 0;
		visitedGrid = new boolean[grid.length][grid[0].length];
		visitedGrid[startCoords.row][startCoords.col] = true;
		queue.add(startCoords);

		while (queue.size() != 0) {
			Coordinates node = queue.poll();

			if (node.equals(targetCoords)) {
				System.out.println("Successful path found, node = " + node);
				return node.stepsFromStartCnt;
			}

			List<Coordinates> neighbors = getEligibleNeighbors(node);
			for (Coordinates neighbor: neighbors) {
				if (!visitedGrid[neighbor.row][neighbor.col]) {
					visitedGrid[neighbor.row][neighbor.col] = true;
					queue.add(neighbor);
				}
			}
		}

		return Integer.MAX_VALUE;
	}

	private static List<Coordinates> getEligibleNeighbors(Coordinates node) {
		List<Coordinates> neighbors = new ArrayList<>();
		char nodeValue = grid[node.row][node.col];

		if (!isTopRow(node)) { //North
			addNeighborIfEligible(node, neighbors, nodeValue, node.row - 1, node.col);
		}
		if (!isRightmostCol(node)) { //E
			addNeighborIfEligible(node, neighbors, nodeValue, node.row, node.col + 1);
		}
		if (!isBottomRow(node)) { //S
			addNeighborIfEligible(node, neighbors, nodeValue, node.row + 1, node.col);
		}
		if (!isLeftmostCol(node)) { //W
			addNeighborIfEligible(node, neighbors, nodeValue, node.row, node.col - 1);
		}

		return neighbors;
	}

	private static void addNeighborIfEligible(Coordinates parentNode, List<Coordinates> neighbors,
											  char nodeValue, int neighborRow, int neighborCol) {
		Coordinates neighbor = new Coordinates(neighborRow, neighborCol);
		neighbor.label = grid[neighborRow][neighborCol];
		neighbor.stepsFromStartCnt = parentNode.stepsFromStartCnt + 1;

		char neighborValue = grid[neighborRow][neighborCol];
		if (nodeValue - neighborValue >= -1) { //can be up to 1 unit lower than neighbor
			neighbors.add(neighbor);
		}
	}

	private static boolean isTopRow(Coordinates node) {
		return node.row == 0;
	}
	private static boolean isBottomRow(Coordinates node) {
		return node.row == grid.length - 1;
	}
	private static boolean isLeftmostCol(Coordinates node) {
		return node.col == 0;
	}
	private static boolean isRightmostCol(Coordinates node) {
		return node.col == grid[0].length - 1;
	}
}

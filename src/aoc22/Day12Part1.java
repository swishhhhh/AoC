package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day12Part1 {

	static class Coordinates {
		int row;
		int col;

		public Coordinates(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public String toString() {
			return "Coordinate{" +
					"row=" + row +
					", col=" + col +
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
	static Coordinates startCoords;
	static Coordinates targetCoords;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day12_input.txt");

		int ctr = 0;

		//setup arrays
		String firstLine = lines.get(0);
		grid = new char[lines.size()][firstLine.length()];
		visitedGrid = new boolean[lines.size()][firstLine.length()];

		for (int row = 0; row < lines.size(); row++) {
			String line = lines.get(row);

			List<Character> list = Helper.charArrayToList(line.toCharArray());
			for (int col = 0; col < list.size(); col++) {
				char cell = list.get(col);

				switch (cell) {
					case 'S' -> {
						startCoords = new Coordinates(row, col);
						grid[row][col] = 'a';
					}
					case 'E' -> {
						targetCoords = new Coordinates(row, col);
						grid[row][col] = 'z';
					}
					default -> {
						grid[row][col] = cell;
					}
				}
			}
		}

		//-----------
		ArrayList<Stack<Coordinates>> successfulPaths = new ArrayList<>();
		Stack<Coordinates> stack = new Stack();
//		stack.push(startCoords);
		visitedGrid[startCoords.row][startCoords.col] = true;
		traverseGrid(startCoords, stack, successfulPaths);

		//find shortest path
		Stack<Coordinates> shortestPath = null;
		for (Stack<Coordinates> path: successfulPaths) {
			if (shortestPath == null || path.size() < shortestPath.size()) {
				shortestPath = path;
			}
		}

		System.out.printf("Total = %s%n", shortestPath.size());
	}

	static boolean traverseGrid(Coordinates tailOfStack, Stack<Coordinates> currentPath,
								ArrayList<Stack<Coordinates>> successfulPaths) {
		currentPath.push(tailOfStack);
//		Coordinates tailOfStack = currentPath.peek();
//		int nodesForward = 0;
		//find all eligible neighbor nodes of tailOfStack
		List<Coordinates> neighbors = getEligibleNeighbors(tailOfStack, currentPath);

//		Stack<Coordinates> localCopyOfPath = (Stack<Coordinates>) currentPath.clone();

		if (neighbors.size() == 0) {
			currentPath.pop();
			return false; //terminating condition 1 = dead-end
		}

		for (Coordinates node: neighbors) {
			if (currentPath.contains(node)) {
				System.out.println("Houston we have a problem!");
			}
//			if (!currentPath.contains(node)) {
//				currentPath.push(node);
				if (node.equals(targetCoords)) {
					Stack<Coordinates> clone = (Stack<Coordinates>) currentPath.clone();
					successfulPaths.add(clone);
					System.out.println("Successful path found, size = " + clone.size());
					currentPath.pop(); //remove last node
					return true; //term condition 2 = new successful path found
				} else {
//					traverseGrid(currentPath, successfulPaths);
					traverseGrid(node, currentPath, successfulPaths);
//					currentPath.pop();
				}
//			}
		}

		currentPath.pop();
		return true; //terminating condition 3 = all possible paths traversed
	}

	private static List<Coordinates> getEligibleNeighbors(Coordinates node, Stack<Coordinates> currentPath) {
		List<Coordinates> neighbors = new ArrayList<>();
		Coordinates neighbor;

		char nodeValue = grid[node.row][node.col];

		if (!isTopRow(node)) { //North
			addNeighborIfEligble(node, neighbors, nodeValue, node.row - 1, node.col, currentPath);
		}
//		if (!isTopRow(node) && !isRightmostCol(node)) { //NE
//			addNeighborIfEligble(node, neighbors, nodeValue, node.row - 1, node.col + 1, currentPath);
//		}
		if (!isRightmostCol(node)) { //E
			addNeighborIfEligble(node, neighbors, nodeValue, node.row, node.col + 1, currentPath);
		}
//		if (!isBottomRow(node) && !isRightmostCol(node)) { //SE
//			addNeighborIfEligble(node, neighbors, nodeValue, node.row + 1, node.col + 1, currentPath);
//		}
		if (!isBottomRow(node)) { //S
			addNeighborIfEligble(node, neighbors, nodeValue, node.row + 1, node.col, currentPath);
		}
//		if (!isBottomRow(node) && !isLeftmostCol(node)) { //SW
//			addNeighborIfEligble(node, neighbors, nodeValue, node.row + 1, node.col - 1, currentPath);
//		}
		if (!isLeftmostCol(node)) { //W
			addNeighborIfEligble(node, neighbors, nodeValue, node.row, node.col - 1, currentPath);
		}
//		if (!isTopRow(node) && !isLeftmostCol(node)) { //NW
//			addNeighborIfEligble(node, neighbors, nodeValue, node.row - 1, node.col - 1, currentPath);
//		}

		return neighbors;
	}

	private static void addNeighborIfEligble(Coordinates node, List<Coordinates> neighbors,
											 char nodeValue, int neighborRow, int neighborCol,
											 Stack<Coordinates> currentPath) {
//		if (visitedGrid[neighborRow][neighborCol]) { //already visited
//			return;
//		}
		Coordinates neighbor = new Coordinates(neighborRow, neighborCol);
		if (currentPath.contains(neighbor)) {
			return;
		}

		char neighborValue = grid[neighborRow][neighborCol];
		if (nodeValue - neighborValue >= -1) { //can be up to 1 unit lower than neighbor
			neighbors.add(neighbor);
//			visitedGrid[neighborRow][neighborCol] = true;
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

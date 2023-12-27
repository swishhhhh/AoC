package aoc23;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static utils.GridUtils.getNeighboringCells;
import static utils.GridUtils.printGrid;

/**
 * <a href="https://adventofcode.com/2023/day/23">Advent of Code 2023 Day 23</a>
 */
public class Day23Part1 {

	static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day23_input.txt");

		char[][] grid = new char[lines.size()][];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		long answer = longestPath(grid);

		if (DEBUG) {
			printGrid(grid);
		}

		System.out.printf("Answer = %s%n", answer);

		long expected = 1930;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static long longestPath(char[][] grid) {
		Coordinates start = getSource(grid);
		Coordinates end = getTarget(grid);
		List<Coordinates> path = new ArrayList<>();

		path.add(start);
		List<Coordinates> longestPath = dfs(path, end, grid, new ArrayList<>());

		overlayPathOnGrid(grid, longestPath);

		return longestPath.size() - 1;
	}

	private static List<Coordinates> dfs(List<Coordinates> path, Coordinates end, char[][] grid,
										 List<Coordinates> longestPathSoFar) {
		Coordinates cursor = path.get(path.size() - 1);
		if (cursor.equals(end)) {
			return path.size() > longestPathSoFar.size() ? new ArrayList<>(path) : longestPathSoFar;
		}

		List<Coordinates> neighbors = getEligibleNeighbors(grid, cursor, path);
		for (Coordinates neighbor : neighbors) {
			path.add(neighbor);
			longestPathSoFar = dfs(path, end, grid, longestPathSoFar);
			path.remove(path.size() - 1);
		}

		return longestPathSoFar;
	}

	private static List<Coordinates> getEligibleNeighbors(char[][] grid, Coordinates cursor, List<Coordinates> path) {
		List<Coordinates> neighbors = getNeighboringCells(grid, cursor);
		neighbors = neighbors.stream().filter(neighbor -> {
			if (grid[cursor.y()][cursor.x()] == '>' && neighbor.x() <= cursor.x()) {
				return false;
			}
			if (grid[cursor.y()][cursor.x()] == 'v' && neighbor.y() <= cursor.y()) {
				return false;
			}

			char c = grid[neighbor.y()][neighbor.x()];
			if (c == '#') {
				return false;
			}

			//check if neighbor is already in path
			return !path.contains(neighbor);

		}).toList();

		return neighbors;
	}

	private static Coordinates getSource(char[][] grid) {
		//starting point is the first dot on the top row of the grid
		for (int i = 0; i < grid[0].length; i++) {
			if (grid[0][i] == '.') {
				return new Coordinates(i, 0);
			}
		}

		throw new RuntimeException("Unable to find starting point");
	}

	private static Coordinates getTarget(char[][] grid) {
		//ending point is the first dot on the last row of the grid
		int lastRow = grid.length - 1;
		for (int i = 0; i < grid[lastRow].length; i++) {
			if (grid[lastRow][i] == '.') {
				return new Coordinates(i, lastRow);
			}
		}

		throw new RuntimeException("Unable to find ending point");
	}

	private static void overlayPathOnGrid(char[][] grid, List<Coordinates> longestPath) {
		for (int i = 0; i < longestPath.size(); i++) {
			Coordinates step = longestPath.get(i);
			if (i == 0) { //starting position
				grid[step.y()][step.x()] = 'S';
				continue;
			}

			Coordinates prevStep = longestPath.get(i - 1);
			char c = '?';
			if      (prevStep.x() < step.x()) c = '>';
			else if (prevStep.x() > step.x()) c = '<';
			else if (prevStep.y() < step.y()) c = 'v';
			else if (prevStep.y() > step.y()) c = '^';
			grid[step.y()][step.x()] = c;
		}
	}
}

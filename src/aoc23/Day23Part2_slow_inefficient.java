package aoc23;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.*;

import static utils.GridUtils.getNeighboringCells;
import static utils.GridUtils.printGrid;

/**
 * <a href="https://adventofcode.com/2023/day/23">Advent of Code 2023 Day 23</a>
 * <P>
 * This was the first attempt at part2 which works correctly but is waaaayyy too slow (3+ hours). It also requires
 * increasing the jvm's thread stack size to almost 3M (-Xss3M), so definitely not ideal. Nonetheless, leaving this
 * solution here for posterity. See alternative (and much faster) graph based approach in ../Day23Part2.
 */
public class Day23Part2_slow_inefficient {
	static final boolean DEBUG = false;
	static long loopCtr = 0;

	private static final Map<Coordinates, List<Coordinates>> corridorsCache = new HashMap<>();

	public static void xmain(String[] args) throws Exception {
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

		long expected = 6230;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static long longestPath(char[][] grid) {
		Coordinates start = getSource(grid);
		Coordinates end = getTarget(grid);
		List<Coordinates> path = new ArrayList<>();
		Set<Coordinates> deadEnds = new HashSet<>();

		path.add(start);
		List<Coordinates> longestPath = dfs(path, end, grid, new ArrayList<>(), deadEnds);

		overlayPathOnGrid(grid, longestPath, deadEnds);

		return longestPath.size() - 1;
	}

	private static List<Coordinates> dfs(List<Coordinates> path, Coordinates end, char[][] grid,
								List<Coordinates> longestPathSoFar, Set<Coordinates> deadEnds){
		loopCtr++;
		if (DEBUG && loopCtr % 100_000 == 0) {
			System.out.printf("loopCtr (in 100K) = %s, longest path = %s, corridor cache size = %s, deadends = %s%n",
					loopCtr / 100_000, longestPathSoFar.size() -1, corridorsCache.size(), deadEnds.size());
		}

		Coordinates cursor = path.get(path.size() - 1);

		List<Coordinates> corridor = corridorsCache.get(cursor);
		if (corridor != null) {
			path.remove(cursor); //about to add it again as part of the corridor (head)
			path.addAll(corridor);
			cursor = path.get(path.size() - 1);
		}

		if (cursor.equals(end)) {
			return path.size() > longestPathSoFar.size() ? new ArrayList<>(path) : longestPathSoFar;
		}

		List<Coordinates> neighbors = getEligibleNeighbors(grid, cursor, path, deadEnds);

		//corridor building logic
		buildCorridorIfAppropriate(grid, path, neighbors);

		for (Coordinates neighbor : neighbors) {
			//see if neighbor is an already explored corridor, and if so, jump to the tail of the corridor
			path.add(neighbor);
			longestPathSoFar = dfs(new ArrayList<>(path), end, grid, longestPathSoFar, deadEnds);

			path.remove(neighbor);
		}

		//dead-ends logic (sadly a wasted effort since our puzzle input doesn't contain any dead-ends)
		if (neighbors.size() == 0 && !cursor.equals(end)) {
			//confirm the reason we have no neighbors is that we hit a wall (and not a loop-back to previous part of the path)
			if (getAllNeighborsExcludingWalls(grid, cursor).size() < 2) {
				updateDeadEnds(path, grid, deadEnds);
			}
		}
		
		return longestPathSoFar;
	}

	private static void buildCorridorIfAppropriate(char[][] grid, List<Coordinates> path, List<Coordinates> eligibleNeighbors) {
		if (eligibleNeighbors.size() <= 1) {
			return;
		}

		//backtrack from 2nd to last node in the path until you find another junction, in between the 2 junctions is a corridor
		List<Coordinates> corridor = new ArrayList<>();
		Coordinates tail = path.get(path.size() - 2); //-1 is the cursor (junction)
		for (int i = path.size() - 2; i >= 0; i--) {
			Coordinates candidate = path.get(i);
			if (getAllNeighborsExcludingWalls(grid, candidate).size() > 2) {
				break;
			}
			corridor.add(candidate);
		}
		corridorsCache.put(tail, corridor);

		corridor = new ArrayList<>(corridor);
		Collections.reverse(corridor);
		Coordinates head = corridor.get(0);
		corridorsCache.put(head, corridor);
	}

	private static void updateDeadEnds(List<Coordinates> path, char[][] grid, Set<Coordinates> deadEnds) {
		//backtrack from end of path and update each step as a deadEnd, stop when you reach a node with more than 2 neighbors
		for (int i = path.size() - 1; i >= 0; i--) {
			Coordinates traceBackStep = path.get(i);
			List<Coordinates> neighbors = getAllNeighborsExcludingWalls(grid, traceBackStep);

			//exclude dead-ends
			neighbors = neighbors.stream().filter(n -> !deadEnds.contains(n)).toList();

			if (neighbors.size() > 2) {
				break;
			}
			deadEnds.add(traceBackStep);
		}
	}

	private static List<Coordinates> getEligibleNeighbors(char[][] grid, Coordinates cursor, List<Coordinates> path,
														  Set<Coordinates> deadEnds) {
		List<Coordinates> neighbors = getAllNeighborsExcludingWalls(grid, cursor);
		neighbors = neighbors.stream().filter(neighbor -> {

			//check if neighbor is already in path
			if (path.contains(neighbor)) {
				return false;
			}

			return !deadEnds.contains(neighbor);
		}).toList();

		return neighbors;
	}

	private static List<Coordinates> getAllNeighborsExcludingWalls(char[][] grid, Coordinates cursor) {
		return getNeighboringCells(grid, cursor).stream().filter(n -> grid[n.y()][n.x()] != '#').toList();
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

	private static void overlayPathOnGrid(char[][] grid, List<Coordinates> longestPath, Set<Coordinates> deadEnds) {
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
			if (corridorsCache.containsKey(step)) c = 'K';
			grid[step.y()][step.x()] = c; //'O';
		}

		for (Coordinates c : deadEnds) {
			grid[c.y()][c.x()] = 'D'; 
		}
	}
}

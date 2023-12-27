package aoc23;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/21">Advent of Code 2023 Day 21</a>
 */
public class Day21Part1 {
	static class Step {
		Coordinates coord;
		long stepCount;

		public Step(Coordinates coord, long stepCount) {
			this.coord = coord;
			this.stepCount = stepCount;
		}

		@Override
		public String toString() {
			return "Step{" +
					"coord=" + coord +
					", stepCount=" + stepCount +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Step step)) return false;

			if (stepCount != step.stepCount) return false;
			return coord.equals(step.coord);
		}
		@Override
		public int hashCode() {
			int result = coord.hashCode();
			result = 31 * result + (int) (stepCount ^ (stepCount >>> 32));
			return result;
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day21_input.txt");

		char[][] grid = new char[lines.size()][];
		Coordinates start = null;
		for (int row = 0; row < grid.length; row++) {
			grid[row] = lines.get(row).toCharArray();
			for (int col = 0; col < grid[row].length; col++) {
				if (grid[row][col] == 'S') {
					start = new Coordinates(col, row);
				}
			}
		}

		final boolean DEBUG = false;
		final long MAX_STEPS = 64;
		long count = process(grid, start, MAX_STEPS);

		if (DEBUG) {
			printGrid(grid);
		}

		System.out.printf("Count = %s%n", count);

		long expected = 3746;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long process(char[][] grid, Coordinates start, long maxSteps) {
		Step step = new Step(start, 0);
		Queue<Step> queue = new LinkedBlockingQueue<>();
		queue.add(step);

		while (!queue.isEmpty()) {
			step = queue.poll();

			if (step.stepCount > maxSteps) {
				continue;
			}

			char c = grid[step.coord.y()][step.coord.x()];
			if (c != '.' && c != 'S') {
				continue; //already visited
			}

			//mark grid for this cell (if not already marked)
			if (c != 'S') {
				String marking;

				if (step.stepCount < 10) { //1 digit number
					marking = String.valueOf(step.stepCount);
				} else { //2+ digit number
					marking = step.stepCount % 2 == 0 ? "E" : "O"; //odd or even
				}
				grid[step.coord.y()][step.coord.x()] = marking.charAt(0);
			}

			List<Coordinates> neighbors = getNeighboringCells(grid, step.coord);
			for (Coordinates neighbor : neighbors) {
				c = grid[neighbor.y()][neighbor.x()];
				if (c != '.') {
					continue;
				}
				queue.add(new Step(neighbor, step.stepCount + 1));
			}
		}

		//count all the cells marked even (either numerically or with 'E'), add 1 for starting cell
		long count = 1; //for starting cell (still marked 'S')
		for (char[] row : grid) {
			for (char c : row) {
				if (c == 'E') {
					count++;
				} else if (c >= '1' && c <= '9') {
					long value = Integer.parseInt(String.valueOf(c));
					if (value % 2 == 0) {
						count++;
					}
				}
			}
		}

		return count;
	}

}

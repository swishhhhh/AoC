package aoc23;

import aoc23.datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/21">Advent of Code 2023 Day 21</a>
 */
public class Day21Part2 {
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

		char[][] originalGrid = new char[lines.size()][];
		int cursorRow = 0, cursorCol = 0;
		for (int row = 0; row < originalGrid.length; row++) {
			originalGrid[row] = lines.get(row).toCharArray();
			for (int col = 0; col < originalGrid[row].length; col++) {
				if (originalGrid[row][col] == 'S') {
					originalGrid[row][col] = '.';
					cursorRow = row;
					cursorCol = col;
				}
			}
		}

		long count = process(originalGrid, cursorRow, cursorCol);
		System.out.printf("Count = %s%n", count);

		long expected = 623540829615589L;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long process(char[][] originalGrid, int originalCursorRow, int originalCursorCol) {
		//warm up for 1 half cycle + 2 full cycles to let the increment stabilize, then you can multiply it out

		long targetSteps = 26501365;
		long fullCycleSteps = originalGrid.length;
		long halfCycleSteps = (fullCycleSteps - 1) / 2;

		//half-cycle warmup
		char[][] clonedGrid = cloneGridInEachDirection(originalGrid, 0); //just make a plain copy
		long steps = halfCycleSteps;
		Coordinates start = new Coordinates(originalCursorCol, originalCursorRow);
		long warmupHalfCycleCnt = process(clonedGrid, start, steps);

		//1st full cycle warmup
		int multiples = 1;
		start = new Coordinates((multiples * originalGrid[0].length) + originalCursorCol,
				(multiples * originalGrid.length) + originalCursorRow);
		clonedGrid = cloneGridInEachDirection(originalGrid, multiples);
		steps = fullCycleSteps + halfCycleSteps;
		long afterFullCycleAndWarmupCount = process(clonedGrid, start, steps);
		long firstCycleIncrement = afterFullCycleAndWarmupCount - warmupHalfCycleCnt;

		//2nd full cycle warmup
		multiples = 2;
		clonedGrid = cloneGridInEachDirection(originalGrid, multiples);
		start = new Coordinates((multiples * originalGrid[0].length) + originalCursorCol,
				(multiples * originalGrid.length) + originalCursorRow);
		steps = (multiples*fullCycleSteps) + halfCycleSteps;
		long after2ndFullCycleCnt = process(clonedGrid, start, steps);
		long secondCycleIncrement = after2ndFullCycleCnt - afterFullCycleAndWarmupCount;


		long stableCycleIncrDiff = secondCycleIncrement - firstCycleIncrement;
		long remainingSteps = targetSteps - steps;
		long remainingCycles = remainingSteps / fullCycleSteps;

		long totalCnt = after2ndFullCycleCnt;
		long cycleIncrement = secondCycleIncrement;
		for (long i = 0; i < remainingCycles; i++) {
			cycleIncrement = cycleIncrement + stableCycleIncrDiff;
			totalCnt+= cycleIncrement;
		}
		return totalCnt;
	}

	private static long process(char[][] grid, Coordinates start, long maxSteps) {
		boolean countOdds = maxSteps % 2 == 1;
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

		//count all the cells marked even or odd (either numerically or with 'E' or 'O')
		long count = 0;
		if (!countOdds) {
			count++; //count starting cell if evens
		}
		for (char[] row : grid) {
			for (char c : row) {
				if (!countOdds && c == 'E') {
					count++;
				} else if (countOdds && c == 'O') {
					count++;
				} else if (c >= '1' && c <= '9') {
					long value = Integer.parseInt(String.valueOf(c));
					if (countOdds && value % 2 != 0) {
						count++;
					} else if (!countOdds && value % 2 == 0) {
						count++;
					}
				}
			}
		}

		return count;
	}
}

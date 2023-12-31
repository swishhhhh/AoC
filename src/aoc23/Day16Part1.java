package aoc23;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/16">Advent of Code 2023 Day 16</a>
 */
public class Day16Part1 {
	static class GridStep {
		Coordinates coord;
		Direction direction;

		public GridStep(Coordinates coord, Direction direction) {
			this.coord = coord;
			this.direction = direction;
		}

		public String getSignature() {
			return coord.x() + "," + coord.y() + "," + direction.getSymbol();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			GridStep gridStep = (GridStep) o;

			if (!coord.equals(gridStep.coord)) return false;
			return direction == gridStep.direction;
		}

		@Override
		public int hashCode() {
			int result = coord.hashCode();
			result = 31 * result + direction.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "GridStep{" +
					"coord=" + coord +
					", direction=" + direction +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day16_input.txt");

		char[][] grid = new char[lines.size()][];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		long count = getEnergizedTileCount(grid, new GridStep(new Coordinates(0, 0), Direction.EAST));
		System.out.printf("Energized tiles count: %s%n", count);

		long expected = 7307;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long getEnergizedTileCount(char[][] grid, GridStep startingStep) {
		char[][] energizedGrid = new char[grid.length][grid[0].length];
		Set<String> visitedCache = new HashSet<>();
		Queue<GridStep> queue = new LinkedBlockingQueue<>();
		queue.add(startingStep);

		while (!queue.isEmpty()) {
			GridStep step = queue.poll();

			while (true) {
				if (outOfBounds(step.coord, grid) || visitedCache.contains(step.getSignature())) {
					break;
				}

				//mark current tile as energized and cache it
				energizedGrid[step.coord.y()][step.coord.x()] = 'y';
				visitedCache.add(step.getSignature());

				char c = grid[step.coord.y()][step.coord.x()];
				switch (c) {
					case '.' -> step = getNextStepSameDirection(step);

					case '|' -> {
						if (step.direction == Direction.NORTH || step.direction == Direction.SOUTH) {
							//traveling in vertical direction, continue on..
							step = getNextStepSameDirection(step);
						} else {
							//traveling in horizontal direction, split beam into north and south, enqueue south and continue north
							queue.add(new GridStep(new Coordinates(step.coord.x(), step.coord.y() + 1), Direction.SOUTH));
							step = new GridStep(new Coordinates(step.coord.x(), step.coord.y() - 1), Direction.NORTH);
						}
					}
					case '-' -> {
						if (step.direction == Direction.EAST || step.direction == Direction.WEST) {
							//traveling in horizontal direction, continue on..
							step = getNextStepSameDirection(step);
						} else {
							//traveling in vertical direction, split beam into east and west, enqueue west and continue east
							queue.add(new GridStep(new Coordinates(step.coord.x() - 1, step.coord.y()), Direction.WEST));
							step = new GridStep(new Coordinates(step.coord.x() + 1, step.coord.y()), Direction.EAST);
						}
					}
					case '/' -> {
						switch (step.direction) {
							case NORTH ->
								step = new GridStep(new Coordinates(step.coord.x() + 1, step.coord.y()), Direction.EAST);
							case SOUTH ->
								step = new GridStep(new Coordinates(step.coord.x() - 1, step.coord.y()), Direction.WEST);
							case EAST ->
								step = new GridStep(new Coordinates(step.coord.x(), step.coord.y() - 1), Direction.NORTH);
							case WEST ->
								step = new GridStep(new Coordinates(step.coord.x(), step.coord.y() + 1), Direction.SOUTH);
						}
					}
					case '\\' -> {
						switch (step.direction) {
							case NORTH ->
									step = new GridStep(new Coordinates(step.coord.x() - 1, step.coord.y()), Direction.WEST);
							case SOUTH ->
									step = new GridStep(new Coordinates(step.coord.x() + 1, step.coord.y()), Direction.EAST);
							case EAST ->
									step = new GridStep(new Coordinates(step.coord.x(), step.coord.y() + 1), Direction.SOUTH);
							case WEST ->
									step = new GridStep(new Coordinates(step.coord.x(), step.coord.y() - 1), Direction.NORTH);
						}
					}
				}
			}
		}

		long count = 0;
		for (char[] row : energizedGrid) {
			for (char c : row) {
				if (c == 'y') {
					count++;
				}
			}

		}
		return count;
	}

	private static GridStep getNextStepSameDirection(GridStep step) {
		return new GridStep(getNextCoord(step.coord, step.direction), step.direction);
	}

	private static boolean outOfBounds(Coordinates coords, char[][] grid) {
		return coords.x() < 0 || coords.x() >= grid[0].length
			|| coords.y() < 0 || coords.y() >= grid.length;
	}
}

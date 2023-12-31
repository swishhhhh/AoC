package aoc23;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static datastructs.Direction.*;
import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/17">Advent of Code 2023 Day 17</a>
 */
public class Day17Part2 {
	static class State {
		Coordinates coords;
		Direction direction;
		int numStepsInDirection;

		public State(Coordinates coords, Direction direction, int numStepsInDirection) {
			this.coords = coords;
			this.direction = direction;
			this.numStepsInDirection = numStepsInDirection;
		}

		@Override
		public String toString() {
			return "State{" +
					"coords=" + coords +
					", direction=" + direction +
					", numStepsInDirection=" + numStepsInDirection +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof State)) return false;

			State state = (State) o;

			if (numStepsInDirection != state.numStepsInDirection) return false;
			if (!coords.equals(state.coords)) return false;
			return direction == state.direction;
		}
		@Override
		public int hashCode() {
			int result = coords.hashCode();
			result = 31 * result + direction.hashCode();
			result = 31 * result + numStepsInDirection;
			return result;
		}
	}

	static class QueueItem extends State {
		long cost;

		public QueueItem(Coordinates coords, Direction direction, int numStepsInDirection, long cost) {
			super(coords, direction, numStepsInDirection);
			this.cost = cost;
		}

		public long getCost() {
			return cost;
		}

		@Override
		public String toString() {
			return "QueueItem{" +
					"cost=" + cost +
					", coords=" + coords +
					", direction=" + direction +
					", numStepsInDirection=" + numStepsInDirection +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof QueueItem)) return false;
			if (!super.equals(o)) return false;

			QueueItem queueItem = (QueueItem) o;

			return cost == queueItem.cost;
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (int) (cost ^ (cost >>> 32));
			return result;
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day17_input.txt");

		char[][] grid = new char[lines.size()][];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		long shortestPath = getShortestPath(grid);
		System.out.printf("Shortest path: %s%n", shortestPath);

		long expected = 1149;
		if (shortestPath != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", shortestPath, expected));
		}
	}

	private static long getShortestPath(char[][] grid) {
		Coordinates start = new Coordinates(0, 0);
		Coordinates end = new Coordinates(grid[0].length - 1, grid.length - 1);

		PriorityQueue<QueueItem> queue =
				new PriorityQueue<>(Comparator.comparingLong(QueueItem::getCost)); //lowest cost with highest priority
		Map<State, Long> statesToCostMap = new HashMap<>();

		queue.add(new QueueItem(start, NW, -1, 0)); //choose a direction not in the 4 directions we'll travel

		Direction[] directions = new Direction[]{NORTH, SOUTH, EAST, WEST};

		while (!queue.isEmpty()) {
			QueueItem item = queue.poll();
			State state = new State(item.coords, item.direction, item.numStepsInDirection);
			if (statesToCostMap.containsKey(state)) {
				//priority queue should ensure that the lowest cost is the one retrieved first from queue
				assert statesToCostMap.get(state) < item.cost;
				continue;
			}

			statesToCostMap.put(state, item.cost);

			for (Direction nextDir : directions) {
				Coordinates nextCoord = getNextCoord(item.coords, nextDir);

				if (outOfBounds(nextCoord, grid)) {
					continue;
				}

				if (nextDir.isReverseOf(item.direction)) { //can't reverse directions
					continue;
				}

				int nextNumStepsInDirection = nextDir.equals(item.direction) ? item.numStepsInDirection + 1 : 1;
				if (nextNumStepsInDirection > 10) {
					continue;
				}

				//changes in direction can only happen after 4 moves
				if (!item.coords.equals(start) && !nextDir.equals(item.direction) && item.numStepsInDirection < 4) {
					continue;
				}

				int costIncrement = Integer.parseInt("" + grid[nextCoord.y()][nextCoord.x()]);
				queue.add(new QueueItem(nextCoord, nextDir, nextNumStepsInDirection, item.cost + costIncrement));
			}
		}

		//get shortest direction from map
		AtomicLong shortestPath = new AtomicLong(Long.MAX_VALUE);
		statesToCostMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().coords.equals(end))
				.forEach(entry -> shortestPath.set(Math.min(shortestPath.get(), entry.getValue())));

		return shortestPath.get();
	}

	private static boolean outOfBounds(Coordinates coords, char[][] grid) {
		return coords.x() < 0 || coords.x() >= grid[0].length
				|| coords.y() < 0 || coords.y() >= grid.length;
	}
}

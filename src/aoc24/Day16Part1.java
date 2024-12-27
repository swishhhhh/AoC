package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static datastructs.Direction.*;
import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2024/day/16">Advent of Code 2024 Day 16</a>
 */
public class Day16Part1 {
    static class State {
        Coordinates coords;
        Direction direction;
        List<Coordinates> path = new ArrayList<>();

        public State(Coordinates coords, Direction direction) {
            this.coords = coords;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "State{" +
                    "coords=" + coords +
                    ", direction=" + direction +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            if (!coords.equals(state.coords)) return false;
            return direction == state.direction;
        }
        @Override
        public int hashCode() {
            int result = coords.hashCode();
            result = 31 * result + direction.hashCode();
            return result;
        }
    }

    static class QueueItem extends State {
        long cost;

        public QueueItem(Coordinates coords, Direction direction, long cost) {
            super(coords, direction);
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
                    '}';
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QueueItem queueItem)) return false;
            if (!super.equals(o)) return false;

            return cost == queueItem.cost;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + Long.hashCode(cost);
            return result;
        }
    }

    private static final boolean DEBUG = false;


    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day16_input.txt");

        long answer = new Day16Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 102460;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        return getShortestPath(grid);
    }

    private static long getShortestPath(char[][] grid) {
        Coordinates start = getStartCoord(grid);
        Coordinates end = getEndCoord(grid);

        PriorityQueue<QueueItem> queue =
                new PriorityQueue<>(Comparator.comparingLong(QueueItem::getCost)); //lowest cost with highest priority
        Map<State, Long> statesToCostMap = new HashMap<>();

        queue.add(new QueueItem(start, EAST, 0)); //cursor starts out facing east

        Direction[] directions = new Direction[]{NORTH, SOUTH, EAST, WEST};

        while (!queue.isEmpty()) {
            QueueItem item = queue.poll();
            State state = new State(item.coords, item.direction);
            if (DEBUG) {
                state.path.addAll(item.path);
            }
            if (statesToCostMap.containsKey(state)) {
                //priority queue should ensure that the lowest cost is the one retrieved first from queue
                assert statesToCostMap.get(state) < item.cost;
                continue;
            }

            statesToCostMap.put(state, item.cost);

            for (Direction nextDir : directions) {
                if (nextDir.isReverseOf(item.direction)) {
                    continue; //no point in turning around 180 degrees
                }

                Coordinates nextCoord = getNextCoord(item.coords, nextDir);
                int nx = nextCoord.x(), ny = nextCoord.y();

                if (GridUtils.isCellOutOfBounds(grid, nx, ny) || grid[ny][nx] == '#') {
                    continue;
                }

                int costIncrement = nextDir.equals(item.direction) ? 1 : 1001; //turning costs an additional 1000 points
                QueueItem nextItem = new QueueItem(nextCoord, nextDir, item.cost + costIncrement);
                if (DEBUG) {
                    nextItem.path.addAll(item.path);
                    nextItem.path.add(nextCoord);
                }
                queue.add(nextItem);
            }
        }

        if (DEBUG) {
            List<State> pathsToTarget = statesToCostMap.keySet()
                    .stream()
                    .filter(state -> state.coords.equals(end))
                    .toList();
            printPath(grid, pathsToTarget.get(0));
        }

        //get shortest direction from map
        AtomicLong shortestPath = new AtomicLong(Long.MAX_VALUE);
        statesToCostMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().coords.equals(end))
                .forEach(entry -> shortestPath.set(Math.min(shortestPath.get(), entry.getValue())));

        return shortestPath.get();
    }

    private static Coordinates getStartCoord(char[][] grid) {
        return getFirstCoordinateWithValue(grid,  'S');
    }

    private static Coordinates getEndCoord(char[][] grid) {
        return getFirstCoordinateWithValue(grid,  'E');
    }

    private static void printPath(char[][] originalGrid, State state) {
        char[][] grid = GridUtils.deepCopyGrid(originalGrid);
        for (Coordinates coord : state.path) {
            if (grid[coord.y()][coord.x()] == '.') {
                grid[coord.y()][coord.x()] = '+';
            }
        }
        GridUtils.printGrid(grid, false);
    }
}
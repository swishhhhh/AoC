package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

import static datastructs.Direction.*;
import static utils.GridUtils.getNextCoord;

/**
 * <a href="https://adventofcode.com/2024/day/16">Advent of Code 2024 Day 16</a>
 */
public class Day16Part2 {
    static class State {
        Coordinates coords;
        Direction direction;
        List<Coordinates> path;

        public State(Coordinates coords, Direction direction, List<Coordinates> path) {
            this.coords = coords;
            this.direction = direction;
            this.path = path;
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

        public QueueItem(Coordinates coords, Direction direction, long cost, List<Coordinates> path) {
            super(coords, direction, path);
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

        long answer = new Day16Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 527;
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

        queue.add(new QueueItem(start, EAST, 0, List.of(start))); //cursor starts out facing east

        Direction[] directions = new Direction[]{NORTH, SOUTH, EAST, WEST};

        long shortestPathToTarget = Long.MAX_VALUE;
        Set<Coordinates> bestTilesToTarget = new HashSet<>();


        while (!queue.isEmpty()) {
            QueueItem item = queue.poll();
            State state = new State(item.coords, item.direction, new ArrayList<>(item.path));

            Long lowestCostToThisStateSoFar = statesToCostMap.get(state);
            if (lowestCostToThisStateSoFar != null && lowestCostToThisStateSoFar < item.cost) {
                continue; //!! note that if lowestCostSoFar == item.cost we do keep going (add to queue) because
                          //   we need to track ALL shortest paths (not just the first shortest path encountered)
            }

            //check if this is the lowest cost so far for this state (a single tile can have multiple states)
            if (lowestCostToThisStateSoFar == null || item.cost < lowestCostToThisStateSoFar) {
                statesToCostMap.put(state, item.cost);
            }

            //check if end tile was reached and if so whether this is the shortest path (for all states in the end tile) so far
            if (item.coords.equals(end)) {
                if (item.cost < shortestPathToTarget) {
                    shortestPathToTarget = item.cost;
                    bestTilesToTarget = new HashSet<>(item.path);
                } else if (item.cost == shortestPathToTarget) {
                    bestTilesToTarget.addAll(item.path);
                }

                //end reached, no need to continue
                continue;
            }

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
                List<Coordinates> newPath = new ArrayList<>(item.path);
                newPath.add(nextCoord);
                queue.add(new QueueItem(nextCoord, nextDir, item.cost + costIncrement, newPath));
            }
        }

        if (DEBUG) {
            printBestTilesToTarget(grid, bestTilesToTarget);
        }

        return bestTilesToTarget.size();
    }

    private static Coordinates getStartCoord(char[][] grid) {
        return getFirstCoordinateWithValue(grid,  'S');
    }

    private static Coordinates getEndCoord(char[][] grid) {
        return getFirstCoordinateWithValue(grid,  'E');
    }

    private static Coordinates getFirstCoordinateWithValue(char[][] grid, char s) {
        //return first coordinate in grid with value s
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == s) {
                    return new Coordinates(col, row);
                }
            }
        }
        throw new RuntimeException("No coordinate found with value " + s);
    }

    private static void printBestTilesToTarget(char[][] originalGrid, Set<Coordinates> bestTilesToTarget) {
        char[][] grid = GridUtils.deepCopyGrid(originalGrid);
        for (Coordinates coord : bestTilesToTarget) {
            if (grid[coord.y()][coord.x()] == '.') {
                grid[coord.y()][coord.x()] = 'O';
            }
        }
        GridUtils.printGrid(grid, false);
    }
}
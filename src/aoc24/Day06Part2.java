package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import org.apache.commons.math3.util.Pair;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2024/day/6">Advent of Code 2024 Day 6</a>
 */
public class Day06Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day6_input.txt");

        long answer = new Day06Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1663;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long startMillis = System.currentTimeMillis();
        char[][] grid = GridUtils.loadGrid(lines);

        //find cell with ^ indicating starting position of the guard
        final Coordinates cursor = getStartingCursor(grid);

        Set<Pair<Coordinates, Direction>> basePath = getBaseGuardPath(grid, cursor);

        //only check cells that are next steps to any of the cells in the basePath
        Set<Coordinates> cellsToCheck = addCellsToCheck(grid, basePath);
        cellsToCheck.removeIf(c -> c.x() == cursor.x() && c.y() == cursor.y()); //don't add obstacle to the starting position
        cellsToCheck.removeIf(c -> grid[c.y()][c.x()] == '#'); //don't add where there's already an obstacle

        if (DEBUG) {
            System.out.printf("Base-path size = %s, cells-to-check size = %s, grid size = %s%n",
                    basePath.size(), cellsToCheck.size(), grid.length * grid[0].length);
        }

        //parallelize the processing
        long count = cellsToCheck.parallelStream()
                .map(cell -> {
                    char[][] gridCopy = GridUtils.deepCopyGrid(grid);
                    gridCopy[cell.y()][cell.x()] = '#';
                    return isPathAnEndlessLoop(gridCopy, cursor) ? 1L : 0L;
                })
                .reduce(0L, Long::sum);

        if (DEBUG) {
            System.out.printf("Total time in millis: %s%n", System.currentTimeMillis() - startMillis);
        }

        return count;
    }

    private static Coordinates getStartingCursor(char[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '^') {
                    return new Coordinates(col, row);
                }
            }
        }

        throw new RuntimeException("Starting cursor not found");
    }

    private Set<Pair<Coordinates, Direction>> getBaseGuardPath(char[][] grid, Coordinates cursor) {
        Set<Pair<Coordinates, Direction>> path = new HashSet<>();

        Direction direction = Direction.NORTH;

        while (true) {
            path.add(new Pair<>(cursor, direction));

            Coordinates next = GridUtils.getNextCoord(cursor, direction);
            if (GridUtils.isCellOutOfBounds(grid, next.x(), next.y())) {
                break;
            }

            char nextChar = grid[next.y()][next.x()];
            if (nextChar == '#') {
                //rotate direction 90 degrees to the right
                switch (direction) {
                    case NORTH -> direction = Direction.EAST;
                    case SOUTH -> direction = Direction.WEST;
                    case WEST  -> direction = Direction.NORTH;
                    case EAST  -> direction = Direction.SOUTH;
                }
                continue;
            }

            cursor = next;
        }

        return path;
    }

    private Set<Coordinates> addCellsToCheck(char[][] grid, Set<Pair<Coordinates, Direction>> basePath) {
        //add cells that are "next" steps to any of the cells in the path
        Set<Coordinates> cellsToCheck = new HashSet<>();
        for (Pair<Coordinates, Direction> pathElement : basePath) {
            Coordinates next = GridUtils.getNextCoord(pathElement.getFirst(), pathElement.getSecond());
            if (!GridUtils.isCellOutOfBounds(grid, next.x(), next.y())) {
                cellsToCheck.add(next);
            }
        }
        return cellsToCheck;
    }

    private boolean isPathAnEndlessLoop(char[][] grid, Coordinates cursor) {
        //figure out if guard will be moving endlessly in a loop
        Set<String> visited = new HashSet<>();

        Direction direction = Direction.NORTH;
        while (true) {
            Coordinates next = GridUtils.getNextCoord(cursor, direction);
            if (GridUtils.isCellOutOfBounds(grid, next.x(), next.y())) {
                break;
            }

            char nextChar = grid[next.y()][next.x()];
            if (nextChar == '#') {
                //rotate direction 90 degrees to the right
                switch (direction) {
                    case NORTH -> direction = Direction.EAST;
                    case SOUTH -> direction = Direction.WEST;
                    case WEST  -> direction = Direction.NORTH;
                    case EAST  -> direction = Direction.SOUTH;
                }
                continue;
            }

            //mark grid already visited
            cursor = next;
            String key = cursor.toString() + direction;
            if (!visited.add(key)) {
                return true;
            }
        }

        return false;
    }
}

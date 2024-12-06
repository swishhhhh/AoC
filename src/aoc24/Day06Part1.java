package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2024/day/6">Advent of Code 2024 Day 6</a>
 */
public class Day06Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day6_input.txt");

        long answer = new Day06Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 4982;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);

        //find cell with ^ indicating starting position of the guard
        Coordinates cursor = getStartingCursor(grid);

        long steps = 1;
        Direction direction = Direction.NORTH;
        Set<Coordinates> visited = new HashSet<>();

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
            if (nextChar == '.' && visited.add(next)) {
                steps++;
            }

            cursor = next;
        }

        return steps;
    }

    private static Coordinates getStartingCursor(char[][] grid) {
        Coordinates cursor = null;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '^') {
                    cursor = new Coordinates(col, row);
                    break;
                }
            }
            if (cursor != null) {
                break;
            }
        }
        return cursor;
    }
}

package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/14">Advent of Code 2024 Day 14</a>
 */
public class Day14Part2 {
    private static final boolean DEBUG = true;
    private static final int GRID_WIDTH = 101;
    private static final int GRID_HEIGHT = 103;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day14_input.txt");

        long answer = new Day14Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 7051;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Coordinates[] startingPositions = new Coordinates[lines.size()];
        Coordinates[] velocities = new Coordinates[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            List<Integer> nums = Helper.extractIntsFromText(lines.get(i), true);
            int px = nums.get(0), py = nums.get(1), vx = nums.get(2), vy = nums.get(3);
            startingPositions[i] = new Coordinates(px, py);
            velocities[i] = new Coordinates(vx, vy);

        }

        long iteration = 0;

        while (true) {
            iteration++;

            //keep looping until every cell in the grid with a numeric value is 1
            char[][] grid = getGridAfterIteration(startingPositions, velocities, iteration);
            if (checkIfAllCellsAreOne(grid)) {
                if (DEBUG) {
                    System.out.printf("Found all 1s after iteration %s%n", iteration);
                    GridUtils.printGrid(grid, false);
                }

                return iteration;
            }

            if (iteration > 1_000_000) {
                throw new RuntimeException("Too many iterations");
            }
        }
    }

    private char[][] getGridAfterIteration(Coordinates[] startingPositions, Coordinates[] velocities, long iteration) {
        Coordinates[] endingPositions = new Coordinates[startingPositions.length];

        for (int i = 0; i < startingPositions.length; i++) {
            int px = startingPositions[i].x(), py = startingPositions[i].y();
            int vx = velocities[i].x(), vy = velocities[i].y();

            int ex = (int) (px + (vx * iteration)) % GRID_WIDTH;
            if (ex < 0) {
                ex = GRID_WIDTH + ex;
            }
            int ey = (int) (py + (vy * iteration)) % GRID_HEIGHT;
            if (ey < 0) {
                ey = GRID_HEIGHT + ey;
            }
            endingPositions[i] = new Coordinates(ex, ey);
        }

        return getGrid(endingPositions);
    }

    private boolean checkIfAllCellsAreOne(char[][] grid) {
        //return true if every cell in the grid with a numeric value is 1
        for (char[] chars : grid) {
            for (int col = 0; col < grid[0].length; col++) {
                if (chars[col] != '.' && chars[col] != '1') {
                    return false;
                }
            }
        }

        return true;
    }

    private char[][] getGrid(Coordinates[] coords) {
        char[][] grid = new char[GRID_HEIGHT][GRID_WIDTH];
        GridUtils.fillGrid(grid, '.');

        for (Coordinates coord : coords) {
            char c = grid[coord.y()][coord.x()];
            grid[coord.y()][coord.x()] = c == '.' ? '1' : (char) (c + 1);
        }

        return grid;
    }
}

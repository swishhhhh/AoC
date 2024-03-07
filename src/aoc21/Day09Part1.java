package aoc21;

import datastructs.Coordinates;
import utils.ResourceLoader;
import java.util.List;

import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2021/day/9">Advent of Code 2021 Day 9</a>
 */
public class Day09Part1 {

    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day9_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        char[][] grid = new char[lines.size()][lines.get(0).length()];

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = line.charAt(col);
            }
        }

        long sum = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                boolean isLowPoint = true;
                for (Coordinates c : getNeighboringCells(grid, new Coordinates(col, row))) {
                    if (grid[row][col] >= grid[c.y()][c.x()]) {
                        isLowPoint = false;
                        break;
                    }
                }

                if (isLowPoint) {
                    if (DEBUG) {
                        System.out.printf("Low point at [%s, %s] = %s%n", row, col, grid[row][col]);
                    }
                    sum+= (Integer.parseInt("" + grid[row][col]) + 1);
                }
            }

        }

        System.out.printf("Sum of risk level = %s%n", sum);

        long answer = sum;
        long expected = 572;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }
}

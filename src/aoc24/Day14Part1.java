package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/14">Advent of Code 2024 Day 14</a>
 */
public class Day14Part1 {
    private static final boolean DEBUG = false;
    private static final int GRID_WIDTH = 101;
    private static final int GRID_HEIGHT = 103;
    private static final int ITERATIONS = 100;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day14_input.txt");

        long answer = new Day14Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 229632480;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Coordinates[] startingPositions = new Coordinates[lines.size()];
        Coordinates[] endingPositions = new Coordinates[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            List<Integer> nums = Helper.extractIntsFromText(lines.get(i), true);
            int px = nums.get(0), py = nums.get(1), vx = nums.get(2), vy = nums.get(3);
            startingPositions[i] = new Coordinates(px, py);
            int ex = (px + (vx * ITERATIONS)) % GRID_WIDTH;
            if (ex < 0) {
                ex = GRID_WIDTH + ex;
            }
            int ey = (py + (vy * ITERATIONS)) % GRID_HEIGHT;
            if (ey < 0) {
                ey = GRID_HEIGHT + ey;
            }
            endingPositions[i] = new Coordinates(ex, ey);
        }

        if (DEBUG) {
            System.out.println("Starting position:");
            drawGrid(startingPositions);

            System.out.println("Ending position:");
            drawGrid(endingPositions);
        }

        return calculateSafetyFactor(endingPositions);
    }

    private long calculateSafetyFactor(Coordinates[] coords) {
        char[][] grid = new char[GRID_HEIGHT][GRID_WIDTH];
        GridUtils.fillGrid(grid, '.');

        for (Coordinates coord : coords) {
            char c = grid[coord.y()][coord.x()];
            grid[coord.y()][coord.x()] = c == '.' ? '1' : (char) (c + 1);
        }

        //sum up the numeric values for each of the 4 quadrants of the grids, then return their product
        long sumNW = 0, sumNE = 0, sumSW = 0, sumSE = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                char c = grid[row][col];
                if (c == '.') {
                    continue;
                }
                c = (char) (c - '0'); //convert char to int
                if (row < GRID_HEIGHT / 2) {
                    if (col < GRID_WIDTH / 2) {
                        sumNW += c;
                    } else if (col > (GRID_WIDTH / 2)){
                        sumNE += c;
                    }
                } else if (row > (GRID_HEIGHT / 2)) {
                    if (col < GRID_WIDTH / 2) {
                        sumSW += c;
                    } else if (col > (GRID_WIDTH / 2) ){
                        sumSE += c;
                    }
                }
            }
        }
        return sumNW * sumNE * sumSW * sumSE;
    }

    private void drawGrid(Coordinates[] coords) {
        char[][] grid = new char[GRID_HEIGHT][GRID_WIDTH];
        GridUtils.fillGrid(grid, '.');

        for (Coordinates coord : coords) {
            char c = grid[coord.y()][coord.x()];
            grid[coord.y()][coord.x()] = c == '.' ? '1' : (char) (c + 1);
        }

        GridUtils.printGrid(grid, false);
    }
}
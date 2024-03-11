package aoc21;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2021/day/11">Advent of Code 2021 Day 11</a>
 */
public class Day11Part1 {

    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {

        String resourceName = "aoc21/Day11_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        char[][] grid = new char[lines.size()][lines.get(0).length()];

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = line.charAt(col);
            }
        }

        if (DEBUG) {
            GridUtils.printGrid(grid, false);
        }

        long answer = 0;
        for (int i = 1; i <= 100; i++) {
            answer+= countFlashes(i, grid);
        }

        System.out.printf("Number of flashes = %s%n", answer);

        long expected = 1637;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long countFlashes(int stepNum, char[][] grid) {
        long flashes = 0;

        Queue<Coordinates> queue = new LinkedBlockingQueue<>();
        boolean[][] flashed = new boolean[grid.length][grid[0].length];

        //initially enqueue every cell in the grid
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                queue.add(new Coordinates(col, row));
            }
        }

        while (!queue.isEmpty()) {
            Coordinates c = queue.poll();

            //if already flashed, skip
            if (flashed[c.y()][c.x()]) {
                continue;
            }

            //increment by 1
            grid[c.y()][c.x()]++;

            //if > 9, flash (increment flashes) AND getNeighbors and enqueue them
            if (grid[c.y()][c.x()] > '9') {
                flashes++;
                flashed[c.y()][c.x()] = true;
                queue.addAll(GridUtils.getNeighboringCells(grid, c, true));
            }
        }

        //reset all flashers to 0
        for (int row = 0; row < flashed.length; row++) {
            for (int col = 0; col < flashed[0].length; col++) {
                if (flashed[row][col]) {
                    grid[row][col] = '0';
                }
            }
        }

        if (DEBUG) {
            System.out.printf("===== After Step %s ====== %n", stepNum);
            GridUtils.printGrid(grid, false);
        }

        return flashes;
    }

}

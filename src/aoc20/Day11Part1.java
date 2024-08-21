package aoc20;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/11">Advent of Code 2020 Day 11</a>
 */
public class Day11Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day11_input.txt");

        long answer = new Day11Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2489;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);

        List<Coordinates> cellsToFlip = new ArrayList<>();
        do {
            cellsToFlip.clear();

            //track which cells to flip
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    Coordinates cursor = new Coordinates(col, row);
                    List<Coordinates> occupiedNeighbors =
                            GridUtils.getNeighboringCells(grid, cursor, true)
                                    .stream()
                                    .filter(c -> grid[c.y()][c.x()] == '#')
                                    .toList();
                    if (grid[row][col] == 'L' && occupiedNeighbors.isEmpty()) {
                        cellsToFlip.add(cursor);
                    } else if (grid[row][col] == '#' && occupiedNeighbors.size() >= 4) {
                        cellsToFlip.add(cursor);
                    }
                }
            }

            //proceed to flip tracked cells
            cellsToFlip.forEach(c -> grid[c.y()][c.x()] = grid[c.y()][c.x()] == 'L' ? '#' : 'L');

        } while (!cellsToFlip.isEmpty());

        //count how many seats are occupied
        long count = 0;
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == '#') {
                    count++;
                }
            }
        }

        return count;
    }
}

package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/8">Advent of Code 2024 Day 8</a>
 */
public class Day08Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day8_input.txt");

        long answer = new Day08Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1293;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);

        //count the non '.' and '#' chars in the grid and update the map
        Map<Character, List<Coordinates>> charsMap = new HashMap<>();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                char c = grid[row][col];
                if (c != '.' && c != '#') {
                    charsMap.computeIfAbsent(c, k -> new ArrayList<>()).add(new Coordinates(col, row));
                }
            }
        }

        //for each char in charsMap, process all permutations of pair coordinates
        Set<Coordinates> antinodesCords = new HashSet<>();
        for (char c : charsMap.keySet()) {
            List<Coordinates> coords = charsMap.get(c);
            for (int i = 0; i < coords.size(); i++) {
                Coordinates c1 = coords.get(i);
                antinodesCords.add(c1); //add c1 itself

                for (int j = i + 1; j < coords.size(); j++) {
                    Coordinates c2 = coords.get(j);
                    antinodesCords.add(c2); //add c2 itself

                    int xDiff = c1.x() - c2.x();
                    int yDiff = c1.y() - c2.y();

                    int iteration = 0;
                    while (true) {
                        iteration++;
                        int ax1 = c1.x() + (iteration * xDiff);
                        int ay1 = c1.y() + (iteration * yDiff);
                        if (GridUtils.isCellOutOfBounds(grid, ax1, ay1)) {
                            break;
                        }
                        antinodesCords.add(new Coordinates(ax1, ay1));
                    }

                    iteration = 0;
                    while (true) {
                        iteration++;
                        int ax2 = c2.x() - (iteration * xDiff);
                        int ay2 = c2.y() - (iteration * yDiff);
                        if (GridUtils.isCellOutOfBounds(grid, ax2, ay2)) {
                            break;
                        }
                        antinodesCords.add(new Coordinates(ax2, ay2));
                    }
                }
            }
        }

        if (DEBUG) {
            for (Coordinates c : antinodesCords) {
                grid[c.y()][c.x()] = 'X';
            }
            GridUtils.printGrid(grid);
        }

        return antinodesCords.size();
    }
}

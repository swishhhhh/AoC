package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/8">Advent of Code 2024 Day 8</a>
 */
public class Day08Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day8_input.txt");

        long answer = new Day08Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 359;
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

        //for each char in charsMap, return all pair permutations of coordinates
        Set<Coordinates> antinodesCords = new HashSet<>();
        for (char c : charsMap.keySet()) {
            List<Coordinates> coords = charsMap.get(c);
            for (int i = 0; i < coords.size(); i++) {
                Coordinates c1 = coords.get(i);

                for (int j = i + 1; j < coords.size(); j++) {
                    Coordinates c2 = coords.get(j);
                    int xDiff = c1.x() - c2.x();
                    int yDiff = c1.y() - c2.y();

                    int ax1 = c1.x() + xDiff;
                    int ay1 = c1.y() + yDiff;
                    Coordinates antCoord1 = new Coordinates(ax1, ay1);
                    if (!GridUtils.isCellOutOfBounds(grid, ax1, ay1)) {
                        antinodesCords.add(antCoord1);
                    }

                    int ax2 = c2.x() - xDiff;
                    int ay2 = c2.y() - yDiff;
                    Coordinates antCoord2 = new Coordinates(ax2, ay2);
                    if (!GridUtils.isCellOutOfBounds(grid, ax2, ay2)) {
                        antinodesCords.add(antCoord2);
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

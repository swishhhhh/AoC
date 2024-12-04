package aoc24;

import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/4">Advent of Code 2024 Day 4</a>
 */
public class Day04Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day4_input.txt");

        long answer = new Day04Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1745;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        long cnt = 0;

        for (int row = 1; row < grid.length - 1; row++) {
            for (int col = 1; col < grid[0].length - 1; col++) {
                if (grid[row][col] == 'A') {
                    char nw = grid[row - 1][col - 1];
                    char ne = grid[row - 1][col + 1];
                    char sw = grid[row + 1][col - 1];
                    char se = grid[row + 1][col + 1];

                    if (nw == 'M' && ne == 'S' &&
                        sw == 'M' && se == 'S') {
                        cnt++;
                        continue;
                    }
                    if (nw == 'M' && ne == 'M' &&
                        sw == 'S' && se == 'S') {
                        cnt++;
                        continue;
                    }
                    if (nw == 'S' && ne == 'S' &&
                        sw == 'M' && se == 'M') {
                        cnt++;
                        continue;
                    }
                    if (nw == 'S' && ne == 'M' &&
                        sw == 'S' && se == 'M') {
                        cnt++;
                    }
                }
            }
        }

        return cnt;
    }
}

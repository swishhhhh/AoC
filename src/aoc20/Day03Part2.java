package aoc20;

import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/3">Advent of Code 2020 Day 3</a>
 */
public class Day03Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day3_input.txt");

        long answer = new Day03Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 6419669520L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        long result = 1;

        result *= countTrees(grid, 1, 1);
        result *= countTrees(grid, 3, 1);
        result *= countTrees(grid, 5, 1);
        result *= countTrees(grid, 7, 1);
        result *= countTrees(grid, 1, 2);

        return result;
    }

    private long countTrees(char[][] grid, int stepsRight, int stepsDown) {
        long cnt = 0;
        int x = 0, y = 0;
        while (y < grid.length - 1) {
            x = (x + stepsRight) % grid[0].length;
            y += stepsDown;
            if (grid[y][x] == '#') {
                cnt++;
            }
        }

        return cnt;
    }
}

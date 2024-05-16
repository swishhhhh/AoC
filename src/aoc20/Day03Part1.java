package aoc20;

import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/3">Advent of Code 2020 Day 3</a>
 */
public class Day03Part1 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day3_input.txt");

        long answer = new Day03Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 159;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        long cnt = 0;
        int x = 0, y = 0;
        while (y < grid.length - 1) {
            x = (x + 3) % grid[0].length;
            y += 1;
            if (grid[y][x] == '#') {
                cnt++;
            }
        }

        return cnt;
    }
}

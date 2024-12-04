package aoc24;

import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/4">Advent of Code 2024 Day 4</a>
 */
public class Day04Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day4_input.txt");

        long answer = new Day04Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2297;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        long cnt = 0;

        String targetWord = "XMAS";
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                for (Direction dir : Direction.values()) {
                    if (isTargetWordInDirection(grid, row, col, dir, targetWord)) {
                        cnt++;
                    }
                }
            }
        }

        return cnt;
    }

    private boolean isTargetWordInDirection(char[][] grid, int row, int col, Direction dir, String targetWord) {
        for (int i = 0; i < targetWord.length(); i++) {
            if (GridUtils.isCellOutOfBounds(grid, row, col) || grid[row][col] != targetWord.charAt(i)) {
                return false;
            }

            row += dir.getDeltaRow();
            col += dir.getDeltaCol();
        }

        return true;
    }
}

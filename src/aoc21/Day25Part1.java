package aoc21;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/25">Advent of Code 2021 Day 25</a>
 */
public class Day25Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day25_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 406;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        char[][] grid = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        int steps = 1;
        while (anyMoves(grid)) {
            steps++;
        }

        return steps;
    }

    private static boolean anyMoves(char[][] grid) {
        boolean anyMoves = false;

        //step 1: mark east moving cells
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '>') {
                    int nextCol = (col + 1) % grid[row].length; //wrap around to beginning of row if needed
                    if (grid[row][nextCol] == '.') {
                        grid[row][col] = '-'; //mark cell
                        anyMoves = true;
                    }
                }
            }
        }

        //step 2: move the marked cells
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '-') {
                    int nextCol = (col + 1) % grid[row].length;
                    grid[row][nextCol] = '>';
                    grid[row][col] = '.';
                }
            }
        }

        //step 3: mark south moving cells
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 'v') {
                    int nextRow = (row + 1) % grid.length;
                    if (grid[nextRow][col] == '.') {
                        grid[row][col] = '|'; //mark cell
                        anyMoves = true;
                    }
                }
            }
        }

        //step 4: move the marked cells
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '|') {
                    int nextRow = (row + 1) % grid.length;
                    grid[nextRow][col] = 'v';
                    grid[row][col] = '.';
                }
            }
        }

        return anyMoves;
    }
}

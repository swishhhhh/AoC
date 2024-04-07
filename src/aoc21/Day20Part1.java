package aoc21;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/20">Advent of Code 2021 Day 20</a>
 */
public class Day20Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day20_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 5461;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        String enhancementAlgoLine = lines.get(0);
        lines.remove(0);
        lines.remove(0);

        char[][] grid = GridUtils.loadGrid(lines);
        final int BUFFER = 5;
        grid = GridUtils.addPerimeter(grid, BUFFER, '.');
        if (DEBUG) {
            GridUtils.printGrid(grid);
        }

        grid = enhanceImage(grid, enhancementAlgoLine);
        grid = enhanceImage(grid, enhancementAlgoLine);

        return countLitPixels(grid, BUFFER);
    }

    private static char[][] enhanceImage(char[][] grid, String enhancementAlgoLine) {
        //start by expanding grid by 1
        grid = GridUtils.addPerimeter(grid, 1, '.');

        //figure out which pixels to flip (but hold off on actually doing so until after entire grid has been evaluated)
        List<Coordinates> pixelsToFlip = new ArrayList<>();
        for (int row = 1; row < grid.length - 1; row++) {
            for (int col = 1; col < grid[0].length - 1; col++) {
                if (shouldPixelFlip(grid, row, col, enhancementAlgoLine)) {
                    pixelsToFlip.add(new Coordinates(col, row));
                }
            }
        }

        //go ahead and flip the marked pixels
        for (Coordinates pixel : pixelsToFlip) {
            grid[pixel.y()][pixel.x()] = grid[pixel.y()][pixel.x()] == '#' ? '.' : '#';
        }

        if (DEBUG) {
            System.out.println("==========================");
            GridUtils.printGrid(grid);
        }

        return grid;
    }

    private static boolean shouldPixelFlip(char[][] grid, int row, int col, String enhancementAlgoLine) {
        char pixelIn = grid[row][col];

        StringBuilder sb = new StringBuilder();
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                sb.append(grid[i][j] == '#' ? 1 : 0);
            }
        }
        int index = Integer.parseInt(sb.toString(), 2); //parse 9 char string as binary number
        char pixelOut = enhancementAlgoLine.charAt(index);

        return pixelIn != pixelOut;
    }

    private static long countLitPixels(char[][] grid, int bufferToIgnore) {
        long count = 0;

        for (int i = bufferToIgnore - 1; i < grid.length - bufferToIgnore; i++) {
            for (int j = bufferToIgnore; j < grid[0].length - bufferToIgnore; j++) {
                if (grid[i][j] == '#') {
                    count++;
                }
            }
        }

        return count;
    }
}

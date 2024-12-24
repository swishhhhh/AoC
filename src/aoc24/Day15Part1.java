package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <a href="https://adventofcode.com/2024/day/15">Advent of Code 2024 Day 15</a>
 */
public class Day15Part1 {
    private static final boolean DEBUG = false;
    private static final Map<Character, Direction> DIRECTIONS = Map.of(
            '^', Direction.NORTH,
            'v', Direction.SOUTH,
            '<', Direction.WEST,
            '>', Direction.EAST
    );

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day15_input.txt");

        long answer = new Day15Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1456590;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int blankLineIdx = getFirstBlankLineNum(lines);
        char[][] grid = GridUtils.loadGrid(lines.subList(0, blankLineIdx));

        //construct instructions line from the remaining lines of the input
        char[] instructions = lines.subList(blankLineIdx + 1, lines.size())
                .stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
                .toCharArray();

        Coordinates cursor = findCursor(grid);

        for (int i = 0; i < instructions.length; i++) {
            cursor = move(grid, cursor, instructions[i]);

            if (DEBUG) {
                System.out.printf("After move %s (%s):%n", i, instructions[i]);
                GridUtils.printGrid(grid, false);
                System.out.println();
            }
        }

        return calculateGPS(grid);
    }

    private int getFirstBlankLineNum(List<String> lines) {
        return IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).trim().isEmpty())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No blank line found"));
    }

    private Coordinates findCursor(char[][] grid) {
        //return coordinates of cell in the grid with a value of '@'
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '@') {
                    return new Coordinates(col, row);
                }
            }
        }

        throw new RuntimeException("No cursor found");
    }

    private Coordinates move(char[][] grid, Coordinates cursor, char instruction) {
        Coordinates next = GridUtils.getNextCoord(cursor, DIRECTIONS.get(instruction));

        if (grid[next.y()][next.x()] == '#') { //hit a wall
            return cursor;
        }

        //if there's a box in the next cell attempt to move it (with recursive calls to move(...)).
        if (grid[next.y()][next.x()] == 'O') {
            Coordinates result = move(grid, next, instruction);
            if (result.equals(next)) { //if the box couldn't be moved, return the cursor at its original position
                return cursor;
            }
        }

        //successfully move (the box or cursor)
        grid[next.y()][next.x()] = grid[cursor.y()][cursor.x()];
        grid[cursor.y()][cursor.x()] = '.';

        return next;
    }

    private long calculateGPS(char[][] grid) {
        //return the sum of x + (100 * y) for each coordinate with a value of 'O'
        long sum = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == 'O') {
                    sum += (col + (100L * row));
                }
            }
        }
        return sum;
    }
}
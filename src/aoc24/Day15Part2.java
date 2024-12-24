package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static datastructs.Direction.*;

/**
 * <a href="https://adventofcode.com/2024/day/15">Advent of Code 2024 Day 15</a>
 */
public class Day15Part2 {
    private static final boolean DEBUG = false;
    private static final Map<Character, Direction> DIRECTIONS = Map.of(
            '^', NORTH,
            'v', SOUTH,
            '<', WEST,
            '>', EAST
    );
    private static final Set<Direction> VERTICAL_DIRECTIONS = Set.of(NORTH, SOUTH);

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day15_input.txt");

        long answer = new Day15Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1489116;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int blankLineIdx = getFirstBlankLineNum(lines);
        char[][] grid = GridUtils.loadGrid(lines.subList(0, blankLineIdx));
        grid = resizeGrid(grid);

        if (DEBUG) {
            System.out.println("Initial grid:");
            GridUtils.printGrid(grid, false);
            System.out.println();
        }

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

    private char[][] resizeGrid(char[][] grid) {
        char[][] newGrid = new char[grid.length][grid[0].length * 2];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                char c = grid[row][col];
                switch (c) {
                    case '#', '.' -> {
                        newGrid[row][col * 2] = c;
                        newGrid[row][(col * 2) + 1] = c;
                    }
                    case 'O' -> {
                        newGrid[row][col * 2] = '[';
                        newGrid[row][(col * 2) + 1] = ']';
                    }
                    case '@' -> {
                        newGrid[row][col * 2] = '@';
                        newGrid[row][(col * 2) + 1] = '.';
                    }
                }
            }
        }

        return newGrid;
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

    private boolean canMove(char[][] grid, Coordinates cursor, char instruction) {
        //return true if cursor can move in the given direction
        Direction direction = DIRECTIONS.get(instruction);
        Coordinates next = GridUtils.getNextCoord(cursor, direction);
        char nextCellValue = grid[next.y()][next.x()];

        switch (nextCellValue) {
            case '#': return false; //hit a wall
            case '.': return true;
        }

        assert nextCellValue == '[' || nextCellValue == ']';

        if (!canMove(grid, next, instruction)) {
            return false;
        }

        //for vertical moves, need to also check the other half of the box diagonally in front of (above or below the) cursor
        if (VERTICAL_DIRECTIONS.contains(direction)) {
            boolean isNorthward = direction == NORTH;
            boolean isLeftBracket = nextCellValue == '[';

            Direction nextDiagonalDirection = isLeftBracket ?
                    (isNorthward ? NE : SE) :
                    (isNorthward ? NW : SW);

            Coordinates nextDiagonal = GridUtils.getNextCoord(cursor, nextDiagonalDirection);
            return canMove(grid, nextDiagonal, instruction);

        } else { //horizontal move
            return true;
        }
    }

    private Coordinates move(char[][] grid, Coordinates cursor, char instruction) {
        if (!canMove(grid, cursor, instruction)) {
            return cursor;
        }

        Direction direction = DIRECTIONS.get(instruction);
        Coordinates next = GridUtils.getNextCoord(cursor, direction);
        char nextCellValue = grid[next.y()][next.x()];

        if (nextCellValue == '[' || nextCellValue == ']') {
            move(grid, next, instruction);

            if (VERTICAL_DIRECTIONS.contains(direction)) {
                boolean isNorthward = direction == NORTH;
                boolean isLeftBracket = nextCellValue == '[';

                Direction nextDiagonalDirection = isLeftBracket ?
                        (isNorthward ? NE : SE) :
                        (isNorthward ? NW : SW);

                Coordinates nextDiagonal = GridUtils.getNextCoord(cursor, nextDiagonalDirection);
                move(grid, nextDiagonal, instruction);
            }
        }

        //successfully move (the box half or cursor)
        grid[next.y()][next.x()] = grid[cursor.y()][cursor.x()];
        grid[cursor.y()][cursor.x()] = '.';

        return next;
    }

    private long calculateGPS(char[][] grid) {
        //return the sum of x + (100 * y) for each coordinate with a value of '['
        long sum = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == '[') {
                    sum += (col + (100L * row));
                }
            }
        }
        return sum;
    }
}
package aoc20;

import datastructs.Coordinates;
import org.apache.commons.math3.util.Pair;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/11">Advent of Code 2020 Day 11</a>
 */
public class Day11Part2 {
    private static final boolean DEBUG = false;

    private static final Collection<Pair<Integer, Integer>> DIRECTIONS = new ArrayList<>(
            List.of(
                    new Pair<>( 0, -1),   //North
                    new Pair<>( 0,  1),   //South
                    new Pair<>( 1,  0),   //East
                    new Pair<>(-1,  0),   //West
                    new Pair<>( 1, -1),   //NE
                    new Pair<>( 1,  1),   //SE
                    new Pair<>(-1,  1),   //SW
                    new Pair<>(-1, -1))   //NW
    );

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day11_input.txt");

        long answer = new Day11Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2180;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);

        List<Coordinates> cellsToFlip = new ArrayList<>();
        do {
            cellsToFlip.clear();

            //track which cells to flip
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    Coordinates cursor = new Coordinates(col, row);
                    List<Coordinates> occupiedNeighbors = getClosestOccupiedSeatInEachDirection(grid, cursor);
                    if (grid[row][col] == 'L' && occupiedNeighbors.isEmpty()) {
                        cellsToFlip.add(cursor);
                    } else if (grid[row][col] == '#' && occupiedNeighbors.size() >= 5) {
                        cellsToFlip.add(cursor);
                    }
                }
            }

            //proceed to flip tracked cells
            cellsToFlip.forEach(c -> grid[c.y()][c.x()] = grid[c.y()][c.x()] == 'L' ? '#' : 'L');

            if (DEBUG) {
                GridUtils.printGrid(grid, false);
                System.out.println();
            }

        } while (!cellsToFlip.isEmpty());

        //count how many seats are occupied
        long count = 0;
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == '#') {
                    count++;
                }
            }
        }

        return count;
    }

    public static List<Coordinates> getClosestOccupiedSeatInEachDirection(char[][] grid, Coordinates cursor) {
        List<Coordinates> neighbors = new ArrayList<>();

        for (Pair<Integer, Integer> dir : DIRECTIONS) {
            int step = 0;
            while(true) {
                step++;
                Coordinates next =
                        new Coordinates(cursor.x() + (step * dir.getFirst()), cursor.y() + (step * dir.getSecond()));
                if (next.y() < 0 || next.y() >= grid.length || next.x() < 0 || next.x() >= grid[0].length
                        || grid[next.y()][next.x()] == 'L') {
                    break;
                }

                if (grid[next.y()][next.x()] == '#') {
                    neighbors.add(next);
                    break;
                }
            }
        }

        return neighbors;
    }
}

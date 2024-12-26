package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2024/day/18">Advent of Code 2024 Day 18</a>
 */
public class Day18Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day18_input.txt");

        long answer = new Day18Part1().execute(lines, 1024);
        System.out.printf("Answer = %s%n", answer);

        long expected = 292;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines, int numCoords) {
        List<Coordinates> corruptedCoords = getCorruptedCoords(lines);
        char[][] grid = loadGrid(corruptedCoords, numCoords);

        long[][] cost = new long[grid.length][grid[0].length];
        GridUtils.fillGrid(cost, Long.MAX_VALUE);

        Coordinates start = new Coordinates(0, 0);
        Coordinates end =  new Coordinates(grid[0].length - 1, grid.length - 1);

        //find the shortest path from start to end moving only north, south, east, west and avoiding cells with '#'
        Queue<Coordinates> queue = new LinkedBlockingQueue<>();
        queue.add(start);
        cost[start.y()][start.x()] = 0;

        //Dijkstra's algorithm
        while (!queue.isEmpty()) {
            Coordinates coord = queue.poll();

            if (coord.equals(end)) {
                grid[coord.y()][coord.x()] = 'E';
                break;
            }

            grid[coord.y()][coord.x()] = 'O';

            for (Coordinates n : GridUtils.getNeighboringCells(grid, coord)) {
                if (grid[n.y()][n.x()] == '#') {
                    continue;
                }

                long newCost = cost[coord.y()][coord.x()] + 1;
                if (newCost < cost[n.y()][n.x()]) { //skip if we already found an equal or better path
                    cost[n.y()][n.x()] = newCost;
                    queue.add(n);
                }
            }
        }

        if (DEBUG) {
            GridUtils.printGrid(grid, false);
        }

        return cost[end.y()][end.x()];
    }

    private List<Coordinates> getCorruptedCoords(List<String> lines) {
        return lines.stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    return new Coordinates(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                })
                .toList();
    }

    private char[][] loadGrid(List<Coordinates> corruptedCoords, int numCoords) {
        int dimensions = getMaxCoords(corruptedCoords) + 1;
        char[][] grid = new char[dimensions][dimensions];
        GridUtils.fillGrid(grid, '.');

        for (int i = 0; i < numCoords; i++) {
            Coordinates coord = corruptedCoords.get(i);
            grid[coord.y()][coord.x()] = '#';
        }
        return grid;
    }

    private int getMaxCoords(List<Coordinates> corruptedCoords) {
        return corruptedCoords.stream()
                .mapToInt(coord -> Math.max(coord.x(), coord.y()))
                .max()
                .orElse(0);
    }
}
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
public class Day18Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day18_input.txt");

        String answer = new Day18Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        String expected = "58,44";
        if (!answer.equals(expected)) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private String execute(List<String> lines) {
        List<Coordinates> corruptedCoords = getCorruptedCoords(lines);
        int dimensions = getMaxCoords(corruptedCoords) + 1;
        char[][] grid = new char[dimensions][dimensions];

        //binary search through all corrupted coords to find the first one that will block off all paths
        int low = 0;
        int high = lines.size() - 1;
        int firstCoordToFail = -1;
        while (low <= high) {
            int mid = (low + high) / 2;
            loadGrid(corruptedCoords, mid, grid);

            if (pathExists(grid)) {
                low = mid + 1;
            } else {
                high = mid - 1;
                firstCoordToFail = mid;
            }
        }

        return lines.get(firstCoordToFail - 1);
    }

    private List<Coordinates> getCorruptedCoords(List<String> lines) {
        return lines.stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    return new Coordinates(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                })
                .toList();
    }

    private int getMaxCoords(List<Coordinates> corruptedCoords) {
        return corruptedCoords.stream()
                .mapToInt(coord -> Math.max(coord.x(), coord.y()))
                .max()
                .orElse(0);
    }

    private void loadGrid(List<Coordinates> corruptedCoords, int numCoords, char[][] grid) {
        GridUtils.fillGrid(grid, '.');
        for (int i = 0; i < numCoords; i++) {
            Coordinates coord = corruptedCoords.get(i);
            grid[coord.y()][coord.x()] = '#';
        }
    }

    private static boolean pathExists(char[][] grid) {
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
                break;
            }

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

        return cost[end.y()][end.x()] != Long.MAX_VALUE;
    }
}
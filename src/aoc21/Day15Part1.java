package aoc21;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/15">Advent of Code 2021 Day 15</a>
 */
public class Day15Part1 {
    static class CoordinateAndCost {
        Coordinates coords;
        Long cost;

        public CoordinateAndCost(Coordinates coords, Long cost) {
            this.coords = coords;
            this.cost = cost;
        }

        public Long getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return "CoordinateAndCost{" +
                    "coords=" + coords +
                    ", cost=" + cost +
                    '}';
        }
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day15_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 613;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        long[][] lowestCostsGrid = new long[grid.length][grid[0].length];
        GridUtils.fillGrid(lowestCostsGrid, Long.MAX_VALUE);
        lowestCostsGrid[0][0] = 0;

        /*
           Dijkstra's algorithm
         */
        Set<Coordinates> visited = new HashSet<>();
        Queue<CoordinateAndCost> queue = new PriorityQueue<>(Comparator.comparingLong(CoordinateAndCost::getCost));
        queue.add(new CoordinateAndCost(new Coordinates(0, 0), 0L));

        while (!queue.isEmpty()) {
            CoordinateAndCost c = queue.poll();

            //base case
            if (c.coords.x() == grid[0].length - 1 && c.coords.y() == grid.length - 1) {
                return c.cost;
            }

            for (Coordinates n : GridUtils.getNeighboringCells(grid, c.coords)) {
                if (visited.contains(n)) {
                    continue;
                }

                long newCost = c.cost + Integer.parseInt("" + grid[n.y()][n.x()]);
                if (newCost < lowestCostsGrid[n.y()][n.x()]) {
                    lowestCostsGrid[n.y()][n.x()] = newCost;
                    queue.add(new CoordinateAndCost(n, lowestCostsGrid[n.y()][n.x()]));
                }
            }

            visited.add(c.coords);
        }

        throw new RuntimeException("Unable to solve");
    }
}

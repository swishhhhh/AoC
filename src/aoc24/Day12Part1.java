package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/12">Advent of Code 2024 Day 12</a>
 */
public class Day12Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day12_input.txt");

        long answer = new Day12Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1483212;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = GridUtils.loadGrid(lines);
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Map<Coordinates, List<Coordinates>> regions = new HashMap<>();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (visited[row][col]) {
                    continue;
                }

                Coordinates start = new Coordinates(col, row);
                regions.put(start, buildRegion(grid, start, visited));
            }
        }

        long price = 0;
        for (Coordinates  start : regions.keySet()) {
            List<Coordinates> region = regions.get(start);
            long perimeter = getRegionPerimeter(region, grid);
            price += (region.size() * perimeter);
        }

        return price;
    }

    private List<Coordinates> buildRegion(char[][] grid, Coordinates start, boolean[][] visited) {
        List<Coordinates> region = new ArrayList<>();
        region.add(start);
        visited[start.y()][start.x()] = true;

        Queue<Coordinates> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Coordinates cur = queue.poll();

            List<Coordinates> neighbors = GridUtils.getNeighboringCells(grid, cur);
            neighbors
                    .stream()
                    .filter(n -> !visited[n.y()][n.x()])
                    .filter(n -> grid[n.y()][n.x()] == grid[cur.y()][cur.x()])
                    .forEach(n -> {
                        visited[n.y()][n.x()] = true;
                        region.add(n);
                        queue.add(n);
                    });
        }

        return region;
    }

    private long getRegionPerimeter(List<Coordinates> region, char[][] grid) {
        long perimeter = 0;
        for (Coordinates cell : region) {
            List<Coordinates> neighbors =
                    GridUtils.getNeighboringCells(grid, cell)
                        .stream()
                        .filter(n -> grid[n.y()][n.x()] == grid[cell.y()][cell.x()])
                        .toList();
            perimeter += (4 - neighbors.size()); //each neighbor subtracts 1 from the perimeter (i.e. an isolated cell has a perim of 4, a fully enclosed one has perim of 0)
        }

        return perimeter;
    }
}

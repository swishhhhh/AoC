package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/12">Advent of Code 2024 Day 12</a>
 */
public class Day12Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day12_input.txt");

        long answer = new Day12Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 897062;
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
            long sides = getRegionSides(region, grid);
            long regionPrice = region.size() * sides;
            if (DEBUG) {
                System.out.printf("Region starting at coord=%s, label=%s, size=%s, sides=%s, price=%s%n",
                        start, grid[start.y()][start.x()], region.size(), sides, regionPrice);
            }
            price += regionPrice;
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

        //sort the cells in the region so that the "visited" logic (later in getRegionSides method) works correctly
        region.sort(Comparator.comparingInt(Coordinates::x)
                    .thenComparingInt(Coordinates::y));

        return region;
    }

    private long getRegionSides(List<Coordinates> region, char[][] grid) {
        Direction[] sidesToCheck = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        Direction[] dedupeSidesToCheck = {Direction.WEST, Direction.NORTH, Direction.WEST, Direction.NORTH};
        Map<Direction, Set<Coordinates>> visited = Map.of(
                Direction.NORTH, new HashSet<>(),
                Direction.EAST, new HashSet<>(),
                Direction.SOUTH, new HashSet<>(),
                Direction.WEST, new HashSet<>()
        );

        long sides = 0;
        for (Coordinates cell : region) {
            //check each of the 4 sides
            for (int i = 0; i < sidesToCheck.length; i++) {
                Coordinates n1 = GridUtils.getNextCoord(cell, sidesToCheck[i]);
                if (GridUtils.isCellOutOfBounds(grid, n1.x(), n1.y())
                        || grid[n1.y()][n1.x()] != grid[cell.y()][cell.x()]) {

                    //we're on the edge, now check if there's a neighbor immediately above (if checking left or right
                    //sides) or to the immediate left (if checking top or bottom sides) was already counted in this
                    //region, and if so don't double-count
                    Coordinates n2 = GridUtils.getNextCoord(cell, dedupeSidesToCheck[i]);
                    Set<Coordinates> visitedThisSide = visited.get(sidesToCheck[i]);
                    if (GridUtils.isCellOutOfBounds(grid, n2.x(), n2.y())
                            || !visitedThisSide.contains(n2)) {
                        sides++;
                    }
                    visited.get(sidesToCheck[i]).add(cell);
                }
            }
        }

        return sides;
    }
}

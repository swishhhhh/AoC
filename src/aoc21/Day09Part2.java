package aoc21;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.GridUtils.getNeighboringCells;

/**
 * <a href="https://adventofcode.com/2021/day/9">Advent of Code 2021 Day 9</a>
 */
public class Day09Part2 {

    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day9_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        char[][] grid = new char[lines.size()][lines.get(0).length()];

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = line.charAt(col);
            }
        }

        long answer = get3LargestBasinsValue(grid);

        System.out.printf("Product of the top 3 basins = %s%n", answer);

        long expected = 847044;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long get3LargestBasinsValue(char[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Map<Integer, Long> basinSizes = new HashMap<>();

        int basins = 0;
        long basinSize;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (visited[row][col]) {
                    continue;
                }

                Queue<Coordinates> queue = new LinkedBlockingQueue<>();
                basins++;
                basinSize = 0;
                visited[row][col] = true;
                queue.add(new Coordinates(col, row));
                while (!queue.isEmpty()) {
                    Coordinates coord = queue.poll();

                    if (grid[coord.y()][coord.x()] == '9') {
                        continue;
                    }

                    basinSize++;
                    getNeighboringCells(grid, coord)
                            .stream()
                            .filter(n -> !visited[n.y()][n.x()])
                            .forEach(n -> {
                                visited[n.y()][n.x()] = true;
                                queue.add(n);
                            });
                }

                if (DEBUG) {
                    System.out.printf("Basin #%s -> size=%s%n", basins, basinSize);
                }
                basinSizes.put(basins, basinSize);
            }
        }

        List<Long> top3 =
                basinSizes
                    .values()
                    .stream()
                    .sorted((l1, l2) -> Long.compare(l2, l1))
                    .limit(3)
                    .toList();

        if (DEBUG) {
            System.out.printf("Top 3 basins: %s%n", top3);
        }

        return top3.get(0) * top3.get(1) * top3.get(2);
    }
}

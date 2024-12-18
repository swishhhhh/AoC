package aoc24;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/10">Advent of Code 2024 Day 10</a>
 */
public class Day10Part1And2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day10_input.txt");

        Day10Part1And2 exec = new Day10Part1And2();
        long part1Answer = exec.execute(lines, false);
        long part2Answer = exec.execute(lines, true);
        System.out.printf("Part 1 answer = %s, Part 2 answer = %s%n", part1Answer, part2Answer);

        long expectedPart1 = 776, expectedPart2 = 1657;
        if (part1Answer != expectedPart1 || part2Answer != expectedPart2) {
            throw new RuntimeException(String.format("Answers for part 1 (%s) or 2 (%s) don't match expected (%s, %s)",
                    part1Answer, part2Answer, expectedPart1, expectedPart2));
        }
    }

    private long execute(List<String> lines, boolean countMultiplePaths) {
        char[][] grid = GridUtils.loadGrid(lines);

        return getTrailHeads(grid)
                .parallelStream()
                .mapToLong(head -> getTrailScore(head, grid, countMultiplePaths))
                .sum();
    }

    private List<Coordinates> getTrailHeads(char[][] grid) {
        List<Coordinates> heads = new ArrayList<>();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '0') {
                    heads.add(new Coordinates(col, row));
                }
            }

        }
        return heads;
    }

    private long getTrailScore(Coordinates head, char[][] grid, boolean countMultiplePaths) {
        Set<Coordinates> visited = new HashSet<>();
        long score = 0;

        Stack<Coordinates> stack = new Stack<>();
        stack.push(head);
        while (!stack.isEmpty()) {
            Coordinates curr = stack.pop();
            if (!countMultiplePaths && !visited.add(curr)) {
                continue;
            }

            int currElevation = Character.getNumericValue(grid[curr.y()][curr.x()]);
            if (currElevation == 9) {
                score++;
                continue;
            }

            List<Coordinates> neighbors = GridUtils.getNeighboringCells(grid, curr);
            for (Coordinates neighbor : neighbors) {
                int neighborElevation = Character.getNumericValue(grid[neighbor.y()][neighbor.x()]);
                if (neighborElevation != currElevation + 1) {
                    continue;
                }

                stack.push(neighbor);
            }
        }

        return score;
    }
}

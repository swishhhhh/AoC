package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <a href="https://adventofcode.com/2021/day/5">Advent of Code 2021 Day 5</a>
 */
public class Day5Part1 {

    private static int[][] grid;

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day5_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        //zip through all lines to get the max x or y coordinate
        AtomicInteger max = new AtomicInteger(0);
        lines.forEach(line -> {
            Helper.extractIntsFromText(line).forEach(i -> {
                max.set(Math.max(max.get(), i));
            });
        });

        //initialize grid
        grid = new int[max.get() + 1][max.get() + 1];

        //loop through all lines again and update grid
        lines.forEach(line -> {
            List<Integer> ints = Helper.extractIntsFromText(line);
            int x1 = ints.get(0), y1 = ints.get(1), x2 = ints.get(2), y2 = ints.get(3);

            if (x1 != x2 && y1 == y2) { //vertical line
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    grid[y1][x]++;
                }
            } else if (x1 == x2 && y1 != y2) { //horizontal line
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    grid[y][x1]++;
                }
            }
        });

        //count coords > 1, print grid
        int ctr = 0;
        boolean print = false;
        for (int[] ints : grid) {
            for (int anInt : ints) {
                if (anInt > 1) ctr++;
                if (print) System.out.print(anInt);
            }
            if (print) System.out.println();
        }

        System.out.printf("Number of points: %s%n", ctr);
    }
}

package aoc20;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/5">Advent of Code 2020 Day 5</a>
 */
public class Day05Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day5_input.txt");

        long answer = new Day05Part2().execute(lines);
        System.out.printf("AnDay05Part1swer = %s%n", answer);

        long expected = 669;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int max = -1, min = Integer.MAX_VALUE;
        Set<Integer> allIds = new HashSet<>();
        for (String line : lines) {
            int row = 0, windowSize = 128;
            for (char c : line.substring(0, 7).toCharArray()) {
                windowSize /= 2;
                if (c == 'B') {
                    row += windowSize;
                }
            }

            int col = 0;
            windowSize = 8;
            for (char c : line.substring(7).toCharArray()) {
                windowSize /= 2;
                if (c == 'R') {
                    col += windowSize;
                }
            }

            int id = (row * 8) + col;
            max = Math.max(max, id);
            min = Math.min(min, id);
            allIds.add(id);
        }

        for (int i = min; i <= max ; i++) {
            if (!allIds.contains(i)) {
                return i;
            }
        }

        return -1;
    }
}

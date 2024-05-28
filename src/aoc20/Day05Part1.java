package aoc20;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/5">Advent of Code 2020 Day 5</a>
 */
public class Day05Part1 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day5_input.txt");

        long answer = new Day05Part1().execute(lines);
        System.out.printf("AnDay05Part1swer = %s%n", answer);

        long expected = 904;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int max = -1;
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
        }

        return max;
    }
}

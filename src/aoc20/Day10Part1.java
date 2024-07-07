package aoc20;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/10">Advent of Code 2020 Day 10</a>
 */
public class Day10Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day10_input.txt");

        long answer = new Day10Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1656;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private int execute(List<String> lines) {
        List<Long> nums = lines.stream().map(Long::parseLong).toList().stream().sorted().toList();
        int[] diffs = new int[4];
        long num = 0;

        for (long n : nums) {
            diffs[(int)(n - num)]++;
            num = n;
        }

        //add last step
        diffs[3]++;

        return diffs[1] * diffs[3];
    }
}

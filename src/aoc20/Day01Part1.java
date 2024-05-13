package aoc20;

import utils.ResourceLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/1">Advent of Code 2020 Day 1</a>
 */
public class Day01Part1 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day1_input.txt");

        long answer = new Day01Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 482811;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Set<Long> nums =
                lines.stream().map(Long::parseLong).filter(l -> l <= 2020).collect(Collectors.toSet());

        for (Long n1 : nums) {
            long n2 = 2020 - n1;
            if (nums.contains(n2)) {
                return n1 * n2;
            }
        }

        throw new RuntimeException("Unable to find answer");
    }
}

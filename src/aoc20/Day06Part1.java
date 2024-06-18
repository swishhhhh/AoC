package aoc20;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/6">Advent of Code 2020 Day 6</a>
 */
public class Day06Part1 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day6_input.txt");

        long answer = new Day06Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 6768;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Set<Character>> groups = new ArrayList<>();
        Set<Character> group = new HashSet<>();

        for (String line : lines) {
            if (line.trim().length() == 0) {
                groups.add(group);
                group = new HashSet<>();
                continue;
            }

            for (char c : line.toCharArray()) {
                group.add(c);
            }
        }

        groups.add(group);

        long sum = 0;
        for (Set<Character> g : groups) {
            sum += g.size();
        }

        return sum;
    }
}

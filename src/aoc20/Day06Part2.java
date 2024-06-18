package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/6">Advent of Code 2020 Day 6</a>
 */
public class Day06Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day6_input.txt");

        long answer = new Day06Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 3489;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Set<Character>> groups = new ArrayList<>();
        Set<Character> group = new HashSet<>();
        boolean firstLineInGroup = true;

        for (String line : lines) {
            if (line.trim().length() == 0) {
                groups.add(group);
                group = new HashSet<>();
                firstLineInGroup = true;
                continue;
            }

            if (firstLineInGroup) {
                group.addAll(Helper.charArrayToList(line.toCharArray()));
                firstLineInGroup = false;
            }

            group.retainAll(Helper.charArrayToList(line.toCharArray()));
        }

        groups.add(group);

        long sum = 0;
        for (Set<Character> g : groups) {
            sum += g.size();
        }

        return sum;
    }
}

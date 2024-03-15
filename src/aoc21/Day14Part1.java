package aoc21;

import utils.ResourceLoader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2021/day/14">Advent of Code 2021 Day 14</a>
 */
public class Day14Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day14_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2584;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        String template = lines.get(0);
        if (DEBUG) {
            System.out.printf("Original template: %s%n", template);
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            map.put(line.substring(0, 2), line.substring(line.length() - 1));
        }

        for (int step = 1; step <= 10; step++) {
            StringBuilder sb = new StringBuilder();
            char prevChar = template.charAt(0);
            sb.append(prevChar);
            for (int i = 1; i < template.length(); i++) {
                char nextChar = template.charAt(i);
                sb.append(map.get("" + prevChar + nextChar));
                sb.append(nextChar);
                prevChar = nextChar;
            }

            template = sb.toString();
            if (DEBUG) {
                System.out.printf("After step %s: %s%n", step, template);
            }
        }

        List<Long> frequencies =
            template.chars()
                .mapToObj(c -> (char) c)
                .toList()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .stream()
                .sorted()
                .toList();
        return frequencies.get(frequencies.size() - 1) - frequencies.get(0); //most frequent - least frequent
    }
}

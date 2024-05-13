package aoc20;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/1">Advent of Code 2020 Day 1</a>
 */
public class Day01Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day1_input.txt");

        long answer = new Day01Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 193171814;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Set<Long> singles =
                lines.stream().map(Long::parseLong).filter(l -> l <= 2020).collect(Collectors.toSet());

        //collect all permutations of pairs that don't exceed 2020
        Map<Long, Long[]> pairs = new HashMap<>();
        for (long n1 : singles) {
            for (long n2 : singles) {
                long sum = n1 + n2;
                if (n1 != n2 && sum <= 2020) {
                    pairs.put(sum, new Long[]{n1, n2});
                }
            }
        }

        for (long n3 : singles) {
            long n1n2 = 2020 - n3;
            if (pairs.containsKey(n1n2)) {
                Long[] pair = pairs.get(n1n2);
                return pair[0] * pair[1] * n3;
            }
        }

        throw new RuntimeException("Unable to find answer");
    }
}

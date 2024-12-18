package aoc24;

import org.apache.commons.math3.util.Pair;
import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2024/day/11">Advent of Code 2024 Day 11</a>
 */
public class Day11Part2 {
    private static final int CYCLES = 75;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day11_input.txt");

        long answer = new Day11Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 236302670835517L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Long> nums = Helper.extractLongsFromText(lines.get(0));

        return nums.stream()
                .mapToLong(num -> recurse(num, CYCLES, new HashMap<>()))
                .sum();
    }

    private long recurse(long num, int cycles, Map<Pair<Long, Integer>, Long> cache) {
        if (cycles == 0) {
            return 1;
        }

        Pair<Long, Integer> key = new Pair<>(num, cycles);
        Long fromCache = cache.get(key);
        if (fromCache != null) {
            return fromCache;
        }

        long value;
        if (num == 0) {
            value = recurse(1, cycles - 1, cache);
        } else {
            String numAsString = String.valueOf(num);
            if (numAsString.length() % 2 == 0) {
                long leftValue = recurse(Long.parseLong(numAsString.substring(0, numAsString.length() / 2)), cycles - 1, cache);
                long rightValue = recurse(Long.parseLong(numAsString.substring(numAsString.length() / 2)), cycles - 1, cache);
                value = leftValue + rightValue;
            } else {
                value = recurse(2024 * num, cycles - 1, cache);
            }
        }

        cache.put(key, value);
        return value;
    }
}

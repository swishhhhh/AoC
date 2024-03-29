package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2021/day/6">Advent of Code 2021 Day 6</a>
 */
public class Day06Part2 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day6_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);
        List<Integer> nums = Helper.extractIntsFromText(lines.get(0));
        Map<Integer, Long> numsMap = new HashMap<>();

        //populate map with initial values
        for (Integer n: nums) {
            Long countForN = numsMap.get(n);
            if (countForN == null) countForN = 0L;
            numsMap.put(n, countForN + 1);
        }

        for (int i = 1; i <= 256; i++) {
            Map<Integer, Long> nextNumsMap = new HashMap<>();
            long new8s = 0;

            for (Integer n: numsMap.keySet()) {
                if (n > 0) {
                    addValue(nextNumsMap, n - 1, numsMap.get(n));
                    continue;
                }

                //n == 0
                new8s+= numsMap.get(0); //8s
                addValue(nextNumsMap, 6, numsMap.get(0));
            }

            numsMap = nextNumsMap;
            if (new8s > 0 ) {
                numsMap.put(8, new8s);
            }
        }

        long answer = numsMap.values().stream().mapToLong(Long::longValue).sum();
        System.out.printf("Map: %s, Number of lantern fish: %s%n", numsMap, answer);

        long expected = 1702631502303L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static void addValue(Map<Integer, Long> map, Integer key, Long value) {
        Long existingValue = map.get(key);
        if (existingValue == null) existingValue = 0L;
        map.put(key, existingValue + value);
    }
}

package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2024/day/1">Advent of Code 2024 Day 1</a>
 */
public class Day01Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day1_input.txt");

        long answer = new Day01Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 23981443;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int size = lines.size();
        int[] arrayA = new int[size];
        Map<Integer, Integer> freqMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            List<Integer> nums = Helper.extractIntsFromText(lines.get(i));
            arrayA[i] = nums.get(0);

            freqMap.merge(nums.get(1), 1, Integer::sum);
        }

        long sum = 0;
        for (int num : arrayA) {
            if (freqMap.containsKey(num)) {
                sum += ((long) num * freqMap.get(num));
            }
        }

        return sum;
    }
}

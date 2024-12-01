package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/1">Advent of Code 2024 Day 1</a>
 */
public class Day01Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day1_input.txt");

        long answer = new Day01Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1258579;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int size = lines.size();
        int[] arrayA = new int[size];
        int[] arrayB = new int[size];

        for (int i = 0; i < size; i++) {
            List<Integer> nums = Helper.extractIntsFromText(lines.get(i));
            arrayA[i] = nums.get(0);
            arrayB[i] = nums.get(1);
        }

        Arrays.sort(arrayA);
        Arrays.sort(arrayB);

        long sum = 0;
        for (int i = 0; i < size; i++) {
            sum += Math.abs(arrayA[i] - arrayB[i]);
        }

        return sum;
    }
}

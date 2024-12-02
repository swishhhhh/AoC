package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/2">Advent of Code 2024 Day 2</a>
 */
public class Day02Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day2_input.txt");

        long answer = new Day02Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 516;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int cnt = 0;
        for (String line : lines) {
            if (isValid(Helper.extractIntsFromText(line))) {
                cnt++;
            }
        }

        return cnt;
    }

    boolean isValid(List<Integer> nums) {
        boolean asc = nums.get(0) < nums.get(1);
        boolean valid = true;
        for (int i = 1; i < nums.size(); i++) {
            int diff = nums.get(i) - nums.get(i - 1);
            if ((asc && diff < 0) || (!asc && diff > 0)) {
                return false;
            }

            diff = Math.abs(diff);
            if (Math.abs(diff) < 1 || Math.abs(diff) > 3) {
                return false;
            }
        }
        return valid;
    }
}

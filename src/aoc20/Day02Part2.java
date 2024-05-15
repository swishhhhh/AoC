package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/2">Advent of Code 2020 Day 2</a>
 */
public class Day02Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day2_input.txt");

        long answer = new Day02Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 249;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long cnt = 0;

        for (String line : lines) {
            if (isValid(line)) {
                cnt++;
            }
        }

        return cnt;
    }

    private boolean isValid(String line) {
        char searchChar = line.split(" ")[1].charAt(0);
        String text = line.split(":")[1].trim();
        List<Integer> nums = Helper.extractIntsFromText(line);

        return text.charAt(nums.get(0) - 1) == searchChar ^ text.charAt(nums.get(1) - 1) == searchChar;
    }
}

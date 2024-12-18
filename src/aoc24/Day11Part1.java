package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/11">Advent of Code 2024 Day 11</a>
 */
public class Day11Part1 {
    private static final boolean DEBUG = false;
    private static final int CYCLES = 25;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day11_input.txt");

        long answer = new Day11Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 198089;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Long> nums = Helper.extractLongsFromText(lines.get(0));

        for (int i = 0; i < CYCLES; i++) {
            List<Long> newNums = new ArrayList<>();
            for (long num : nums) {
                if (num == 0) {
                    newNums.add(1L);
                    continue;
                }

                String numAsString = String.valueOf(num);
                if (numAsString.length() % 2 == 0) {
                    newNums.add(Long.parseLong(numAsString.substring(0, numAsString.length() / 2)));
                    newNums.add(Long.parseLong(numAsString.substring(numAsString.length() / 2)));
                    continue;
                }

                newNums.add(num * 2024);
            }

            nums = newNums;

            if (DEBUG) {
                System.out.printf("%s), size=%s, %s%n", (i + 1), nums.size(),
                        nums.toString().substring(0, Math.min(nums.toString().length(), 100)));
            }
        }

        return nums.size();
    }
}

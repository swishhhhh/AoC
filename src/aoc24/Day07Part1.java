package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/7">Advent of Code 2024 Day 7</a>
 */
public class Day07Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day7_input.txt");

        long answer = new Day07Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 66343330034722L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long validSums = 0;
        for (String line: lines) {
            validSums += processLine(line);
        }
        return validSums;
    }

    private long processLine(String line) {
        List<Long> nums = Helper.extractLongsFromText(line);
        long targetResult = nums.get(0);
        return recurseEvaluate(nums, targetResult, nums.get(1), 1);
    }

    private long recurseEvaluate(List<Long> nums, long targetResult, long runningResult, int level) {
        if (level == nums.size() - 2) {
            if (runningResult + nums.get(level + 1) == targetResult ||
                runningResult * nums.get(level + 1) == targetResult) {
                return targetResult;
            }
            return 0;
        }

        int nextLevel = level + 1;
        long nextNum = nums.get(nextLevel);

        //addition first
        long nextResult = runningResult + nextNum;
        if (nextResult <= targetResult) {  // Optimization: only proceed if sum is still possible
            long result = recurseEvaluate(nums, targetResult, nextResult, nextLevel);
            if (result > 0) {
                return result;
            }
        }

        //multiplication next
        nextResult = runningResult * nextNum;
        if (nextResult <= targetResult) {  // Optimization: only proceed if product is still possible
            long result = recurseEvaluate(nums, targetResult, nextResult, nextLevel);
            if (result > 0) {
                return result;
            }
        }

        return 0;
    }
}

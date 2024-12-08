package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/7">Advent of Code 2024 Day 7</a>
 */
public class Day07Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day7_input.txt");

        long answer = new Day07Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 637696070419031L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long sumOfValidLines = 0;
        for (String line: lines) {
            sumOfValidLines += processLine(line);
        }
        return sumOfValidLines;
    }

    private long processLine(String line) {
        List<Long> nums = Helper.extractLongsFromText(line);
        long targetResult = nums.get(0);
        return recurseEvaluate(nums, targetResult, nums.get(1), 1);
    }

    private long recurseEvaluate(List<Long> nums, long targetResult, long runningResult, int level) {
        if (level == nums.size() - 2) {
            if (runningResult + nums.get(level + 1) == targetResult ||
                runningResult * nums.get(level + 1) == targetResult ||
                Long.parseLong("" + runningResult + nums.get(level + 1)) == targetResult) {
                return targetResult;
            }
            return 0;
        }

        int nextLevel = level + 1;
        long nextNum = nums.get(nextLevel);

        //addition first
        long nextResult = runningResult + nextNum;
        if (nextResult <= targetResult) {
            long result = recurseEvaluate(nums, targetResult, nextResult, nextLevel);
            if (result > 0) {
                return result;
            }
        }

        //multiplication next
        nextResult = runningResult * nextNum;
        if (nextResult <= targetResult) {
            long result = recurseEvaluate(nums, targetResult, nextResult, nextLevel);
            if (result > 0) {
                return result;
            }
        }

        //concatenation next
        nextResult = Long.parseLong("" + runningResult + nums.get(level + 1));
        if (nextResult <= targetResult) {
            long result = recurseEvaluate(nums, targetResult, nextResult, nextLevel);
            if (result > 0) {
                return result;
            }
        }

        return 0;
    }
}

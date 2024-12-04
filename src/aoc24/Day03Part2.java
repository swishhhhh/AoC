package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/3">Advent of Code 2024 Day 3</a>
 */
public class Day03Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day3_input.txt");

        long answer = new Day03Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 82857512;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        String line = sb.toString();

        long sum = 0;
        int idx = 0, mulIdx, doIdx, dontIdx;
        boolean enabled = true;
        while (true) {
            mulIdx = line.indexOf("mul(", idx);
            if (mulIdx == -1) {
                break;
            }

            doIdx = line.indexOf("do()", idx);
            if (doIdx == -1) {
                doIdx = Integer.MAX_VALUE;
            }

            dontIdx = line.indexOf("don't()", idx);
            if (dontIdx == -1) {
                dontIdx = Integer.MAX_VALUE;
            }

            if (doIdx < dontIdx && doIdx < mulIdx) {
                enabled = true;
                idx = doIdx + 4;
                continue;
            }

            if (dontIdx < doIdx && dontIdx < mulIdx) {
                enabled = false;
                idx = dontIdx + 7;
                continue;
            }

            int endIdx = line.indexOf(")", mulIdx);
            String nums = line.substring(mulIdx + 4, endIdx);
            String[] parts = nums.split(",", -1);
            if (parts.length != 2 || !Helper.isNumeric(parts[0]) || !Helper.isNumeric(parts[1])
                || parts[0].length() > 3 || parts[1].length() > 3) {
                idx += 4;

                if (DEBUG) {
                    System.out.printf("rejecting = %s%n", nums);
                }

                continue;
            }

            if (enabled) {
                long a = Long.parseLong(parts[0]);
                long b = Long.parseLong(parts[1]);
                sum += (a * b);
            }

            if (DEBUG) {
                System.out.printf("nums=%s, sum=%s, enabled=%s%n", nums, sum, enabled);
            }

            idx = endIdx;
        }

        return sum;
    }
}

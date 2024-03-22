package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/17">Advent of Code 2021 Day 17</a>
 */
public class Day17Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day17_input.txt");

        List<Integer> nums = Helper.extractIntsFromText(lines.get(0), true);
        int bottomOfTargetZone = nums.get(2);
        int yVelocity = Math.abs(bottomOfTargetZone) - 1;

        //calculate triangular numbers sequence
        long[] triangularNums = new long[Math.abs(bottomOfTargetZone)];
        triangularNums[0] = 0;
        for (int i = 1; i < triangularNums.length; i++) {
            triangularNums[i] = triangularNums[i - 1] + i;
        }
        long highestPoint = triangularNums[triangularNums.length - 1];

        System.out.printf("Initial Y-velocity = %s, highest point = %s%n", yVelocity, highestPoint);

        long expected = 30628;
        if (highestPoint != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", highestPoint, expected));
        }
    }
}

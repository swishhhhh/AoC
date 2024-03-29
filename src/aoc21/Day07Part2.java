package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/7">Advent of Code 2021 Day 7</a>
 */
public class Day07Part2 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day7_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);
        List<Integer> nums = Helper.extractIntsFromText(lines.get(0));

        //step 1: get min and max positions
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE; //initialize
        for (Integer n: nums) {
            min = Math.min(min, n);
            max = Math.max(max, n);
        }

        //step 2: for i = 0 to (max - min) precalculate the triangular number for i
        long[] triangularNums = new long[max - min + 1];
        triangularNums[0] = 0;
        for (int i = 1; i < triangularNums.length; i++) {
            triangularNums[i] = triangularNums[i - 1] + i;
        }

        //step 3: for i = min to max, calculate diff to i for each position, sum total, save if shortest to date
        int target = 0;
        long leastFuel = Long.MAX_VALUE;

        for (int i = min; i <= max; i++) {
            long cumulativeFuel = 0;
            for (Integer pos: nums) {
                cumulativeFuel+= triangularNums[Math.abs(i - pos)];
            }
            if (cumulativeFuel < leastFuel) {
                target = i;
                leastFuel = cumulativeFuel;
            }
        }

        System.out.printf("Target position requiring least fuel (%s): %s%n", leastFuel, target);

        long expected = 98368490;
        if (leastFuel != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", leastFuel, expected));
        }
    }
}

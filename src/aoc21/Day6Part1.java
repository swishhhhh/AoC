package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/6">Advent of Code 2021 Day 6</a>
 */
public class Day6Part1 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day6_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);
        List<Integer> nums = Helper.extractIntsFromText(lines.get(0));

        for (int i = 1; i <= 80; i++) {
            List<Integer> nextNums = new ArrayList<>();
            List<Integer> newNums = new ArrayList<>();

            for (Integer n: nums) {
                if (n > 0) {
                    nextNums.add(n - 1);
                    continue;
                }

                //n == 0
                newNums.add(8);
                nextNums.add(6);
            }

            nums = nextNums;
            nums.addAll(newNums);
//            System.out.printf("After day %s: %s%n", i, nums);
        }

        System.out.printf("Number of lantern fish: %s%n", nums.size());
    }
}

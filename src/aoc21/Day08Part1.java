package aoc21;

import utils.ResourceLoader;

import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2021/day/8">Advent of Code 2021 Day 8</a>
 */
public class Day08Part1 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day8_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        int[] digitToSegCnt = new int[]{6, 2, 5, 5, 4, 5, 6, 3, 7, 6};
        Set<Integer> segsToCount = Set.of(digitToSegCnt[1], digitToSegCnt[4], digitToSegCnt[7], digitToSegCnt[8]);

        long answer = 0;
        for (String line : lines) {
            String[] ary = line.split("\\|")[1].trim().split(" ");
            for (String s : ary) {
                if (segsToCount.contains(s.length())) {
                    answer++;
                }
            }
        }

        System.out.printf("Number of selected digits %s%n", answer);

        long expected = 355;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }
}

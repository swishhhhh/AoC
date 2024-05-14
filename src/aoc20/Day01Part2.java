package aoc20;

import utils.ResourceLoader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/1">Advent of Code 2020 Day 1</a>
 */
public class Day01Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day1_input.txt");

        long answer = new Day01Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 193171814;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Long> nums = lines
                .stream().map(Long::parseLong)
                .filter(l -> l <= 2020) //remove potential out of bound numbers
                .collect(Collectors.toSet()) //eliminate potential duplicates
                .stream().toList(); //convert to list so we can access elements with ".get(n)"

        for (int i = 0; i < nums.size(); i++) {
            for (int j = i + 1; j < nums.size(); j++) {
                for (int k = j + 1; k < nums.size(); k++) {
                    long sum = nums.get(i) + nums.get(j) + nums.get(k);
                    if (sum == 2020) {
                        return nums.get(i) * nums.get(j) * nums.get(k);
                    }
                }
            }
        }

        throw new RuntimeException("Unable to find answer");
    }
}

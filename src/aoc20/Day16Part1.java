package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/16">Advent of Code 2020 Day 16</a>
 */
public class Day16Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day16_input.txt");

        long answer = new Day16Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 24980;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, int[]> rules = new HashMap<>();
        List<List<Integer>> tickets = new ArrayList<>();
        parse(lines, rules, tickets);

        long invalidValuesSum = 0;

        for (List<Integer> ticket : tickets) {
            invalidValuesSum += validate(ticket, rules);
        }

        return invalidValuesSum;
    }

    private void parse(List<String> lines, Map<String,int[]> rules, List<List<Integer>> otherTickets) {
        for (String line : lines) {
            if (line.contains(" or ")) {
                String fieldName = line.substring(0, line.indexOf(':'));
                rules.put(fieldName, Helper.extractIntsFromText(line).stream().mapToInt(Integer::intValue).toArray());
            } else if (line.contains(",")) {
                otherTickets.add(Helper.extractIntsFromText(line));
            }
        }
    }

    private long validate(List<Integer> ticket, Map<String, int[]> rules) {
        long sum = 0;
        for (int fldVal : ticket) {
            boolean validFieldValue =
                    rules.values().stream().anyMatch(ranges ->
                        (fldVal >= ranges[0] && fldVal <= ranges[1]) || (fldVal >= ranges[2] && fldVal <= ranges[3])
            );

            if (!validFieldValue) {
                sum += fldVal;
            }
        }

        return sum;
    }
}

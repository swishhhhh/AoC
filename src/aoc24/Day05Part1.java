package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2024/day/5">Advent of Code 2024 Day 5</a>
 */
public class Day05Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day5_input.txt");

        long answer = new Day05Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 5452;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        //load rules and updates
        Set<String> rules = new HashSet<>();
        List<List<Integer>> updates = new java.util.ArrayList<>();
        for (String line: lines) {
            if (line.contains("|")) {
                rules.add(line);
            } else if (line.contains(",")) {
                List<Integer> update = Helper.extractIntsFromText(line);
                updates.add(update);
            }
        }

        //validate updates
        long sumOfMiddleNumbers = 0;
        for (List<Integer> update: updates) {
            if (isValidUpdate(update, rules)) {
                sumOfMiddleNumbers += update.get(update.size() / 2);
            }
        }

        return sumOfMiddleNumbers;
    }

    private static boolean isValidUpdate(List<Integer> update, Set<String> rules) {
        /*
           for each update, validate page numbers starting from the 2nd page and ensuring that all preceding pages
           are not in violation of any rules
         */
        for (int i = 1; i < update.size(); i++) {
            int pageNum = update.get(i);
            int prevPageNum = update.get(i - 1);
            if (rules.contains(pageNum + "|" + prevPageNum)) {
                return false;
            }
        }
        return true;
    }
}

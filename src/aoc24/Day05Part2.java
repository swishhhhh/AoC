package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2024/day/5">Advent of Code 2024 Day 5</a>
 */
public class Day05Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day5_input.txt");

        long answer = new Day05Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 4598;
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

        //retain only invalid updates
        updates.removeIf(update -> isValidUpdate(update, rules));

        long sumOfMiddleNumbers = 0;

        //for each invalid update, rearrange the numbers to comply with the rules and make the update valid
        for (List<Integer> invalidUpdate: updates) {
            LinkedList<Integer> validUpdate = new LinkedList<>();
            validUpdate.add(invalidUpdate.get(0)); //initialize with first page number, then for subsequent pages find right insertion point
            for (int i = 1; i < invalidUpdate.size(); i++) {
                int pageToInsert = invalidUpdate.get(i);
                //figure out the insertion point by checking (from left to right) each number against any rules
                int insertAt = validUpdate.size(); //default to end of list
                for (int j = 0; j < validUpdate.size(); j++) {
                    if (rules.contains(pageToInsert + "|" + validUpdate.get(j))) {
                        insertAt = j;
                        break;
                    }
                }
                validUpdate.add(insertAt, pageToInsert);
            }
            sumOfMiddleNumbers += validUpdate.get(validUpdate.size() / 2);
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
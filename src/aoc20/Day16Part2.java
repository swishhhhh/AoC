package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/16">Advent of Code 2020 Day 16</a>
 */
public class Day16Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day16_input.txt");

        long answer = new Day16Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 809376774329L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, int[]> rules = new HashMap<>();
        List<List<Integer>> tickets = new ArrayList<>();
        parse(lines, rules, tickets);

        //save myTicket
        List<Integer> myTicket = tickets.get(0);

        //remove invalid tickets
        tickets.removeIf(ticket -> !validate(ticket, rules));

        //pre-calculate all possible matches to avoid repeated calculations
        Set<Integer> remainingFields = new HashSet<>();
        Queue<Integer> queue = new ArrayBlockingQueue<>(rules.size());
        for (int i = 0; i < rules.size(); i++) {
            remainingFields.add(i);
            queue.add(i);
        }
        Map<Integer, Set<String>> fieldToPossibleMatchingRules = new HashMap<>();
        for (int fieldIdx : remainingFields) {
            fieldToPossibleMatchingRules.put(fieldIdx,
                    new HashSet<>(getMatchingRulesForFieldIdx(fieldIdx, tickets, rules)));
        }

        Map<String, Integer> matchedRulesToFieldsIdxMap = new HashMap<>();

        /*
           for each field, cycle through the rules/field-names and retain a count of valid rules, if only 1 rule
           matched then pair that rule with the field and remove that rule from consideration for other fields
         */
        int loopsSinceLastFieldMatch = 0;
        while (!queue.isEmpty()) {
            if (loopsSinceLastFieldMatch > rules.size()) {
                throw new RuntimeException("No solution found");
            }

            int fieldIdx = queue.poll();
            List<String> possibleRules = new ArrayList<>(fieldToPossibleMatchingRules.get(fieldIdx));
            possibleRules.removeAll(matchedRulesToFieldsIdxMap.keySet()); //remove already matched rules

            if (possibleRules.size() == 1) {
                if (DEBUG) {
                    System.out.printf("Field %s is %s%n", fieldIdx, possibleRules.get(0));
                }

                String ruleName = possibleRules.get(0);
                matchedRulesToFieldsIdxMap.put(ruleName, fieldIdx);
                loopsSinceLastFieldMatch = 0;
            } else {
                if (DEBUG) {
                    System.out.printf("Field %s matches %s rules (%s) %n", fieldIdx, possibleRules.size(), possibleRules);
                }

                queue.add(fieldIdx);
                loopsSinceLastFieldMatch++;
            }
        }

        return matchedRulesToFieldsIdxMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("departure"))
                .mapToLong(entry -> myTicket.get(entry.getValue()))
                .reduce(1L, (a, b) -> a * b);
    }

    private List<String> getMatchingRulesForFieldIdx(int fieldIdx, List<List<Integer>> tickets, Map<String, int[]> rules) {
        return rules.entrySet().stream()
                .filter(entry -> {
                    int[] ranges = entry.getValue();
                    int minRange1 = ranges[0], maxRange1 = ranges[1];
                    int minRange2 = ranges[2], maxRange2 = ranges[3];

                    return tickets.stream().allMatch(ticket -> {
                        int val = ticket.get(fieldIdx);
                        return (val >= minRange1 && val <= maxRange1) ||
                                (val >= minRange2 && val <= maxRange2);
                    });
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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

    private boolean validate(List<Integer> ticket, Map<String, int[]> rules) {
        for (int fldVal : ticket) {
            boolean valid =
                    rules.values().stream().anyMatch(ranges ->
                        (fldVal >= ranges[0] && fldVal <= ranges[1]) || (fldVal >= ranges[2] && fldVal <= ranges[3])
            );

            if (!valid) {
                return false;
            }
        }

        return true;
    }
}

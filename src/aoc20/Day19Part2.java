package aoc20;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <a href="https://adventofcode.com/2020/day/19">Advent of Code 2020 Day 19</a>
 */
public class Day19Part2 {

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day19_input.txt");

        long answer = new Day19Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 381; //for part1: 248
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        AtomicLong cnt = new AtomicLong();

        Map<Integer, String> rules = parseRules(lines);

        //comment out for part1
        rules.put(8, "42 | 42 8");
        rules.put(11, "42 31 | 42 11 31");

        List<String> messages = parseMessages(lines);
        messages.forEach(message -> {
            if (matchesRule(message, 0, rules, new HashMap<>())) {
                cnt.getAndIncrement();
            }
        });

        return cnt.get();
    }

    private static boolean matchesRule(String message, int ruleId, Map<Integer, String> rules, Map<String, Boolean> memo) {
        String key = message + "-" + ruleId;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        String rule = rules.get(ruleId);

        if (rule.startsWith("\"")) { //literal value
            rule = rule.substring(1, rule.length() - 1);
            boolean result = message.equals(rule);
            memo.put(key, result);
            return result;
        }

        String[] subRules = rule.split(" \\| ");
        for (String subRule : subRules) {
            if (matchesRuleSequence(message, subRule, rules, memo)) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    private static boolean matchesRuleSequence(String message, String ruleSequence, Map<Integer, String> rules, Map<String, Boolean> memo) {
        if (message.isEmpty() && ruleSequence.isEmpty()) {
            return true;
        }
        if (message.isEmpty() || ruleSequence.isEmpty()) {
            return false;
        }

        int spaceIndex = ruleSequence.indexOf(' ');
        int currentRuleId = Integer.parseInt(spaceIndex == -1 ? ruleSequence : ruleSequence.substring(0, spaceIndex));
        String remainingRules = spaceIndex == -1 ? "" : ruleSequence.substring(spaceIndex + 1);

        for (int i = 1; i <= message.length(); i++) {
            if (matchesRule(message.substring(0, i), currentRuleId, rules, memo)) {
                if (matchesRuleSequence(message.substring(i), remainingRules, rules, memo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<Integer, String> parseRules(List<String> lines) {
        Map<Integer, String> rules = new HashMap<>();
        lines.stream()
                .filter(line -> line.contains(":"))
                .forEach(line -> {
                    String[] ary = line.split(":");
                    rules.put(Integer.parseInt(ary[0].trim()), ary[1].trim());
                });

        return rules;
    }

    private List<String> parseMessages(List<String> lines) {
        return lines.stream()
                .filter(line -> !line.contains(":") && !line.isBlank())
                .toList();
    }
}

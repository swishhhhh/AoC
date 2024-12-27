package aoc24;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/19">Advent of Code 2024 Day 19</a>
 */
public class Day19Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day19_input.txt");

        long answer = new Day19Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 639963796864990L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        String[] patterns = Arrays.stream(lines.get(0).split(",")).map(String::trim).toArray(String[]::new);
        List<String> designs = new ArrayList<>(lines.size() - 2);
        for (int i = 2; i < lines.size(); i++) {
            designs.add(lines.get(i));
        }

        long cnt = 0;

        for (String design : designs) {
            long n = getNumberOfVariations(design, 0, patterns, 0, new HashMap<>());
            if (DEBUG) {
               System.out.printf("%s variations for design%s%n", n, design);
            }
            cnt += n;
        }

        return cnt;
    }

    private long getNumberOfVariations(String design, int idx, String[] patterns, long incomingTotal, Map<String, Long> cachedTotals) {
        if (idx >= design.length()) {
            return incomingTotal + 1;
        }

        if (cachedTotals.containsKey(design.substring(idx))) {
            return incomingTotal + cachedTotals.get(design.substring(idx));
        }

        long total = incomingTotal;
        for (String pattern : patterns) {
            if (design.startsWith(pattern, idx)) {
                long newTotal = getNumberOfVariations(design, idx + pattern.length(), patterns, total, cachedTotals);
                    cachedTotals.put(design.substring(idx + pattern.length()), newTotal - total);
                total = newTotal;
            }
        }

        return total;
    }
}
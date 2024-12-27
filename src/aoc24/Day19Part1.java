package aoc24;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/19">Advent of Code 2024 Day 19</a>
 */
public class Day19Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day19_input.txt");

        long answer = new Day19Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 260;
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

        long validDesigns = 0;

        for (String design : designs) {
            if (validDesign(design, 0, patterns, new HashSet<>())) {
                validDesigns++;
                if (DEBUG) {
                    System.out.printf("Design %s is valid%n", design);
                }
            } else {
                if (DEBUG) {
                    System.out.printf("Design %s is NOT valid%n", design);
                }
            }
        }

        return validDesigns;
    }

    private boolean validDesign(String design, int idx, String[] patterns, Set<String> impossibles) {
        if (idx >= design.length()) {
            return true;
        }

        if (impossibles.contains(design.substring(idx))) {
            return false;
        }

        //if the design starting at idx begins with any of the patterns, keep going, otherwise return false
        for (String pattern : patterns) {
            if (design.startsWith(pattern, idx)) {
                if (validDesign(design, idx + pattern.length(), patterns, impossibles)) {
                    return true;
                } else {
                    impossibles.add(design.substring(idx + pattern.length()));
                }
            }
        }

        return false;
    }
}
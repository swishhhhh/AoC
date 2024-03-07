package aoc21;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/10">Advent of Code 2021 Day 10</a>
 */
public class Day10Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day10_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        long answer = getMiddleScore(lines);
        System.out.printf("Middle score = %s%n", answer);

        long expected = 2292863731L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long getMiddleScore(List<String> lines) {
        Map<Character, Character> pairingMap = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
        Set<Character> openChars = pairingMap.keySet();
        Map<Character, Integer> scoringMap = Map.of(')', 1, ']', 2, '}', 3, '>', 4);
        List<Long> scores = new ArrayList<>();

        for (String line : lines) {
            Stack<Character> stack = new Stack<>();
            boolean corruptLine = false;

            for (char c : line.toCharArray()) {
                if (openChars.contains(c)) {
                    stack.push(c);
                    continue;
                }

                //at this point we're dealing with a closing char
                char expectedClosingChar = pairingMap.get(stack.pop());
                if (c != expectedClosingChar) {
                    //unexpected closing char encountered, corrupted line, skip
                    corruptLine = true;
                    break;
                }
            }

            if (corruptLine || stack.isEmpty()) {
                continue;
            }

            //at this point we're dealing with an incomplete line
            long lineScore = 0;
            while (!stack.isEmpty()) {
                char c = stack.pop();
                char closingChar = pairingMap.get(c);
                lineScore*= 5;
                lineScore+= scoringMap.get(closingChar);
            }

            scores.add(lineScore);

            if (DEBUG) {
                System.out.printf("Line score = %s%n", lineScore);
            }
        }

        Collections.sort(scores);

        return scores.get(scores.size() / 2); //return middle score
    }
}

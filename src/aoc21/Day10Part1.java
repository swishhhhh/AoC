package aoc21;

import utils.ResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * <a href="https://adventofcode.com/2021/day/10">Advent of Code 2021 Day 10</a>
 */
public class Day10Part1 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day10_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        long answer = scoreSyntax(lines);
        System.out.printf("Score = %s%n", answer);

        long expected = 415953;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long scoreSyntax(List<String> lines) {
        Map<Character, Character> pairingMap = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
        Set<Character> openChars = pairingMap.keySet();
        Map<Character, Integer> scoringMap = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);

        long score = 0;

        for (String line : lines) {
            Stack<Character> stack = new Stack<>();
            for (char c : line.toCharArray()) {
                if (openChars.contains(c)) {
                    stack.push(c);
                    continue;
                }

                //at this point we're dealing with a closing char
                char expectedClosingChar = pairingMap.get(stack.pop());
                if (c == expectedClosingChar) {
                    continue;
                }

                //unexpected closing char encountered, score it
                score+= scoringMap.get(c);
                break;
            }
        }

        return score;
    }
}

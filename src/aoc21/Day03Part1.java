package aoc21;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/3">Advent of Code 2021 Day 3</a>
 */
public class Day03Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day3_input.txt");

        int EXPECTED_LINE_LENGTH = 12;
        int[] bitCounters = new int[EXPECTED_LINE_LENGTH]; //increment each counter for 1s and decrement for 0s

        int lineCtr = 0;
        for (String line: lines) {
            lineCtr++;
            char[] lineChars = line.toCharArray();
            if (lineChars.length != EXPECTED_LINE_LENGTH) {
                throw new IllegalArgumentException(String.format("Line length != %s (line number %s%n) ", EXPECTED_LINE_LENGTH, lineCtr));
            }

            int idx = 0;
            for (char c: line.toCharArray()) {
                if (c == '0') {
                    bitCounters[idx]--;
                } else {
                    bitCounters[idx]++;
                }
                idx++;
            }
        }

        StringBuilder gamma = new StringBuilder();
        StringBuilder epsilon = new StringBuilder();
        for (int bitCounter : bitCounters) {
            if (bitCounter > 0) {
                gamma.append("1");
                epsilon.append("0");
            } else {
                gamma.append("0");
                epsilon.append("1");
            }
        }

        int gammaN = Integer.parseInt(gamma.toString(), 2);
        int epsilonN = Integer.parseInt(epsilon.toString(), 2);
        long answer = gammaN * epsilonN;
        System.out.printf("gamma = %s, epsilon = %s, product = %s%n", gammaN, epsilonN, answer);

        long expected = 741950;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }
}

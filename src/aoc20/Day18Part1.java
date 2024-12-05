package aoc20;

import datastructs.Equation;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/18">Advent of Code 2020 Day 18</a>
 */
public class Day18Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day18_input.txt");

        long answer = new Day18Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 13976444272545L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long sum = 0;
        for (String line : lines) {
            sum += evaluateExpression(line);
        }
        return sum;
    }

    private long evaluateExpression(String line) {
        long result = 0;
        Equation.Operator operator = Equation.Operator.ADD;
        int idx = 0;

        while(idx < line.length()) {
            char c = line.charAt(idx);
            if (c == ' ') {
                idx++;
                continue;
            }

            if (c == '+' || c == '*') {
                operator = c == '+' ? Equation.Operator.ADD : Equation.Operator.MULTIPLY;
                idx++;
                continue;
            }

            if (c == '(') {
                int closingParenIdx = findClosingParenIdx(line, idx);
                long subResult = evaluateExpression(line.substring(idx + 1, closingParenIdx));
                result = operator == Equation.Operator.ADD ? result + subResult : result * subResult;
                idx = closingParenIdx + 1;
                continue;
            }

            if (!Helper.isDigit(c)) {
                throw new RuntimeException(String.format("Unexpected character %s at idx %s", c, idx));
            }

            //assumption: all the numbers in the input will be single-digit numbers
            result = operator == Equation.Operator.ADD ? result + (c - '0') : result * (c - '0');
            idx++;
        }

        return result;
    }

    private int findClosingParenIdx(String line, int idx) {
        int openParenCount = 1;
        while (openParenCount > 0) {
            idx++;
            switch (line.charAt(idx)) {
                case '(' -> openParenCount++;
                case ')' -> openParenCount--;
            }
        }
        return idx;
    }
}

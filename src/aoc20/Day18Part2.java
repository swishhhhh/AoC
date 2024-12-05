package aoc20;

import datastructs.Equation;
import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/18">Advent of Code 2020 Day 18</a>
 */
public class Day18Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day18_input.txt");

        long answer = new Day18Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 88500956630893L;
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
        String resolvedLine = processSubExpressions(line); //process the parts of the line in parentheses
        resolvedLine = processAdditionOrMultiplications(resolvedLine, Equation.Operator.ADD);
        resolvedLine = processAdditionOrMultiplications(resolvedLine, Equation.Operator.MULTIPLY);
        return Long.parseLong(resolvedLine.trim());
    }

    private String processSubExpressions(String line) {
        StringBuilder sb = new StringBuilder();
        int idx = 0;

        while(idx < line.length()) {
            char c = line.charAt(idx);
            if (c != '(') {
                sb.append(c);
                idx++;
                continue;
            }

            //c is '(', so find the closing parenthesis
            int closingParenIdx = findClosingParenIdx(line, idx);
            long subResult = evaluateExpression(line.substring(idx + 1, closingParenIdx));
            sb.append(subResult);
            idx = closingParenIdx + 1;
        }

        return sb.toString();
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

    private String processAdditionOrMultiplications(String line, Equation.Operator operator) {
        //by the time we get here, we've already processed the sub-expressions (so there shouldn't be any parentheses in the line)
        StringBuilder sb = new StringBuilder();
        int idx = 0;

        while(idx < line.length()) {
            char c = line.charAt(idx);
            if (!String.valueOf(c).equals(operator.getSymbol())) {
                sb.append(c);
                idx++;
                continue;
            }

            //c is an operator (i.e. '+' or '*'), so we need to find the previous number (already on sb)
            StringBuilder prevNumSb = new StringBuilder();
            while (!Helper.isDigit(sb.charAt(sb.length() - 1))) {
                sb.replace(sb.length() - 1, sb.length(), "");
            }
            while (!sb.isEmpty() && Helper.isDigit(sb.charAt(sb.length() - 1))) {
                prevNumSb.insert(0, sb.charAt(sb.length() - 1));
                sb.replace(sb.length() - 1, sb.length(), ""); //remove the prev char from sb
            }
            long prevNum = Long.parseLong(prevNumSb.toString());

            //find next number in line
            idx++;
            StringBuilder nextNumSb = new StringBuilder();
            while (!Helper.isDigit(line.charAt(idx))) {
                idx++;
            }
            while (idx < line.length() && Helper.isDigit(line.charAt(idx))) {
                nextNumSb.append(line.charAt(idx));
                idx++;
            }
            long nextNum = Long.parseLong(nextNumSb.toString());

            long result = operator == Equation.Operator.ADD ? prevNum + nextNum : prevNum * nextNum;
            idx++;

            //append the sum to the end of sb
            sb.append(result);
            sb.append(' ');
        }

        return sb.toString();
    }
}

package aoc24;

import aoc24.day24.BooleanGate;
import utils.ResourceLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2024/day/24">Advent of Code 2024 Day 24</a>
 */
public class Day24Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day24_input.txt");

        long answer = new Day24Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 64755511006320L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, Boolean> solvedVariables = new HashMap<>();
        Queue<BooleanGate> queue = new ArrayDeque<>();

        loadInput(lines, solvedVariables, queue);

        solveGates(queue, solvedVariables);

        //collect solvedVariable names starting with z, sort them in reverse order, then build a binary string based on
        // their (z-variables) values, and finally convert that binary string to a base-10 number and return it
        return solvedVariables
                .keySet().stream()
                .filter(v -> v.startsWith("z"))
                .sorted(Comparator.reverseOrder())
                .map(var -> solvedVariables.get(var) ? "1" : "0")
                .collect(Collectors.collectingAndThen(
                        Collectors.joining(),
                        binaryString -> Long.parseLong(binaryString, 2)
                ));
    }

    private static void loadInput(List<String> lines, Map<String, Boolean> solvedVariables, Queue<BooleanGate> queue) {
        //read in solved variables (first part of input) and boolean-gates (2nd part of input)
        for (String line: lines) {
            if (line.startsWith("#")) {
                continue;
            }

            if (line.contains(":")) {
                String[] ary = line.split(":");
                String varName = ary[0];
                boolean value = ary[1].trim().equals("1");
                solvedVariables.put(varName, value);
                continue;
            }

            if (line.contains("->")) {
                String[] ary = line.split(" ");
                String var1 = ary[0], var2 = ary[2], operatorName = ary[1], resultVar = ary[4];
                BooleanGate gate = new BooleanGate(resultVar, var1, var2, BooleanGate.fromSymbol(operatorName));
                queue.add(gate);
            }
        }
    }

    private static void solveGates(Queue<BooleanGate> queue, Map<String, Boolean> solvedVariables) {
        long cyclesCtr = 0;
        int cyclesSinceLastSolvedCtr = 0;
        while (!queue.isEmpty()) {
            cyclesCtr++;
            BooleanGate bg = queue.poll();
            if (!bg.isOperand1Resolved()) {
                Boolean val = solvedVariables.get(bg.getOperandVariable1());
                if (val != null) {
                    bg.resolveOperandValue1(val);
                }
            }
            if (!bg.isOperand2Resolved()) {
                Boolean val = solvedVariables.get(bg.getOperandVariable2());
                if (val != null) {
                    bg.resolveOperandValue2(val);
                }
            }
            if (bg.isOperand1Resolved() && bg.isOperand2Resolved()) {
                boolean result = bg.solve();
                solvedVariables.put(bg.getResultVarName(), result);
                cyclesSinceLastSolvedCtr = 0;

            } else { //put unsolved gate back onto the tail of the queue
                queue.add(bg);
                cyclesSinceLastSolvedCtr++;
                if (cyclesSinceLastSolvedCtr > queue.size()) {
                    System.err.printf("Houston, we got a problem! Haven't solved a gate for %s cycles, " +
                            "and queue size is %s%n", cyclesSinceLastSolvedCtr, queue.size());
                    break;
                }
            }
        }

        if (DEBUG) {
            System.out.printf("Cycles Ctr = %s%n", cyclesCtr);
        }
    }
}
package aoc20;

import org.apache.commons.math3.util.Pair;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/8">Advent of Code 2020 Day 8</a>
 */
public class Day08Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day8_input.txt");

        long answer = new Day08Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2188;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Pair<String, Integer>> instructions = new ArrayList<>();
        for (String line : lines) {
            String[] ary = line.split(" ");
            instructions.add(new Pair<>(ary[0], Helper.extractIntsFromText(ary[1], true).get(0)));
        }

        int idx = -1;
        for (Pair<String, Integer> instruction : instructions) {
            idx++;
            String instructionType = instruction.getKey();
            if (instructionType.equals("acc")) {
                continue;
            }

            long result = executeBootSequence(idx, instructions);
            if (result != Long.MAX_VALUE) {
                return result;
            }
        }

        throw new RuntimeException("Unable to solve");
    }

    private long executeBootSequence(int instructionNumToFlip, List<Pair<String, Integer>> instructions) {
        Set<Integer> visited = new HashSet<>();
        long accum = 0;
        int idx = 0;
        while (idx < instructions.size()) {
            Pair<String, Integer> instruction = instructions.get(idx);
            String instType = instruction.getKey();
            if (idx == instructionNumToFlip) { //flip instruction type
                instType = instType.equals("nop") ? "jmp" : "nop";
            }

            if (visited.contains(idx)) {
                return Long.MAX_VALUE; //indicates endless loop
            }
            visited.add(idx);

            int instValue = instruction.getValue();
            switch (instType) {
                case "nop" -> idx++;
                case "acc" -> {
                    accum += instValue;
                    idx++;
                }
                case "jmp" -> idx += instValue;
            }
        }

        return accum;
    }
}

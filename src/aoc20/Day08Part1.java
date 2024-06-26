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
public class Day08Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day8_input.txt");

        long answer = new Day08Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1684;
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

        long accum = 0;
        int idx = 0;
        Set<Integer> visited = new HashSet<>();
        while (!visited.contains(idx)) {
            visited.add(idx);

            switch (instructions.get(idx).getKey()) {
                case "nop" -> idx++;
                case "acc" -> {
                    accum += instructions.get(idx).getValue();
                    idx++;
                }
                case "jmp" -> idx += instructions.get(idx).getValue();
            }
        }

        return accum;
    }

}

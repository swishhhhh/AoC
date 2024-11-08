package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/14">Advent of Code 2020 Day 14</a>
 */
public class Day14Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day14_input.txt");

        long answer = new Day14Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 14862056079561L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        String mask = "";
        Map<Long, Long> memory = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("mask")) {
                mask = line.split("=")[1].trim();
                continue;
            }

            List<Long> nums = Helper.extractLongsFromText(line);
            long address = nums.get(0);
            long decimalNum = nums.get(1);
            String output = Helper.padLeft(Long.toBinaryString(decimalNum), '0', 36); //convert to 36 bit binary string
            output = applyMask(output, mask); //apply mask
            memory.put(address, Long.parseLong(output, 2)); //convert binary (output) to decimal and update memory
        }

        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    private String applyMask(String input, String mask) {
        char[] output = input.toCharArray();

        for (int i = 0; i < mask.length(); i++) {
            if (mask.charAt(i) != 'X') {
                output[i] = mask.charAt(i);
            }
        }

        return new String(output);
    }
}

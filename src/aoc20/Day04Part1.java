package aoc20;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/4">Advent of Code 2020 Day 4</a>
 */
public class Day04Part1 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day4_input.txt");

        long answer = new Day04Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 200;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Map<String, String>> passports = new ArrayList<>();
        Map<String, String> pass = new HashMap<>();

        for (String line : lines) {
            if (line.trim().length() == 0) {
                passports.add(pass);
                pass = new HashMap<>();
                continue;
            }

            for (String token : line.split(" ")) {
                String[] kv = token.split(":");
                pass.put(kv[0], kv[1]);
            }
        }

        passports.add(pass);

        return passports.stream().filter(this::isValid).count();
    }

    private boolean isValid(Map<String, String> p) {
        return p.size() == 8 || (p.size() == 7 && !p.containsKey("cid"));
    }
}

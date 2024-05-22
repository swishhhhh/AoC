package aoc20;

import utils.ResourceLoader;

import java.util.*;

import static utils.Helper.isBetween;

/**
 * <a href="https://adventofcode.com/2020/day/4">Advent of Code 2020 Day 4</a>
 */
public class Day04Part2 {
    public static void main(String... args) throws Exception {

        List<String> lines = ResourceLoader.readStrings("aoc20/Day4_input.txt");

        long answer = new Day04Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 116;
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
        if (p.size() < 7 || p.size() == 7 && p.containsKey("cid")) return false;

        if (!isBetween(1920, Integer.parseInt(p.get("byr")), 2002)) return false;

        if (!isBetween(2010, Integer.parseInt(p.get("iyr")), 2020)) return false;

        if (!isBetween(2020, Integer.parseInt(p.get("eyr")), 2030)) return false;

        String hgt = p.get("hgt");
        if (!hgt.endsWith("cm") && !hgt.endsWith("in")) return false;
        int units = Integer.parseInt(hgt.substring(0, hgt.length() - 2));
        if (hgt.endsWith("cm") && !isBetween(150, units, 193)) return false;
        if (hgt.endsWith("in") && !isBetween(59, units, 76)) return false;

        String hcl = p.get("hcl");
        if (hcl.length() != 7 || !hcl.matches("^#[a-zA-Z0-9]*$")) return false;

        if (!Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(p.get("ecl"))) return false;

        String pid = p.get("pid");
        if (pid.length() != 9 || !pid.matches("^[0-9]*$")) return false;

        return true;
    }
}

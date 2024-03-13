package aoc21;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/13">Advent of Code 2021 Day 13</a>
 */
public class Day13Part1 {

    public static void main(String... args) throws Exception {

        String resourceName = "aoc21/Day13_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        Set<Coordinates> coords = loadCoordinates(lines);
        String firstFoldInstruction = getFirstFoldInstruction(lines);

        long answer;
        assert firstFoldInstruction != null;
        if (firstFoldInstruction.contains("x=")) {
            answer = foldOnXaxis(coords, firstFoldInstruction);
        } else {
            answer = foldOnYaxis(coords, firstFoldInstruction);
        }

        System.out.printf("Number of dots = %s%n", answer);

        long expected = 802;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static Set<Coordinates> loadCoordinates(List<String> lines) {
        Set<Coordinates> coords = new HashSet<>();

        for (String line : lines) {
            if (line.isBlank()) {
                break;
            }

            String[] ary = line.trim().split(",");
            coords.add(new Coordinates(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])));
        }

        return coords;
    }

    private static String getFirstFoldInstruction(List<String> lines) {
        for (String line : lines) {
            if (line.startsWith("fold")) {
                return line;
            }
        }

        return null;
    }

    private static long foldOnXaxis(Set<Coordinates> coords, String firstFoldInstruction) {
        int centerLine = Integer.parseInt(firstFoldInstruction.split("=")[1]);
        Set<Coordinates> folded = new HashSet<>();

        for (Coordinates c : coords) {
            if (c.x() < centerLine) {
                folded.add(c);
            } else {
                Coordinates transposed = new Coordinates(centerLine - (c.x() - centerLine), c.y());
                folded.add(transposed);
            }
        }

        return folded.size();
    }

    private static long foldOnYaxis(Set<Coordinates> coords, String firstFoldInstruction) {
        int centerLine = Integer.parseInt(firstFoldInstruction.split("=")[1]);
        Set<Coordinates> folded = new HashSet<>();

        for (Coordinates c : coords) {
            if (c.y() < centerLine) {
                folded.add(c);
            } else {
                Coordinates transposed = new Coordinates(c.x(), centerLine - (c.y() - centerLine));
                folded.add(transposed);
            }
        }

        return folded.size();
    }
}

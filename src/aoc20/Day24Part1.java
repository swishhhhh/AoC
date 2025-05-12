package aoc20;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/24">Advent of Code 2020 Day 24</a>
 */
public class Day24Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day24_input.txt");

        long answer = new Day24Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 523;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Set<Coordinates> blackTiles = new HashSet<>();

        for (String line : lines) {
            processLine(line, blackTiles);
        }

        return blackTiles.size();
    }

    private void processLine(String line, Set<Coordinates> flippedTiles) {
        int x = 0, y = 0;
        int idx = 0;
        while (idx < line.length()) {
            if (line.substring(idx).startsWith("e")) {
                x+= 2;
                idx++;
            } else if (line.substring(idx).startsWith("w")) {
                x-= 2;
                idx++;
            } else  if (line.substring(idx).startsWith("ne")) {
                x+= 1;
                y-= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("se")) {
                x+= 1;
                y+= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("nw")) {
                x-= 1;
                y-= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("sw")) {
                x-= 1;
                y+= 2;
                idx+= 2;
            } else {
                throw new RuntimeException(String.format("Unexpected line %s at idx %s", line, idx));
            }
        }

        Coordinates tile = new Coordinates(x, y);
        if (!flippedTiles.remove(tile)) { //returns false if the tile was already not present (before the removal attempt)
            flippedTiles.add(tile);
        }
    }
}

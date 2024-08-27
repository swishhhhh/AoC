package aoc20;

import datastructs.Direction;
import utils.ResourceLoader;

import java.util.List;

import static datastructs.Direction.*;
import static datastructs.Direction.WEST;

/**
 * <a href="https://adventofcode.com/2020/day/12">Advent of Code 2020 Day 12</a>
 */
public class Day12Part1 {
    private static final List<Direction> DIRECTIONS = List.of(NORTH, EAST, SOUTH, WEST);

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day12_input.txt");

        long answer = new Day12Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 582;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long xPos = 0, yPos = 0;
        Direction facingDirection = Direction.EAST;

        for (String line : lines) {
            char instruction = line.charAt(0);
            int units = Integer.parseInt(line.substring(1));

            if (instruction == 'F') {
                instruction = facingDirection.getSymbol().charAt(0);
            }

            switch (instruction) {
                case 'N' -> yPos += units;
                case 'S' -> yPos -= units;
                case 'E' -> xPos += units;
                case 'W' -> xPos -= units;
                case 'R', 'L' -> facingDirection = rotateDirection(facingDirection, instruction, units);
            }
        }

        return Math.abs(xPos) + Math.abs(yPos);
    }

    private static Direction rotateDirection(Direction currentFacingDir, char rightOrLeft, int degrees) {
        int idx = DIRECTIONS.indexOf(currentFacingDir);
        int ninetyDegreeTurns = (degrees / 90) % 4;

        switch (rightOrLeft) {
            case 'R' -> idx = (idx + ninetyDegreeTurns) % 4;
            case 'L' -> {
                idx -= ninetyDegreeTurns;
                if (idx < 0) {
                    idx = 4 + idx;
                }
            }
        }

        return DIRECTIONS.get(idx);
    }
}

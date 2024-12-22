package aoc24;

import org.apache.commons.math3.util.Pair;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static utils.Helper.greatestCommonDenominator;

/**
 * <a href="https://adventofcode.com/2024/day/13">Advent of Code 2024 Day 13</a>
 */
public class Day13Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day13_input.txt");

        long answer = new Day13Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 107487112929999L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Pair<Long, Long>> aButtons = new ArrayList<>();
        List<Pair<Long, Long>> bButtons = new ArrayList<>();
        List<Pair<Long, Long>> targets = new ArrayList<>();
        
        parseInput(lines, aButtons, bButtons, targets);
        
        long sum = 0;

        for (int i = 0; i < aButtons.size(); i++) {
            sum += solve(aButtons.get(i), bButtons.get(i), targets.get(i));
        }

        return sum;
    }

    private void parseInput(List<String> lines, List<Pair<Long, Long>> aButtons,
                            List<Pair<Long, Long>> bButtons, List<Pair<Long, Long>> targets) {
        final long TARGET_ADDITION = 10000000000000L;

        for (String line : lines) {
            List<Long> nums = Helper.extractLongsFromText(line);
            if (line.startsWith("Button A")) {
                aButtons.add(new Pair<>(nums.get(0), nums.get(1)));
            } else if (line.startsWith("Button B")) {
                bButtons.add(new Pair<>(nums.get(0), nums.get(1)));
            } else if (line.startsWith("Prize")) {
                targets.add(new Pair<>(nums.get(0) + TARGET_ADDITION, nums.get(1) + TARGET_ADDITION));
            }
        }
    }

    private long solve(Pair<Long, Long> aButton, Pair<Long, Long> bButton, Pair<Long, Long> target) {
        long aButtonX = aButton.getFirst();
        long aButtonY = aButton.getSecond();
        long bButtonX = bButton.getFirst();
        long bButtonY = bButton.getSecond();
        long targetX = target.getFirst();
        long targetY = target.getSecond();

        //shortcut to bail early if target is unreachable
        if (targetX % greatestCommonDenominator(aButtonX, bButtonX) != 0
                || targetY % greatestCommonDenominator(aButtonY, bButtonY) != 0) {
            return 0;
        }

        //solve the system using Cramer's rule

        //calculate determinant
        long det = (aButtonX * bButtonY) - (aButtonY * bButtonX);
        if (det == 0) {
            return 0; //to avoid dividing by zero
        }

        double x = (double)((targetX * bButtonY) - (targetY * bButtonX)) / det;
        double y = (double)((targetY * aButtonX) - (targetX * aButtonY)) / det;

        //convert x and y to longs (from doubles) and make sure they're not negative
        long minA = Math.max(0, (long)Math.ceil(x));
        long minB = Math.max(0, (long)Math.ceil(y));

        //verify the solution
        if ((minA * aButtonX) + (minB * bButtonX) == targetX
                && (minA * aButtonY) + (minB * bButtonY) == targetY) {
            return (3L * minA) + minB;
        }

        return 0;
    }
}

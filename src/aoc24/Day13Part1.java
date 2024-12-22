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
public class Day13Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day13_input.txt");

        long answer = new Day13Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 25629;
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
        for (String line : lines) {
            List<Long> nums = Helper.extractLongsFromText(line);
            if (line.startsWith("Button A")) {
                aButtons.add(new Pair<>(nums.get(0), nums.get(1)));
            } else if (line.startsWith("Button B")) {
                bButtons.add(new Pair<>(nums.get(0), nums.get(1)));
            } else if (line.startsWith("Prize")) {
                targets.add(new Pair<>(nums.get(0), nums.get(1)));
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

        long maxButtonPressesA = Math.max(targetX / aButtonX, targetY / aButtonY);
        long maxButtonPressesB = Math.max(targetX / bButtonX, targetY / bButtonY);
        long leastTokens = Long.MAX_VALUE;

        for (int a = 0; a <= maxButtonPressesA; a++) {
            for (int b = 0; b < maxButtonPressesB; b++) {
                if ((a * aButtonX) + (b * bButtonX) == targetX
                    && (a * aButtonY) + (b * bButtonY) == targetY)  {
                    //found a solution
                    long tokens = (3L * a) + b;
                    leastTokens = Math.min(leastTokens, tokens);

                    if ((a * aButtonX) + (b * bButtonX) >= targetX
                        || (a * aButtonY) + (b * bButtonY) >= targetY){
                        break; //no need to continue since already met or overshot the target
                    }
                }
            }
        }

        return leastTokens == Long.MAX_VALUE ? 0 : leastTokens;
    }
}

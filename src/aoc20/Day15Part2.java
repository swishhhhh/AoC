package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/15">Advent of Code 2020 Day 15</a>
 */
public class Day15Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day15_input.txt");

        int iterations = 30_000_000;
        long answer = new Day15Part2().execute(lines, iterations);
        System.out.printf("Answer = %s%n", answer);

        long expected = 8984;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines, int iterations) {
        // Fixed-size array to store the last two occurrences of each number
        int[] history = new int[iterations]; //index of the array signifies the num spoken, the value of each array element is the turn that num was last spoken in (or -1 if the num was never spoken)
        Arrays.fill(history, -1); // Initialize with -1 for unspoken numbers

        List<Long> initialNums = Helper.extractLongsFromText(lines.get(0));
        int lastSpokenNum = -1;

        //process initial numbers
        int thisTurn = 0;
        for (long num : initialNums) {
            thisTurn++;
            history[(int) num] = thisTurn;
            lastSpokenNum = (int) num;
        }

        //subsequent turns
        for (; thisTurn < iterations; thisTurn++) {
            int lastTurnNumWasSpoken = history[lastSpokenNum];
            history[lastSpokenNum] = thisTurn;

            //determine the next number to speak
            lastSpokenNum = (lastTurnNumWasSpoken == -1) ? 0 : thisTurn - lastTurnNumWasSpoken;
        }

        return lastSpokenNum;
    }
}

package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/15">Advent of Code 2020 Day 15</a>
 */
public class Day15Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day15_input.txt");

        int iterations = 2020;
        long answer = new Day15Part1().execute(lines, iterations);
        System.out.printf("Answer = %s%n", answer);

        long expected = 234;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines, int iterations) {
        Map<Long, int[]> history = new HashMap<>();
        long lastSpoken = -1;
        List<Long> initialNums = Helper.extractLongsFromText(lines.get(0));
        int turn = 0;

        //process the initial (input) numbers
        for (long num : initialNums) {
            turn++;
            history.put(num, new int[]{turn, -1}); // {currentTurn, previousTurn}
            lastSpoken = num;
        }

        while (turn < iterations) {
            turn++;
            int[] turns = history.getOrDefault(lastSpoken, new int[]{-1, -1});

            //nextSpoken = 0 if it was spoken previously only once, otherwise it's the difference of last 2 times it was spoken
            long nextSpoken = (turns[1] == -1) ? 0 : turns[0] - turns[1];

            int[] nextTurns = history.getOrDefault(nextSpoken, new int[]{-1, -1});
            history.put(nextSpoken, new int[]{turn, nextTurns[0]});

            lastSpoken = nextSpoken;
        }

        return lastSpoken;
    }
}

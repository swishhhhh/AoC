package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/21">Advent of Code 2021 Day 21</a>
 */
public class Day21Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day21_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 412344;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        int pos1 = Helper.extractIntsFromText(lines.get(0)).get(1);
        int pos2 = Helper.extractIntsFromText(lines.get(1)).get(1);

        int die = 0;
        int score1 = 0, score2 = 0, rolls = 0, turn = 0;

        do {
            turn++;
            int increment = ((die + 1) % 100) + ((die + 2) % 100) + ((die + 3) % 100);
            die = (die + 3) % 100;
            rolls += 3;

            if (turn % 2 == 1) {
                pos1 = ((pos1 + increment - 1) % 10) + 1; //-1/+1 is to roll 0 over to 10
                score1 += pos1;
            } else {
                pos2 = ((pos2 + increment - 1) % 10) + 1;
                score2 += pos2;
            }

        } while (score1 < 1000 && score2 < 1000);

        return (long) Math.min(score1, score2) * rolls;
    }
}

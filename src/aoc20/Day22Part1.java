package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/22">Advent of Code 2020 Day 22</a>
 */
public class Day22Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day22_input.txt");

        long answer = new Day22Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 32083;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Deque<Integer> player1 = new ArrayDeque<>();
        Deque<Integer> player2 = new ArrayDeque<>();

        loadDecks(lines, player1, player2);

        while (!player1.isEmpty() && !player2.isEmpty()) {
            int card1 = player1.pop();
            int card2 = player2.pop();

            if (card1 > card2) {
                player1.add(card1);
                player1.add(card2);
            } else {
                player2.add(card2);
                player2.add(card1);
            }
        }

        return calculateScore(player1.isEmpty() ? player2 : player1);
    }

    private void loadDecks(List<String> lines, Deque<Integer> player1, Deque<Integer> player2) {
        Deque<Integer> current = player1;
        for (String line : lines) {
            if (line.startsWith("Player 2")) {
                current = player2;
                continue;
            }

            if (!Helper.isNumeric(line)) {
                continue;
            }

            current.add(Integer.parseInt(line));
        }
    }

    private long calculateScore(Deque<Integer> cards) {
        int multiplier = cards.size();
        long score = 0;
        for (int card : cards) {
            score += (long) card * multiplier;
            multiplier--;
        }

        return score;
    }
}

package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/22">Advent of Code 2020 Day 22</a>
 */
public class Day22Part2 {
    private static final boolean DEBUG = false;
    private long gamesCounter = 0, roundsCounter = 0;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day22_input.txt");

        Day22Part2 executor = new Day22Part2();
        long answer = executor.execute(lines);
        System.out.printf("Answer = %s, total games = %s, total rounds = %s%n",
                answer, executor.gamesCounter, executor.roundsCounter);

        long expected = 35495;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Deque<Integer> player1 = new ArrayDeque<>();
        Deque<Integer> player2 = new ArrayDeque<>();

        loadDecks(lines, player1, player2);

        Deque<Integer> winner = playGame(player1, player2, new HashSet<>(), new HashSet<>(), 1) ? player1 : player2;

        return calculateScore(winner);
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

    /**
     * @return true if player1 won, false if player2 won
     */
    private boolean playGame(Deque<Integer> player1, Deque<Integer> player2,
                             Set<String> player1States, Set<String> player2States, int gameNumber) {
        gamesCounter++;
        int roundThisGame = 0;

        while (!player1.isEmpty() && !player2.isEmpty()) {
            roundThisGame++;
            roundsCounter++;

            /*
              if there was a previous round in this game that had exactly the same cards in the same order in the same
               players' decks, the game instantly ends in a win for player 1.
             */
            if (!player1States.add(getDeckState(player1)) || !player2States.add(getDeckState(player2))) {
                if (DEBUG) {
                    System.out.printf("[game %s, round %s] Player 1 instant win%n", gameNumber, roundThisGame);
                }

                return true; //indicates player1 won
            }

            if (DEBUG) {
                System.out.printf("[game %s, round %s] %s, %s%n", gameNumber, roundThisGame, player1, player2);
            }

            int card1 = player1.pop();
            int card2 = player2.pop();

            /*
              If both players have at least as many cards remaining in their deck as the value of the card they just
               drew, the winner of the round is determined by playing a new game of Recursive Combat
             */
            boolean player1Won;
            if (player1.size() >= card1 && player2.size() >= card2) {
                player1Won = playGame(
                        new ArrayDeque<>(player1.stream().limit(card1).toList()),
                        new ArrayDeque<>(player2.stream().limit(card2).toList()),
                        new HashSet<>(), new HashSet<>(), gameNumber + 1);
            } else { //the winner of the round is the player with the higher-value card
                player1Won = card1 > card2;
            }

            if (player1Won) {
                player1.add(card1);
                player1.add(card2);
            } else {
                player2.add(card2);
                player2.add(card1);
            }
        }

        return !player1.isEmpty();
    }

    private String getDeckState(Deque<Integer> cards) {
        return cards.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

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

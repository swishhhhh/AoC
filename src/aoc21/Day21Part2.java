package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2021/day/21">Advent of Code 2021 Day 21</a>
 */
public class Day21Part2 {
    private static final boolean DEBUG = false;

    private record State (int playerPos1, int playerPos2, long playerScore1, long playerScore2) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            if (playerPos1 != state.playerPos1) return false;
            if (playerPos2 != state.playerPos2) return false;
            if (playerScore1 != state.playerScore1) return false;
            return playerScore2 == state.playerScore2;
        }

        @Override
        public int hashCode() {
            int result = playerPos1;
            result = 31 * result + playerPos2;
            result = 31 * result + (int) (playerScore1 ^ (playerScore1 >>> 32));
            result = 31 * result + (int) (playerScore2 ^ (playerScore2 >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "State{" +
                    "pos1=" + playerPos1 +
                    ", pos2=" + playerPos2 +
                    ", score1=" + playerScore1 +
                    ", score2=" + playerScore2 +
                    '}';
        }
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day21_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 214924284932572L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        Map<Integer, Integer> dieTotalFreqMap = getDieTotalsFrequenciesMap();

        long turn = 0, player1WinsCount = 0, player2WinsCount = 0;
        int pos1 = Helper.extractIntsFromText(lines.get(0)).get(1);
        int pos2 = Helper.extractIntsFromText(lines.get(1)).get(1);
        Map<State, Long> gamesMap = new HashMap<>();
        gamesMap.put(new State(pos1, pos2, 0L, 0L), 1L); //add initial game/universe

        while (!gamesMap.isEmpty()) {
            turn++;
            Map<State, Long> nextMap = new HashMap<>();
            for (State s1 : gamesMap.keySet()) {
                for (Integer dieTotalVariation : dieTotalFreqMap.keySet()) {
                    State s2 = applyIncrement(s1, dieTotalVariation, turn);
                    Long numGamesForThisState = nextMap.get(s2);
                    if (numGamesForThisState == null) {
                        numGamesForThisState = 0L;
                    }
                    numGamesForThisState += (gamesMap.get(s1) * dieTotalFreqMap.get(dieTotalVariation));

                    if (s2.playerScore1() >= 21) {
                        player1WinsCount += numGamesForThisState;
                    } else if (s2.playerScore2() >= 21) {
                        player2WinsCount += numGamesForThisState;
                    } else {
                        nextMap.put(s2, numGamesForThisState);
                    }
                }
            }
            gamesMap = nextMap;

            if (DEBUG) {
                System.out.printf(".... after round %s, player1 wins = %s, player2 wins = %s%n", turn, player1WinsCount, player2WinsCount);
            }
        }

        return Math.max(player1WinsCount, player2WinsCount);
    }

    private static State applyIncrement(State s1, Integer increment, long turn) {
        int pos1 = s1.playerPos1();
        int pos2 = s1.playerPos2();
        long score1 = s1.playerScore1();
        long score2 = s1.playerScore2();

        if (turn % 2 == 1) { //player1's turn
            pos1 = ((pos1 + increment - 1) % 10) + 1; //-1/+1 is to roll 0 over to 10
            score1 += pos1;
        } else { //player2's turn
            pos2 = ((pos2 + increment - 1) % 10) + 1;
            score2 += pos2;
        }

        return new State(pos1, pos2, score1, score2);
    }

    private static Map<Integer, Integer> getDieTotalsFrequenciesMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                for (int k = 1; k <= 3; k++) {
                    int total = i + j + k;
                    Integer freq = map.get(total);
                    if (freq == null) {
                        freq = 0;
                    }
                    map.put(total, freq + 1);
                }
            }
        }
        return map;
    }
}

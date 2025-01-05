package aoc24;

import utils.GridUtils;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/25">Advent of Code 2024 Day 25</a>
 */
public class Day25 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day25_input.txt");

        long answer = new Day25().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 3242;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<int[]> locks = getLocks(lines);
        List<int[]> keys = getKeys(lines);

        return matches(locks, keys);
    }

    private List<int[]> getLocks(List<String> lines) {
        List<int[]> locks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += 8) {
            //locks start with a line of 5 hashes "#####"
            String line = lines.get(i);
            if (line.startsWith("#####")) {
                char[][] grid = GridUtils.loadGrid(lines.subList(i + 1, i + 6));
                int[] lock = new int[5];
                for (int col = 0; col < grid[0].length; col++) {
                    for (int row = grid.length; row > 0; row--) {
                        if (grid[row - 1][col] == '#') {
                            lock[col] = row;
                            break;
                        }
                    }
                }
                locks.add(lock);
            }
        }

        return locks;
    }

    private List<int[]> getKeys(List<String> lines) {
        List<int[]> keys = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += 8) {
            //locks start with a line of 5 dots "....."
            String line = lines.get(i);
            if (line.startsWith(".....")) {
                char[][] grid = GridUtils.loadGrid(lines.subList(i + 1, i + 6));
                int[] key = new int[5];
                for (int col = 0; col < grid[0].length; col++) {
                    for (int row = 0; row < grid.length; row++) {
                        if (grid[row][col] == '#') {
                            key[col] = 5 - row;
                            break;
                        }
                    }
                }
                keys.add(key);
            }
        }

        return keys;
    }

    private long matches(List<int[]> locks, List<int[]> keys) {
        long count = 0;

        //for every pair of locks and keys count 1 if each of their corresponding 5 columns sum up to 5 or less
        for (int[] lock : locks) {
            for (int[] key : keys) {
                boolean match = true;
                for (int col = 0; col < 5; col++) {
                    if (lock[col] + key[col] > 5) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    count++;
                }
            }
        }

        return count;
    }
}
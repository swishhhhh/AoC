package aoc24;

import datastructs.Coordinates;
import org.apache.commons.math3.util.Pair;
import utils.GridUtils;
import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.GridUtils.getNeighboringCells;

/**
 * <a href="https://adventofcode.com/2024/day/21">Advent of Code 2024 Day 21</a>
 */
public class Day21Part1And2 {
    static class State {
        char from;
        char to;
        int level;

        public State(char from, char to, int level) {
            this.from = from;
            this.to = to;
            this.level = level;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            return from == state.from && to == state.to && level == state.level;
        }

        @Override
        public int hashCode() {
            int result = from;
            result = 31 * result + to;
            result = 31 * result + level;
            return result;
        }

        @Override
        public String toString() {
            return "State{" +
                    "from=" + from +
                    ", to=" + to +
                    ", level=" + level +
                    '}';
        }
    }

//    private static final int LEVELS = 3;  //for part 1
    private static final int LEVELS = 26; //for part 2

    private static final char[][] NUMBER_PAD = {
            {'7', '8', '9'},
            {'4', '5', '6'},
            {'1', '2', '3'},
            {'#', '0', 'A'}
    };
    private static final char[][] DIRECTIONAL_PAD = {
            {'#', '^', 'A'},
            {'<', 'v', '>'}
    };

    private final Map<Pair<Character, Character>, Collection<String>> pathsCache = new HashMap<>();
    private final Map<State, Long> shortestPathLengthsCache = new HashMap<>();

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day21_input.txt");

        long answer = new Day21Part1And2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 205620604017764L; //164960 part1
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        return lines.stream()
                .mapToLong(this::getComplexity)
                .sum();
    }

    private long getComplexity(String line) {
        long shortestSequence = getShortestSequenceSinglePath(line, LEVELS + 1, NUMBER_PAD);

        StringBuilder numericDigits = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (Helper.isDigit(c)) {
                numericDigits.append(c);
            }
        }

        return shortestSequence * (long) Integer.parseInt(numericDigits.toString());
    }

    private long getShortestSequenceSinglePath(String path, int level, char[][] pad) {
        long shortestSequence = 0;
        for (int i = 0; i < path.length(); i++) {
            char from = i == 0 ? 'A' : path.charAt(i - 1);
            char to = path.charAt(i);
            State state = new State(from, to, level);
            Long cachedLength = shortestPathLengthsCache.get(state);
            if (cachedLength != null) {
                shortestSequence += shortestPathLengthsCache.get(state);
            } else {
                Collection<String> shortestPaths = getShortestPaths(pad, from, to);
                long len = getShortestSequenceMultiPaths(shortestPaths, level - 1);
                shortestPathLengthsCache.put(state, len);
                shortestSequence += len;
            }
        }
        return shortestSequence;
    }

    private long getShortestSequenceMultiPaths(Collection<String> paths, int level) {
        if (level == 1) {
            return paths.stream().mapToInt(String::length).min().orElseThrow();
        }

        return paths.stream()
                .mapToLong(path -> getShortestSequenceSinglePath(path, level, DIRECTIONAL_PAD))
                .min()
                .orElseThrow(() -> new IllegalStateException("No valid paths found"));
    }

    private Collection<String> getShortestPaths(char[][] pad, char from, char to) {
        //first check if shortest paths for this pair are already in the cache
        Pair<Character, Character> key = new Pair<>(from, to);
        Collection<String> paths = pathsCache.get(key);
        if (paths != null) {
            return paths;
        }

        Collection<String> shortestPaths = new ArrayList<>();
        Coordinates start = GridUtils.getFirstCoordinateWithValue(pad, from);
        Coordinates end = GridUtils.getFirstCoordinateWithValue(pad, to);

        Queue<List<Coordinates>> queue = new LinkedBlockingQueue<>();
        queue.add(new ArrayList<>(List.of(start)));

        int shortestPathLen = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            List<Coordinates> path = queue.poll();
            Coordinates lastCoord = path.get(path.size() - 1);

            if (lastCoord.equals(end)) {
                shortestPaths.add(tracePath(path));
                shortestPathLen = path.size();
                continue;
            }

            if (path.size() > shortestPathLen) {
                break; //no need to go further
            }

            List<Coordinates> neighbors = getNeighboringCells(pad, lastCoord)
                    .stream()
                    .filter(n -> pad[n.y()][n.x()] != '#')
                    .filter(n -> !path.contains(n)) //don't revisit already visited cells
                    .toList();

            neighbors.forEach(n -> {
                List<Coordinates> newPath = new ArrayList<>(path);
                newPath.add(n);
                queue.add(newPath);
            });
        }

        //add to cache
        pathsCache.put(key, shortestPaths);
        return shortestPaths;
    }

    private String tracePath(List<Coordinates> path) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < path.size() - 1; i++) {
            Coordinates from = path.get(i);
            Coordinates to = path.get(i + 1);

            if (to.x() > from.x()) {
                sb.append('>');
            } else if (to.x() < from.x()) {
                sb.append('<');
            } else if (to.y() > from.y()) {
                sb.append('v');
            } else {
                sb.append('^');
            }
        }

        sb.append('A'); //last step is always the 'A' button press (activate)

        return sb.toString();
    }
}
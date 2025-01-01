package aoc24;

import datastructs.Coordinates;
import datastructs.Direction;
import org.apache.commons.math3.util.Pair;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static datastructs.Direction.*;
import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2024/day/20">Advent of Code 2024 Day 20</a>
 */
public class Day20Part1 {
    private static final boolean DEBUG = false;
    private static final Direction[] DIRECTIONS = new Direction[]{NORTH, EAST, SOUTH, WEST};
    private static final int MIN_CHEAT_SAVING = 100;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day20_input.txt");

        long answer = new Day20Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1497;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        char[][] grid = loadGrid(lines);
        Coordinates start = getFirstCoordinateWithValue(grid, 'S');
        Coordinates end = getFirstCoordinateWithValue(grid, 'E');
        Map<Coordinates, Integer> path = new LinkedHashMap<>();
//      findPathDfs(grid, start, end, path); //requires too much stack-depth, use bfs instead
        findPathBfs(grid, start, end, path);

        if (DEBUG) {
            printDebugGrid(grid, path);
        }

        Collection<Pair<Coordinates, Coordinates>> cheats = findCheats(grid, path);
        if (DEBUG) {
            System.out.printf("Cheats: %s%n", cheats);
        }
        return cheats.size();
    }

    private void findPathBfs(char[][] grid, Coordinates start, Coordinates end, Map<Coordinates, Integer> path) {
        //only one valid path so no need to look for shortest one
        Queue<Coordinates> queue = new LinkedBlockingQueue<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Coordinates coord = queue.poll();

            if (path.containsKey(coord)) {
                continue; //already visited
            }

            path.put(coord, path.size());

            if (coord.equals(end)) {
                break;
            }

            List<Coordinates> neighbors = getNeighboringCells(grid, coord)
                    .stream()
                    .filter(n -> grid[n.y()][n.x()] != '#')
                    .filter(n -> !path.containsKey(n)) //don't revisit already visited cells
                    .toList();

            queue.addAll(neighbors);
        }
    }

    private Collection<Pair<Coordinates, Coordinates>> findCheats(char[][] grid, Map<Coordinates, Integer> path) {
        //return list of pairs of coords that allow bypassing a wall ('#') that reduces the length of the path by
        // "minSavingPerCheat" or more
        Collection<Pair<Coordinates, Coordinates>> cheats = new HashSet<>();

        for (Map.Entry<Coordinates, Integer> entry : path.entrySet()) {
            Coordinates coord = entry.getKey();
            int step = entry.getValue();

            for (Direction dir : DIRECTIONS) {
                Coordinates n1 = getNextCoord(coord, dir);
                Coordinates n2 = getNextCoord(n1, dir);

                //n1 has to be a wall, n2 has to be a non-wall and neither n1 nor n2 can be out of bounds
                if (isCellOutOfBounds(grid, n1.x(), n1.y()) || isCellOutOfBounds(grid, n2.x(), n2.y())
                        || grid[n1.y()][n1.x()] != '#' || grid[n2.y()][n2.x()] == '#') {
                    continue;
                }

                if (!path.containsKey(n2)) {
                    //this shouldn't really happen since the path (observed) covers every non-wall tile
                    continue;
                }

                int cheatSaving = path.get(n2) - step - 2; //cheat itself has a cost of 2
                if (cheatSaving >= MIN_CHEAT_SAVING) {
                    cheats.add(new Pair<>(n1, n2));
                }
            }
        }

        return cheats;
    }

    /*
     * unused method, too many steps (almost 10k) on the path so this recursive method isn't stack friendly
     */
    private boolean findPathDfs(char[][] grid, Coordinates cursor, Coordinates target, Map<Coordinates, Integer> path) {
        //there's only one path, so simple dfs search should suffice
        if (path.containsKey(cursor)) {
            return false; //already visited
        }

        path.put(cursor, path.size());

        if (cursor.equals(target)) {
            return true;
        }

        List<Coordinates> neighbors = getNeighboringCells(grid, cursor)
                .stream()
                .filter(n -> grid[n.y()][n.x()] != '#')
                .toList();

        for (Coordinates n : neighbors) {
            if (findPathDfs(grid, n, target, path)) {
                return true;
            }
        }

        return false;
    }

    private void printDebugGrid(char[][] grid, Map<Coordinates, Integer> path) {
        //print grid with path
        char[][] gridWithPath = deepCopyGrid(grid);
        List <Coordinates> pathCoords = path.keySet()
                .stream()
                .toList();

        for (Map.Entry<Coordinates, Integer> entry : path.entrySet()) {
            Coordinates coord = entry.getKey();
            int step = entry.getValue();
            char c;
            if (step == 0) {
                c = 'S';
            } else if (step == path.size() - 1) {
                c = 'E';
            } else {
                Coordinates prevCoord = pathCoords.get(step - 1);
                //figure out in which direction we moved based on previous coord
                if (prevCoord.x() < coord.x()) {
                    c = '>';
                } else if (prevCoord.x() > coord.x()) {
                    c = '<';
                } else if (prevCoord.y() < coord.y()) {
                    c = 'v';
                } else {
                    c = '^';
                }
            }
            gridWithPath[coord.y()][coord.x()] = c;
        }
        printGrid(gridWithPath, false);
    }
}
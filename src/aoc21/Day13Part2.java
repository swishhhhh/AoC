package aoc21;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2021/day/13">Advent of Code 2021 Day 13</a>
 */
public class Day13Part2 {

    public static void main(String... args) throws Exception {

        String resourceName = "aoc21/Day13_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        Set<Coordinates> coords = loadCoordinates(lines);
        String answer = fold(coords, lines);

        System.out.print(answer);

        String expected = getExpectedAnswer();
        if (!answer.equals(expected)) {
            throw new RuntimeException(String.format("%nAnswer: %n%s%nDoesn't match expected: %n%s", answer, expected));
        }
    }

    private static Set<Coordinates> loadCoordinates(List<String> lines) {
        Set<Coordinates> coords = new HashSet<>();

        for (String line : lines) {
            if (line.isBlank()) {
                break;
            }

            String[] ary = line.trim().split(",");
            coords.add(new Coordinates(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])));
        }

        return coords;
    }

    private static String fold(Set<Coordinates> coords, List<String> lines) {
        for (String line : lines) {
            if (!line.startsWith("fold")) {
                continue;
            }

            if (line.contains("x=")) {
                coords = foldOnXaxis(coords, line);
            } else {
                coords = foldOnYaxis(coords, line);
            }
        }

        char[][] grid = plotGrid(coords);

        StringBuilder output = new StringBuilder();
        for (char[] row : grid) {
            for (char c : row) {
                output.append(c);
            }
            output.append(System.lineSeparator());
        }

        return output.toString();
    }

    private static Set<Coordinates> foldOnXaxis(Set<Coordinates> coords, String firstFoldInstruction) {
        int centerLine = Integer.parseInt(firstFoldInstruction.split("=")[1]);
        Set<Coordinates> folded = new HashSet<>();

        for (Coordinates c : coords) {
            if (c.x() < centerLine) {
                folded.add(c);
            } else {
                Coordinates transposed = new Coordinates(centerLine - (c.x() - centerLine), c.y());
                folded.add(transposed);
            }
        }

        return folded;
    }

    private static Set<Coordinates> foldOnYaxis(Set<Coordinates> coords, String firstFoldInstruction) {
        int centerLine = Integer.parseInt(firstFoldInstruction.split("=")[1]);
        Set<Coordinates> folded = new HashSet<>();

        for (Coordinates c : coords) {
            if (c.y() < centerLine) {
                folded.add(c);
            } else {
                Coordinates transposed = new Coordinates(c.x(), centerLine - (c.y() - centerLine));
                folded.add(transposed);
            }
        }

        return folded;
    }

    private static char[][] plotGrid(Set<Coordinates> coords) {
        int maxX = 0, maxY = 0;
        for (Coordinates c : coords) {
            maxX = Math.max(maxX, c.x());
            maxY = Math.max(maxY, c.y());
        }

        char[][] grid = new char[maxY + 1][maxX + 1];
        GridUtils.fillGrid(grid, '.');

        for (Coordinates c : coords) {
            grid[c.y()][c.x()] = '#';
        }

        return grid;
    }

    private static String getExpectedAnswer() {
        return
            "###..#..#.#..#.####.####..##..#..#.###." + System.lineSeparator() +
            "#..#.#.#..#..#.#.......#.#..#.#..#.#..#" + System.lineSeparator() +
            "#..#.##...####.###....#..#....#..#.###." + System.lineSeparator() +
            "###..#.#..#..#.#.....#...#.##.#..#.#..#" + System.lineSeparator() +
            "#.#..#.#..#..#.#....#....#..#.#..#.#..#" + System.lineSeparator() +
            "#..#.#..#.#..#.#....####..###..##..###." + System.lineSeparator();
    }
}

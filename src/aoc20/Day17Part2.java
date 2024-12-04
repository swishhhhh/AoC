package aoc20;

import datastructs.Coordinates4D;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/17">Advent of Code 2020 Day 17</a>
 */
public class Day17Part2 {
    private static final int[] DIRECTIONS = {-1, 0, 1};

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day17_input.txt");

        long answer = new Day17Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2280;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Set<Coordinates4D> activeCubes = new HashSet<>();

        //load level 0
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    activeCubes.add(new Coordinates4D(x, y, 0, 0));
                }
            }
        }

        int minX = 0, maxX = lines.get(0).length() - 1;
        int minY = 0, maxY = lines.size() - 1;
        int minZ = 0, maxZ = 0;
        int minW = 0, maxW = 0;

        for (int level = 1; level <= 6; level++) {
            Set<Coordinates4D> nextGenActiveCubes = new HashSet<>();

            //for each cube in the ever expanding (by 1 layer in each direction) grid, count how many active neighbors
            //it has and apply rules
            for (int x = minX - level; x <= maxX + level; x++) {
                for (int y = minY - level; y <= maxY + level; y++) {
                    for (int z = minZ - level; z <= maxZ + level; z++) {
                        for (int w = minW - level; w <= maxW + level; w++) {
                            Coordinates4D cube = new Coordinates4D(x, y, z, w);
                            List<Coordinates4D> activeNeighbors = getActiveNeighbors(cube, activeCubes);
                            if (activeCubes.contains(cube)) {
                                if (activeNeighbors.size() == 2 || activeNeighbors.size() == 3) {
                                    nextGenActiveCubes.add(cube);
                                }
                            } else {
                                if (activeNeighbors.size() == 3) {
                                    nextGenActiveCubes.add(cube);
                                }
                            }
                        }
                    }
                }
            }

            activeCubes = nextGenActiveCubes;
        }

        return activeCubes.size();
    }

    private List<Coordinates4D> getActiveNeighbors(Coordinates4D cube, Set<Coordinates4D> activeCubes) {
        List<Coordinates4D> activeNeighbors = new ArrayList<>();

        for (int x : DIRECTIONS) {
            for (int y : DIRECTIONS) {
                for (int z : DIRECTIONS) {
                    for (int w : DIRECTIONS) {
                        if (x == 0 && y == 0 && z == 0 && w == 0) {
                            continue;
                        }

                        Coordinates4D neighbor = new Coordinates4D(cube.x() + x, cube.y() + y, cube.z() + z, cube.w() + w);
                        if (activeCubes.contains(neighbor)) {
                            activeNeighbors.add(neighbor);
                        }
                    }
                }
            }
        }

        return activeNeighbors;
    }
}

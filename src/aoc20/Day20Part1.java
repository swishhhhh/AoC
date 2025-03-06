package aoc20;

import datastructs.Coordinates;
import datastructs.Direction;
import org.apache.commons.math3.util.Pair;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static datastructs.Direction.*;

/**
 * <a href="https://adventofcode.com/2020/day/20">Advent of Code 2020 Day 20</a>
 */
public class Day20Part1 {
    private static class Tile {
        static final List<Direction> DIRECTIONS = List.of(NORTH, EAST, SOUTH, WEST);

        int id;
        String[][] perimeters = new String[8][4]; //8 orientations, 4 sides

        public Tile(int id) {
            this.id = id;
        }

        String getPerimeter(int orientation, Direction side) {
            return perimeters[orientation][DIRECTIONS.indexOf(side)];
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Tile tile)) return false;

            return id == tile.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "Tile{" + "id=" + id + '}';
        }
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day20_input.txt");

        long answer = new Day20Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 23386616781851L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Tile> tiles = loadTiles(lines);

        int gridDimensions = (int) Math.sqrt(tiles.size());
        Pair<Tile, Integer>[][] grid = new Pair[gridDimensions][gridDimensions];

        if (!solve(new Coordinates(0, 0), grid, tiles, new HashSet<>())) {
            throw new RuntimeException("Unable to solve");
        }

        //return product of the ids of the 4 corner cells in the grid
        return (long) grid[0][0].getFirst().id
                    * grid[0][gridDimensions - 1].getFirst().id
                    * grid[gridDimensions - 1][0].getFirst().id
                    * grid[gridDimensions - 1][gridDimensions - 1].getFirst().id;
    }

    private List<Tile> loadTiles(List<String> lines) {
        List<Tile> tiles = new ArrayList<>();

        Tile tile = null;
        List<String> tileLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                populateTilePerimeters(tile, tileLines);
                continue;
            }

            if (line.startsWith("Tile")) {
                tile = new Tile(Helper.extractIntsFromText(line).get(0));
                tileLines.clear();
                tiles.add(tile);
                continue;
            }

            tileLines.add(line);
        }

        //last tile
        populateTilePerimeters(tile, tileLines);

        return tiles;
    }

    private void populateTilePerimeters(Tile tile, List<String> tileLines) {
        //orientation 0
        String[] perimeter = new String[4];
        perimeter[0] = tileLines.get(0);
        perimeter[1] = tileLines.stream().map(line -> line.charAt(line.length() - 1) + "").reduce("", (a, b) -> a + b);
        perimeter[2] = tileLines.get(tileLines.size() - 1);
        perimeter[3] = tileLines.stream().map(line -> line.charAt(0) + "").reduce("", (a, b) -> a + b);
        tile.perimeters[0] = perimeter;

        //orientations 1-3, rotate previous one by 90 degrees to the right
        for (int i = 1; i <= 3; i++) {
            tile.perimeters[i] = rotate(tile.perimeters[i - 1]);
        }

        //orientation 4, flip orientation 0
        tile.perimeters[4] = flip(tile.perimeters[0]);

        //orientations 5-7, rotate previous one by 90 degrees to the right
        for (int i = 5; i <= 7; i++) {
            tile.perimeters[i] = rotate(tile.perimeters[i - 1]);
        }
    }

    private String[] rotate(String[] perimeterIn) {
        String[] perimeterOut = new String[4];

        //north -> east
        perimeterOut[1] = perimeterIn[0];

        //east -> south
        perimeterOut[2] = new StringBuilder(perimeterIn[1]).reverse().toString();

        //south -> west
        perimeterOut[3] = perimeterIn[2];

        //west -> north
        perimeterOut[0] = new StringBuilder(perimeterIn[3]).reverse().toString();

        return perimeterOut;
    }

    private String[] flip(String[] perimeterIn) {
        String[] perimeterOut = new String[4];

        //north -> reverse north
        perimeterOut[0] = new StringBuilder(perimeterIn[0]).reverse().toString();

        //east -> west
        perimeterOut[3] = perimeterIn[1];

        //south -> reverse south
        perimeterOut[2] = new StringBuilder(perimeterIn[2]).reverse().toString();

        //west -> east
        perimeterOut[1] = perimeterIn[3];

        return perimeterOut;
    }

    private boolean solve(Coordinates cell, Pair<Tile, Integer>[][] grid, List<Tile> tiles, Set<Integer> tilesUsed) {
        for (Tile tile : tiles) {
            if (tilesUsed.contains(tile.id)) {
                continue;
            }

            tilesUsed.add(tile.id);

            for (int perimeterIdx = 0; perimeterIdx < tile.perimeters.length; perimeterIdx++) {
                if (tileFits(tile, perimeterIdx, grid, cell)) {
                    //place tile in grid
                    grid[cell.y()][cell.x()] = new Pair<>(tile, perimeterIdx);

                    //if grid is complete, you have your solution, return true
                    if (cell.x() == grid.length - 1 && cell.y() == grid.length - 1) {
                        return true;
                    }

                    //otherwise, move to next cell and recurse into this method again
                    Coordinates nextCell = nextCell(cell, grid.length);
                    if (solve(nextCell, grid, tiles, tilesUsed)) {
                        return true;
                    }

                    //if that doesn't work, remove tile+perimeter pair from grid before next iteration
                    grid[cell.y()][cell.x()] = null;
                }
            }

            //backtrack: remove tile from tilesUsed set before moving on to next tile
            tilesUsed.remove(tile.id);
        }

        //if you get here you've tried all tiles and hit a dead-end
        return false;
    }

    private boolean tileFits(Tile tile, int i, Pair<Tile, Integer>[][] grid, Coordinates cell) {
        //check peer to the north (if any)
        if (cell.y() > 0) {
            Pair<Tile, Integer> north = grid[cell.y() - 1][cell.x()];
            if (north != null) {
                String peerPerimeter = north.getFirst().getPerimeter(north.getSecond(), SOUTH);
                String tilePerimeter = tile.getPerimeter(i, NORTH);
                if (!peerPerimeter.equals(tilePerimeter)) {
                    return false;
                }
            }
        }

        //check peer to the east (if any)
        if (cell.x() < grid.length - 1) {
            Pair<Tile, Integer> east = grid[cell.y()][cell.x() + 1];
            if (east != null) {
                String peerPerimeter = east.getFirst().getPerimeter(east.getSecond(), WEST);
                String tilePerimeter = tile.getPerimeter(i, EAST);
                if (!peerPerimeter.equals(tilePerimeter)) {
                    return false;
                }
            }
        }

        //check peer to the south (if any)
        if (cell.y() < grid.length - 1) {
            Pair<Tile, Integer> south = grid[cell.y() + 1][cell.x()];
            if (south != null) {
                String peerPerimeter = south.getFirst().getPerimeter(south.getSecond(), NORTH);
                String tilePerimeter = tile.getPerimeter(i, SOUTH);
                if (!peerPerimeter.equals(tilePerimeter)) {
                    return false;
                }
            }
        }

        //check peer to the west (if any)
        if (cell.x() > 0) {
            Pair<Tile, Integer> west = grid[cell.y()][cell.x() - 1];
            if (west != null) {
                String peerPerimeter = west.getFirst().getPerimeter(west.getSecond(), EAST);
                String tilePerimeter = tile.getPerimeter(i, WEST);
                if (!peerPerimeter.equals(tilePerimeter)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Coordinates nextCell(Coordinates cell, int dimensions) {
        int row = cell.y();
        int col = cell.x();
        if (col < dimensions - 1) {
            col++;
        } else {
            col = 0;
            row++;
        }
        return new Coordinates(col, row);
    }
}

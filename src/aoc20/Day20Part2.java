package aoc20;

import datastructs.Coordinates;
import datastructs.Direction;
import org.apache.commons.math3.util.Pair;
import utils.GridUtils;
import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

import static datastructs.Direction.*;

/**
 * <a href="https://adventofcode.com/2020/day/20">Advent of Code 2020 Day 20</a>
 */
public class Day20Part2 {
    private static final boolean DEBUG = false;

    private static class Tile {
        static final List<Direction> DIRECTIONS = List.of(NORTH, EAST, SOUTH, WEST);

        int id;
        String[][] perimeters = new String[8][4]; //8 orientations, 4 sides
        char[][] center;

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

        long answer = new Day20Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2376;
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

        char[][] compositeGrid = stitchImages(grid);
        char[][] monster = loadMonster();
        int monsters = countMonsters(compositeGrid, monster);
        long totalHashes = countHashes(compositeGrid);
        long monsterHashes = countHashes(monster);

        return totalHashes - (monsters * monsterHashes);
    }

    private List<Tile> loadTiles(List<String> lines) {
        List<Tile> tiles = new ArrayList<>();

        Tile tile = null;
        List<String> tileLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                populateTile(tile, tileLines);
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
        populateTile(tile, tileLines);

        return tiles;
    }

    private String[] rotatePerimeter(String[] perimeterIn) {
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

    private void populateTile(Tile tile, List<String> tileLines) {
        //orientation 0
        String[] perimeter = new String[4];
        perimeter[0] = tileLines.get(0);
        perimeter[1] = tileLines.stream().map(line -> line.charAt(line.length() - 1) + "").reduce("", (a, b) -> a + b);
        perimeter[2] = tileLines.get(tileLines.size() - 1);
        perimeter[3] = tileLines.stream().map(line -> line.charAt(0) + "").reduce("", (a, b) -> a + b);
        tile.perimeters[0] = perimeter;

        //orientations 1-3, rotate previous one by 90 degrees to the right
        for (int i = 1; i <= 3; i++) {
            tile.perimeters[i] = rotatePerimeter(tile.perimeters[i - 1]);
        }

        //orientation 4, flip orientation 0
        tile.perimeters[4] = flipPerimeter(tile.perimeters[0]);

        //orientations 5-7, rotate previous one by 90 degrees to the right
        for (int i = 5; i <= 7; i++) {
            tile.perimeters[i] = rotatePerimeter(tile.perimeters[i - 1]);
        }

        //populate center
        final int centerRows = tileLines.size();
        final int centerCols = tileLines.get(0).length();
        tile.center = new char[centerRows - 2][centerCols - 2];
        for (int row = 1; row < centerRows - 1; row++) {
            String currentRow = tileLines.get(row);
            for (int col = 1; col < centerCols - 1; col++) {
                tile.center[row - 1][col - 1] = currentRow.charAt(col);
            }
        }
    }

    private String[] flipPerimeter(String[] perimeterIn) {
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

    private int countMonsters(char[][] inputGrid, char[][] inputMonster) {
        char[][] grid = GridUtils.deepCopyGrid(inputGrid);

        for (int orientation = 0; orientation < 8; orientation++) {
            //try finding monsters for each orientation, returning the first count > 0

            //flip grid on 4th orientation, otherwise (if not 0 or 4) rotate 90 degrees from previous orientation
            if (orientation == 4) {
                grid = flipGrid(inputGrid);
            } else if (orientation % 4 != 0) {
                grid = rotateGrid(grid);
            }

            int count = 0;
            for (int row = 0; row < grid.length - inputMonster.length; row++) {
                for (int col = 0; col < grid.length - inputMonster[0].length; col++) {
                    if (monsterMatches(grid, inputMonster, row, col)) {
                        count++;
                    }
                }
            }

            if (DEBUG) {
                System.out.printf("========= Composite Grid orientation %s, monster count = %s =========%n",
                        orientation, count);
                GridUtils.printGrid(grid, false);
                System.out.println();
            }

            if (count > 0) {
                return count;
            }
        }

        throw new RuntimeException("No monsters found in any of the orientations");
    }

    private boolean monsterMatches(char[][] compositeGrid, char[][] monster, int gridRow, int gridCol) {
        for (int monsterRow = 0; monsterRow < monster.length; monsterRow++) {
            for (int monsterCol = 0; monsterCol < monster[0].length; monsterCol++) {
                if (monster[monsterRow][monsterCol] == '#') {
                    if (compositeGrid[gridRow + monsterRow][gridCol + monsterCol] != '#') {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private long countHashes(char[][] grid) {
        return Arrays.stream(grid)
                .mapToLong(row -> new String(row).chars().filter(ch -> ch == '#').count())
                .sum();
    }

    private char[][] loadMonster() {
        return new char[][]
                {
                        "                  # ".toCharArray(),
                        "#    ##    ##    ###".toCharArray(),
                        " #  #  #  #  #  #   ".toCharArray()
                };
    }

    private char[][] stitchImages(Pair<Tile, Integer>[][] grid) {
        int tileSize = grid[0][0].getFirst().center.length;
        char[][] compositeGrid = new char[tileSize * grid.length][tileSize * grid.length];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                Tile tile = grid[row][col].getFirst();
                int orientation = grid[row][col].getSecond();

                //transpose center to match the orientation
                // make a copy of tile.center first (so original one is preserved as we try different orientations)
                char[][] center;
                if (orientation >= 4) {
                    center = flipGrid(tile.center);
                } else {
                    center = GridUtils.deepCopyGrid(tile.center);
                }

                for (int i = 0; i < orientation % 4; i++) {
                    center = rotateGrid(center);
                }

                for (int i = 0; i < tileSize; i++) {
                    for (int j = 0; j < tileSize; j++) {
                        char c = center[i][j];
                        compositeGrid[row * tileSize + i][col * tileSize + j] = c;
                    }
                }
            }
        }

        return compositeGrid;
    }

    private char[][] flipGrid(char[][] input) {
        char[][] output = new char[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            for (int col = 0; col < input[0].length; col++) {
                output[row][col] = input[row][input[0].length - col - 1];
            }
        }
        return output;
    }

    private char[][] rotateGrid(char[][] input) {
        char[][] output = new char[input[0].length][input.length];

        //rotate 90 degrees clockwise
        for (int row = 0; row < output.length; row++) {
            for (int col = 0; col < output[0].length; col++) {
                output[row][col] = input[input.length - col - 1][row];
            }
        }

        return output;
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

            //remove tile from tilesUsed set before moving on to next tile
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

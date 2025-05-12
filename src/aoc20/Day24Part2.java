package aoc20;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2020/day/24">Advent of Code 2020 Day 24</a>
 */
public class Day24Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day24_input.txt");

        long answer = new Day24Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 4225;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Set<Coordinates> blackTiles = new HashSet<>();

        for (String line : lines) {
            processInputLine(line, blackTiles);
        }

        for (int i = 0; i < 100; i++) {
            blackTiles = flipTiles(blackTiles);
        }

        return blackTiles.size();
    }

    private void processInputLine(String line, Set<Coordinates> blackTiles) {
        int x = 0, y = 0;
        int idx = 0;
        while (idx < line.length()) {
            if (line.substring(idx).startsWith("e")) {
                x+= 2;
                idx++;
            } else if (line.substring(idx).startsWith("w")) {
                x-= 2;
                idx++;
            } else  if (line.substring(idx).startsWith("ne")) {
                x+= 1;
                y-= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("se")) {
                x+= 1;
                y+= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("nw")) {
                x-= 1;
                y-= 2;
                idx+= 2;
            } else if (line.substring(idx).startsWith("sw")) {
                x-= 1;
                y+= 2;
                idx+= 2;
            } else {
                throw new RuntimeException(String.format("Unexpected line %s at idx %s", line, idx));
            }
        }

        Coordinates tile = new Coordinates(x, y);
        if (!blackTiles.remove(tile)) { //returns false if the tile was already not present (before the removal attempt)
            blackTiles.add(tile);
        }
    }

    private Set<Coordinates> flipTiles(Set<Coordinates> blackTiles) {
        /*
          for each black tile in the set, iterate through its 6 adjacent tiles (in the blackTiles set)
          - if any adjacent tile is white (not present in the blackTiles set)
            - add to a set of whiteTilesToCheck later/next
          - if any adjacent tile is black
            - increment a counter
          - at end of iteration (of adjacent tiles), if counter is 1 or 2, add black tile to nextGenBlackTiles set

          for each white tile in the set of whiteTilesToCheck
            - iterate through its 6 adjacent tiles
            - if any adjacent tile is black (present in the blackTiles set)
              - increment a counter
            - at end of iteration (of adjacent tiles), if counter is 2, add the tile to nextGenBlackTiles set
         */

        Set<Coordinates> nextGenBlackTiles = new HashSet<>();
        Set<Coordinates> whiteTilesToCheck = new HashSet<>();
        for (Coordinates tile : blackTiles) {
            int blackAdjacentTiles = 0;
            for (Coordinates adjTile : getAdjacentTiles(tile)) {
                if (blackTiles.contains(adjTile)) {
                    blackAdjacentTiles++;
                } else {
                    whiteTilesToCheck.add(adjTile);
                }
            }

            if (blackAdjacentTiles == 1 || blackAdjacentTiles == 2) {
                nextGenBlackTiles.add(tile);
            }
        }

        for (Coordinates tile : whiteTilesToCheck) {
            int blackAdjacentTiles = 0;
            for (Coordinates adjTile : getAdjacentTiles(tile)) {
                if (blackTiles.contains(adjTile)) {
                    blackAdjacentTiles++;
                }
            }

            if (blackAdjacentTiles == 2) {
                nextGenBlackTiles.add(tile);
            }
        }

        return nextGenBlackTiles;
    }

    private Coordinates[] getAdjacentTiles(Coordinates tile) {
        return new Coordinates[] {
                new Coordinates(tile.x() + 2, tile.y()),     //east
                new Coordinates(tile.x() - 2, tile.y()),     //west
                new Coordinates(tile.x() + 1, tile.y() - 2), //ne
                new Coordinates(tile.x() + 1, tile.y() + 2), //se
                new Coordinates(tile.x() - 1, tile.y() - 2), //nw
                new Coordinates(tile.x() - 1, tile.y() + 2)  //sw
        };
    }
}

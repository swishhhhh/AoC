package utils;

import aoc23.datastructs.Coordinates; //TODO clean this up once Coordinates class is moved to common (non aoc23) pkg

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GridUtils {

    public static void printGrid(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            System.out.printf("%03d", i);
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    public static void fillGrid(char[][] grid, char fillWith) {
        for (char[] row : grid) {
            Arrays.fill(row, fillWith);
        }
    }

    public static boolean isCellOutOfBounds(char[][] grid, int x, int y) {
        return x < 0 || x >= grid[0].length || y < 0 || y >= grid.length;
    }

    public static List<Coordinates> getNeighboringCells(char[][] grid, Coordinates cursor) {
        return Stream.of(
                        new Coordinates(cursor.x(), cursor.y()-1), //North
                        new Coordinates(cursor.x(), cursor.y()+1), //South
                        new Coordinates(cursor.x()+1, cursor.y()), //East
                        new Coordinates(cursor.x()-1, cursor.y())) //West
                .filter(coord -> coord.y() >= 0 && coord.y() < grid.length) //avoid out-of-bounds
                .filter(coord -> coord.x() >= 0 && coord.x() < grid[0].length) // ditto
                .collect(Collectors.toList());
    }

    public static char[][] cloneGridInEachDirection(char[][] srcGrid, int multipleInEachDir) {
        int multiple = ((2 * multipleInEachDir) + 1);
        int newGridSizeRows = srcGrid.length * multiple;
        int newGridSizeCols = srcGrid[0].length * multiple;
        char[][] newGrid = new char[newGridSizeRows][newGridSizeCols];

        for (int i = 0; i < multiple; i++) {
            for (int row = 0; row < newGridSizeRows; row++) {
                for (int j = 0; j < multiple; j++) {
                    System.arraycopy(srcGrid[row % srcGrid.length], 0, newGrid[row], j * srcGrid.length, srcGrid.length);
                }
            }
        }

        return newGrid;
    }
}

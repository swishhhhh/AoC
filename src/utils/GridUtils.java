package utils;

import java.util.Arrays;

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
}

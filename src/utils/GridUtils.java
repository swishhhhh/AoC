package utils;

import datastructs.Coordinates;
import datastructs.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GridUtils {

    public static void printGrid(char[][] grid) {
        printGrid(grid, true);
    }
    public static void printGrid(char[][] grid, boolean withLineNumbers) {
        for (int i = 0; i < grid.length; i++) {
            if (withLineNumbers) {
                System.out.printf("%03d", i);
            }
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

    public static void fillGrid(long[][] grid, long fillWith) {
        for (long[] row : grid) {
            Arrays.fill(row, fillWith);
        }
    }

    public static boolean isCellOutOfBounds(char[][] grid, int x, int y) {
        return isCellOutOfBounds(grid.length, grid[0].length, x, y);
    }

    public static boolean isCellOutOfBounds(int gridHeight, int gridWidth, int x, int y) {
        return x < 0 || x >= gridWidth || y < 0 || y >= gridHeight;
    }

    public static List<Coordinates> getNeighboringCells(char[][] grid, Coordinates cursor) {
        return getNeighboringCells(grid, cursor, false);
    }

    public static List<Coordinates> getNeighboringCells(char[][] grid, Coordinates cursor, boolean includeDiagonal) {
        Collection<Coordinates> neighbors = new ArrayList<>(
                List.of(
                    new Coordinates(cursor.x(), cursor.y()-1), //North
                    new Coordinates(cursor.x(), cursor.y()+1), //South
                    new Coordinates(cursor.x()+1, cursor.y()), //East
                    new Coordinates(cursor.x()-1, cursor.y())) //West
        );

        if (includeDiagonal) {
            neighbors.addAll(
                List.of(
                    new Coordinates(cursor.x()+1, cursor.y()-1), //NE
                    new Coordinates(cursor.x()+1, cursor.y()+1), //SE
                    new Coordinates(cursor.x()-1, cursor.y()+1), //SW
                    new Coordinates(cursor.x()-1, cursor.y()-1)) //NW
            );
        }

        return neighbors
                .stream()
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

    public static char[][] addPerimeter(char[][] gridIn, int perimSize, char fillPerimWith) {
        char[][] gridOut = new char[gridIn.length + (2 * perimSize)][gridIn[0].length + (2 * perimSize)];

        //copy main part of grid
        for (int row = 0; row < gridIn.length; row++) {
            System.arraycopy(gridIn[row], 0, gridOut[row + perimSize], perimSize, gridIn[0].length);
        }

        //fill in perimeter for first and last rows
        for (int col = 0; col < gridOut.length; col++) {
            for (int i = 0; i < perimSize; i++) {
                gridOut[i][col] = fillPerimWith; //first row
                gridOut[gridOut.length - 1 - i][col] = fillPerimWith; //last row
            }
        }

        //fill in perim for first and last cols
        for (int row = 0; row < gridOut[0].length; row++) {
            for (int i = 0; i < perimSize; i++) {
                gridOut[row][i] = fillPerimWith; //first col
                gridOut[row][gridOut.length - 1 - i] = fillPerimWith; //last col
            }
        }

        return gridOut;
    }

    public static Coordinates getNextCoord(Coordinates coord, Direction direction) {
        int x = coord.x(), y = coord.y();
        switch (direction) {
            case NORTH -> y = y - 1;
            case SOUTH -> y = y + 1;
            case WEST  -> x = x - 1;
            case EAST  -> x = x + 1;
            case NE -> {
                y = y - 1;
                x = x + 1;
            }
            case NW -> {
                y = y - 1;
                x = x - 1;
            }
            case SE -> {
                y = y + 1;
                x = x + 1;
            }
            case SW -> {
                y = y + 1;
                x = x - 1;
            }
        }

        return new Coordinates(x, y);
    }

    public static char[][] loadGrid(List<String> lines) {
        char[][] grid = new char[lines.size()][lines.get(0).length()];
        for (int row = 0; row < grid.length; row++) {
            grid[row] = lines.get(row).toCharArray();
        }

        return grid;
    }

    public static char[][] deepCopyGrid(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    public static char[][][] deepCopyGrid(char[][] []original) {
        char[][][] copy = new char[original.length][][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = deepCopyGrid(original[i]);
        }
        return copy;
    }

    public static Coordinates getFirstCoordinateWithValue(char[][] grid, char s) {
        //return first coordinate in grid with value s
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == s) {
                    return new Coordinates(col, row);
                }
            }
        }
        throw new RuntimeException("No coordinate found with value " + s);
    }

    public static int getManhattanDistanceBetweenCells(Coordinates source, Coordinates target) {
        return Math.abs(source.x() - target.x()) + Math.abs(source.y() - target.y());
    }

    public static List<Coordinates> getNeighborsWithinManhattanDistance(Coordinates coord, int maxDistance, int gridHeight, int gridWidth) {
        List<Coordinates> neighbors = new ArrayList<>();

        //check all possible points within the Manhattan distance
        for (int dy = -maxDistance; dy <= maxDistance; dy++) {
            for (int dx = -maxDistance; dx <= maxDistance; dx++) {
                //skip the current coordinate itself
                if (dx == 0 && dy == 0) continue;

                int newX = coord.x() + dx;
                int newY = coord.y() + dy;

                //check if the new coordinates are within grid bounds
                if (!isCellOutOfBounds(gridHeight, gridWidth, newX, newY)) {
                    //calculate Manhattan distance: |x1-x2| + |y1-y2|
                    int manhattanDist = Math.abs(dx) + Math.abs(dy);

                    //only add if within the specified Manhattan distance
                    if (manhattanDist <= maxDistance) {
                        neighbors.add(new Coordinates(newX, newY));
                    }
                }
            }
        }

        return neighbors;
    }
}

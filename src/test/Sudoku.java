package test;

import datastructs.Coordinates;
import utils.GridUtils;
import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sudoku {
    private static final int DEBUG_LEVEL = 0; //{0 = off, 1 = brief, 2 = verbose}
    private static final int MAX_RECURSION_DEBUG_LEVEL = 10;
    private static final boolean RANDOMIZE = false; //for puzzles with multiple answers, randomize=false will ensure same solution every time

    private static class State {
        static long instancesCtr = 1;

        char[][] grid;
        char[][][] possibilities;
        int solvedCellCount;
        int recursionLevel;

        State deepCopy() {
            instancesCtr++;
            State copy = new State();
            copy.grid = GridUtils.deepCopyGrid(grid);
            copy.possibilities = GridUtils.deepCopyGrid(possibilities);
            copy.solvedCellCount = solvedCellCount;
            copy.recursionLevel = recursionLevel + 1;
            return copy;
        }
    }

    public static void main(String... args) throws Exception {
        new Sudoku().execute();
    }

    private void execute() throws Exception {
        long startMillis = System.currentTimeMillis();
        State state = new State();
        state.grid = loadInput();
        initPossibilities(state);

        boolean solved = solve(state);

        System.out.println("------------------");
        GridUtils.printGrid(state.grid, false);

        System.out.printf("Solved = %s, states counter = %s, runtime (in millis) %s%n",
                solved, State.instancesCtr, (System.currentTimeMillis() - startMillis));
    }

    private char[][] loadInput() throws Exception {
        List<String> lines = ResourceLoader.readStrings("test/sudoku.txt");
        if (lines.size() < 9) {
            throw new RuntimeException("Invalid input (< 9 lines)");
        }

        char[][] grid = new char[9][];

        for (int i = 0; i < 9; i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        if (!validate(grid)) {
            throw new RuntimeException("Invalid input");
        }

        return grid;
    }

    private boolean validate(char[][] grid) {
        if (grid.length != 9) {
            return false;
        }

        //validate each row
        int rowCtr = 0;
        for (char[] row : grid) {
            rowCtr++;
            if (row.length != 9) {
                return false;
            }

            Set<Character> rowSet = new HashSet<>();
            for (char c : row) {
                if (c == '.'){
                    continue;
                }

                if (!Helper.isDigit(c)) {
                    System.err.printf("Found invalid char '%s' on row %s %n", c, rowCtr);
                    return false;
                }

                if (!rowSet.add(c)) {
                    System.err.printf("Found duplicate digit '%s' on row %s%n", c, rowCtr);
                    return false;
                }
            }
        }

        //validate each column
        int colCtr = 0;
        for (int col = 0; col < grid[0].length; col++) {
            colCtr++;
            Set<Character> colSet = new HashSet<>();
            for (char[] row : grid) {
                char c = row[col];
                if (Helper.isDigit(c) && !colSet.add(c)) {
                    System.err.printf("Found duplicate digit '%s' on col %s%n", c, colCtr);
                    return false;
                }
            }
        }

        //validate each box
        for (int row = 0; row < grid.length; row += 3) {
            for (int col = 0; col < grid[row].length; col += 3) {
                Set<Character> boxSet = new HashSet<>();
                for (int i = row; i < row + 3; i++) {
                    for (int j = col; j < col + 3; j++) {
                        char c = grid[i][j];
                        if (Helper.isDigit(c) && !boxSet.add(c)) {
                            System.err.printf("Found duplicate digit '%s' in box (%s,%s)%n", c, (row + 1), (col + 1));
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private void initPossibilities(State state) {
        char[][][] possibilities = new char[9][9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                possibilities[i][j] = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
            }
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                char value = state.grid[row][col];
                if (Helper.isDigit(value)) {
                    state.solvedCellCount++;
                    updatePossibilities(state.grid, row, col, possibilities);
                }
            }
        }

        state.possibilities = possibilities;
    }

    private void updatePossibilities(char[][] grid, int row, int col, char[][][] possibilities) {
        int value = Character.getNumericValue(grid[row][col]);

        //remove all other possibilities (other than value) for this cell
        char[] p = new char[9];
        Arrays.fill(p, '.');
        p[value - 1] = grid[row][col];
        possibilities[row][col] = p;

        //remove value from all the cell's peers
        getHorizontalPeers(row, col)
                .forEach(c -> possibilities[c.y()][c.x()][value - 1] = '.');
        getVerticalPeers(row, col)
                .forEach(c -> possibilities[c.y()][c.x()][value - 1] = '.');
        getBoxPeers(row, col)
                .forEach(c -> possibilities[c.y()][c.x()][value - 1] = '.');
    }

    private List<Coordinates> getHorizontalPeers(int row, int col) {
        return IntStream.range(0, 9)
                .filter(i -> i != col)
                .mapToObj(i -> new Coordinates(i, row))
                .collect(Collectors.toList());
    }

    private List<Coordinates> getVerticalPeers(int row, int col) {
        return IntStream.range(0, 9)
                .filter(i -> i != row)
                .mapToObj(i -> new Coordinates(col, i))
                .collect(Collectors.toList());
    }

    private List<Coordinates> getBoxPeers(int row, int col) {
        int initRow = row / 3 * 3;
        int initCol = col / 3 * 3;
        return IntStream.range(0, 9)
                .mapToObj(k -> new Coordinates(
                        initCol + (k % 3),
                        initRow + (k / 3)))
                .filter(coord -> coord.x() != col || coord.y() != row)
                .collect(Collectors.toList());
    }

    private boolean solve(State state) {
        solveSingles(state);
        solveGroups(state);
        if (state.solvedCellCount == 81) {
            return true;
        }

        //if we get here, get first unsolved cell and get all its possibilities, and recursively "solve".
        // Clone the state first, so you work off a copy (in case that recursion branch doesn't work out)
        Coordinates coord = getFirstEmptyCell(state);
        assert coord != null;
        int row = coord.y();
        int col = coord.x();

        List<Integer> possibilities = possibilitiesToList(state.possibilities[row][col]);
        if (RANDOMIZE) {
            Collections.shuffle(possibilities);
        }

        for (int i : possibilities) {
            char p = Character.forDigit(i, 10);

            State copy = state.deepCopy();
            copy.grid[row][col] = p;
            updatePossibilities(copy.grid, row, col, copy.possibilities);
            copy.solvedCellCount++;

            if (DEBUG_LEVEL >= 1 && state.recursionLevel <= MAX_RECURSION_DEBUG_LEVEL) {
                System.out.printf("------ states count=%s, level=%s, trying %s for cell %s,%s ------------%n",
                        State.instancesCtr, state.recursionLevel, p, row, col);
            }
            if (DEBUG_LEVEL >= 2) {
                GridUtils.printGrid(copy.grid, false);
            }

            if (solve(copy)) {
                state.grid = copy.grid;
                state.possibilities = copy.possibilities;
                state.solvedCellCount = copy.solvedCellCount;
                return true;
            }
        }

        return false;
    }

    private static Coordinates getFirstEmptyCell(State state) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (state.grid[row][col] == '.') {
                    return new Coordinates(col, row);
                }
            }
        }
        return null;
    }

    private boolean solveSingles(State state) {
        boolean anySolved = false;

        while(solveSingle(state)){
            anySolved = true;
        }

        return anySolved;
    }

    private boolean solveSingle(State state) {
        char[][] grid = state.grid;
        char[][][] possibilities = state.possibilities;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                char v = grid[row][col];
                if (Helper.isDigit(v)) {
                    continue;
                }

                List<Integer> cellPossibilities = possibilitiesToList(possibilities[row][col]);
                if (cellPossibilities.size() == 1) {
                    grid[row][col] = Character.forDigit(cellPossibilities.get(0), 10);
                    updatePossibilities(grid, row, col, possibilities);
                    state.solvedCellCount++;
                    if (DEBUG_LEVEL >= 2) {
                        System.out.printf("Solved cell {%s,%s} to %s, # solved = %s%n", row, col,
                                cellPossibilities.get(0), state.solvedCellCount);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private List<Integer> possibilitiesToList(char[] possibilities) {
        return IntStream.range(0, possibilities.length)
                .filter(i -> possibilities[i] != '.')
                .mapToObj(i -> Character.getNumericValue(possibilities[i]))
                .collect(Collectors.toList());
    }

    private void solveGroups(State state) {
        while (true) {
            if (!solveGroup(state)){
                break;
            }
        }
    }

    private boolean solveGroup(State state) {
        return solveGroupRows(state) ||
               solveGroupCols(state) ||
               solveGroupBoxes(state);
    }

    private boolean solveGroupRows(State state) {
        char[][] grid = state.grid;
        char[][][] possibilities = state.possibilities;

        for (int row = 0; row < grid.length; row++) {
            //for each unsolved cell in this row, get the possibilities, and for each possibility,
            // see if it's the only one for that cell in the row
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != '.') {
                    continue;
                }

                char[] possibilitiesThisCell = possibilities[row][col];
                for (char p : possibilitiesThisCell) {
                    if (p == '.') {
                        continue;
                    }
                    int pVal = Character.getNumericValue(p);

                    //check if this possibility is the only one for this cell in the row
                    boolean isOnlyOne = true;
                    for (int col2 = 0; col2 < grid[row].length; col2++) {
                        if (col2 == col) {
                            continue;
                        }

                        if (possibilities[row][col2][pVal - 1] == p) {
                            isOnlyOne = false;
                            break;
                        }
                    }

                    if (!isOnlyOne) {
                        continue;
                    }

                    //if it is, then remove the rest of the possibilities from this cell
                    if (removePossibilitiesAndAttemptSolveSingle(state, possibilitiesThisCell, pVal, row, col, possibilities)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean solveGroupCols(State state) {
        char[][] grid = state.grid;
        char[][][] possibilities = state.possibilities;

        for (int col = 0; col < grid[0].length; col++) {
            //for each unsolved cell in this col, get the possibilities, and for each possibility,
            // see if it's the only one for that cell in the col
            for (int row = 0; row < grid.length; row++) {
                if (grid[row][col] != '.') {
                    continue;
                }

                char[] possibilitiesThisCell = possibilities[row][col];
                for (char p : possibilitiesThisCell) {
                    if (p == '.') {
                        continue;
                    }
                    int pVal = Character.getNumericValue(p);

                    //check if this possibility is the only one for this cell in the col
                    boolean isOnlyOne = true;
                    for (int row2 = 0; row2 < grid.length; row2++) {
                        if (row2 == row) {
                            continue;
                        }

                        if (possibilities[row2][col][pVal - 1] == p) {
                            isOnlyOne = false;
                            break;
                        }
                    }

                    if (!isOnlyOne) {
                        continue;
                    }

                    //if it is, then remove the rest of the possibilities from this cell
                    if (removePossibilitiesAndAttemptSolveSingle(state, possibilitiesThisCell, pVal, row, col, possibilities)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean solveGroupBoxes(State state) {
        char[][] grid = state.grid;
        char[][][] possibilities = state.possibilities;

        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                //for each unsolved cell in this box, get the possibilities, and for each possibility,
                // see if it's the only one for that cell in the col
                for (int row = boxRow * 3; row < (boxRow + 1) * 3; row++) {
                    for (int col = boxCol * 3; col < (boxCol + 1) * 3; col++) {
                        if (grid[row][col] != '.') {
                            continue;
                        }

                        char[] possibilitiesThisCell = possibilities[row][col];
                        for (char p : possibilitiesThisCell) {
                            if (p == '.') {
                                continue;
                            }
                            int pVal = Character.getNumericValue(p);

                            //check if this possibility is the only one for this cell in the box
                            boolean isOnlyOne = true;
                            for (int row2 = boxRow * 3; row2 < (boxRow + 1) * 3; row2++) {
                                for (int col2 = boxCol * 3; col2 < (boxCol + 1) * 3; col2++) {
                                    if (row2 == row && col2 == col) {
                                        continue;
                                    }

                                    if (possibilities[row2][col2][pVal - 1] == p) {
                                        isOnlyOne = false;
                                        break;
                                    }
                                }
                            }

                            if (!isOnlyOne) {
                                continue;
                            }

                            //if it is, then remove the rest of the possibilities from this cell
                            if (removePossibilitiesAndAttemptSolveSingle(state, possibilitiesThisCell, pVal, row, col, possibilities)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean removePossibilitiesAndAttemptSolveSingle(State state, char[] possibilitiesThisCell, int pVal, int row, int col, char[][][] possibilities) {
        List<Integer> removals = possibilitiesToList(possibilitiesThisCell);
        removals.remove(Integer.valueOf(pVal));

        if (DEBUG_LEVEL >= 2) {
            System.out.printf("Rows: removing %s from (%s, %s)%n", removals, row, col);
        }

        for (int r : removals) {
            possibilities[row][col][r - 1] = '.';
        }

        return solveSingles(state);
    }
}
package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/4">Advent of Code 2021 Day 4</a>
 */
public class Day04Part2 {

    private static final Map<Integer, Integer[][]> boardsNumbers = new HashMap<>();
    private static final Map<Integer, Boolean[][]> boardsStatuses = new HashMap<>();
    private static final List<Integer> boardsAlreadyDone = new ArrayList<>();

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day4_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        String numbersLine = lines.get(0); //first line contains the numbers

        int lineN = 2; //boards start on line 3
        int boardNum = 0;
        while (lineN < lines.size()) {
            boardNum++;
            Integer[][] board = new Integer[5][5];
            for (int i = 0; i < 5; i++) {
                board[i] = Helper.extractIntsFromText(lines.get(lineN + i)).toArray(new Integer[]{});
            }
            boardsNumbers.put(boardNum, board);
            boardsStatuses.put(boardNum, emptyBoardStatusGrid());

            lineN+= 6; //empty/extra line between each board...
        }

        //play numbers
        int lastNumCalled = -1;
        List<Integer> nums = Helper.extractIntsFromText(numbersLine);
        for (int num: nums) {
            markBoards(num);
            checkForWins();
            if (boardsAlreadyDone.size() >= boardsNumbers.size()) {
                lastNumCalled = num;
                break;
            }
        }

        int losingBoard = boardsAlreadyDone.get(boardsAlreadyDone.size() - 1);
        int answer = calculateScore(losingBoard, lastNumCalled);

        //------------
        System.out.printf("Number of Boards: %s, Last Board: %s, Score = %s%n", boardsNumbers.size(), losingBoard, answer);

        long expected = 2980;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static void markBoards(int num) {
        for (Map.Entry<Integer, Integer[][]> entry: boardsNumbers.entrySet()) {
            int boardNum = entry.getKey();
            Integer[][] board = entry.getValue();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == num) {
                        boardsStatuses.get(boardNum)[i][j] = true;
                    }
                }
            }
        }
    }

    private static void checkForWins() {
        for (Map.Entry<Integer, Boolean[][]> entry: boardsStatuses.entrySet()) {
            int boardNum = entry.getKey();
            Boolean[][] board = entry.getValue();

            if (boardsAlreadyDone.contains(boardNum)) {
                continue;
            }

            //check rows & cols together (same iterations)
            for (int i = 0; i < board.length; i++) {
                boolean allCellsOnRowDone = true;
                boolean allCellsOnColDone = true;
                for (int j = 0; j < board[i].length; j++) {
                    allCellsOnRowDone &= board[i][j];
                    allCellsOnColDone &= board[j][i];
                }
                if (allCellsOnRowDone || allCellsOnColDone) {
                    boardsAlreadyDone.add(boardNum);
                    break; //on to next board
                }
            }
        }
    }

    private static int calculateScore(int boardNum, int lastNumCalled) {
        Integer[][] winningBoardNumbers = boardsNumbers.get(boardNum);
        Boolean[][] winningBoardStatus = boardsStatuses.get(boardNum);
        int sum = 0;

        for (int i = 0; i < winningBoardStatus.length; i++) {
            for (int j = 0; j < winningBoardStatus[i].length; j++) {
                if (!winningBoardStatus[i][j]) {
                    sum+= winningBoardNumbers[i][j];
                }
            }
        }

        return sum * lastNumCalled;
    }

    private static Boolean[][] emptyBoardStatusGrid() {
        Boolean[][] grid = new Boolean[5][5];
        for (Boolean[] booleans : grid) {
            Arrays.fill(booleans, Boolean.FALSE);
        }
        return grid;
    }
}

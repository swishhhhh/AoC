package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day4Part1 {

    private static final Map<Integer, Integer[][]> boardsNumbers = new HashMap<>();
    private static final Map<Integer, Boolean[][]> boardsStatuses = new HashMap<>();

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
        int winningBoard = -1;
        int lastNumCalled = -1;
        List<Integer> nums = Helper.extractIntsFromText(numbersLine);
        for (int num: nums) {
            markBoards(num);
            winningBoard = checkForWin(); //returns -1 if no board won yet
            if (winningBoard != -1) {
                lastNumCalled = num;
                break;
            }
        }

        int score = calculateScore(winningBoard, lastNumCalled);

        System.out.printf("Number of Boards: %s, Winning Board: %s, Score = %s%n", boardsNumbers.size(), winningBoard, score);
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

    private static int checkForWin() {
        for (Map.Entry<Integer, Boolean[][]> entry: boardsStatuses.entrySet()) {
            int boardNum = entry.getKey();
            Boolean[][] board = entry.getValue();

            //check rows & cols together (same iterations)
            for (int i = 0; i < board.length; i++) {
                boolean allCellsOnRowDone = true;
                boolean allCellsOnColDone = true;
                for (int j = 0; j < board[i].length; j++) {
                    allCellsOnRowDone &= board[i][j];
                    allCellsOnColDone &= board[j][i];
                }
                if (allCellsOnRowDone || allCellsOnColDone) {
                    return boardNum;
                }
            }
        }

        return -1;
    }

    private static int calculateScore(int winningBoardNum, int lastNumCalled) {
        Integer[][] winningBoardNumbers = boardsNumbers.get(winningBoardNum);
        Boolean[][] winningBoardStatus = boardsStatuses.get(winningBoardNum);
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

package aoc21;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/23">Advent of Code 2021 Day 23</a>
 */
public class Day23Part2 {

    private static final boolean DEBUG = false;

    private static final Set<Integer> CHAMBER_ENTRANCES = Set.of(2, 4, 6, 8);

    private static class State {
        /*
          id is 27 chars long:
           - 0..10 represent the corridor
           - 11-14 the 1st row of the 4 chambers
           - 15-18 the 2nd row of the chambers
           - 19-22 the 3rd row
           - 23-26 the 4th row
         */
        private final String id;
        private final char[] grid; //for caching purposes (to avoid repeated ".toCharArray()" calls)
        private long cost;
        List<String> path;

        public State(String id) {
            this.id = id;
            this.grid = id.toCharArray();
            if (DEBUG) {
                path = new ArrayList<>();
            }
        }

        public String getId() {
            return id;
        }

        public char[] getGrid() {
            return grid;
        }

        public long getCost() {
            return cost;
        }

        public void setCost(long cost) {
            this.cost = cost;
        }

        public String asPrintedGrid() {
            char[] chars = this.getGrid();
            StringBuilder sb = new StringBuilder();
            sb.append("#############\n");   //line 1
            sb.append("#");                 //line 2
            for (int i = 0; i < 11; i++) {
                sb.append(chars[i]);
            }
            sb.append("#\n");

            for (int i = 0; i < 4; i++) {
                sb.append(i == 0 ? "###" : "  #");  //line 3 through 6
                for (int j = 11 + (i * 4); j < 15 + (i * 4); j++) {
                    sb.append(chars[j]).append("#");
                }
                sb.append(i == 0 ? "##\n" : "  \n");
            }

            sb.append("  #########  \n");   //line 7

            return sb.toString();
        }

        @Override
        public String toString() {
            return "State{" +
                    ", id=" + this.id +
                    ", cost=" + this.cost +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State state)) return false;

            return id.equals(state.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day23_input.txt");

        long answer = new Day23Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 53308;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        State source = parseSourceState(lines);
        State target = new State("...........ABCDABCDABCDABCD");

        /*
           Dijkstra's algorithm
         */
        Set<String> visited = new HashSet<>();
        Map<String, Long> costsMap = new HashMap<>();
        Queue<State> queue = new PriorityQueue<>(Comparator.comparingLong(State::getCost));
        queue.add(source);

        long loopCtr = 0;

        while (!queue.isEmpty()) {
            State s = queue.poll();

            loopCtr++;

            if (s.equals(target)) { //base case
                if (DEBUG) {
                    System.out.printf("Found target: loop=%s, cost=%s%n", loopCtr, s.getCost());
                    System.out.printf("=============%n%nPath:%n");
                    for (String id : s.path) {
                        System.out.printf("%s", new State(id).asPrintedGrid());
                    }
                }
                return s.getCost();
            }

            for (State s2 : getAllMovesFrom(s)) {
                String id = s2.getId();
                long newCost = s2.getCost();

                if (visited.contains(id)) {
                    continue;
                }

                if (!costsMap.containsKey(id) || newCost < costsMap.get(id)) {
                    costsMap.put(id, s2.getCost());
                    queue.add(s2);
                }
            }

            visited.add(s.getId());
        }

        throw new RuntimeException("Unable to find a solution.");
    }

    private State parseSourceState(List<String> lines) {
        assert lines.size() == 5;

        StringBuilder sb = new StringBuilder("...........");
        String l = lines.get(2);
        sb.append(l.charAt(3)).append(l.charAt(5)).append(l.charAt(7)).append(l.charAt(9));
        sb.append("DCBA");
        sb.append("DBAC");
        l = lines.get(3);
        sb.append(l.charAt(3)).append(l.charAt(5)).append(l.charAt(7)).append(l.charAt(9));

        return new State(sb.toString());
    }

    private List<State> getAllMovesFrom(State state) {
        List<State> moves = new ArrayList<>();

        char[] grid = state.getGrid();

        //check corridor to see if any of them contain pieces (A,B,C,D) that can move into their target chambers
        for (int i = 0; i < 11; i++) {
            if (grid[i] == '.') {
                continue;
            }

            char piece = grid[i];
            int chamberEntrance = getChamberEntranceIdx(piece); //represents the spot in the corridor just outside the target chamber

            if (canMoveToChamber(state, i, chamberEntrance)) {
                moves.add(moveToChamber(state, i, chamberEntrance));
            }
        }

        //next check each of the chambers and see if you can move its top piece to a target chamber or at least the corridor
        for (int i = 0; i < 4; i++) {
            moves.addAll(movesFromChamber(state, (char) ('A' + i)));
        }

        return moves;
    }

    private int getChamberEntranceIdx(char piece) {
        return switch (piece) {
            case 'A' -> 2;
            case 'B' -> 4;
            case 'C' -> 6;
            case 'D' -> 8;
            default -> -1; //should not happen
        };
    }

    private boolean canMoveToChamber(State state, int srcPos, int chamberEntrance) {
        //verify that the corridor is clear between srcPos and chamberEntrance
        int nextPosFromSrc = chamberEntrance < srcPos ? srcPos - 1 : srcPos + 1;
        if (!isCorridorSegmentClear(state, nextPosFromSrc, chamberEntrance)) {
            return false;
        }

        char[] srcGrid = state.getGrid();
        char piece = srcGrid[srcPos];

        return canMoveIntoChamber(getChamberOccupants(srcGrid, piece), piece);
    }

    private boolean isCorridorSegmentClear(State s, int srcPos, int chamberEntrance) {
        int min = Math.min(srcPos, chamberEntrance);
        int max = Math.max(srcPos, chamberEntrance);
        char[] grid = s.getGrid();

        for (int i = min; i <= max; i++) {
            if (grid[i] != '.') {
                return false;
            }
        }

        return true;
    }

    private char[] getChamberOccupants(char[] grid, char piece) {
        char[] occupants = new char[4];

        int idx = getChamberUpperIdx(piece);
        for (int i = 0; i < 4; i++) {
            occupants[i] = grid[idx];
            idx += 4;
        }

        return occupants;
    }

    private boolean canMoveIntoChamber(char[] grid, char piece) {
        //chamber has to contain only empty cells or cells occupied by the same piece
        for (char cell : grid) {
            if (cell != '.' && cell != piece) {
                return false;
            }
        }
        return true;
    }

    private State moveToChamber(State srcState, int srcPos, int chamberEntrance) {
        char[] srcGrid = srcState.getGrid();
        char piece = srcGrid[srcPos];

        int steps = Math.abs(srcPos - chamberEntrance); //number of steps to chamber entrance
        steps++; //to move into upper chamber
        int targetPos = getChamberUpperIdx(piece);

        char[] chamberOccupants = getChamberOccupants(srcGrid, piece);
        //already at the upper part of the chamber, now see how far down you can get
        for (int i = 1; i < 4; i++) {
            if (chamberOccupants[i] == '.') {
                steps++;
                targetPos += 4;
            }
        }

        char[] newGrid = new char[srcGrid.length];
        System.arraycopy(srcGrid, 0, newGrid, 0, newGrid.length);
        newGrid[srcPos] = '.';
        newGrid[targetPos] = piece;
        State newState = new State(String.valueOf(newGrid));
        newState.setCost(srcState.getCost() + (long) steps * getCostPerStep(piece));
        if (DEBUG) {
            newState.path.addAll(srcState.path);
            newState.path.add(srcState.getId());
        }

        return newState;
    }

    private int getChamberUpperIdx(char piece) {
        return switch (piece) {
            case 'A' -> 11;
            case 'B' -> 12;
            case 'C' -> 13;
            case 'D' -> 14;
            default -> -1; //should not happen
        };
    }

    private int getCostPerStep(char piece) {
        return switch (piece) {
            case 'A' -> 1;
            case 'B' -> 10;
            case 'C' -> 100;
            case 'D' -> 1000;
            default -> -1; //should not happen
        };
    }

    private List<State> movesFromChamber(State state, char srcChamber) {
        List<State> moves = new ArrayList<>();

        char[] srcGrid = state.getGrid();
        int srcChamberEntrance = getChamberEntranceIdx(srcChamber);
        int srcChamberUpperIdx = getChamberUpperIdx(srcChamber);
        char[] srcChamberOccupants = getChamberOccupants(srcGrid, srcChamber);

        if (isChamberEmpty(srcChamberOccupants) || doesChamberContainOnly(srcChamberOccupants, srcChamber)) {
            return moves;
        }

        int srcPos = srcChamberUpperIdx;
        int stepsOutOfSrcChamber = 1;
        //get first/top non-empty cell in chamber
        for (char occupant : srcChamberOccupants) {
            if (occupant != '.') {
                break;
            }
            srcPos += 4;
            stepsOutOfSrcChamber++;
        }

        List<Integer> targetPositions = new ArrayList<>();

        //check left side of corridor
        for (int i = srcChamberEntrance - 1; i >= 0; i--) {
            if (CHAMBER_ENTRANCES.contains(i)) {
                continue; //can't stop at any chamber entrance
            }

            if (srcGrid[i] != '.') {
                break;
            }
            targetPositions.add(i);
        }
        //check right side of corridor
        for (int i = srcChamberEntrance + 1; i < 11; i++) {
            if (CHAMBER_ENTRANCES.contains(i)) {
                continue; //can't stop at any chamber entrance
            }

            if (srcGrid[i] != '.') {
                break;
            }
            targetPositions.add(i);
        }

        //check target chamber
        char piece = srcGrid[srcPos];
        int targetChamberEntrance = getChamberEntranceIdx(piece);
        int stepsIntoChamber = 0;
        if (isCorridorSegmentClear(state, srcChamberEntrance, targetChamberEntrance)) {
            char[] targetChamberOccupants = getChamberOccupants(srcGrid, piece);
            int targetUpper = getChamberUpperIdx(piece);

            if (canMoveIntoChamber(srcGrid, piece)) {
                //look for last/lowest empty cell in chamber
                for (int i = 3; i >= 0; i--) {
                    if (targetChamberOccupants[i] == '.') {
                        stepsIntoChamber = i + 1;
                        targetPositions.add(targetUpper + (i * 4));
                        break;
                    }
                }
            }
        }

        for (int targetPos : targetPositions) {
            int steps = stepsOutOfSrcChamber;

            if (targetPos > 10) { //if target is a chamber
                steps += Math.abs(srcChamberEntrance - targetChamberEntrance); //from source to target chamber entrances
                steps += stepsIntoChamber;
            } else {
                steps += Math.abs(targetPos - srcChamberEntrance); //number of steps from chamber entrance to targetPos
            }

            char[] newGrid = new char[srcGrid.length];
            System.arraycopy(srcGrid, 0, newGrid, 0, newGrid.length);
            newGrid[srcPos] = '.';
            newGrid[targetPos] = srcGrid[srcPos];
            State newState = new State(String.valueOf(newGrid));
            newState.setCost(state.getCost() + (long) steps * getCostPerStep(piece));
            if (DEBUG) {
                newState.path.addAll(state.path);
                newState.path.add(state.getId());
            }
            moves.add(newState);
        }

        return moves;
    }

    private boolean isChamberEmpty(char[] srcChamberOccupants) {
        return String.valueOf(srcChamberOccupants).equals("....");
    }

    private boolean doesChamberContainOnly(char[] chamberOccupants, char piece) {
        for (char occupant : chamberOccupants) {
            if (occupant != piece && occupant != '.') {
                return false;
            }
        }

        return true;
    }
}

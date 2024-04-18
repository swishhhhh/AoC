package aoc21;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/23">Advent of Code 2021 Day 23</a>
 */
public class Day23Part1 {

    private static final boolean DEBUG = false;

    private static final Set<Integer> CHAMBER_ENTRANCES = Set.of(2, 4, 6, 8);
    private static final Set<Integer> UPPER_CHAMBER_HALVES = Set.of(11, 12, 13, 14);
    private static final Set<Integer> LOWER_CHAMBER_HALVES = Set.of(15, 16, 17, 18);

    private static class State {
        private final String id; //19 chars: 0..10 represent the corridor, 11-14 the upper halves of chambers A-D, 15-18 the lower halves
        private final char[] grid; //for caching purposes (to avoid repeated ".toCharArray()" calls)
        long cost;
        List<String> path;

        public State(String id) {
            this.id = id;
            this.grid = id.toCharArray();
            if (DEBUG) {
                path = new ArrayList<>();
            }
        }

        public String getId() {
            return this.id;
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
            sb.append("###");               //line 3
            for (int i = 11; i < 15; i++) {
                sb.append(chars[i]).append("#");
            }
            sb.append("##\n");
            sb.append("  #");               //line 4
            for (int i = 15; i < 19; i++) {
                sb.append(chars[i]).append("#");
            }
            sb.append("  \n");
            sb.append("  #########  \n");   //line 5

            return sb.toString();
        }

        @Override
        public String toString() {
            return "State{" +
                    ", id=" + id +
                    ", cost=" + cost +
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

        long answer = new Day23Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 13336;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        State source = parseSourceState(lines);
        State target = new State("...........ABCDABCD");

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

            //base case
            if (s.equals(target)) {
                if (DEBUG) {
                    System.out.printf("Found target: loop=%s, cost=%s%n", loopCtr, s.getCost());
                    System.out.printf("=============%n%nWinning Path:%n");
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

    private List<State> movesFromChamber(State state, char srcChamber) {
        List<State> moves = new ArrayList<>();

        char[] srcGrid = state.getGrid();
        int srcChamberEntrance = getChamberEntranceIdx(srcChamber);
        int upperIdx = getChamberUpperIdx(srcChamber);
        int lowerIdx = getChamberLowerIdx(srcChamber);

        if (srcGrid[lowerIdx] == '.' && srcGrid[upperIdx] == '.') { //empty chamber
            return moves;
        }

        if (srcGrid[lowerIdx] == srcChamber && srcGrid[upperIdx] == srcChamber) { //chamber already filled with right pieces
            return moves;
        }

        if (srcGrid[lowerIdx] == srcChamber && srcGrid[upperIdx] == '.') { //lower half already has right piece
            return moves;
        }

        int srcPos = upperIdx;
        if (srcGrid[upperIdx] == '.') {
            srcPos = lowerIdx;
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
        if (isCorridorSegmentClear(state, srcChamberEntrance, targetChamberEntrance)) {
            int targetLower = getChamberLowerIdx(piece);
            if (srcGrid[targetLower] == '.') {
                targetPositions.add(targetLower);
            } else {
                int targetUpper = getChamberUpperIdx(piece);
                if (srcGrid[targetUpper] == '.') {
                    targetPositions.add(targetUpper);
                }
            }
        }

        for (int targetPos : targetPositions) {
            int steps = 0;
            if (srcPos == lowerIdx) {
                steps++; //to move from lower half of source chamber into its upper half
            }
            steps++; //to move from upper half to source entrance

            if (UPPER_CHAMBER_HALVES.contains(targetPos) || LOWER_CHAMBER_HALVES.contains(targetPos)) { //if target is a chamber
                steps += Math.abs(srcChamberEntrance - targetChamberEntrance); //from source to target chamber entrances
                steps++;     //to move into upper half of target chamber
                if (LOWER_CHAMBER_HALVES.contains(targetPos)) {
                    steps++; //to move from upper half of target chamber to lower half
                }
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

    private boolean canMoveToChamber(State state, int srcPos, int chamberEntrance) {
        //verify that the corridor is clear between srcPos and chamberEntrance
        int nextPosFromSrc = chamberEntrance < srcPos ? srcPos - 1 : srcPos + 1;
        if (!isCorridorSegmentClear(state, nextPosFromSrc, chamberEntrance)) {
            return false;
        }

        char[] srcGrid = state.getGrid();
        char piece = srcGrid[srcPos];

        //if upper chamber is occupied, can't move in...
        int upperIdx = getChamberUpperIdx(piece);
        if (srcGrid[upperIdx] != '.') {
            return false;
        }

        //if upper chamber is empty make sure lower chamber contains piece of same type
        int lowerIdx = getChamberLowerIdx(piece);
        return srcGrid[lowerIdx] == '.' || srcGrid[lowerIdx] == piece;
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

    private int getChamberEntranceIdx(char piece) {
        return switch (piece) {
            case 'A' -> 2;
            case 'B' -> 4;
            case 'C' -> 6;
            case 'D' -> 8;
            default -> -1; //should not happen
        };
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

    private int getChamberLowerIdx(char piece) {
        return getChamberUpperIdx(piece) + 4;
    }

    private State moveToChamber(State srcState, int srcPos, int chamberEntrance) {
        char[] srcGrid = srcState.getGrid();
        char piece = srcGrid[srcPos];

        int steps = Math.abs(srcPos - chamberEntrance); //number of steps to chamber entrance
        steps++; //to move into upper chamber
        int targetPos = getChamberUpperIdx(piece);

        int lowerIdx = getChamberLowerIdx(piece);
        if (srcGrid[lowerIdx] == '.') { //if lower chamber is empty, add 1 more step
            steps++;
            targetPos = lowerIdx;
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

    private int getCostPerStep(char piece) {
        return switch (piece) {
            case 'A' -> 1;
            case 'B' -> 10;
            case 'C' -> 100;
            case 'D' -> 1000;
            default -> -1; //should not happen
        };
    }

    private State parseSourceState(List<String> lines) {
        assert lines.size() == 5;

        StringBuilder sb = new StringBuilder("...........");
        String l = lines.get(2);
        sb.append(l.charAt(3)).append(l.charAt(5)).append(l.charAt(7)).append(l.charAt(9));
        l = lines.get(3);
        sb.append(l.charAt(3)).append(l.charAt(5)).append(l.charAt(7)).append(l.charAt(9));

        return new State(sb.toString());
    }
}

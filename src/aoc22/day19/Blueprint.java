package aoc22.day19;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Blueprint {
//    public static final int MINUTES_TO_COLLECT = 24;
//    private static final int[] TRIANGULAR_SEQUENCE;

    private final int ID;
    private final int minutesToCollect;
    private final int costOfOreRobotInOre;
    private final int costOfClayRobotInOre;
    private final int costOfObsidianRobotInOre;
    private final int costOfObsidianRobotInClay;
    private final int costOfGeodeRobotInOre;
    private final int costOfGeodeRobotInObsidian;
    private int maxOreRobotsToBuy;
    private int maxClayRobotsToBuy;
    private int maxObsidianRobotsToBuy;
    private int maxGeodeRobotsToBuy = Integer.MAX_VALUE; //implicitly unlimited

    private int[] triangularSequenceTable;

//    private Map<String, Integer> maxGeodesMemoTable = new HashMap<>();
    private Set<String> visitedSet = new HashSet<>();
    private long cacheHits;
    private long cacheMisses;

    public Blueprint(int ID, int minutesToCollect, int costOfOreRobotInOre, int costOfClayRobotInOre,
                     int costOfObsidianRobotInOre, int costOfObsidianRobotInClay,
                     int costOfGeodeRobotInOre, int costOfGeodeRobotInObsidian) {
        this.ID = ID;
        this.minutesToCollect = minutesToCollect;
        this.costOfOreRobotInOre = costOfOreRobotInOre;
        this.costOfClayRobotInOre = costOfClayRobotInOre;
        this.costOfObsidianRobotInOre = costOfObsidianRobotInOre;
        this.costOfObsidianRobotInClay = costOfObsidianRobotInClay;
        this.costOfGeodeRobotInOre = costOfGeodeRobotInOre;
        this.costOfGeodeRobotInObsidian = costOfGeodeRobotInObsidian;

        this.maxOreRobotsToBuy = Math.max(costOfOreRobotInOre, costOfClayRobotInOre);
        this.maxOreRobotsToBuy = Math.max(this.maxOreRobotsToBuy, costOfObsidianRobotInOre);
        this.maxOreRobotsToBuy = Math.max(this.maxOreRobotsToBuy, costOfGeodeRobotInOre);

        this.maxClayRobotsToBuy = this.costOfObsidianRobotInClay;
        this.maxObsidianRobotsToBuy = this.costOfGeodeRobotInObsidian;

        this.initTriangularSequenceTable();
    }

    private void initTriangularSequenceTable() {
        this.triangularSequenceTable = new int[this.minutesToCollect + 1];
        int sum = 0;
        this.triangularSequenceTable[0] = 0;
        for (int i = 1; i < this.triangularSequenceTable.length; i++) {
            this.triangularSequenceTable[i] = i + this.triangularSequenceTable[i-1];
        }
    }

    public int getID() {
        return ID;
    }

    /*
     * DFS
     */
    public State getStateWithMaxGeodes() {
        State initialState = new State(1, 0, 0, 0, 0);

        Map<Integer, Integer> maxGeodesAtMinuteMark = new HashMap<>();

        Stack<State> stack = new Stack<>();
        stack.push(initialState);

        State maxState = null;

        //DFS
        long loopCtr = 0;
        long loopsOptimized = 0;
        while (!stack.isEmpty()) {
            loopCtr++;
            if (loopCtr % 10_000_000 == 0) {
                State tempState = stack.peek();
                double hitPct = (double) cacheHits / (cacheHits+cacheMisses) * 100D;
                System.out.printf("Loop ctr=%s, Cache Size=%s, Cache Hits=%s, Misses=%s, Hit Rate=%2.2f%%, " +
                                "Optimized=%s, Queue size=%s, Peek Minutes=%s, State=%s%n",
                        loopCtr, visitedSet.size(), cacheHits, cacheMisses, hitPct, loopsOptimized,
                        stack.size(), tempState.getMinuteCount(), tempState);
            }

            State incomingState = stack.pop();

            //term condition: MINUTES_TO_COLLECT minutes reached
            if (incomingState.getMinuteCount() >= this.minutesToCollect) {
                if (maxState == null || incomingState.getTotalGeode() > maxState.getTotalGeode()) {
                    maxState = incomingState;
                    System.out.println("Max geodes = " + maxState.getTotalGeode());
                }
                continue;
            }

            //term condition: figure out max number of geodes that can be collected in most optimistic scenario =
            //          num-geodes so far + (num-geode-robots * minutes remaining) + triangularSequence(minutes - 1)
            // triangularSequence = https://en.wikipedia.org/wiki/Triangular_number
//            int maxOptimistic = getMaxOptimisticTotalGeodesFromState(incomingState);

            //optimization 1: if numGeodes at this stage (minute mark) < maxGeodesAtMinuteMark(minuteMark)
//            Integer maxGeodesSoFarAtThisStage = maxGeodesAtMinuteMark.get(incomingState.getMinuteCount());
//            if (maxGeodesSoFarAtThisStage == null) {
//                maxGeodesSoFarAtThisStage = -1;
//            }
//            if (maxOptimistic < maxGeodesSoFarAtThisStage) {
//                loopsOptimized++;
//                continue; //no point in continuing from here
//            } else {
//                maxGeodesAtMinuteMark.put(incomingState.getMinuteCount(), incomingState.getTotalGeode());
//            }

            //optimization 2: if incomingState's signature was already encountered, continue/skip..
            String signature = incomingState.getSignature();
//            Integer maxGeodesFromCache = maxGeodesMemoTable.get(signature);
//            if (maxGeodesFromCache != null) {
            if (visitedSet.contains(signature)) {
                this.cacheHits++;
//                if (incomingState.getTotalGeode() <= maxGeodesFromCache) {
                    loopsOptimized++;
                    continue;
//                }
            } else {
//                maxGeodesMemoTable.put(signature, incomingState.getTotalGeode());
                visitedSet.add(signature);
                this.cacheMisses++;
            }


            State state;

            //don't buy anything scenario
            state = incomingState.clone();
            state.harvest();
            state.addToPath(incomingState);
            stack.push(state);

            //Buy Ore robots scenario - check if you need another (max not exceeded) and you have the funds in ore...
            if (incomingState.getOreRobotsCount() < this.maxOreRobotsToBuy
                    && this.costOfOreRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfOreRobotInOre);
                state.harvest();
                state.setOreRobotsCount(state.getOreRobotsCount() + 1);
                state.addToPath(incomingState);
                stack.push(state);
            }

            //Buy Clay robots scenario - check if you need another one and you have the funds in ore...
            if (incomingState.getClayRobotsCount() < this.maxClayRobotsToBuy
                    && this.costOfClayRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfClayRobotInOre);
                state.harvest();
                state.setClayRobotsCount(state.getClayRobotsCount() + 1);
                state.addToPath(incomingState);
                stack.push(state);
            }

            //Buy Obsidian robots scenario - check if you need another one and you have the funds in ore & clay...
            if (incomingState.getObsidianRobotsCount() < this.maxObsidianRobotsToBuy
                    && this.costOfObsidianRobotInOre <= incomingState.getTotalOre()
                    && this.costOfObsidianRobotInClay <= incomingState.getTotalClay()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfObsidianRobotInOre);
                state.setTotalClay(state.getTotalClay() - this.costOfObsidianRobotInClay);
                state.harvest();
                state.setObsidianRobotsCount(state.getObsidianRobotsCount() + 1);
                state.addToPath(incomingState);
                stack.push(state);
            }

            //Buy Geode robots scenario - check if you have the funds in ore & obsidian... (no need to check max for geodes..)
            if (this.costOfGeodeRobotInOre <= incomingState.getTotalOre()
                    && this.costOfGeodeRobotInObsidian <= incomingState.getTotalObsidian()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfGeodeRobotInOre);
                state.setTotalObsidian(state.getTotalObsidian() - this.costOfGeodeRobotInObsidian);
                state.harvest();
                state.setGeodeRobotsCount(state.getGeodeRobotsCount() + 1);
                state.addToPath(incomingState);
                stack.push(state);
            }
        }

        return maxState;
    }

    private int getMaxOptimisticTotalGeodesFromState(State state) {
        /*
         * max number of geodes that can be collected in most optimistic scenario =
         *     num-geodes so far + (num-geode-robots * minutes remaining) + triangularSequence(minutes - 1)
         *
         *     triangularSequence = https://en.wikipedia.org/wiki/Triangular_number
         */
        int minutesRemaining = this.minutesToCollect - state.getMinuteCount();
        int max = state.getTotalGeode()
                + (state.getGeodeRobotsCount() * minutesRemaining)
                + this.triangularSequenceTable[minutesRemaining - 1];

        return max;
    }

    public State oldDFSgetStateWithMaxGeodes() {
        State initialState = new State(1, 0, 0, 0, 0);
//        Queue<State> queue = new LinkedBlockingQueue<>();
//        queue.add(initialState);

        //assumption: purchase a geode robot as soon as you have the funds
        //setup max geodes at minute count map
        Map<Integer, Integer> maxGeodeRobotsAtMinuteMark = new HashMap<>();

        Stack<State> stack = new Stack<>();
        stack.push(initialState);

//        int maxGeodesAfter24Mins = 0;
        State maxState = null;

        //DFS
        long loopCtr = 0;
//        while (!queue.isEmpty()) {
        while (!stack.isEmpty()) {
            loopCtr++;
            if (loopCtr % 10_000_000 == 0) {
                //State tempState = queue.peek();
                State tempState = stack.peek();
                System.out.printf("Loop ctr = %s, Queue size=%s, Peek Minutes=%s, State=%s%n",
                        loopCtr, stack.size(), tempState.getMinuteCount(), tempState);
            }

            //State incomingState = queue.poll();
            State incomingState = stack.pop();

            //term condition: if numGeodeRobots at this stage (minute mark) < maxGeodeRobotsAtMinuteMark(minuteMark)
            Integer maxGeodeRobotsAtThisStage = maxGeodeRobotsAtMinuteMark.get(incomingState.getMinuteCount());
            if (maxGeodeRobotsAtThisStage == null) {
                maxGeodeRobotsAtThisStage = 0;
            }
            if (incomingState.getGeodeRobotsCount() < maxGeodeRobotsAtThisStage) {
                continue; //no point in continuing from here
            } else if (incomingState.getGeodeRobotsCount() > maxGeodeRobotsAtThisStage) {
                maxGeodeRobotsAtMinuteMark.put(incomingState.getMinuteCount(), incomingState.getGeodeRobotsCount());
            }

            //term condition: MINUTES_TO_COLLECT minutes reached
            if (incomingState.getMinuteCount() >= this.minutesToCollect) {
                if (maxState == null || incomingState.getTotalGeode() > maxState.getTotalGeode()) {
//                    maxGeodesAfter24Mins = incomingState.getTotalGeode();
                    maxState = incomingState;
                    System.out.println("Max geodes = " + maxState.getTotalGeode());
                }
                continue;
            }


            State state;

            //don't buy anything scenario
            state = incomingState.clone();
            state.harvest();
            state.addToPath(incomingState);
//          queue.add(state);
            stack.push(state);

            //Buy Ore robots scenario - check if you need another (max not exceeded) and you have the funds in ore...
            if (incomingState.getOreRobotsCount() < this.maxOreRobotsToBuy
                    && this.costOfOreRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfOreRobotInOre);
                state.harvest();
                state.setOreRobotsCount(state.getOreRobotsCount() + 1);
                state.addToPath(incomingState);
//                queue.add(state);
                stack.push(state);
//                anyRobotsBought = true;
            }

            //Buy Clay robots scenario - check if you need another one and you have the funds in ore...
            if (incomingState.getClayRobotsCount() < this.maxClayRobotsToBuy
                    && this.costOfClayRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfClayRobotInOre);
                state.harvest();
                state.setClayRobotsCount(state.getClayRobotsCount() + 1);
                state.addToPath(incomingState);
//                queue.add(state);
                stack.push(state);
//                anyRobotsBought = true;
            }

            //Buy Obsidian robots scenario - check if you need another one and you have the funds in ore & clay...
            if (incomingState.getObsidianRobotsCount() < this.maxObsidianRobotsToBuy
                    && this.costOfObsidianRobotInOre <= incomingState.getTotalOre()
                    && this.costOfObsidianRobotInClay <= incomingState.getTotalClay()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfObsidianRobotInOre);
                state.setTotalClay(state.getTotalClay() - this.costOfObsidianRobotInClay);
                state.harvest();
                state.setObsidianRobotsCount(state.getObsidianRobotsCount() + 1);
                state.addToPath(incomingState);
//                queue.add(state);
                stack.push(state);
//                anyRobotsBought = true;
            }

            //Buy Geode robots scenario - check if you have the funds in ore & obsidian... (no need to check max for geodes..)
            if (this.costOfGeodeRobotInOre <= incomingState.getTotalOre()
                    && this.costOfGeodeRobotInObsidian <= incomingState.getTotalObsidian()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfGeodeRobotInOre);
                state.setTotalObsidian(state.getTotalObsidian() - this.costOfGeodeRobotInObsidian);
                state.harvest();
                state.setGeodeRobotsCount(state.getGeodeRobotsCount() + 1);
                state.addToPath(incomingState);
//                queue.add(state);
                stack.push(state);
//                anyRobotsBought = true;
            }
        }

        return maxState;
    }

    public State BFSgetStateWithMaxGeodes() {
        State initialState = new State(1, 0, 0, 0, 0);
        Queue<State> queue = new LinkedBlockingQueue<>();
        queue.add(initialState);

        //assumption: purchase a geode robot as soon as you have the funds
        //setup max geodes at minute count map
        Map<Integer, Integer> maxGeodeRobotsAtMinuteMark = new HashMap<>();

//        int maxGeodesAfter24Mins = 0;
        State maxState = null;

        //BFS
        int loopCtr = 0;
        while (!queue.isEmpty()) {
            loopCtr++;
            if (loopCtr % 1000 == 0) {
                State tempState = queue.peek();
                System.out.printf("Loop ctr = %s, Queue size=%s, Peek Minutes=%s, State=%s%n",
                        loopCtr, queue.size(), tempState.getMinuteCount(), tempState);
            }

            State incomingState = queue.poll();

            //term condition: if numGeodes at this stage (minute mark) < maxGeodesAtMinuteMark(minuteMark)
            Integer maxGeodeRobotsAtThisStage = maxGeodeRobotsAtMinuteMark.get(incomingState.getMinuteCount());
            if (maxGeodeRobotsAtThisStage == null) {
                maxGeodeRobotsAtThisStage = 0;
            }
            if (incomingState.getGeodeRobotsCount() < maxGeodeRobotsAtThisStage) {
                continue; //no point in continuing from here
            } else if (incomingState.getGeodeRobotsCount() > maxGeodeRobotsAtThisStage) {
                maxGeodeRobotsAtMinuteMark.put(incomingState.getMinuteCount(), incomingState.getGeodeRobotsCount());
            }

            //term condition: MINUTES_TO_COLLECT minutes reached
            if (incomingState.getMinuteCount() >= this.minutesToCollect) {
                if (maxState == null || incomingState.getTotalGeode() > maxState.getTotalGeode()) {
//                    maxGeodesAfter24Mins = incomingState.getTotalGeode();
                    maxState = incomingState;
                    System.out.println("Max geodes = " + maxState.getTotalGeode());
                }
                continue;
            }


            State state;
            boolean anyRobotsBought = false;

            //don't buy anything scenario
//            if (!anyRobotsBought) {
                state = incomingState.clone();
                state.harvest();
                state.addToPath(incomingState);
                queue.add(state);
//            }

            //Buy Ore robots scenario - check if you need another (max not exceeded) and you have the funds in ore...
            if (incomingState.getOreRobotsCount() < this.maxOreRobotsToBuy
                    && this.costOfOreRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfOreRobotInOre);
                state.harvest();
                state.setOreRobotsCount(state.getOreRobotsCount() + 1);
                state.addToPath(incomingState);
                queue.add(state);
                anyRobotsBought = true;
            }

            //Buy Clay robots scenario - check if you need another one and you have the funds in ore...
            if (incomingState.getClayRobotsCount() < this.maxClayRobotsToBuy
                    && this.costOfClayRobotInOre <= incomingState.getTotalOre()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfClayRobotInOre);
                state.harvest();
                state.setClayRobotsCount(state.getClayRobotsCount() + 1);
                state.addToPath(incomingState);
                queue.add(state);
                anyRobotsBought = true;
            }

            //Buy Obsidian robots scenario - check if you need another one and you have the funds in ore & clay...
            if (incomingState.getObsidianRobotsCount() < this.maxObsidianRobotsToBuy
                    && this.costOfObsidianRobotInOre <= incomingState.getTotalOre()
                    && this.costOfObsidianRobotInClay <= incomingState.getTotalClay()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfObsidianRobotInOre);
                state.setTotalClay(state.getTotalClay() - this.costOfObsidianRobotInClay);
                state.harvest();
                state.setObsidianRobotsCount(state.getObsidianRobotsCount() + 1);
                state.addToPath(incomingState);
                queue.add(state);
                anyRobotsBought = true;
            }

            //Buy Geode robots scenario - check if you have the funds in ore & obsidian... (no need to check max for geodes..)
            if (this.costOfGeodeRobotInOre <= incomingState.getTotalOre()
                    && this.costOfGeodeRobotInObsidian <= incomingState.getTotalObsidian()) {
                state = incomingState.clone();
                state.setTotalOre(state.getTotalOre() - this.costOfGeodeRobotInOre);
                state.setTotalObsidian(state.getTotalObsidian() - this.costOfGeodeRobotInObsidian);
                state.harvest();
                state.setGeodeRobotsCount(state.getGeodeRobotsCount() + 1);
                state.addToPath(incomingState);
                queue.add(state);
                anyRobotsBought = true;
            }
        }

        return maxState;
    }

    @Override
    public String toString() {
        return "Blueprint{" +
                "ID=" + ID +
                ", minutesToCollect=" + minutesToCollect +
                ", costOfOreRobotInOre=" + costOfOreRobotInOre +
                ", costOfClayRobotInOre=" + costOfClayRobotInOre +
                ", costOfObsidianRobotInOre=" + costOfObsidianRobotInOre +
                ", costOfObsidianRobotInClay=" + costOfObsidianRobotInClay +
                ", costOfGeodeRobotInOre=" + costOfGeodeRobotInOre +
                ", costOfGeodeRobotInObsidian=" + costOfGeodeRobotInObsidian +
                ", maxOreRobotsToBuy=" + maxOreRobotsToBuy +
                ", maxClayRobotsToBuy=" + maxClayRobotsToBuy +
                ", maxObsidianRobotsToBuy=" + maxObsidianRobotsToBuy +
                ", maxGeodeRobotsToBuy=" + maxGeodeRobotsToBuy +
                '}';
    }
}

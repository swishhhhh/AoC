package aoc22.day19;

import java.util.*;

public class Blueprint {
    private final int ID;
    private final int minutesToCollect;
    private final int costOfOreRobotInOre;
    private final int costOfClayRobotInOre;
    private final int costOfObsidianRobotInOre;
    private final int costOfObsidianRobotInClay;
    private final int costOfGeodeRobotInOre;
    private final int costOfGeodeRobotInObsidian;
    private int maxOreRobotsToBuy;
    private final int maxClayRobotsToBuy;
    private final int maxObsidianRobotsToBuy;

    private final Set<String> visitedSet = new HashSet<>();
    private long cacheHits;
    private long cacheMisses;

    private boolean debug = false;

    public Blueprint(int ID, int minutesToCollect, int costOfOreRobotInOre, int costOfClayRobotInOre,
                     int costOfObsidianRobotInOre, int costOfObsidianRobotInClay,
                     int costOfGeodeRobotInOre, int costOfGeodeRobotInObsidian, boolean debug) {
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

        this.debug = debug;
    }

    public int getID() {
        return ID;
    }

    /*
     * DFS
     */
    public State getStateWithMaxGeodes() {
        State initialState = new State(1, 0, 0, 0, 0);
        Stack<State> stack = new Stack<>();
        stack.push(initialState);
        State maxState = null;

        long loopCtr = 0;
        while (!stack.isEmpty()) {
            loopCtr++;
            if (debug && loopCtr % 1_000_000 == 0) {
                State tempState = stack.peek();
                double hitPct = (double) cacheHits / (cacheHits+cacheMisses) * 100D;
                System.out.printf("Loop ctr=%s, Cache Size=%s, Cache Hits=%s, Misses=%s, Hit Rate=%2.2f%%, " +
                                "Queue size=%s, Peek Minutes=%s, State=%s%n",
                        loopCtr, visitedSet.size(), cacheHits, cacheMisses, hitPct,
                        stack.size(), tempState.getMinuteCount(), tempState);
            }

            State incomingState = stack.pop();

            //term condition: time ran out
            if (incomingState.getMinuteCount() >= this.minutesToCollect) {
                if (maxState == null || incomingState.getTotalGeode() > maxState.getTotalGeode()) {
                    maxState = incomingState;
                    if (debug) {
                        System.out.println("Max geodes = " + maxState.getTotalGeode());
                    }
                }
                continue;
            }

            //optimization: if incomingState's signature was already encountered, continue/skip..
            incomingState = pruneExcessMinerals(incomingState);
            String signature = incomingState.getSignature();
            if (visitedSet.contains(signature)) {
                this.cacheHits++;
                continue;
            } else {
                visitedSet.add(signature);
                this.cacheMisses++;
            }

            //don't buy anything scenario
            State state = incomingState.clone();
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

    private State pruneExcessMinerals(State incomingState) {
        State state = incomingState.clone();
        int minsLeft = minutesToCollect - state.getMinuteCount();

        //(minsLeft * maxOreRobotsToBuy) = if you spent the most ore you can spend for every remaining step
        //(state.getOreRobotsCount() * (minsLeft-1)) = amount of ore existing ore robots will make from here until end
        int maxOrePossiblyNeededFromHere = (minsLeft * maxOreRobotsToBuy) - (state.getOreRobotsCount() * (minsLeft-1));
        if (state.getTotalOre() > maxOrePossiblyNeededFromHere) {
            state.setTotalOre(maxOrePossiblyNeededFromHere);
        }

        //ditto (but with clay)..
        int maxClayPossiblyNeededFromHere = (minsLeft * maxClayRobotsToBuy) - (state.getClayRobotsCount() * (minsLeft-1));
        if (state.getTotalClay() > maxClayPossiblyNeededFromHere) {
            state.setTotalClay(maxClayPossiblyNeededFromHere);
        }

        //ditto (but with obsidian)..
        int maxObsidianPossiblyNeededFromHere = (minsLeft * maxObsidianRobotsToBuy) - (state.getObsidianRobotsCount() * (minsLeft-1));
        if (state.getTotalObsidian() > maxObsidianPossiblyNeededFromHere) {
            state.setTotalObsidian(maxObsidianPossiblyNeededFromHere);
        }

        return state;
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
                '}';
    }
}

package aoc22;

import utils.ResourceLoader;
import java.util.*;

/**
 * <a href="https://adventofcode.com/2022/day/16">Advent of Code 2022 Day 16</a>
 * IMPORTANT!! need to run this with at least 6G of heap space (-Xmx6G), will still take a few minutes to run...
 */
public class Day16Part2 {
	static class Chamber {
		private final String id;
		private final int flowRate;
		private final List<String> neighborIds = new ArrayList<>(); //edges - ids of other chambers reachable from this one

		public Chamber(String id, int flowRate) {
			this.id = id;
			this.flowRate = flowRate;
		}

		public String getId() {
			return id;
		}
		public int getFlowRate() {
			return flowRate;
		}
		public List<String> getNeighborIds() {
			return neighborIds;
		}

		public void addNeighbor(String id) {
			this.neighborIds.add(id);
		}
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Chamber chamber = (Chamber) o;

			return Objects.equals(id, chamber.id);
		}
		@Override
		public int hashCode() {
			return id != null ? id.hashCode() : 0;
		}
		@Override
		public String toString() {
			return "Chamber{" +
					"id='" + id + '\'' +
					", flowRate=" + flowRate +
					", neighborIds=" + neighborIds +
					'}';
		}
	}

	static class State {
		private int playerNum;
		private String chamberId;
		private final LinkedHashMap<String, Boolean> nonZeroValvesOpened; //true means already opened
		private int minutesRemaining;
		private boolean actionValve; //true = valve was just checked, false = just arrived at the chamber
		private int cumulativeFlowRate;
		private List<State> path = new ArrayList<>();

		public State(int playerNum, String chamberId, LinkedHashMap<String, Boolean> nonZeroValvesOpened,
					 int minutesRemaining, boolean actionValve, int cumulativeFlowRate, List<State> path) {
			this.playerNum = playerNum;
			this.chamberId = chamberId;
			this.nonZeroValvesOpened = nonZeroValvesOpened;
			this.minutesRemaining = minutesRemaining;
			this.actionValve = actionValve;
			this.cumulativeFlowRate = cumulativeFlowRate;
			if (path != null) {
				this.path = path;
			}
		}

		public int getPlayerNum() {
			return playerNum;
		}
		public String getChamberId() {
			return chamberId;
		}
		public Map<String, Boolean> getNonZeroValvesOpened() {
			return nonZeroValvesOpened;
		}
		public boolean isChamberValveOpen(String chamberId) {
			return Boolean.TRUE.equals(this.getNonZeroValvesOpened().get(chamberId));
		}
		public int getMinutesRemaining() {
			return minutesRemaining;
		}
		public int getCumulativeFlowRate() {
			return cumulativeFlowRate;
		}
		public boolean isActionValve() {
			return actionValve;
		}
		public List<State> getPath() {
			return path;
		}

		public void setChamberId(String chamberId) {
			this.chamberId = chamberId;
		}
		public void setPlayerNum(int playerNum) {
			this.playerNum = playerNum;
		}
		public void setMinutesRemaining(int minutesRemaining) {
			this.minutesRemaining = minutesRemaining;
		}
		public void setActionValve(boolean actionValve) {
			this.actionValve = actionValve;
		}
		public void setCumulativeFlowRate(int cumulativeFlowRate) {
			this.cumulativeFlowRate = cumulativeFlowRate;
		}
		public void addToPath(State state) {
			this.path.add(state);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			State state = (State) o;

			if (playerNum != state.playerNum) return false;
			if (minutesRemaining != state.minutesRemaining) return false;
			if (actionValve != state.actionValve) return false;
			if (!Objects.equals(chamberId, state.chamberId)) return false;
			return Objects.equals(nonZeroValvesOpened, state.nonZeroValvesOpened);
		}
		@Override
		public int hashCode() {
			int result = chamberId != null ? chamberId.hashCode() : 0;
			result = 31 * result + (nonZeroValvesOpened != null ? nonZeroValvesOpened.hashCode() : 0);
			result = 31 * result + minutesRemaining;
			result = 31 * result + playerNum;
			result = 31 * result + (actionValve ? 1 : 0);
			return result;
		}
		@Override
		public String toString() {
			return "State{" +
					", playerNum=" + playerNum +
					"chamberId='" + chamberId + '\'' +
					", nonZeroValvesOpened=" + nonZeroValvesOpened +
					", minutesRemaining=" + minutesRemaining +
					", actionValve=" + actionValve +
					", cumulativeFlowRate=" + cumulativeFlowRate +
					'}';
		}
		@Override
		protected State clone() {
			return new State(playerNum, chamberId, new LinkedHashMap<>(nonZeroValvesOpened), minutesRemaining,
					actionValve, cumulativeFlowRate, new ArrayList<>(path));
		}

		public String getSignature() {
			StringBuilder sb = new StringBuilder();
			sb.append(chamberId); //2 chars
			sb.append(playerNum); //1 char
			sb.append("|").append(minutesRemaining).append("|"); //3-4 chars
			sb.append((actionValve ? "Y" : "N")); //1 chars

			StringBuilder sb2 = new StringBuilder();
			for (Boolean valveOpen: nonZeroValvesOpened.values()) {
				sb2.append(valveOpen ? "1" : "0");
			}
			sb.append(Integer.parseInt(sb2.toString(), 2)); //compress down to integer (max 5 chars for 15 bits/valves)

			return sb.toString(); //max 13 chars
		}
	}

	private static final Map<String, Chamber> allChambers = new HashMap<>();
	private static final Set<Chamber> nonZeroFlowChambers = new LinkedHashSet<>();
	private static final Map<String, Integer> maxFlowRateIncrementMemoTable = new HashMap<>();
	private static long recursionCtr;
	private static long cacheHits;
	private static long cacheMisses;
	private static long loopAvoidanceOptimizationCtr;

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day16_input.txt");

		for (String line: lines) {
			String[] ary = line.split(" ");
			String id = ary[1];
			int flowRate = Integer.parseInt(ary[4].split("=")[1].replace(";", ""));
			Chamber ch = new Chamber(id, flowRate);
			for (int i = 9; i < ary.length; i++) {
				ch.addNeighbor(ary[i].replace(",", ""));
			}
			allChambers.put(id, ch);
			if (flowRate > 0) nonZeroFlowChambers.add(ch);
		}

		Chamber root = allChambers.get("AA");

		LinkedHashMap<String, Boolean> nonZeroValvesOpened = new LinkedHashMap<>();
		for (Chamber ch: nonZeroFlowChambers) {
			nonZeroValvesOpened.put(ch.getId(), Boolean.FALSE); //valves all start out closed
		}
		State initialState = new State(1, root.getId(), nonZeroValvesOpened, 26,
				false,0, null);
		int answer = calculateMaxFlowRates(initialState);

		System.out.printf("Max Flow Rate = %s%n", answer);

		long expected = 2679;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static int calculateMaxFlowRates(State incomingState) {
		recursionCtr++;

		//out of time: if player-1, reset to player 2, else done
		if (incomingState.getMinutesRemaining() <= 0) {
			//term condition 1: out of time
			if (incomingState.getPlayerNum() == 2) {
				return incomingState.getCumulativeFlowRate(); //no increase in cumulative rate
			}

			//reset for player2
			incomingState = incomingState.clone();
			incomingState.setPlayerNum(2);
			incomingState.setChamberId("AA");
			incomingState.setMinutesRemaining(26);
		}

		//term condition 2: increment found in memo-table
		String signature = incomingState.getSignature();
		Integer maxIncrRateFromCache = maxFlowRateIncrementMemoTable.get(signature);
		if (maxIncrRateFromCache != null) {
			cacheHits++;
			return incomingState.getCumulativeFlowRate() + maxIncrRateFromCache;
		} else {
			cacheMisses++;
		}

		if (DEBUG && recursionCtr % 100_000 == 0) {
			double hitPct = (double) cacheHits / (cacheHits+cacheMisses) * 100D;
			System.out.printf("Recursion count=%s, Cache Size=%s, Cache Hits=%s, Misses=%s, Hit Rate=%2.2f%%, " +
							"Loop Avoidance Count=%s %n",
					recursionCtr, maxFlowRateIncrementMemoTable.size(), cacheHits, cacheMisses, hitPct,
					loopAvoidanceOptimizationCtr);
		}

		//for each of the following: actionValve=true, actionValve=false X all neighbors
		//clone state, apply transitions to it (time decrement, open valve OR change chamber-id, add to path?)
		List<State> states = new ArrayList<>();
		Chamber thisChamber = allChambers.get(incomingState.getChamberId());

		//first (if valve has flow-rate > 0 and valve is unopened), simulate opening the valve and THEN moving to the neighbors
		if (thisChamber.getFlowRate() > 0 && !incomingState.isChamberValveOpen(thisChamber.getId())) {
			//open valve in current chamber
			State state = incomingState.clone();
			state.addToPath(incomingState);
			state.setMinutesRemaining(state.getMinutesRemaining() - 1); //decrement time
			state.setActionValve(true); //indicate it's an "open valve" action
			state.getNonZeroValvesOpened().put(state.getChamberId(), true); //update valves table
			int rateIncrement = thisChamber.getFlowRate() * state.getMinutesRemaining();
			state.setCumulativeFlowRate(state.getCumulativeFlowRate() + rateIncrement);
			states.add(state);

			//continue to each of neighbors
			iterateNeighbors(state, states, thisChamber);
		}

		//next, simulate scenario of bypassing this chamber (not opening its valve) and moving to every neighbor..
		iterateNeighbors(incomingState, states, thisChamber);

		//loop through each state and recurse...
		int maxUntilEndAllPaths = -1;
		for (State state: states) {
			int maxUntilEndThisPath = calculateMaxFlowRates(state);
			maxUntilEndAllPaths = Math.max(maxUntilEndAllPaths, maxUntilEndThisPath);
		}

		//memoize increment to cache
		Integer maxIncrementalRate = maxUntilEndAllPaths - incomingState.getCumulativeFlowRate();
		maxFlowRateIncrementMemoTable.put(signature, maxIncrementalRate);
		return maxUntilEndAllPaths; //term condition 3
	}

	private static void iterateNeighbors(State incomingState, List<State> states, Chamber thisChamber) {
		for (String neighborId: thisChamber.getNeighborIds()) {

			/* unfortunately this optimization didn't pan out - see note in the method itself
			if (alreadyVisitedChamberSinceLastValveOpened(incomingState, neighborId)) {
				loopAvoidanceOptimizationCtr++;
				continue;
			}
			 */

			State state = incomingState.clone();
			state.setMinutesRemaining(state.getMinutesRemaining() - 1); //decrement time
			state.setChamberId(neighborId);
			state.setActionValve(false); //NOT an "open valve" action (rather, a "move to chamber" action)
			states.add(state);
		}
	}

	private static boolean alreadyVisitedChamberSinceLastValveOpened(State incomingState, String chamberId) {
		/*
		 * Optimization: skip adding neighbor/chambers for the SAME player if they've already passed the neighbor
		 *  further up in the path UNLESS there was a valve opened in between
		 * 		i.e.:  A -> B -> A = no point in going from B to A since no valve opened in between
		 *			   A -> B -> open valve B -> A = there is a point going back to A
		 *
		 * However... this optimization didn't pan out because it only saves a very small fraction of loops (chamber
		 *  recursions) at the expense of the extra work (to backtrace and check the path) and the end result is
		 *  around the same run-time (~2:20-2:25 running Java19 with 6GB heap on an Intel 4-core i5-3330S CPU @ 2.70GHz).
		 *
		 *  Stats on full run:
		 * 		Recursion count=105200000, Cache Size=33270509, Cache Hits=63470564, Misses=33270546, Hit Rate=65.61%,
		 * 			Loop Avoidance Count=258608
		 *
		 *  Note how the recursion count is still at ~105MM (compares with ~106MM without the optimization) and
		 *    the loop avoidance count is a mere ~258K.
		 */
		for (State prevState : incomingState.getPath()) {
			if (prevState.isActionValve() || prevState.getPlayerNum() != incomingState.getPlayerNum()) {
				return false;
			}
			if (prevState.getChamberId().equals(chamberId)) {
				return true;
			}
		}
		return false;
	}
}

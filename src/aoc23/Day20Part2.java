package aoc23;

import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Helper.lowestCommonMultiplier;

/**
 * <a href="https://adventofcode.com/2023/day/20">Advent of Code 2023 Day 20</a>
 */
public class Day20Part2 {

	private static class Module {
		String id;
		String type;
		boolean on = false;
		long lowPulsesSentCtr;
		long hiPulsesSentCtr;
		boolean cycleCountFound = false;
		long cycleCount = -1;
		Map<String, Module> destinationModules = new HashMap<>();
		Map<String, Integer> incomingPulseCache = new HashMap<>(); //key = sender module, value = 1 for high pulse, -1 for low

		public Module(String id, String type, boolean on) {
			this.id = id;
			this.type = type;
			this.on = on;
		}

		@Override
		public String toString() {
			return "Module{" +
					"id='" + id + '\'' +
					", type='" + type + '\'' +
					", on=" + on +
					", lowPulsesCtr=" + lowPulsesSentCtr +
					", hiPulsesCtr=" + hiPulsesSentCtr +
					", cycleCount=" + cycleCount +
					", destinationModules=" + destinationModules.keySet() +
					", incomingPulseCache=" + incomingPulseCache +
					'}';
		}

	}

	private static class Pulse {
		String senderId;
		String receiverId;
		int lowOrHigh; //-1 for low, 1 for high

		public Pulse(String senderId, String receiverId, int lowOrHigh) {
			this.senderId = senderId;
			this.receiverId = receiverId;
			this.lowOrHigh = lowOrHigh;
		}

		@Override
		public String toString() {
			return "Pulse{" +
					"sender='" + senderId + '\'' +
					", receiver='" + receiverId + '\'' +
					", lowOrHi=" + lowOrHigh +
					'}';
		}
	}

	private final static String FLIP_FLOP = "flip-flop";
	private final static String CONJUNCTION = "conjunction";
	private final static String BROADCAST = "broadcast";
	private final static String SINK = "sink";
	private final static int HIGH = 1;
	private final static int LOW = -1;
	private final static String BUTTON = "button";
	private final static String BROADCASTER = "broadcaster";

	private final static boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day20_input.txt");

		Map<String, Module> modules = loadModules(lines);

		if (!findCycleCountsForFlipFlops(modules)) {
			throw new RuntimeException("Unable to find cycle counts for flip-flops");
		}

		long answer = process(modules, "rx");

		System.out.printf("Answer = %s%n", answer);

		long expected = 262775362119547L;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static boolean findCycleCountsForFlipFlops(Map<String, Module> modules) {
		final long maxLoops = 1_000_000;
		int loopCtr = 0;
		while (loopCtr < maxLoops) {
			loopCtr++;

			if (DEBUG && loopCtr % 1000 == 0) {
				List<String> missingCycles =
						modules.values().stream()
								.filter(m -> m.type.equals(FLIP_FLOP) && !m.cycleCountFound)
								.map(m -> m.id).toList();
				System.out.printf("Loop ctr = %s, still missing cycles for flip-flop modules: %s%n", loopCtr, missingCycles);
			}

			Queue<Pulse> queue = new LinkedBlockingQueue<>();

			//initial pulse sent when button is pressed
			queue.add(new Pulse("initial", BUTTON, LOW));

			while (!queue.isEmpty()) {
				Pulse pulse = queue.poll();
				processPulse(pulse, modules, queue);
			}

			boolean allFlipFlopCyclesFound = true;
			for (Module m : modules.values()) {
				if (!m.type.equals(FLIP_FLOP)) {
					continue;
				}

				if (!m.cycleCountFound && m.hiPulsesSentCtr > 0) {
					m.cycleCountFound = true;
					m.cycleCount = loopCtr;
				}

				if (!m.cycleCountFound) {
					allFlipFlopCyclesFound = false;
				}
			}

			if (allFlipFlopCyclesFound) {
				if (DEBUG) {
					modules.values().stream().filter(m -> m.type.equals(FLIP_FLOP))
							.forEach(m -> System.out.printf("FF Module %s: cycle=%s%n", m.id, m.cycleCount));
				}
				return true;
			}
		}

		return false;
	}

	private static long process(Map<String, Module> modules, String target) {
		/*
		 * Assumptions: the tail of the machine/graph looks like this (flow from right to left), example modules from
		 *    our puzzle input in parentheses):
		 *   target(rx) <- conjunction-mod-A(zr) <- conjunction-mod-B1(gc) <- conjunction-mod-C1(dn) <- flip-flop-modules[ff1]
		 *                                       <- conjunction-mod-B2(sz) <- conjunction-mod-C1(ms) <- flip-flop-modules[ff2]
		 *                                       <- conjunction-mod-B3(cm) <- conjunction-mod-C3(ks) <- flip-flop-modules[ff3]
		 *                                       <- conjunction-mod-B4(xf) <- conjunction-mod-C4(tc) <- flip-flop-modules[ff4]
		 *
		 *  Note: There can be more (or fewer) mod-Bs and Cs, but each B has exactly one C (otherwise this code needs
		 *        further adapting). Also note that [ff1], [ff2] etc are different lists/arrays of n incoming flip-flop
		 *        modules of possibly different sizes (first one can be 10, 2nd 7, etc).
		 *
		 *  Steps:
		 *       1. Starting from target(rx) find conjunction modules A, B1-n, C1-n.
		 *       2. For each C module, collect its incoming flip-flop modules' cycleCounts (always binary multiples)
		 *          and sum them up.
		 *       3. Calculate the lowest common multiplier of all the C module's sum-of-cycle-counts, that should be the
		 *          answer.
		 *
		 *  Rationale: 1. target(rx) needs a low pulse sent from mod-A.
		 *             2. mod-A will send a low pulse when all its incoming Bs send it highs.
		 *             3. Each mod-B will send a high when at least one of its inputs (corresponding C) sends a low.
		 *                Since each B has only one C as its input, that means each C has to send a low as well.
		 *             4. Each C will send a low when ALL its ff inputs send a high.
		 *             5. The ff inputs for a given C module will all be sending highs when they are all in their "high
		 *                windows" concurrently. E.g. if we have 3 ff input-modules with cycles of 1, 2 & 8 respectively,
		 *                the first time (button-press) they'll all send highs is at 11 (1st one sends high at
		 *                {1, 3, 5, 7, 9, 11,..}, the 2nd at intervals {2-3, 6-7, 10-11,..} and the 3rd at intervals
		 *                {8-15, 24-31,..}). So a quick way of finding the first time they all hit their open windows is
		 *                to sum up their cycleCounts (e.g. 1 + 2 + 8 in the previous example). This pattern works
		 *                because of the aforementioned binary multiples of each cycle-count.
		 *             6. So for each module C's sum-of-cycles, it is guaranteed that all of its ff inputs will be
		 *                sending highs every time a multiple of that (sum-of-cycles) number is hit. Not sure why this
		 *                can't happen even more frequently, but it might be an additional "by design" aspect of the
		 *                puzzle inputs.
		 *             7. Taken together, this means that the LCM of all the module-Cs' sum-of-cycles will give you the
		 *                answer - i.e. the 1st button press when all the stars align in that all the ff-modules high
		 *                windows are open.
		 */

		//find target module
		Module targetMod = modules.get(target);
		assert targetMod != null;

		//find conjunction A
		Module conjunctionA = getIncomingModules(modules, targetMod).get(0);
		assert conjunctionA != null;

		//find conjunction Bs
		List<Module> modBs = getIncomingModules(modules, conjunctionA);

		//find conjunction Cs
		List<Module> modCs = new ArrayList<>();
		modBs.forEach(m -> modCs.addAll(getIncomingModules(modules, m)));

		//for each modC, find its incoming flip-flops, and add up its cycle counts and store in
		List<Long> cycleCounts = new ArrayList<>();
		for(Module modC : modCs) {
			List<Module> ffMods = getIncomingModules(modules, modC);
			cycleCounts.add(ffMods.stream().mapToLong(m -> m.cycleCount).sum());
		}

		return lowestCommonMultiplier(cycleCounts);
	}

	private static List<Module> getIncomingModules(Map<String, Module> modules, Module toModule) {
		return modules.values().stream().filter(m -> toModule.incomingPulseCache.containsKey(m.id)).toList();
	}

	private static void processPulse(Pulse pulse, Map<String, Module> modules, Queue<Pulse> queue) {
		Module sender = modules.get(pulse.senderId);
		Module receiver = modules.get(pulse.receiverId);

		switch (receiver.type) {
			case FLIP_FLOP -> {
				/*
				 * If a flip-flop module receives a high pulse, it is ignored and nothing happens.
				 * If a flip-flop module receives a low pulse, it flips between on and off.
				 *    If it was off, it turns on and sends a high pulse.
				 *    If it was on, it turns off and sends a low pulse.
				 */
				if (pulse.lowOrHigh == HIGH) {
					break;
				}

				int nextPulseHighOrLow = receiver.on ? LOW : HIGH;
				receiver.on = !receiver.on;
				for (String nextRecipientID : receiver.destinationModules.keySet()) {
					queue.add(new Pulse(receiver.id, nextRecipientID, nextPulseHighOrLow));
					if (nextPulseHighOrLow == LOW) {
						receiver.lowPulsesSentCtr++;
					} else {
						receiver.hiPulsesSentCtr++;
					}
				}
			}
			case CONJUNCTION -> {
				/*
				 * Conjunction modules remember the type of the most recent pulse received from each of their connected
				 * input modules. When a conjunction module pulse is received, it first updates its memory for that input.
				 * Then, if it remembers high pulses for all inputs, it sends a low pulse;
				 * otherwise, it sends a high pulse.
				 */
				receiver.incomingPulseCache.put(sender.id, pulse.lowOrHigh);
				int nextPulseHighOrLow = allPulsesInCacheHigh(receiver.incomingPulseCache) ? LOW : HIGH;
				for (String nextRecipientID : receiver.destinationModules.keySet()) {
					queue.add(new Pulse(receiver.id, nextRecipientID, nextPulseHighOrLow));
					if (nextPulseHighOrLow == LOW) {
						receiver.lowPulsesSentCtr++;
					} else {
						receiver.hiPulsesSentCtr++;
					}
				}
			}

			case BROADCAST -> {
				/*
				 * When the broadcast module receives a pulse, it sends the same pulse to all of its destination modules.
				 */
				for (String nextRecipientID : receiver.destinationModules.keySet()) {
					queue.add(new Pulse(receiver.id, nextRecipientID, pulse.lowOrHigh));
					if (pulse.lowOrHigh == LOW) {
						receiver.lowPulsesSentCtr++;
					} else {
						receiver.hiPulsesSentCtr++;
					}
				}
			}
		}

	}

	private static boolean allPulsesInCacheHigh(Map<String, Integer> pulseCache) {
		return pulseCache.values().stream().allMatch(type -> type == HIGH);
	}

	private static Map<String, Module> loadModules(List<String> lines) {
		Map<String, Module> modules = new HashMap<>();

		//load modules first
		for (String line : lines) {
			String[] ary = line.split("->");
			String id = ary[0].trim();
			String type = null;
			if (id.startsWith("%")) {
				type = FLIP_FLOP;
				id = id.substring(1);
			} else if (id.startsWith("&")) {
				type = CONJUNCTION;
				id = id.substring(1);
			} else if (id.equals("broadcaster")){
				type = BROADCAST;
			}

			boolean onOrOff = !type.equals(FLIP_FLOP); //only flip-flops start out off (rest don't matter but confusing if left off)
			Module module = new Module(id, type, onOrOff);
			modules.put(id, module);
		}

		//add one more module to represent the button (for ease of tracking pulse counts).
		Module buttonModule = new Module(BUTTON, BROADCAST, true);
		modules.put(BUTTON, buttonModule);
		buttonModule.destinationModules.put(BROADCASTER, modules.get(BROADCASTER));

		//second pass to load targets (destination modules)
		for (String line : lines) {
			String[] ary = line.split("->");
			String id = ary[0].trim();
			if (List.of('%', '&').contains(id.charAt(0))) {
				id = id.substring(1);
			}
			Module module = modules.get(id);

			String[] ary2 = ary[1].trim().split(",");
			for (String destId : ary2) {
				destId = destId.trim();
				module.destinationModules.put(destId, module);

				//important step!! initialize destination modules "incomingPulseCache" with a low from this sender
				Module destModule = modules.get(destId);
				if (destModule == null) {
					//add a new module of type sink
					destModule = new Module(destId, SINK, true);
					modules.put(destId, destModule);
				}
				destModule.incomingPulseCache.put(id, LOW);
			}
		}

		return modules;
	}
}

package aoc23;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2023/day/20">Advent of Code 2023 Day 20</a>
 */
public class Day20Part1 {

	private static class Module {
		String id;
		String type;
		boolean on = false;
		long lowPulsesSentCtr;
		long hiPulsesSentCtr;
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

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day20_input.txt");

		Map<String, Module> modules = loadModules(lines);
		long answer = process(modules, 1000);

		System.out.printf("Answer = %s%n", answer);

		long expected = 670984704;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static long process(Map<String, Module> modules, int numOfButtonPresses) {
		for (int i = 0; i < numOfButtonPresses; i++) {
			Queue<Pulse> queue = new LinkedBlockingQueue<>();

			//initial pulse sent when button is pressed
			queue.add(new Pulse("initial", BUTTON, LOW));
			while (!queue.isEmpty()) {
				Pulse pulse = queue.poll();
				processPulse(pulse, modules, queue);
			}
		}

		//tally up the pulses
		long totalHigh = 0;
		long totalLow = 0; //numOfButtonPresses; //account for original pulses from button
		for (Module mod : modules.values()) {
			totalHigh += mod.hiPulsesSentCtr;
			totalLow += mod.lowPulsesSentCtr;
		}

		return totalHigh * totalLow;
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

			boolean onOrOff = !type.equals(FLIP_FLOP);  //only flip-flops start out off (rest don't matter but confusing if left off)
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

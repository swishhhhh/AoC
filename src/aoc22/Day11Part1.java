package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2022/day/11">Advent of Code 2022 Day 11</a>
 */
public class Day11Part1 {
	static class Monkey {
		public Monkey(String name) {
			this.name = name;
		}

		public String name;
		public Queue<Integer> items = new LinkedBlockingQueue<>();
		public int inspectedCtr = 0;
		public String operator1;
		public String operator2;
		public String operand;
		public int divisibleNum;
		public int throwToMonkeyNumIfTrue;
		public int throwToMonkeyNumIfFalse;

		@Override
		public String toString() {
			return "Monkey{" +
					"name='" + name + '\'' +
					", items=" + items +
					", inspectedCtr=" + inspectedCtr +
					", operator1='" + operator1 + '\'' +
					", operator2='" + operator2 + '\'' +
					", operand='" + operand + '\'' +
					", divisibleNum=" + divisibleNum +
					", throwToMonkeyNumIfTrue=" + throwToMonkeyNumIfTrue +
					", throwToMonkeyNumIfFalse=" + throwToMonkeyNumIfFalse +
					'}';
		}
	}

	private static final List<Monkey> monkeys = new ArrayList<>();
	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day11_input.txt");
		Monkey m = null;

		//parse input
		for (String line: lines) {
			line = line.trim();
			if (line.startsWith("Monkey")) {
				m = new Monkey(line);
				monkeys.add(m);
			} else if (line.startsWith("Starting items:")) {
				List<Integer> nums = Helper.extractIntsFromText(line);
				m.items.addAll(nums);
			} else if (line.trim().startsWith("Operation:")) {
				String[] ary = line.split(" ");
				m.operator1 = ary[ary.length - 3];
				m.operand = ary[ary.length - 2];
				m.operator2 = ary[ary.length - 1];
			} else if (line.startsWith("Test:")) {
				m.divisibleNum = Helper.extractIntsFromText(line).get(0);
			} else if (line.startsWith("If true:")) {
				m.throwToMonkeyNumIfTrue = Helper.extractIntsFromText(line).get(0);
			} else if (line.startsWith("If false:")) {
				m.throwToMonkeyNumIfFalse = Helper.extractIntsFromText(line).get(0);
			}
		}

		//rounds
		for (int round = 1; round <= 20; round++) {
			for (Monkey monkey: monkeys) {
				int size = monkey.items.size();
				for (int i = 0; i < size; i++) {
					monkey.inspectedCtr++;
					int itemLevel = monkey.items.poll();
					int op1 = Helper.isNumeric(monkey.operator1) ? Integer.parseInt(monkey.operator1) : itemLevel;
					int op2 = Helper.isNumeric(monkey.operator2) ? Integer.parseInt(monkey.operator2) : itemLevel;
					int newLevel = monkey.operand.equals("+") ? op1 + op2 : op1 * op2;
					newLevel = newLevel / 3;

					int throwToMonkeyNum =
							newLevel % monkey.divisibleNum == 0 ? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse ;

					Monkey throwToMonkey = monkeys.get(throwToMonkeyNum);
					throwToMonkey.items.add(newLevel);
				}
			}
		}

		if (DEBUG) {
			monkeys.forEach(System.out::println);
		}

		//sort monkeys by reverse order of inspectedCtr and return product of first 2..
		monkeys.sort((m1, m2) -> m2.inspectedCtr - m1.inspectedCtr);

		long answer = monkeys.get(0).inspectedCtr * monkeys.get(1).inspectedCtr;
		System.out.printf("Total = %s%n", answer);

		long expected = 67830;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

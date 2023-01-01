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
public class Day11Part2 {
	static class Monkey {
		public Monkey(String name) {
			this.name = name;
		}

		public String name;
		public Queue<Long> items = new LinkedBlockingQueue<>();
		public long inspectedCtr = 0;
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

	static List<Monkey> monkeys = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day11_input.txt");
		Monkey m = null;
		long commonMultiplier = 1;

		//parse input
		for (String line: lines) {
			line = line.trim();
			if (line.startsWith("Monkey")) {
				m = new Monkey(line);
				monkeys.add(m);
			} else if (line.startsWith("Starting items:")) {
				List<Integer> nums = Helper.extractIntsFromText(line);
				for (Integer n: nums) {
					m.items.add((long)n);
				}
			} else if (line.trim().startsWith("Operation:")) {
				String[] ary = line.split(" ");
				m.operator1 = ary[ary.length - 3];
				m.operand = ary[ary.length - 2];
				m.operator2 = ary[ary.length - 1];
			} else if (line.startsWith("Test:")) {
				m.divisibleNum = Helper.extractIntsFromText(line).get(0);
				commonMultiplier*= m.divisibleNum;
			} else if (line.startsWith("If true:")) {
				m.throwToMonkeyNumIfTrue = Helper.extractIntsFromText(line).get(0);
			} else if (line.startsWith("If false:")) {
				m.throwToMonkeyNumIfFalse = Helper.extractIntsFromText(line).get(0);
			}
		}

		//rounds
		for (int round = 1; round <= 10_000; round++) {
			for (Monkey monkey: monkeys) {
				int size = monkey.items.size();
				for (int i = 0; i < size; i++) {
					monkey.inspectedCtr++;
					Long itemLevel = monkey.items.poll();
					long op1 = Helper.isNumeric(monkey.operator1) ? Long.parseLong(monkey.operator1) : itemLevel;
					long op2 = Helper.isNumeric(monkey.operator2) ? Long.parseLong(monkey.operator2) : itemLevel;
					long newLevel = monkey.operand.equals("+") ? op1 + op2 : op1 * op2;

					newLevel = newLevel % commonMultiplier; //keeps the number from getting too large without messing up the modulo operations
					long remainder = newLevel % monkey.divisibleNum;
					int throwToMonkeyNum = remainder == 0L ? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse;

					Monkey throwToMonkey = monkeys.get(throwToMonkeyNum);
					throwToMonkey.items.add(newLevel);
				}

			}
		}

		monkeys.forEach(System.out::println);

		//sort monkeys by reverse order of inspectedCtr and return product of first 2..
		monkeys.sort((m1, m2) -> Long.compare(m2.inspectedCtr, m1.inspectedCtr));
		System.out.printf("Common Multiplier = %s, Total = %s%n", commonMultiplier,
				monkeys.get(0).inspectedCtr * monkeys.get(1).inspectedCtr
		);
	}
}

package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Day11Part2 {
	static class Monkey {
		public Monkey(String name) {
			this.name = name;
		}

		public String name;
//		public Queue<BigDecimal> items = new LinkedBlockingQueue<>();
		public Queue<BigInteger> items = new LinkedBlockingQueue<>();
//		public Queue<Double> items = new LinkedBlockingQueue<>();
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

		for (String line: lines) {
			//parse input
			line = line.trim();
			if (line.startsWith("Monkey")) {
				m = new Monkey(line);
				monkeys.add(m);
			} else if (line.startsWith("Starting items:")) {
				List<Integer> nums = Helper.extractIntsFromText(line);
				for (Integer n: nums) {
//					m.items.add(new BigDecimal(n));
					m.items.add(new BigInteger(String.valueOf(n)));
//					m.items.add((double)n);
				}
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
		for (int round = 1; round <= 10_000; round++) {
			if (round % 10 == 0) System.out.println("Round: " + round);

			if (round == 47) {
				String x  = "";
			}

//			System.out.println("Round: " + round);
//			monkeys.forEach(monkey -> System.out.println(monkey));

			for (Monkey monkey: monkeys) {
				int size = monkey.items.size();
				for (int i = 0; i < size; i++) {
					monkey.inspectedCtr++;
//					BigDecimal itemLevel = monkey.items.poll();
					BigInteger itemLevel = monkey.items.poll();
//					Double itemLevel = monkey.items.poll();
//					if (Double.isInfinite(itemLevel) || Double.isNaN(itemLevel)) {
//						System.out.println(itemLevel);
//					}
//					BigDecimal op1 = Helper.isNumeric(monkey.operator1) ? new BigDecimal(monkey.operator1) : itemLevel;
					BigInteger op1 = Helper.isNumeric(monkey.operator1) ? new BigInteger(monkey.operator1) : itemLevel;
//					BigDecimal op2 = Helper.isNumeric(monkey.operator2) ? new BigDecimal(monkey.operator2) : itemLevel;
					BigInteger op2 = Helper.isNumeric(monkey.operator2) ? new BigInteger(monkey.operator2) : itemLevel;
//					double op1 = Helper.isNumeric(monkey.operator1) ? Integer.parseInt(monkey.operator1) : itemLevel;
//					double op2 = Helper.isNumeric(monkey.operator2) ? Integer.parseInt(monkey.operator2) : itemLevel;
//					BigDecimal newLevel = monkey.operand.equals("+") ? op1.add(op2) : op1.multiply(op2);
					BigInteger newLevel = monkey.operand.equals("+") ? op1.add(op2) : op1.multiply(op2);
//					BigDecimal newLevel = monkey.operand.equals("+") ?
//							new BigDecimal(op1.doubleValue() + op2.doubleValue()) :
//							new BigDecimal(op1.doubleValue() * op2.doubleValue()) ;
//					double newLevel = monkey.operand.equals("+") ? op1 + op2 : op1 * op2;
//					newLevel = Math.floor(newLevel / 3L);
//					newLevel = newLevel.divide(new BigDecimal(3));

					int throwToMonkeyNum = -1;

//					if (Double.isInfinite(newLevel) || Double.isNaN(newLevel)) {
////						System.out.println(newLevel);
//
//						BigDecimal bdNewLevel = monkey.operand.equals("+") ?
//							new BigDecimal(op1).add(new BigDecimal(op2)):
//								new BigDecimal(op1).multiply(new BigDecimal(op2));
//						throwToMonkeyNum = bdNewLevel.remainder(new BigDecimal(monkey.divisibleNum)).intValue() == 0
//								? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse;
//
//						newLevel = bdNewLevel.doubleValue();
//					}
//					else {
//						double remainder = newLevel % monkey.divisibleNum;
//						throwToMonkeyNum = remainder < 0.5d ? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse;
//					}
					throwToMonkeyNum =
//							newLevel.remainder(new BigDecimal(monkey.divisibleNum)).intValue() == 0
							newLevel.remainder(new BigInteger(String.valueOf(monkey.divisibleNum))).intValue() == 0
									? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse;
//					double remainder = newLevel % monkey.divisibleNum;
//					int throwToMonkeyNum = remainder < 0.5d ? monkey.throwToMonkeyNumIfTrue : monkey.throwToMonkeyNumIfFalse;

					Monkey throwToMonkey = monkeys.get(throwToMonkeyNum);
					throwToMonkey.items.add(newLevel);
				}
			}
		}

		monkeys.forEach(monkey -> System.out.println(monkey));

		//figure out 2 highest monkey inspectors
//		BigDecimal highest = new BigDecimal(0), secondHighest = new BigDecimal(0);
		long highest = 0L, secondHighest = 0L;
		for (Monkey monkey: monkeys) {
			if (monkey.inspectedCtr > highest) {
				secondHighest = highest;
				highest = monkey.inspectedCtr;
			} else if (monkey.inspectedCtr > secondHighest) {
				secondHighest = monkey.inspectedCtr;
			}
		}

		System.out.printf("Total = %s%n", highest * secondHighest);
	}
}

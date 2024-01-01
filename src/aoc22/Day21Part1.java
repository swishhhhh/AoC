package aoc22;

import aoc22.datastructs.Equation;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2022/day/21">Advent of Code 2022 Day 21</a>
 */
public class Day21Part1 {
	public static Map<String, Long> solvedVariables = new HashMap<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day21_input.txt");
		Queue<Equation> queue = new LinkedBlockingQueue<>();

		//split out the solved variables from the unsolved equations, and load up the queue with the latter
		for (String line: lines) {
			String[] ary = line.split(" ");
			String varName = ary[0].replace(":", "");
			if (ary.length == 2) {
				solvedVariables.put(varName, Long.parseLong(ary[1]));
			} else {
				Equation eq = new Equation(varName, ary[1], ary[3], Equation.createOperatorFromSymbol(ary[2]));
				queue.add(eq);
			}
		}

		//keep solving equations until root is solved
		long answer = 0L;
		long cyclesCtr = 0;
		int cyclesSinceLastSolvedCtr = 0;
		while (queue.size() > 0) {
			cyclesCtr++;
			Equation eq = queue.poll();
			if (!eq.isOperand1Resolved()) {
				Long val = solvedVariables.get(eq.getOperandVariable1());
				if (val != null) {
					eq.resolveOperandValue1(val);
				}
			}
			if (!eq.isOperand2Resolved()) {
				Long val = solvedVariables.get(eq.getOperandVariable2());
				if (val != null) {
					eq.resolveOperandValue2(val);
				}
			}
			if (eq.isOperand1Resolved() && eq.isOperand2Resolved()) {
				long result = eq.solve();
				solvedVariables.put(eq.getEquationVarName(), result);
				cyclesSinceLastSolvedCtr = 0;
				if (eq.getEquationVarName().equals("root")) {
					answer = result;
					break; //done!
				}
			} else { //put unsolved equation back onto the tail of the queue
				queue.add(eq);
				cyclesSinceLastSolvedCtr++;
				if (cyclesSinceLastSolvedCtr > queue.size()) {
					System.err.printf("Houston, we got a problem! Haven't solved an equation for %s cycles, " +
							"and queue size is %s%n", cyclesSinceLastSolvedCtr, queue.size());
					break;
				}
			}
		}

		System.out.printf("Cycles Ctr = %s, Root's Answer = %s%n", cyclesCtr, answer);

		long expected = 157714751182692L;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

package aoc22;

import datastructs.Equation;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2022/day/21">Advent of Code 2022 Day 21</a>
 */
public class Day21Part2 {
	public static Map<String, Long> solvedVariables = new HashMap<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day21_input.txt");
		Queue<Equation> queue = new LinkedBlockingQueue<>();

		solvedVariables.put("zero", 0L);

		//split out the solved variables from the unsolved equations (ignore the "humn" variable)
		for (String line: lines) {
			String[] ary = line.split(" ");
			String varName = ary[0].replace(":", "");
			if (varName.equals("humn")) {
				continue;
			}

			//for part2 treat "root's" operator as an equal sign... which means root can be converted to a regular
			// equation by subtracting one operand from the other and the result = 0
			// 											(i.e. zero = rootOperand1 - rootOperand2)
			if (varName.equals("root")) {
				Equation eq = new Equation("zero", ary[1], ary[3], Equation.Operator.SUBTRACT);
				eq.resolveValue(0L);
				queue.add(eq);
				continue;
			}

			if (ary.length == 2) {
				solvedVariables.put(varName, Long.parseLong(ary[1]));
			} else {
				Equation eq = new Equation(varName, ary[1], ary[3], Equation.createOperatorFromSymbol(ary[2]));
				queue.add(eq);
			}
		}

		//keep solving equations until humn is solved
		long answer = 0L;
		long cyclesCtr = 0;
		int cyclesSinceLastSolvedCtr = 0;
		while (queue.size() > 0) {
			cyclesCtr++;
			Equation eq = queue.poll();


			Long op1Val = solvedVariables.get(eq.getOperandVariable1());
			if (!eq.isOperand1Resolved() && op1Val != null) {
				eq.resolveOperandValue1(op1Val);
			}

			Long op2Val = solvedVariables.get(eq.getOperandVariable2());
			if (!eq.isOperand2Resolved() && op2Val != null) {
				eq.resolveOperandValue2(op2Val);
			}

			Long resultsVal = solvedVariables.get(eq.getEquationVarName());
			if (!eq.isResultValueResolved() && resultsVal != null) {
				eq.resolveValue(resultsVal);
			}

			//if we have both operands solve result
			if (eq.isOperand1Resolved() && eq.isOperand2Resolved()) {
				long result = eq.solve();
				solvedVariables.put(eq.getEquationVarName(), result);
				cyclesSinceLastSolvedCtr = 0;

			} else if ((eq.isOperand1Resolved() || eq.isOperand2Resolved()) && eq.isResultValueResolved()) {
			//if we have 1 of the operands AND the result, solve the other operand
				String varName = eq.isOperand1Resolved() ? eq.getOperandVariable2() : eq.getOperandVariable1();
				int operandToSolveFor = eq.isOperand1Resolved() ? 2 : 1;
				eq.solve();
				long value = operandToSolveFor == 1 ? eq.getOperandValue1() : eq.getOperandValue2();
				solvedVariables.put(varName, value);
				cyclesSinceLastSolvedCtr = 0;
				if (varName.equals("humn")) {
					answer = value;
					break; //done!
				}
			}
			else { //put unsolved equation back onto the tail of the queue
				queue.add(eq);
				cyclesSinceLastSolvedCtr++;
				if (cyclesSinceLastSolvedCtr > queue.size() + 1) {
					System.err.printf("Houston, we got a problem! Haven't solved an equation for %s cycles, " +
							"and queue size is %s%n", cyclesSinceLastSolvedCtr, queue.size());
					break;
				}
			}
		}

		System.out.printf("Cycles Ctr = %s, Human's Answer = %s%n", cyclesCtr, answer);

		long expected = 3373767893067L;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

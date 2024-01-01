package aoc22;

import utils.ResourceLoader;

import java.util.List;
import java.util.Stack;

import static utils.Helper.extractIntsFromText;

/**
 * <a href="https://adventofcode.com/2022/day/5">Advent of Code 2022 Day 5</a>
 */
public class Day05Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day5_input.txt");

		int numInitialLines = 0, numStacks = 0;

		//find stack-numbers line (1st one that doesn't start with "[") to determine both numInitialLines and numStacks
		for (String line : lines) {
			if (line.trim().startsWith("[")) {
				numInitialLines++;
				continue;
			}

			List<Integer> nums = extractIntsFromText(line);
			numStacks = nums.get(nums.size()-1);
			break;
		}

		Stack<String>[] stacks = new Stack[numStacks];
		for (int i = 0; i < numStacks; i++) {
			stacks[i] = new Stack<>();
		}

		//read init lines backwards
		for (int i = numInitialLines - 1; i >= 0; i--) {
			String line = lines.get(i);
			int cursor = 0;
			for (int j = 0; j < numStacks; j++) {
				if (cursor >= line.length()) {
					continue;
				}

				cursor++; //past opening bracket
				String value = line.substring(cursor, cursor + 1);
				if (value.trim().length() > 0) {
					stacks[j].push(value);
				}
				cursor+= 3;
			}
		}

		for (int i = numInitialLines + 2; i < lines.size(); i++) {
			List<Integer> nums = extractIntsFromText(lines.get(i));
			int numMoves = nums.get(0);
			int srcStack = nums.get(1);
			int targetStack = nums.get(2);

			for (int j = 0; j < numMoves; j++) {
				stacks[targetStack - 1].push(stacks[srcStack - 1].pop());
			}
		}

		StringBuilder answer = new StringBuilder();
		for (Stack<String> stack : stacks) {
			answer.append(stack.peek());
		}

		System.out.println(answer);

		String expected = "LJSVLTWQM";
		if (!answer.toString().equals(expected)) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

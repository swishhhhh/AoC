package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;
import java.util.Stack;

public class Day5Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day5_input.txt");

		int numInitialLines = 8;
//		int numInitialLines = 3;
		int numStacks = 9;
//		int numStacks = 3;

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
			List<Integer> nums = Helper.extractIntsFromText(lines.get(i));
			int numMoves = nums.get(0);
			int srcStack = nums.get(1);
			int targetStack = nums.get(2);

			Stack<String> tempStack = new Stack<>();
			for (int j = 0; j < numMoves; j++) {
				tempStack.push(stacks[srcStack - 1].pop());
			}
			for (int j = 0; j < numMoves; j++) {
				stacks[targetStack - 1].push(tempStack.pop());
			}
		}

		for (Stack<String> stack : stacks) {
			System.out.print(stack.peek());
		}
	}
}

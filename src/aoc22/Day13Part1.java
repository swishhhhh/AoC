package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/13">Advent of Code 2022 Day 13</a>
 */
public class Day13Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day13_input.txt");

		int pairsCtr = 0;
		int sumOfCorrectPairs = 0;
		String part1, part2;
		for (int i = 0; i < lines.size(); i+= 3) {
			pairsCtr++;
			part1 = lines.get(i);
			part2 = lines.get(i+1);

			int result = compareLines(part1, part2);
			if (result == -1) {
				sumOfCorrectPairs+= pairsCtr;
			}

			if (result == 0) {
				System.err.printf("Found pair that are same: '%s' | '%s'%n", part1, part2);
			}
		}

		System.out.printf("Sum of Correct Pairs = %s%n", sumOfCorrectPairs);
	}

	private static int compareLines(String leftPart, String rightPart) {
		String[] leftAry = parseArray(leftPart);
		String[] rightAry = parseArray(rightPart);

		for (int i = 0; i < Math.max(leftAry.length, rightAry.length); i++) {

			//handle case where 1 of the arrays are done already
			if (i >= leftAry.length) { //left side ran out of items
				return -1;
			}

			if (i >= rightAry.length) { //right side ran out of items
				return 1;
			}

			//handle case where both sides are ints, compare them...
			boolean isLeftAnInt = Helper.isNumeric(leftAry[i]);
			boolean isRightAnInt = Helper.isNumeric(rightAry[i]);
			if (isLeftAnInt && isRightAnInt) {
				int l = Integer.parseInt(leftAry[i]);
				int r = Integer.parseInt(rightAry[i]);
				if (l < r) return -1;
				if (r < l) return 1;
				continue;
			}

			//handle case where one side is an int and the other not -> convert int to array
			if (!isLeftAnInt && isRightAnInt) {
				rightAry[i] = "[" + rightAry[i] + "]";
			}
			if (!isRightAnInt && isLeftAnInt) {
				leftAry[i] = "[" + leftAry[i] + "]";
			}

			//compare arrays by recursing
			int result = compareLines(leftAry[i], rightAry[i]);
			if (result != 0) {
				return result;
			}
		}

		//both parts are equal
		return 0;
	}

	private static String[] parseArray(String input) {
		if (!input.startsWith("[") || !input.endsWith("]")) {
			throw new IllegalArgumentException("Not an array (missing enclosing brackets) " + input);
		}

		ArrayList<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int openBracketsCtr = 0;

		//ignore enclosing brackets by starting at 1 and ending at len - 1
		for (int i = 1; i < input.length() - 1; i++) {
			String s = input.substring(i, i+1);
			//if [
			if (s.equals("[")) {
				openBracketsCtr++;
				sb.append(s);
				continue;
			}

			//if ]
			if (s.equals("]")) {
				openBracketsCtr--;
				sb.append(s);
				continue;
			}

			//if ,
			if (s.equals(",")) {
				if (openBracketsCtr == 0) {
					list.add(sb.toString());
					sb = new StringBuilder();
				} else {
					sb.append(s);
				}
				continue;
			}

			//everything else
			sb.append(s);
		}

		//add last item
		if (sb.length() > 0) {
			list.add(sb.toString());
		}

		return list.toArray(new String[]{});
	}
}

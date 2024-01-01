package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/13">Advent of Code 2022 Day 13</a>
 */
public class Day13Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day13_input.txt");
		lines = lines
				.stream()
				.filter(line -> line.trim().length() > 1) //filter out blank lines
				.collect(Collectors.toList());

		String packet1 = "[[2]]";
		String packet2 = "[[6]]";
		lines.add(packet1);
		lines.add(packet2);

		lines.sort((l, r) -> compareLines(l, r));

		int packet1Loc = lines.indexOf(packet1) + 1;
		int packet2Loc = lines.indexOf(packet2) + 1;
		long answer = packet1Loc * packet2Loc;
		System.out.printf("Packet1 location: %s, Packet2 location = %s, Product = %s%n", packet1Loc, packet2Loc, answer);

		long expected = 23111;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static int compareLines(String leftPart, String rightPart) {
		String[] leftAry = parseArray(leftPart);
		String[] rightAry = parseArray(rightPart);

		for (int i = 0; i < Math.max(leftAry.length, rightAry.length); i++) {

			//handle case where 1 of the arys are done already
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

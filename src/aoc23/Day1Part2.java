package aoc23;

import utils.ResourceLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/1">Advent of Code 2023 Day 1</a>
 */
public class Day1Part2 {
	private static final Collection<String> ALPHA_DIGITS =
			Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
	private static final Map<String, String> DIGITS_MAP =
			Map.of("one", "1",
					"two", "2",
					"three", "3",
					"four", "4",
					"five", "5",
					"six", "6",
					"seven", "7",
					"eight", "8",
					"nine", "9");

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day1_input.txt");

		long sum = 0;
		for (String line: lines) {
			StringBuilder sb = new StringBuilder(getFirstAlphaOrNumDigitInLine(line));
			sb.append(getLastAlphaOrNumDigitInLine(line));
			long num = Long.parseLong(sb.toString());
			System.out.printf("Digit on line %s: %s%n", line, num);
			sum+= num;
		}

		System.out.printf("Sum = %s%n", sum);
	}

	private static String getFirstAlphaOrNumDigitInLine(String line) {
		int firstIdxFromAlpha = Integer.MAX_VALUE;
		String firstValueFromAlpha = null;

		for (String s : ALPHA_DIGITS) {
			int idx = line.indexOf(s);
			if (idx != -1 && idx < firstIdxFromAlpha) {
				firstIdxFromAlpha = idx;
				firstValueFromAlpha = DIGITS_MAP.get(s);
			}
		}

		int firstIdxFromNum = Integer.MAX_VALUE;
		char[] ary = line.toCharArray();
		for (int i = 0; i < ary.length; i++) {
			if (i > firstIdxFromAlpha) {
				break;
			}

			if (ary[i] >= '0' && ary[i] <= '9') {
				firstIdxFromNum = i;
				break;
			}
		}

		return firstIdxFromAlpha < firstIdxFromNum ? firstValueFromAlpha : String.valueOf(ary[firstIdxFromNum]);
	}

	private static String getLastAlphaOrNumDigitInLine(String line) {
		int lastIdxFromAlpha = Integer.MIN_VALUE;
		String lastValueFromAlpha = null;

		for (String s : ALPHA_DIGITS) {
			int idx = line.lastIndexOf(s);
			if (idx != -1 && idx > lastIdxFromAlpha) {
				lastIdxFromAlpha = idx;
				lastValueFromAlpha = DIGITS_MAP.get(s);
			}
		}

		int lastIdxFromNum = Integer.MIN_VALUE;
		char[] ary = line.toCharArray();

		for (int i = ary.length - 1; i >= 0; i--) {
			if (i < lastIdxFromAlpha) {
				break;
			}

			if (ary[i] >= '0' && ary[i] <= '9') {
				lastIdxFromNum = i;
				break;
			}
		}

		return lastIdxFromAlpha > lastIdxFromNum ? lastValueFromAlpha : String.valueOf(ary[lastIdxFromNum]);
	}
}

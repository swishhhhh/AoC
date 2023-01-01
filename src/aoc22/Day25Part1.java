package aoc22;

import utils.ResourceLoader;
import java.util.*;

/**
 * <a href="https://adventofcode.com/2022/day/25">Advent of Code 2022 Day 25</a>
 */
public class Day25Part1 {

	static long[] FIVER_MULTIPLIERS = new long[21];
	static long[] POS_MAX_VALUES = new long[21];
	static String[] fiverDigits = {"2", "1", "0", "-", "="};

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day25_input.txt");

		initializeTables();

		long sum = 0;
		for (String line: lines) {
			long lineVal = snafuToDecimal(line);
			System.out.println(line + " -> " + lineVal);
			sum+= lineVal;
		}

		System.out.printf("Total in decimal = %s%n", sum);
		System.out.printf("Total in snafu = %s%n", decimalToSnafu(sum));
	}

	private static void initializeTables() {
		//prefill the positional values
		FIVER_MULTIPLIERS[0] = 1;
		long l = 1;
		for (int i = 1; i < FIVER_MULTIPLIERS.length; i++) {
			l*= 5;
			FIVER_MULTIPLIERS[i] = l;
		}

		//prefill max-values table
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < POS_MAX_VALUES.length; i++) {
			sb.append("2");
			POS_MAX_VALUES[i] = snafuToDecimal(sb.toString());
		}
	}

	static String decimalToSnafu(long dec) {
		//first determine len of snafu string by maxing out each fiver place
		int snafuStringLen = calculateSnafuStringLen(dec);

		StringBuilder snafuString = new StringBuilder();
		long sum = 0;
		for (int i = 0; i < snafuStringLen; i++) {
			//for each decimal place in reverse order {2,1,0,-,=}, find number with the closest positive distance
			int digit = snafuStringLen - i;
			String testStr = stringOfValues(digit, "2"); //e.g. 3= 222, 2=22, 1=2

			long delta = dec - sum;
			String selected = "X";
			for (String s: fiverDigits) {
				testStr = s + testStr.substring(1);
				long distance = snafuToDecimal(testStr) - delta;
				if (distance < 0) {
					break;
				}
				selected = s;
			}
			snafuString.append(selected);
			sum+= snafuToDecimal(selected + stringOfValues(digit-1, "0"));
		}

		return snafuString.toString();
	}

	private static String stringOfValues(int len, String value) {
		return String.valueOf(value).repeat(len);
	}

	private static int calculateSnafuStringLen(long value) {
		int len = 1;

		for (long posMaxValue : POS_MAX_VALUES) {
			if (value <= posMaxValue) {
				return len;
			}
			len++;
		}

		throw new IllegalStateException("can't support this number");
	}

	private static long snafuToDecimal(String snafu) {
		long sum = 0;
		for (int i = 0; i < snafu.length(); i++) {
			char c = snafu.charAt(snafu.length() - 1 - i);
			switch (c) {
				case '2' -> sum+= (2*FIVER_MULTIPLIERS[i]);
				case '1' -> sum+= FIVER_MULTIPLIERS[i];
				case '-' -> sum-= FIVER_MULTIPLIERS[i];
				case '=' -> sum-= (2*FIVER_MULTIPLIERS[i]);
			}
		}

		return sum;
	}
}
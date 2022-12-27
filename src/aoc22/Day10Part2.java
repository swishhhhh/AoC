package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/10">Advent of Code 2022 Day 10</a>
 */
public class Day10Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day10_input.txt");
		String[][] crt = new String[6][40];

		int cycle = 0;
		int register = 1;
		long prevValue = 0;
		long value;
		boolean noop;

		for (String line: lines) {
			String[] ary = line.split(" ");
			cycle++;
			if (ary.length == 2) {
				value = Integer.parseInt(ary[1]);
				noop = false;
			} else {
				noop = true;
				value = 0;
			}

			int row = getRow(cycle);
			int col = getCol(cycle);
			crt[row][col] = isSpriteOverCycle(cycle, register) ? "#" : ".";

			register += prevValue;
			prevValue = value;

			//2nd cycle
			if (!noop) {
				cycle++;
				col = getCol(cycle);
				row = getRow(cycle);
				crt[row][col] = isSpriteOverCycle(cycle, register) ? "#" : ".";

				register += prevValue;
				prevValue = 0;
			}
		}

		for (int row = 0; row < crt.length; row++) {
			for (int col = 0; col < crt[row].length; col++) {
				System.out.print(crt[row][col]);
			}
			System.out.println();
		}
	}

	private static int getCol(int cycle) {
		return (cycle -1) % 40;
	}

	private static int getRow(int cycle) {
		return (cycle - 1) / 40;
	}

	private static boolean isSpriteOverCycle(int cycle, int middleOfSprite) {
		int col = getCol(cycle);
		return Math.abs(middleOfSprite - col) <= 1;
	}
}

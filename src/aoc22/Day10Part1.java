package aoc22;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/10">Advent of Code 2022 Day 10</a>
 */
public class Day10Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day10_input.txt");

		int cycle = 0;
		long register = 1, multipliedSum = 0;
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

			if (cycle <= 220) {
				if ((cycle - 20) % 40 == 0) {
					multipliedSum = multipliedSum + (cycle * register);
				}
			}

			register += prevValue;
			prevValue = value;

			//2nd cycle
			if (!noop) {
				cycle++;
				if (cycle <= 220) {
					if ((cycle - 20) % 40 == 0) {
						multipliedSum = multipliedSum + (cycle * register);
					}
				}

				register += prevValue;
				prevValue = 0;
			}
		}

		System.out.printf("Steps = %s, MultipliedSum = %s%n", cycle, multipliedSum);
	}
}

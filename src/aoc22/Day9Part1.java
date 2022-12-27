package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/9">Advent of Code 2022 Day 9</a>
 */
public class Day9Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day9_input.txt");

		int hx = 0, hy = 0, tx = 0, ty = 0;
		HashSet<String> tailSet = new HashSet<>();
		addToTailSet(tx, ty, tailSet);

		for (String line: lines) {
			String[] ary = line.split(" ");
			String direction = ary[0];
			int steps = Integer.parseInt(ary[1]);

			for (int i = 0; i < steps; i++) {
				switch (direction) {
					case "R" -> {
						hx++;
						if (tx + 1 < hx) {
							tx++;
							ty = hy;
						}
						addToTailSet(tx, ty, tailSet);
					}
					case "L" -> {
						hx--;
						if (tx - 1 > hx) {
							tx--;
							ty = hy;
						}
						addToTailSet(tx, ty, tailSet);
					}
					case "U" -> {
						hy++;
						if (ty + 1 < hy) {
							ty++;
							tx = hx;
						}
						addToTailSet(tx, ty, tailSet);
					}
					case "D" -> {
						hy--;
						if (ty - 1 > hy) {
							ty--;
							tx = hx;
						}
						addToTailSet(tx, ty, tailSet);
					}
				}
			}

		}

		System.out.printf("Total = %s%n", tailSet.size());
	}

	private static void addToTailSet(int tx, int ty, HashSet<String> tailSet) {
		tailSet.add(tx + "," + ty);
	}
}

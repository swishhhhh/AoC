package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/9">Advent of Code 2022 Day 9</a>
 */
public class Day9Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day9_input_part2.txt");

		int[] xAry = new int[10];
		int[] yAry = new int[xAry.length];
		int head = 0, tail = xAry.length - 1;
		HashSet<String> tailSet = new HashSet<>();
		addToTailSet(xAry[tail], yAry[tail], tailSet);

		for (String line: lines) {
			String[] ary = line.split(" ");
			String direction = ary[0];
			int steps = Integer.parseInt(ary[1]);

			for (int step = 0; step < steps; step++) {
				switch (direction) {
					case "R" -> xAry[head]++;
					case "L" -> xAry[head]--;
					case "U" -> yAry[head]++;
					case "D" -> yAry[head]--;
				}

				for (int j = 1; j <= tail; j++) {
					int leaderX = xAry[j-1];
					int followerX = xAry[j];
					int leaderY = yAry[j-1];
					int followerY = yAry[j];

					int xMovement = 0, yMovement = 0;
					if (leaderX - 1 > followerX) { //leader is 2 steps to the right, increment X
						xMovement = 1;
						if (leaderY > followerY) { //leader is NE, increment Y as well
							yMovement = 1;
						} else if (leaderY < followerY) { //leader is SE, decrement Y as well
							yMovement = -1;
						}
					} else if (leaderX + 1 < followerX) { //leader is 2 steps to the left, decrement X
						xMovement = -1;
						if (leaderY > followerY) { //leader NW, increment Y
							yMovement = 1;
						} else if (leaderY < followerY) { //leader SW, decrement Y
							yMovement = -1;
						}
					}
					if (leaderY - 1 > followerY) { //leader is 2 steps up
						yMovement = 1;
						if (leaderX > followerX) { //leader NE
							xMovement = 1;
						} else if (leaderX < followerX) { //NW
							xMovement = -1;
						}
					} else if (leaderY + 1 < followerY) { //leader is 2 steps down
						yMovement = -1;
						if (leaderX > followerX) { //leader SE
							xMovement = 1;
						} else if (leaderX < followerX) { //SW
							xMovement = -1;
						}
					}

					xAry[j] = xAry[j] + xMovement;
					yAry[j] = yAry[j] + yMovement;
				}

				addToTailSet(xAry[tail], yAry[tail], tailSet);
			}
		}

		System.out.printf("Total = %s%n", tailSet.size());
	}

	private static void addToTailSet(int tx, int ty, HashSet<String> tailSet) {
		tailSet.add(tx + "," + ty);
	}
}

package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;

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

					if (leaderX - 1 > followerX && leaderY - 1 > followerY) { //2 NE, move 1 NE
						xAry[j]++;
						yAry[j]++;
					} else if (leaderX - 1 > followerX && leaderY + 1 < followerY) { //2 SE
						xAry[j]++;
						yAry[j]--;
					} else if (leaderX + 1 < followerX && leaderY - 1 > followerY) { //2 NW
						xAry[j]--;
						yAry[j]++;
					} else if (leaderX + 1 < followerX && leaderY + 1 < followerY) { //2 SW
						xAry[j]--;
						yAry[j]--;
					} else if (leaderX - 1 > followerX) { //follower is 2 over to the left, move 1 step to the right and align yAry
						xAry[j]++;
						yAry[j] = yAry[j-1];
					} else if (leaderX + 1 < followerX) { //follower is 2 over to the right, move 1 step to the left..
						xAry[j]--;
						yAry[j] = yAry[j-1];
					} else if (leaderY - 1 > followerY) { //follower is 2 over to the bottom, move 1 step up and align xAry
						yAry[j]++;
						xAry[j] = xAry[j-1];
					} else if (leaderY + 1 < followerY) { //follower is 2 over to the top, move 1 step down..
						yAry[j]--;
						xAry[j] = xAry[j-1];
					}
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

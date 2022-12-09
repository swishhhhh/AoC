package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;

public class Day9Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day9_input_part2.txt");
//		List<Integer> numbers = ResourceLoader.readInts("aoc22/Day9_input.txt");

		int[] xAry = new int[10];
		int[] yAry = new int[xAry.length];
		int head = 0, tail = xAry.length - 1;
//		int hx = 0, hy = 0, tx = 0, ty = 0;
		HashSet<String> tailSet = new HashSet<>();
		addToTailSet(xAry[tail], yAry[tail], tailSet);
		int ctr = 0;

		for (String line: lines) {
			String[] ary = line.split(" ");
			String direction = ary[0];
			int steps = Integer.parseInt(ary[1]);

			for (int i = 0; i < steps; i++) {
				ctr++;
//				if (ctr == 12) {
//					String x = "";
//				}
//				boolean isDiagonal = hx != tx && hy != ty;
				switch (direction) {
					case "R" -> {
//						hx++;
						xAry[head]++;
						for (int j = 0; j < tail; j++) {
							if (xAry[j+1] + 1 < xAry[j]) {
								xAry[j+1]++;
								if (yAry[j+1] + 1 < yAry[j]) yAry[j+1]++;
								if (yAry[j+1] - 1 > yAry[j]) yAry[j+1]--;
							}

							addToTailSet(xAry[tail], yAry[tail], tailSet);
						}
					}
					case "L" -> {
//						hx--;
//						if (tx - 1 > hx) tx--;
//						if (isDiagonal&& hx != tx) ty = hy;
//						addToTailSet(xAry[tail], yAry[tail], tailSet);
						xAry[head]--;
						for (int j = 0; j < tail; j++) {
//							boolean wasDiagonal = xAry[j] + 1 != xAry[j+1] && yAry[j] != yAry[j+1];
							if (xAry[j+1] - 1 > xAry[j]) {
								xAry[j+1]--;
//								yAry[j+1] = yAry[j];
								if (yAry[j+1] + 1 < yAry[j]) yAry[j+1]++;
								if (yAry[j+1] - 1 > yAry[j]) yAry[j+1]--;
							}
//							if (wasDiagonal && xAry[j] != xAry[j+1]) yAry[j+1] = yAry[j];
							addToTailSet(xAry[tail], yAry[tail], tailSet);
						}
					}
					case "U" -> {
						yAry[head]++;
						for (int j = 0; j < tail; j++) {
							if (yAry[j+1] + 1 < yAry[j]) {
								yAry[j+1]++;
								if (xAry[j+1] + 1 < xAry[j]) xAry[j+1]++;
								if (xAry[j+1] - 1 > xAry[j]) xAry[j+1]--;
							}
							addToTailSet(xAry[tail], yAry[tail], tailSet);
						}
					}
					case "D" -> {
//						hy--;
//						if (ty - 1 > hy) ty--;
//						if (isDiagonal && hy != ty) tx = hx;
//						addToTailSet(xAry[tail], yAry[tail], tailSet);
						yAry[head]--;
						for (int j = 0; j < tail; j++) {
							if (yAry[j+1] - 1 > yAry[j]) {
								yAry[j+1]--;
//								xAry[j+1] = xAry[j];
								if (xAry[j+1] + 1 < xAry[j]) xAry[j+1]++;
								if (xAry[j+1] - 1 > xAry[j]) xAry[j+1]--;
							}
//							if (wasDiagonal && yAry[j] != yAry[j+1]) xAry[j+1] = xAry[j];
							addToTailSet(xAry[tail], yAry[tail], tailSet);
						}
					}
				}
//				System.out.println(String.valueOf(tx) + "," + String.valueOf(ty));
			}

		}

		System.out.printf("Total = %s%n", tailSet.size());
	}

	private static void addToTailSet(int tx, int ty, HashSet<String> tailSet) {
		tailSet.add(String.valueOf(tx) + "," + String.valueOf(ty));
	}


}

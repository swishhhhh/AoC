package aoc22;

import utils.ResourceLoader;

import java.util.*;

public class Day4Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day4_input.txt");

		int ctr = 0;
		int min1, max1, min2, max2;

		for (String line: lines) {
			String[] pairAry = line.split(",");

			String[] rangeAry = pairAry[0].split("-");
			min1 = Integer.parseInt(rangeAry[0]);
			max1 = Integer.parseInt(rangeAry[1]);

			rangeAry = pairAry[1].split("-");
			min2 = Integer.parseInt(rangeAry[0]);
			max2 = Integer.parseInt(rangeAry[1]);

			if (max1 - min1 >= max2 - min2) { //pair1 is larger (or equal)
				if (max1 >= max2 && min1 <= min2) {
					ctr++;
				}
			} else { //pair2 is larger
				if (max2 >= max1 && min2 <= min1) {
					ctr++;
				}
			}
		}

		System.out.printf("Ctr = %s%n", ctr);
	}
}

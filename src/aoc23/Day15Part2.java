package aoc23;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/15">Advent of Code 2023 Day 15</a>
 */
public class Day15Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day15_input.txt");

		LinkedList<String>[] boxes = new LinkedList[256];
		HashMap<String, Integer>[] focalBoxes = new HashMap[256];

		String[] tokens = lines.get(0).split(",");
		for (String token : tokens) {
			processToken(token, boxes, focalBoxes);
		}

		long sum = addUpBoxes(boxes, focalBoxes);

		System.out.printf("Sum = %s%n", sum);
	}

	private static long addUpBoxes(LinkedList<String>[] boxes, HashMap<String, Integer>[] focalBoxes) {
		long sum = 0;

		for (int i = 0; i < boxes.length; i++) {
			int slot = 0;
			if (boxes[i] == null) {
				continue;
			}
			for (String label : boxes[i]) {
				slot++;
				int focalLength = focalBoxes[i].get(label);
				long focusingPower = (i + 1) * slot * focalLength;
				sum+= focusingPower;
			}
		}

		return sum;
	}

	private static void processToken(String token, LinkedList<String>[] boxes, HashMap<String, Integer>[] focalBoxes) {
		String label;
		int focalLength = 0;
		boolean removeOperation = token.endsWith("-");

		if (removeOperation) {
			label = token.split("-")[0];
		} else { //add/replace operation
			String[] ary = token.split("=");
			label = ary[0];
			focalLength = Integer.parseInt(ary[1]);
		}

		int boxNum = hash(label);

		if (boxes[boxNum] == null) {
			boxes[boxNum] = new LinkedList<>();
		}
		LinkedList<String> box = boxes[boxNum];

		if (focalBoxes[boxNum] == null) {
			focalBoxes[boxNum] = new HashMap<>();
		}
		HashMap<String, Integer> focalBox = focalBoxes[boxNum];

		if (removeOperation) {
			box.remove(label);
			focalBox.remove(label);
		} else { //add/replace operation
			int idx = box.indexOf(label);
			if (idx != -1) {
				box.remove(label);
				box.add(idx, label);
			} else {
				box.add(label);
			}
			focalBox.put(label, focalLength);
		}
	}

	private static int hash(String token) {
		int value = 0;

		for (char c : token.toCharArray()) {
			value+= c;
			value*= 17;
			value%= 256;
		}

		return value;
	}
}

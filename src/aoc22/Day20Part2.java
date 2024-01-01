package aoc22;

import utils.ResourceLoader;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *  <a href="https://adventofcode.com/2022/day/20">Advent of Code 2022 Day 20</a>
 */
public class Day20Part2 {

	static final long KEY = 811589153L;

	static class NumHolder {
		String id;
		long value;

		public NumHolder(String id, long value) {
			this.id = id;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			NumHolder numHolder = (NumHolder) o;

			return Objects.equals(id, numHolder.id);
		}

		@Override
		public int hashCode() {
			return id != null ? id.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "NumHolder{" +
					"id='" + id + '\'' +
					", value=" + value +
					'}';
		}
	}

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<Integer> numbers = ResourceLoader.readInts("aoc22/Day20_input.txt");

		int ctr = 0;
		LinkedList<NumHolder> list = new LinkedList<>();

		//populate new rotating list
		for (int num: numbers) {
			ctr++;
			list.add(new NumHolder(String.valueOf(ctr), num * KEY));
		}
		if (DEBUG) {
			list.forEach(nh2 -> System.out.print(nh2.value + ","));
			System.out.println();
		}

		for (int i = 0; i < 10; i++) {
			ctr = 0;
			for (int ignored: numbers) {
				ctr++;
				rotate(list, String.valueOf(ctr));
			}
		}
		if (DEBUG) {
			list.forEach(nh2 -> System.out.print(nh2.value + ","));
			System.out.println();
		}

		//find 0
		int zeroIdx = -1;
		for (NumHolder nh: list) {
			zeroIdx++;
			if (nh.value == 0) {
				break;
			}
		}

		int increment = 1000 % list.size();
		int idx1 = zeroIdx + increment;
		if (idx1 >= list.size()) {
			idx1-= list.size();
		}

		increment = 2000 % list.size();
		int idx2 = zeroIdx + increment;
		if (idx2 >= list.size()) {
			idx2-= list.size();
		}

		increment = 3000 % list.size();
		int idx3 = zeroIdx + increment;
		if (idx3 >= list.size()) {
			idx3-= list.size();
		}

		long val1 = list.get(idx1).value;
		long val2 = list.get(idx2).value;
		long val3 = list.get(idx3).value;
		long answer = val1 + val2 + val3;

		if (DEBUG) {
			System.out.printf("zeroIdx=%s, idx1=%s, idx2=%s, idx3=%s", zeroIdx, idx1, idx2, idx3);
		}

		System.out.printf("val1=%s, val2=%s, val3=%s, sum=%s%n", val1, val2, val3, answer);
		long expected = 9738258246847L;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	static void rotate(LinkedList<NumHolder> list, String itemId) {
		int listSize = list.size();
		NumHolder finder = new NumHolder(itemId, -1);
		int idx = list.indexOf(finder);

		NumHolder item = list.get(idx);
		if (item.value == 0) { //noop
			return;
		}

		long increment = item.value % (listSize-1);
		list.remove(item);

		int insertionPoint = idx + (int)increment;
		if (insertionPoint >= listSize) {
			insertionPoint-= (listSize - 1);
		} else if (insertionPoint < 0) {
			insertionPoint+= (listSize - 1);
		}

		list.add(insertionPoint, item);
	}
}

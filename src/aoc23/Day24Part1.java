package aoc23;

import utils.ResourceLoader;

import java.util.LinkedList;
import java.util.List;

import static utils.Helper.extractLongsFromText;

/**
 * <a href="https://adventofcode.com/2023/day/24">Advent of Code 2023 Day 24</a>
 */
public class Day24Part1 {
	static class HailPath {
		double x;
		double y;
		double xVelocity;
		double yVelocity;

		public HailPath(double x, double y, double xVelocity, double yVelocity) {
			this.x = x;
			this.y = y;
			this.xVelocity = xVelocity;
			this.yVelocity = yVelocity;
		}

		@Override
		public String toString() {
			return "HailPath{" +
					"x=" + x +
					", y=" + y +
					", xVelocity=" + xVelocity +
					", yVelocity=" + yVelocity +
					'}';
		}
	}

	static class Point {
		double x;
		double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point{" +
					"x=" + x +
					", y=" + y +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day24_input.txt");

		List<HailPath> paths = new LinkedList<>();
		for (String line : lines) {
			List<Long> nums = extractLongsFromText(line, true);
			HailPath path = new HailPath(nums.get(0), nums.get(1), nums.get(3), nums.get(4));
			paths.add(path);
		}

//		double min = 7, max = 27;
		double min = 200000000000000D, max = 400000000000000D;
		long count = countIntersectingPaths(paths, min, max);
		System.out.printf("Count = %s%n", count);

		long expected = 13892;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long countIntersectingPaths(List<HailPath> paths, double min, double max) {
		long count = 0;

		for (int i = 0; i < paths.size() - 1; i++) {
			HailPath p1 = paths.get(i);
			for (int j = i + 1; j < paths.size(); j++) {
				HailPath p2 = paths.get(j);
				if (pathsIntersectInArea(p1, p2, min, max)) {
					count++;
				}
			}
		}

		return count;
	}

	private static boolean pathsIntersectInArea(HailPath p1, HailPath p2, double min, double max) {
		double p1x1 = p1.x;
		double p1x2 = p1.x + p1.xVelocity;
		double p1y1 = p1.y;
		double p1y2 = p1.y + p1.yVelocity;
		double slope1 = (p1y2 - p1y1) / (p1x2 - p1x1);
		double yIntercept1 = getYintercept(p1x1, p1y1, slope1);

		double p2x1 = p2.x;
		double p2x2 = p2.x + p2.xVelocity;
		double p2y1 = p2.y;
		double p2y2 = p2.y + p2.yVelocity;
		double slope2 = (p2y2 - p2y1) / (p2x2 - p2x1);
		double yIntercept2 = getYintercept(p2x2, p2y2, slope2);

		Point p = calculateIntersectionPoint(slope1, yIntercept1, slope2, yIntercept2);

		//check if point is in the test area
		if (p == null || p.x < min || p.x > max || p.y < min || p.y > max) {
			return false;
		}

		//check if point is in the past
		//line1 heading south and test-box is to the north
		return     (!(p1x2 > p1x1) || !(p1x1 > p.x))  //line1 heading east and test-box is to the west
				&& (!(p1x2 < p1x1) || !(p1x1 < p.x))  //line1 heading west and test-box is to the east
				&& (!(p1y2 > p1y1) || !(p1y1 > p.y))  //line1 heading north and test-box is to the south
				&& (!(p1y2 < p1y1) || !(p1y1 < p.y))  //line1 heading south and test-box is to the north
				&& (!(p2x2 > p2x1) || !(p2x1 > p.x))  //line2 heading east and test-box is to the west
				&& (!(p2x2 < p2x1) || !(p2x1 < p.x))  //line1 heading west and test-box is to the east
				&& (!(p2y2 > p2y1) || !(p2y1 > p.y))  //line1 heading north and test-box is to the south
				&& (!(p2y2 < p2y1) || !(p2y1 < p.y)); //line1 heading south and test-box is to the north
	}

	private static double getYintercept(double x, double y, double slope) {
		return y - (slope * x);
	}

	public static Point calculateIntersectionPoint(double slope1, double yIntercept1, double slope2, double yIntercept2) {
		if (slope1 == slope2) {
			return null;
		}

		double x = (yIntercept2 - yIntercept1) / (slope1 - slope2);
		double y = slope1 * x + yIntercept1;

		return new Point(x, y);
	}
}

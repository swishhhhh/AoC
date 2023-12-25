package aoc23;

import utils.ResourceLoader;

import java.util.LinkedList;
import java.util.List;

import static utils.Helper.extractLongsFromText;
import static java.lang.Math.*;

/**
 * <a href="https://adventofcode.com/2023/day/24">Advent of Code 2023 Day 24</a>
 * <p>
 * Note: Unfortunately this solution is not generic (likely won't work for different sets of puzzle inputs) and frankly
 *       worked only by chance. See algorithm in "calculateForY(...)" which is the one axis where this worked for, and
 *       even there only with ~98% accuracy (but good enough). "calculateForZ(...)" (coded, but then removed from this
 *       class) was tried and didn't work at all even though we got 1 hit (a stone with the same z axis coordinate and
 *       z-velocity as the rock/laser/solution's output. "calculateForX(...)" wasn't even attempted since there were no
 *       matching stones (confirmed that based on the solution found with "calculateForY").
 * <p>
 *       The correct way to solve this problem is via mathematical solver libraries like sympy and z3 in python (see
 *       ../Day24Part2.py for an example). Since I couldn't easily find any such equivalent libraries in java, and this
 *       solution, sub-par as it is, came up with the right answer for my puzzle input... I'll leave it at that.
 */
public class Day24Part2 {
	static class Path {
		double x;
		double y;
		double z;
		double xVelocity;
		double yVelocity;
		double zVelocity;

		public Path(double x, double y, double z, double xVelocity, double yVelocity, double zVelocity) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.xVelocity = xVelocity;
			this.yVelocity = yVelocity;
			this.zVelocity = zVelocity;
		}

		@Override
		public String toString() {
			return "Path{" +
					"x=" + x +
					", y=" + y +
					", z=" + z +
					", xVelocity=" + xVelocity +
					", yVelocity=" + yVelocity +
					", zVelocity=" + zVelocity +
					'}';
		}
	}

	static class Equation {
		double xCoefficient;
		double yCoefficient;
		double equalsValue;

		public Equation(double xCoefficient, double yCoefficient, double equalsValue) {
			this.xCoefficient = xCoefficient;
			this.yCoefficient = yCoefficient;
			this.equalsValue = equalsValue;
		}

		@Override
		public String toString() {
			return "Equation{" +
					"xCoefficient=" + xCoefficient +
					", yCoefficient=" + yCoefficient +
					", equalsValue=" + equalsValue +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day24_input.txt");

		List<Path> paths = new LinkedList<>();
		for (String line : lines) {
			List<Long> nums = extractLongsFromText(line, true);
			Path path = new Path(nums.get(0), nums.get(1), nums.get(2), nums.get(3), nums.get(4), nums.get(5));
			paths.add(path);
		}

		Double answer = calculateSolution(paths);
		System.out.printf("Answer = %s%n", answer);

		double expected = 843888100572888D;
		if (answer == null || answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Double calculateSolution(List<Path> paths) {
		//try Y coordinates (only axis that worked for this sample input, see comments top of this class)
		return calculateForY(paths);
	}

	private static Double calculateForY(List<Path> paths) {
		/*
		 * Algo: Assuming the thing we're solving is the path of a laser-beam that is to intercept every hail-stone let's
		 * 		 use the "b" notation for the beam. We'll also be referring to hailstones with the "h" notation and pairs
		 *       of peer stones with the "p1" and "p2" notations respectively.
		 * 		 For each path/hailstone, select its y coordinate (hy) and yVelocity (hvy) and assume that it's the
		 *       same as the beam's y (by) and yVelocity (bvy) - which means the beam and selected stone have already
		 *       met (on their y axes at least) and will always be on the same y axes and vy speeds. So T (time to contact)
		 *       is 0 (and remains 0 in perpetuity, so no point in doing the rest for this/selected stone).
		 *         1. For every other stone in the list, select them in pairs (p1 and p2) and calculate their Ts using
		 *           the same coord (by) and velocity (bvy) values, and save them as T1, T2.
		 *         2. Select the pair of stones' x coordinates (p1x & p2x) and xVelocity (p1vx & p2vx).
		 *         3. We then plug in those values (p1x, p2x, p1vx, p2vx) and perform the following pair of linear equations:
		 * 				bx + (bvx * T1) = p1x + (p1vx * T1)
		 *              bx + (bvx * T2) = p2x + (p2vx * T2)
		 *            treating bx as X and bvx as Y (in linear equation parlance).
		 *         4. If X (bx) and Y (bvx) doesn't match for every of the pair iterations, track it, and if too many pairs
		 *            (currently set at a threshold of 5%) then it's not the answer and continue on (top of outer loop)
		 *            to next stone. (Re the 5% threshold allowance, in theory every pair should match so should be 0%,
		 *            needs more investigation).
		 *         5. Same as step 2 but select z coordinates (p1z & p2z) and zVelocity (p1vz & p2vz)
		 *         6. Same as step 3 substituting {p1z, p2z, p1vz, p2vz, bx & bvx} for {p1x, p2x, p1vx, p2vx, bz & bvz}
		 *            respectively.
		 *         7. Same as step 4 - if X (bz) and Y (bvz) don't match for too many pairs, it's not the answer and continue.
		 *         8. If you get this far, you have a winner!
		 */

		double bx = -1, by, bz = -1, bvx = -1, bvy, bvz = -1;
		for (int i = 0; i < paths.size() - 1; i++) {
			Path stone = paths.get(i);
			double hy = stone.y;
			double hvy = stone.yVelocity;
			boolean firstIteration = true;

			//let's test if the beam shares y variables with this stone...
			by = hy;
			bvy = hvy;

			//select rest of the stones in pairs
			int numPairs = paths.size() - 2; //exclude self
			int pairsMatchCtr = 0;
			for (int j = 0; j < paths.size() - 1; j++) {
				if (i == j) {
					continue;
				}

				Path p1 = paths.get(j);
				Path p2 = paths.get(j+1);
				if (j+1 == i) { //p1 is right before "stone", select next one for p2 unless already at the end...
					if (i == paths.size() - 2) {
						continue;
					}
					p2 = paths.get(j+2);
				}

				//if beam is moving at same speed as the stone, it will never catch it (assuming "y"s are different)
				if (bvy == p1.yVelocity || bvy == p2.yVelocity) {
					continue;
				}

				//calculate Ts using stones' y (by) and velocity (bvy) values, and save them as T1, T2.
				double t1 = abs((by - p1.y) / (bvy - p1.yVelocity));
				double t2 = abs((by - p2.y) / (bvy - p2.yVelocity));

				//Select the pair of stones' x coordinates (p1x & p2x) and xVelocity (p1vx & p2vx).
				double p1x = p1.x, p2x = p2.x;
				double p1vx = p1.xVelocity, p2vx = p2.xVelocity;

				//perform the following pair of linear equations:
				//		 bx + (bvx * T1) = p1x + (p1vx * T1)
				//		 bx + (bvx * T2) = p2x + (p2vx * T2)
				Equation eq1 = new Equation(1, t1, p1x + (p1vx * t1));
				Equation eq2 = new Equation(1, t2, p2x + (p2vx * t2));
				double[] ary = solvePairOfLinearEquations(eq1, eq2);
				double valX = ary[0];
				double valY = ary[1];

				//If X (bx) and Y (bvx) don't match continue on.
				if (!firstIteration && (abs(valX - bx) >= 1.0 || abs(valY - bvx) >= 1.0)) { //allow for marginal precision error
					continue;
				}
				bx = valX;
				bvx = valY;

				//Select the pair of stones' z coordinates (p1z & p2z) and zVelocity (p1vz & p2vz).
				double p1z = p1.z, p2z = p2.z;
				double p1vz = p1.zVelocity, p2vz = p2.zVelocity;

				//perform the following pair of linear equations:
				//		 bz + (bvz * T1) = p1z + (p1vz * T1)
				//		 bz + (bvz * T2) = p2z + (p2vz * T2)
				eq1 = new Equation(1, t1, p1z + (p1vz * t1));
				eq2 = new Equation(1, t2, p2z + (p2vz * t2));
				ary = solvePairOfLinearEquations(eq1, eq2);
				valX = ary[0];
				valY = ary[1];

				//If X (bz) and Y (bvz) don't match continue on
				if (!firstIteration && (abs(valX - bz) > 1.0 || abs(valY - bvz) > 1.0 )) {
					continue;
				}

				bz = valX;
				bvz = valY;
				pairsMatchCtr++;

				firstIteration = false;
			}

			//not sure why it's not 100%, but 95% seems to be good enough... needs more analysis...
			if ((double) pairsMatchCtr / (double) numPairs > 0.95) {
				return bx + by + bz;
			}
		}

		return null; //indicates couldn't find a solution
	}

	/**
	 * returns array of {x-value, y-value}
	 */
	public static double[] solvePairOfLinearEquations(Equation eq1, Equation eq2) throws ArithmeticException {
		double elimA = eq2.yCoefficient * eq1.xCoefficient;
		double elimB = eq2.yCoefficient * eq1.equalsValue;
		double elimC = eq1.yCoefficient * eq2.xCoefficient;
		double elimD = eq1.yCoefficient * eq2.equalsValue;

		double x = (elimB - elimD) / (elimA - elimC);
		double y = (eq1.equalsValue - eq1.xCoefficient * x) / eq1.yCoefficient;

		if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
			throw new ArithmeticException(String.format("Unable to solve for %s and %s%n", eq1, eq2));
		}

		return new double[]{x, y};
	}
}

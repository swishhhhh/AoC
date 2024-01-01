package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 *  <a href="https://adventofcode.com/2022/day/18">Advent of Code 2022 Day 18</a>
 */
public class Day18Part1 {

	private static final int GRID_SIZE = 22;
	private static final boolean[][][] grid = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day18_input.txt");

		//fill grid
		for (String line: lines) {
			List<Integer> nums = Helper.extractIntsFromText(line);
			int x, y, z;
			x = nums.get(0);
			y = nums.get(1);
			z = nums.get(2);
			grid[x][y][z] = true;
		}

		int ctr = 0;
		for (int x = 0; x < GRID_SIZE; x++) {
			for (int y = 0; y < GRID_SIZE; y++) {
				for (int z = 0; z < GRID_SIZE; z++) {
					if (grid[x][y][z]) { //make sure current cell is occupied
						ctr+= getExposedSurfaces(x, y, z);
					}
				}
			}
		}

		long answer = ctr;
		System.out.printf("Total = %s%n", ctr);

		long expected = 4244;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static int getExposedSurfaces(int x, int y, int z) {
		return 6 - getNumberOfNeighbors(x, y, z);
	}

	private static int getNumberOfNeighbors(int x, int y, int z) {
		int ctr = 0;

		//6 directions: N, S, E, W, F(ront), B(ack)
		//N
		if (y > 0 && grid[x][y-1][z]) ctr++;

		//S
		if (y < GRID_SIZE - 1 && grid[x][y+1][z]) ctr++;

		//E
		if (x < GRID_SIZE - 1 && grid[x+1][y][z]) ctr++;

		//W
		if (x > 0 && grid[x-1][y][z]) ctr++;

		//F
		if (z > 0 && grid[x][y][z-1]) ctr++;

		//B
		if (z < GRID_SIZE - 1 && grid[x][y][z+1]) ctr++;

		return ctr;
	}
}

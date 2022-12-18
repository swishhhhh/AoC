package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

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

		System.out.printf("Total = %s%n", ctr);
	}

	private static int getExposedSurfaces(int x, int y, int z) {
		return 6 - getNumberOfNeighbors(x, y, z);
	}

	private static int getNumberOfNeighbors(int x, int y, int z) {
		int ctr = 0;

		//6 directions: N, S, E, W, F(ront), B(ack)
		//W
		if (x > 0 && grid[x-1][y][z]) ctr++;

		//E
		if (x < GRID_SIZE - 1 && grid[x+1][y][z]) ctr++;

		//N
		if (y > 0 && grid[x][y-1][z]) ctr++;

		//S
		if (y < GRID_SIZE - 1 && grid[x][y+1][z]) ctr++;

		//F
		if (z > 0 && grid[x][y][z-1]) ctr++;

		//B
		if (z < GRID_SIZE - 1 && grid[x][y][z+1]) ctr++;

		return ctr;
	}
}

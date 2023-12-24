package aoc23;

import aoc23.datastructs.Coordinates3D;
import utils.ResourceLoader;

import java.util.*;

import static java.lang.Math.max;
import static utils.Helper.extractIntsFromText;

/**
 *  <a href="https://adventofcode.com/2023/day/22">Advent of Code 2023 Day 22</a>
 */
public class Day22Part2 {

	static class Brick {
		String id;
		Coordinates3D[] cubes; //cubes[0] should always be at the origin (x, y & z all == 0)
		String axisAlignment; //x, y or z aligned

		public Brick(String id, Coordinates3D[] cubes, String axisAlignment) {
			this.id = id;
			this.cubes = cubes;
			this.axisAlignment = axisAlignment;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Brick brick)) return false;

			if (!Objects.equals(id, brick.id)) return false;
			return Arrays.equals(cubes, brick.cubes);
		}

		@Override
		public int hashCode() {
			int result = id != null ? id.hashCode() : 0;
			result = 31 * result + Arrays.hashCode(cubes);
			return result;
		}

		@Override
		public String toString() {
			return "Brick{" +
					"id='" + id + '\'' +
					"axisAlignment='" + axisAlignment + '\'' +
					", cubes=" + Arrays.toString(cubes) +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day22_input.txt");

		//pass 1 figure out max coordinate of each axis
		int maxX = 0, maxY = 0, maxZ = 0;
		for (String line: lines) {
			List<Integer> nums = extractIntsFromText(line);
			maxX = max(maxX, max(nums.get(0), nums.get(3)));
			maxY = max(maxY, max(nums.get(1), nums.get(4)));
			maxZ = max(maxZ, max(nums.get(2), nums.get(5)));
		}

		//each cell in the grid is a String and either null (indicating unoccupied) or with a value
		// with the format of "BrXXXXCuY" where XXXX is the brick id and Y the cube number in the brick
		// e.g. Br0005Cu0 indicates the cell is occupied by the first cube of a brick with id Br0005
		String[][][] grid = new String[maxX + 1][maxY + 1][maxZ + 1];
		Map<String, Brick> bricksMap = new HashMap<>();
		Map<String, Coordinates3D> brickLocationsMap = new HashMap<>(); //brickId ->

		//create bricks and fill grid
		for (int i = 0; i < lines.size(); i++) {
			String brickId = "Br" + String.format("%04d", i);
			Brick brick = createBrickAndPlaceOnGrid(lines.get(i), brickId, grid, brickLocationsMap);
			bricksMap.put(brickId, brick);
		}

		settleBricks(grid, bricksMap, brickLocationsMap);
		long count = countBricks(grid, bricksMap, brickLocationsMap);
//		long count = countBricksSafeToRemove(grid, bricksMap, brickLocationsMap);

		System.out.printf("Count = %s%n", count);

		long expected = 68525;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long countBricks(String[][][] grid, Map<String, Brick> bricksMap,
									Map<String, Coordinates3D> brickLocationsMap) {
		long bricksMovedCnt = 0;
		for (Brick brick : bricksMap.values()) {
			String[][][] clonedGrid = cloneGrid(grid);
			Map<String, Coordinates3D> clonedBricksLocationsMap = new HashMap<>(brickLocationsMap);
			removeBrick(brick, clonedGrid, clonedBricksLocationsMap);
			settleBricks(clonedGrid, bricksMap, clonedBricksLocationsMap);

			for (String brickId : brickLocationsMap.keySet()) {
				if (brickId.equals(brick.id)) {
					continue; //don't count the removed brick itself, only the other bricks...
				}

				if (!brickLocationsMap.get(brickId).equals(clonedBricksLocationsMap.get(brickId))) {
					bricksMovedCnt++;
				}
			}
		}

		return bricksMovedCnt;
	}

	private static String[][][] cloneGrid(String[][][] srcGrid) {
		String[][][] targetGrid = new String[srcGrid.length][srcGrid[0].length][srcGrid[0][0].length];
		for (int i = 0; i < srcGrid.length; i++) {
			for (int j = 0; j < srcGrid[i].length; j++) {
				System.arraycopy(srcGrid[i][j], 0, targetGrid[i][j], 0, targetGrid[i][j].length);
			}
		}
		return targetGrid;
	}

	private static Brick createBrickAndPlaceOnGrid(String line, String brickId, String[][][] grid,
												   Map<String, Coordinates3D> brickLocationsMap) {
		List<Integer> nums = extractIntsFromText(line);
		int x1 = nums.get(0);
		int y1 = nums.get(1);
		int z1 = nums.get(2);
		int x2 = nums.get(3);
		int y2 = nums.get(4);
		int z2 = nums.get(5);

		String alignment;
		int brickLength;
		//only 1 alignment can be true (i.e. brick is never diagonal)
		if (x1 != x2) {
			alignment = "x-axis";
			brickLength = x2 - x1 + 1;
		} else if (y1 != y2) {
			alignment = "y-axis";
			brickLength = y2 - y1 + 1;
		} else if (z1 != z2) {
			alignment = "z-axis";
			brickLength = z2 - z1 + 1;
		} else {
			alignment = "single-cube";
			brickLength = 1;
		}

		//create the cubes that will make up the brick
		Coordinates3D[] cubes = new Coordinates3D[brickLength];
		int cubeN = -1;
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					cubeN++;
					cubes[cubeN] = new Coordinates3D(x - x1, y - y1, z - z1);
				}
			}
		}
		Brick brick = new Brick(brickId, cubes, alignment);

		//place brick at its starting place in the grid
		Coordinates3D targetLocation = new Coordinates3D(x1, y1, z1);
		moveBrick(brick, targetLocation, grid, brickLocationsMap);

		return brick;
	}

	private static void settleBricks(String[][][] grid, Map<String, Brick> bricksMap,
									 Map<String, Coordinates3D> brickLocationsMap) {
		//sort the brick locations on their z1 coords (lowest to highest) so that you process the bricks bottom up
		List<Coordinates3D> locations = brickLocationsMap
				.values()
				.stream()
				.sorted(Comparator.comparingInt(Coordinates3D::z))
				.toList();

		//for each brick, check if it can move down and if so move as far down as it can go
		for (Coordinates3D c : locations) {
			String cellValue = grid[c.x()][c.y()][c.z()];
			String brickId = cellValue.substring(0, 6);
			Brick brick = bricksMap.get(brickId);
			dropBrick(brick, grid, brickLocationsMap);
		}
	}

	private static void dropBrick(Brick brick, String[][][] grid, Map<String, Coordinates3D> brickLocationsMap) {
		int movesDownCtr = countAvailableMovesDown(brick, grid, brickLocationsMap);
		if (movesDownCtr <  1) {
			return;
		}

		Coordinates3D firstCube = brickLocationsMap.get(brick.id);
		Coordinates3D targetLocation = new Coordinates3D(firstCube.x(), firstCube.y(), firstCube.z() - movesDownCtr);
		moveBrick(brick, targetLocation, grid, brickLocationsMap);
	}

	private static int countAvailableMovesDown(Brick brick, String[][][] grid, Map<String, Coordinates3D> brickLocationsMap) {
		//get brick's bottom profile (all cubes with z == 0)
		List<Coordinates3D> zCubes =
				Arrays.stream(brick.cubes).filter(c -> c.z() == 0).toList();

		Coordinates3D bottomCube = brickLocationsMap.get(brick.id);
		List<Coordinates3D> cubesToCheck = new ArrayList<>();
		for (Coordinates3D cu : zCubes) {
			//adjust their coords to their grid location coords
			cubesToCheck.add(new Coordinates3D(cu.x() + bottomCube.x(), cu.y() + bottomCube.y(), cu.z() + bottomCube.z()));
		}

		int movesDownCtr = 0;
		while (true) {
			boolean canMoveDownAnotherLayer = true;
			for (Coordinates3D c : cubesToCheck) {
				int targetZ = c.z() - (1 + movesDownCtr);
				if (targetZ < 1) { //can't go lower than z=1
					canMoveDownAnotherLayer = false;
					break;
				}

				String cellValue = grid[c.x()][c.y()][targetZ];
				if (cellValue != null) {
					canMoveDownAnotherLayer = false;
					break;
				}
			}
			if (canMoveDownAnotherLayer) {
				movesDownCtr++;
			} else {
				break;
			}
		}

		return movesDownCtr;
	}

	private static void moveBrick(Brick brick, Coordinates3D targetLoc, String[][][] grid,
								  Map<String, Coordinates3D> brickLocationsMap) {
		//check if brick is already on the grid and if so remove it
		removeBrick(brick, grid, brickLocationsMap);

		//place brick in new target location in the grid
		for (int i = 0; i < brick.cubes.length; i++) {
			Coordinates3D c = brick.cubes[i];
			Coordinates3D gridLoc = new Coordinates3D(targetLoc.x() + c.x(), targetLoc.y() + c.y(), targetLoc.z() + c.z());
			grid[gridLoc.x()][gridLoc.y()][gridLoc.z()] = brick.id + "Cu" + i;

			if (i == 0) {
				brickLocationsMap.put(brick.id, gridLoc);
			}
		}
	}

	private static void removeBrick(Brick brick, String[][][] grid, Map<String, Coordinates3D> brickLocationsMap) {
		Coordinates3D curLoc = brickLocationsMap.get(brick.id);
		if (curLoc != null) {
			for (Coordinates3D c : brick.cubes) {
				grid[curLoc.x() + c.x()][curLoc.y() + c.y()][curLoc.z() + c.z()] = null;
				brickLocationsMap.remove(brick.id);
			}
		}
	}
}

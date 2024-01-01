package aoc22;

import aoc22.datastructs.Coordinates3D;
import utils.Helper;
import utils.ResourceLoader;

import java.util.LinkedList;
import java.util.List;

/**
 *  <a href="https://adventofcode.com/2022/day/18">Advent of Code 2022 Day 18</a>
 */
public class Day18Part2 {

	private static final int GRID_SIZE = 22;
	private static boolean[][][] mainGrid;
	private static boolean[][][] reachableGrid;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day18_input.txt");

		//fill main grid
		mainGrid = newGrid();
		for (String line: lines) {
			List<Integer> nums = Helper.extractIntsFromText(line);
			int x, y, z;
			x = nums.get(0);
			y = nums.get(1);
			z = nums.get(2);
			mainGrid[x][y][z] = true;
		}

		reachableGrid = newGrid();

		int ctr = 0;
		for (int x = 0; x < GRID_SIZE; x++) {
			for (int y = 0; y < GRID_SIZE; y++) {
				for (int z = 0; z < GRID_SIZE; z++) {
					if (mainGrid[x][y][z]) { //make sure current cell is occupied
						ctr+= getReachableSurfaces(x, y, z);
					}
				}
			}
		}

		long answer = ctr;
		System.out.printf("Total = %s%n", ctr);

		long expected = 2460;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static int getReachableSurfaces(int x, int y, int z) {
		int ctr = 0;

		//N
		if (isCellReachableFromOutside(x, y-1, z)) ctr++;

		//S
		if (isCellReachableFromOutside(x, y+1, z)) ctr++;

		//E
		if (isCellReachableFromOutside(x+1, y, z)) ctr++;

		//W
		if (isCellReachableFromOutside(x-1, y, z)) ctr++;

		//F(ront)
		if (isCellReachableFromOutside(x, y, z-1)) ctr++;

		//B(ack)
		if (isCellReachableFromOutside(x, y, z+1)) ctr++;

		return ctr;
	}

	/*
	 * BFS search
	 */
	private static boolean isCellReachableFromOutside(int x, int y, int z) {
		//terminating condition 1: if starting cell is already out of bounds
		if (isCellOutOfBounds(new Coordinates3D(x, y, z))) {
			return true;
		}

		LinkedList<Coordinates3D> queue = new LinkedList<>();
		boolean[][][] visitedGrid = newGrid();
		visitedGrid[x][y][z] = true;
		queue.add(new Coordinates3D(x, y, z));

		boolean reachable = false;
		while (queue.size() != 0) {
			Coordinates3D cell = queue.poll();

			//terminating condition 2: check if cell is outside already, if so reachable=true, break
			if (isCellOutOfBounds(cell)) {
				reachable = true;
				break;
			}

			//if cell is a block (mainGrid[x][y][z]==true), continue
			if (mainGrid[cell.getX()][cell.getY()][cell.getZ()]) {
				continue;
			}

			//term condition 3: if reachableGrid[x][y][z]==true, break true
			if (reachableGrid[cell.getX()][cell.getY()][cell.getZ()]) {
				reachable = true;
				break;
			}

			//get all 6 neighbors, for each neighbor, if !visited, visited=true and enqueue:
			List<Coordinates3D> neighbors = getNeighborsIncludingOutOfBounds(cell);
			for (Coordinates3D neighbor: neighbors) {
				//out of bound neighbors should be enqueued but not added to visited grid (already have term condition 1 above)
				if (isCellOutOfBounds(neighbor)) {
					queue.add(neighbor);

				} else if (!visitedGrid[neighbor.getX()][neighbor.getY()][neighbor.getZ()]) {
					visitedGrid[neighbor.getX()][neighbor.getY()][neighbor.getZ()] = true;
					queue.add(neighbor);
				}
			}
		}

		//if cell is reachable, then all visited empty cells are by definition reachable as well
		if (reachable) {
			for (int x2 = 0; x2 < GRID_SIZE; x2++) {
				for (int y2 = 0; y2 < GRID_SIZE; y2++) {
					for (int z2 = 0; z2 < GRID_SIZE; z2++) {
						if (visitedGrid[x2][y2][z2]) {
							reachableGrid[x2][y2][z2] = true;
						}
					}
				}
			}

		}

		return reachable;
	}

	private static List<Coordinates3D> getNeighborsIncludingOutOfBounds(Coordinates3D cell) {
		int x = cell.getX();
		int y = cell.getY();
		int z = cell.getZ();

		return List.of(
				new Coordinates3D(x-1, y, z),
				new Coordinates3D(x+1, y, z),
				new Coordinates3D(x, y-1, z),
				new Coordinates3D(x, y+1, z),
				new Coordinates3D(x, y, z-1),
				new Coordinates3D(x, y, z+1));
	}

	private static boolean isCellOutOfBounds(Coordinates3D cell) {
		return cell.getX() < 0 || cell.getX() >= GRID_SIZE ||
				cell.getY() < 0 || cell.getY() >= GRID_SIZE ||
				cell.getZ() < 0 || cell.getZ() >= GRID_SIZE;
	}

	private static boolean[][][] newGrid() {
		return new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
	}
}

package aoc22;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.Helper;
import utils.ResourceLoader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static datastructs.Direction.*;
import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2022/day/23">Advent of Code 2022 Day 23</a>
 */
public class Day23Part2 {

	private static char[][] grid;
	private static final int PAD_MARGIN = 55;
	static LinkedList<List<Direction>> dirQ = new LinkedList<>();
	static Map<Integer, Coordinates> elfLocations = new HashMap<>();

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day23_input.txt");

		//setup grid and elf locations
		grid = new char[lines.size() + (2*PAD_MARGIN)][lines.get(0).length() + (2*PAD_MARGIN)];
		Helper.fillCharArray2D(grid, '.');

		int elfCtr = 0;
		int row = -1;
		for (String line: lines) {
			row++;
			char[] charAry = line.toCharArray();
			for (int col = 0; col < charAry.length; col++) {
				grid[row + PAD_MARGIN][col + PAD_MARGIN] = charAry[col];

				if (charAry[col] == '#') {
					elfCtr++;
					elfLocations.put(elfCtr, new Coordinates(col+PAD_MARGIN, row+PAD_MARGIN));

					//to identify your elves (not useful for too many elves or you get non-readable chars...
					// but works great for initial example of 22 elves)
//					grid[row + PAD_MARGIN][col + PAD_MARGIN] = (char) ('A' + (char)(elfCtr - 1));
				}
			}
		}

		if (DEBUG) {
			printGrid(grid);
		}

		//setup directions Q
		dirQ.add(List.of(NORTH, NE, NW));
		dirQ.add(List.of(SOUTH, SE, SW));
		dirQ.add(List.of(WEST, NW, SW));
		dirQ.add(List.of(EAST, NE, SE));

		//loop
		int round = 0;
		for (; round < 10_000; round++) {
			//first half of round: proposals
			int numMoves = 0;
			Map<Coordinates, Integer> moveProposalsByLocationCnt = new HashMap<>();
			Map<Integer, Coordinates> moveProposalsByElf = new HashMap<>();
			for (int elf: elfLocations.keySet()) {
				if (!hasAnyNeighbors(elf)) {
					continue;
				}

				//find suitable side
				List<Direction> suitableSide = findSuitableSide(elf, dirQ);
				if (suitableSide == null) {
					continue;
				}

				Direction proposedSide = suitableSide.get(0); //first of the 3 is the direction to actually move to
				Coordinates proposedMove = getLocation(elfLocations.get(elf), proposedSide);
				Integer cnt = moveProposalsByLocationCnt.get(proposedMove);
				if (cnt == null) {
					cnt = 0;
				}
				moveProposalsByLocationCnt.put(proposedMove, ++cnt);
				moveProposalsByElf.put(elf, proposedMove);
			}

			//2nd half of round: evaluate proposals and move if no collision..
			for (int elf: elfLocations.keySet()) {
				Coordinates target = moveProposalsByElf.get(elf);
				if (target == null) {
					continue;
				}

				//check if there's a proposals collision
				Integer cnt = moveProposalsByLocationCnt.get(target);
				if (cnt != null && cnt > 1) {
					continue;
				}

				//actual move
				Coordinates src = elfLocations.get(elf);
				elfLocations.put(elf, target);
				char elfSymbol = grid[src.y()][src.x()];
				grid[src.y()][src.x()] = '.';
				grid[target.y()][target.x()] = elfSymbol;
				numMoves++;
			}

			//rotate front of queue to back..
			dirQ.add(dirQ.remove(0));

			if (DEBUG) {
				System.out.printf("End of round %s, number of elves moved = %s%n", round + 1, numMoves);
			}

			if (numMoves == 0) {
				if (DEBUG) {
					System.out.println("No elves moved this round, ending early");
				}
				break;
			}
		}

		//find leftmost, rightmost, topmost and bottommost elves to calculate the dimensions of the final grid
		int left = Integer.MAX_VALUE, right = 0, top = Integer.MAX_VALUE, bottom = 0;
		for (row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				if (grid[row][col] != '.') {
					left = Math.min(left, col);
					right = Math.max(right, col);
					top = Math.min(top, row);
					bottom = Math.max(bottom, row);
				}
			}
		}

		if (DEBUG) {
			printGrid(grid);
		}

		int gridArea = ((right - left) + 1) * ((bottom - top) + 1);
		long emptyTiles = gridArea - elfLocations.size();

		long answer = round + 1;

		if (DEBUG) {
			System.out.printf("Right=%s, Left=%s, Top=%s, Bottom=%s%n", right, left, top, bottom);
		}
		System.out.printf("Grid Area = %s, Elves count = %s, Empty Tiles = %s, Number of Rounds = %s%n",
				gridArea, elfLocations.size(), emptyTiles, answer);

		long expected = 1116;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}

	private static Coordinates getLocation(Coordinates coordinates, Direction proposedDirection) {
		int x = coordinates.x(), y = coordinates.y();
		switch (proposedDirection) {
			case NORTH -> y--;
			case EAST -> x++;
			case SOUTH -> y++;
			case WEST -> x--;
			case NE -> {y--; x++;}
			case NW -> {y--; x--;}
			case SE -> {y++; x++;}
			case SW -> {y++; x--;}
		}

		return new Coordinates(x, y);
	}

	private static List<Direction> findSuitableSide(int elf, LinkedList<List<Direction>> dirQ) {
		List<Direction> suitableSide = null;
		for (List<Direction> sideToCheck: dirQ) {
			if (!hasAnyNeighbors(elf, sideToCheck)) {
				suitableSide = sideToCheck;
				break;
			}
		}

		return suitableSide;
	}

	private static boolean hasAnyNeighbors(int elf) {
		return hasAnyNeighbors(elf, List.of(Direction.values())); //check all directions
	}

	private static boolean hasAnyNeighbors(int elf, List<Direction> directionsToCheck) {
		Coordinates elfLocation = elfLocations.get(elf);
		for (Direction dir : directionsToCheck) {
			Coordinates neighborCell = getLocation(elfLocation, dir);
			if (neighborCell.x() < 0 || neighborCell.y() < 0
					|| neighborCell.x() >= grid[0].length
					|| neighborCell.y() >= grid.length) {
				continue;
			}
			if (grid[neighborCell.y()][neighborCell.x()] != '.') {
				return true;
			}
		}
		return false;
	}
}

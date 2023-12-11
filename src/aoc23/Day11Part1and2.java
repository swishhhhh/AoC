package aoc23;

import aoc23.datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/11">Advent of Code 2023 Day 11</a>
 */
public class Day11Part1and2 {
	private static final int EXPANSION =
//			1;			//part 1
			999_999;	//part 2

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day11_input.txt");

		boolean[] rowsWithGalaxies = new boolean[lines.size()];
		boolean[] colsWithGalaxies = new boolean[lines.get(0).length()];
		List<Coordinates> originalGalaxyCoords = new ArrayList<>();

		char[][] grid = new char[lines.size()][lines.get(0).length()];
		for (int row = 0; row < grid.length; row++) {
			grid[row] = lines.get(row).toCharArray();
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col] == '#') {
					rowsWithGalaxies[row] = true;
					colsWithGalaxies[col] = true;
					originalGalaxyCoords.add(new Coordinates(col, row));
				}
			}
		}

		//for each missing row increment adjustment by 1 for all subsequent rows
		int[] rowAdjustments = new int[rowsWithGalaxies.length];
		for (int row = 0; row < rowsWithGalaxies.length; row++) {
			if (!rowsWithGalaxies[row]) {
				//add 1 to each subsequent row
				for (int i = row + 1; i < rowAdjustments.length; i++) {
					rowAdjustments[i]+= EXPANSION;
				}
			}
		}

		//for each missing col increment adjustment by 1 for all subsequent cols
		int[] colAdjustments = new int[colsWithGalaxies.length];
		for (int col = 0; col < colsWithGalaxies.length; col++) {
			if (!colsWithGalaxies[col]) {
				//add 1M to each subsequent col
				for (int i = col + 1; i < colAdjustments.length; i++) {
					colAdjustments[i]+= EXPANSION;
				}
			}
		}

		//convert/clone galaxies to their new/expanded coordinates
		List<Coordinates> expandedGalaxyCoords = new ArrayList<>();
		for (Coordinates galaxy : originalGalaxyCoords) {
			Coordinates expandedGalaxy =
					new Coordinates(galaxy.x() + colAdjustments[galaxy.x()], galaxy.y() + rowAdjustments[galaxy.y()]);
			expandedGalaxyCoords.add(expandedGalaxy);
		}

		long sum = 0;
		//permeate through each pair of galaxies and calculate distances between each pair
		for (int i = 0; i < expandedGalaxyCoords.size() - 1; i++) {
			Coordinates g1 = expandedGalaxyCoords.get(i);
			for (int j = i + 1; j < expandedGalaxyCoords.size(); j++) {
				Coordinates g2 = expandedGalaxyCoords.get(j);
				long distance = Math.abs(g1.x() - g2.x()) + Math.abs(g1.y() - g2.y());
				sum+= distance;
			}
		}

		System.out.printf("Sum = %s%n", sum);
	}
}

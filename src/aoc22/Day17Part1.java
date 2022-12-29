package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.TetrisShape;
import utils.ResourceLoader;

import java.util.List;

import static aoc22.datastructs.TetrisShape.*;

/**
 *  <a href="https://adventofcode.com/2022/day/17">Advent of Code 2022 Day 17</a>
 */
public class Day17Part1 {
	private static final TetrisShape[] shapes =
			new TetrisShape[]{newHorizontalLine(), newPlus(), newL(), newVerticalLine(), newSquare()};
	private static final int numGridCols = 7;
	private static final int numGridRows = 8000; //to be optimized in part2

	public static void main(String[] args) throws Exception {

		List<String> lines = ResourceLoader.readStrings("aoc22/Day17_input.txt");
		char[] horizontalShiftsAry = lines.get(0).toCharArray();
		int shiftIndex = -1;
		int shapeIndex = -1;

		//fill grid
		char[][] grid = new char[numGridRows][numGridCols];
		for (int i = 0; i < numGridRows; i++) {
			for (int j = 0; j < numGridCols; j++) {
				grid[i][j] = '.';
			}
		}

		int rockCtr = 0;
		int topOfTower = -1; //floor = -1 (first row is 0)
		Coordinates rockCoordinate; //indicates the location of the rock's bottom right on the grid

		while (rockCtr < 2022) {
			//next rock
			shapeIndex++;
			if (shapeIndex >= shapes.length) {
				shapeIndex = 0;
			}
			TetrisShape rock = shapes[shapeIndex].clone();
			rockCtr++;
			rockCoordinate = new Coordinates(2, topOfTower + 4);

			//-----
			boolean canMoveDown = true;
			while (canMoveDown) {
				//next shift
				shiftIndex++;
				if (shiftIndex >= horizontalShiftsAry.length) {
					shiftIndex = 0;
				}
				int horizontalMovement = horizontalShiftsAry[shiftIndex] == '<' ? -1 : 1;

				//apply horizontal shift, THEN vertical/down-movement

				//attempt to move left
				if (horizontalMovement == -1) {
					if (rockCoordinate.x() > 0) { //make sure not already up against left wall
						int[] leftProfile = rock.getLeftProfile();
						boolean canMoveLeft = true;
						for (int i = 0; i < rock.getHeight(); i++) { //check profile from bottom of rock up
							if (grid[rockCoordinate.y()+i]
									[rockCoordinate.x() + leftProfile[rock.getHeight() - 1 - i] - 1] == '#') {
								canMoveLeft = false;
								break;
							}
						}

						if (canMoveLeft) {
							rockCoordinate = new Coordinates(rockCoordinate.x() - 1, rockCoordinate.y());
						}
					}
				}

				//attempt to move right
				if (horizontalMovement == 1) {
					if (rockCoordinate.x() + rock.getWidth() < numGridCols) { //make sure not already up against right wall
						int[] rightProfile = rock.getRightProfile();
						boolean canMoveRight = true;
						for (int i = 0; i < rock.getHeight(); i++) { //check profile from bottom of rock up
							if (grid[rockCoordinate.y()+i]
									[rockCoordinate.x() + rightProfile[rock.getHeight() - 1 - i]] == '#') {
								canMoveRight = false;
								break;
							}
						}

						if (canMoveRight) {
							rockCoordinate = new Coordinates(rockCoordinate.x() + 1, rockCoordinate.y());
						}
					}
				}

				//attempt to move down
				if (rockCoordinate.y() == 0) { //make sure not already on the floor
					canMoveDown = false;
				} else {
					//check bottom profile
					int[] bottomProfile = rock.getBottomProfile();
					for (int i = 0; i < rock.getWidth(); i++) {
						if (grid[rockCoordinate.y() + bottomProfile[i] - 1][rockCoordinate.x()+i] == '#') {
							canMoveDown = false;
							break;
						}
					}
				}

				if (canMoveDown) {
					rockCoordinate = new Coordinates(rockCoordinate.x(), rockCoordinate.y() - 1);
				} else {
					//set rock down -> draw shape
					char[][] bitmap = rock.toBitmap();
					for (int i = 0; i < rock.getHeight(); i++) {
						for (int j = 0; j < rock.getWidth(); j++) {
							char pixel = bitmap[rock.getHeight() - i - 1][j]; //draw shape from bottom up (grid numbers rows that way)
							if (pixel == '#') { //only draw parts of the rock's shape (so as not to possibly erase parts of previous rocks)
								grid[rockCoordinate.y()+i][rockCoordinate.x()+j] = pixel;
							}
						}
					}

					topOfTower = Math.max(topOfTower, rockCoordinate.y() + rock.getHeight() - 1);
				}
			}
		}

		printBottomOfGrid(grid,topOfTower + 3, 30);
		System.out.printf("Tower Height = %s%n", topOfTower + 1);
	}

	static void printBottomOfGrid(char[][] grid, int fromLine, int numOfLines) {
		for (int i = fromLine; i >= Math.max(0, fromLine - numOfLines); i--) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < numGridCols; j++) {
				sb.append(grid[i][j]);
			}
			System.out.printf("%03d:|%s|%n", i, sb);
		}
	}
}

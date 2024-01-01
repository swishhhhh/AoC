package aoc22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.TetrisShape;
import utils.ResourceLoader;

import java.util.List;
import java.util.Objects;

import static aoc22.datastructs.TetrisShape.*;

/**
 *  <a href="https://adventofcode.com/2022/day/17">Advent of Code 2022 Day 17</a>
 */
public class Day17Part2 {
	static class StateSignature {
		int shiftIndex;
		int shapeIndex;
		String topRowsOfGrid;
		long numRocks;
		long topOfTower;

		public StateSignature(int shiftIndex, int shapeIndex, String topRowsOfGrid, long numRocks, long topOfTower) {
			this.shiftIndex = shiftIndex;
			this.shapeIndex = shapeIndex;
			this.topRowsOfGrid = topRowsOfGrid;
			this.numRocks = numRocks;
			this.topOfTower = topOfTower;
		}

		public long getNumRocks() {
			return numRocks;
		}
		public long getTopOfTower() {
			return topOfTower;
		}
		public String getSignature() {
			return "StateSignature{" +
					"shiftIndex=" + shiftIndex +
					", shapeIndex=" + shapeIndex +
					", topRowsOfGrid='" + topRowsOfGrid + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			StateSignature that = (StateSignature) o;

			if (shiftIndex != that.shiftIndex) return false;
			if (shapeIndex != that.shapeIndex) return false;
			return Objects.equals(topRowsOfGrid, that.topRowsOfGrid);
		}

		@Override
		public int hashCode() {
			int result = shiftIndex;
			result = 31 * result + shapeIndex;
			result = 31 * result + (topRowsOfGrid != null ? topRowsOfGrid.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "StateSignature{" +
					"shiftIndex=" + shiftIndex +
					", shapeIndex=" + shapeIndex +
					", numRocks=" + numRocks +
					", topOfTower=" + topOfTower +
					", topRowsOfGrid='" + topRowsOfGrid + '\'' +
					'}';
		}
	}

	private static final TetrisShape[] shapes =
			new TetrisShape[]{newHorizontalLine(), newPlus(), newL(), newVerticalLine(), newSquare()};
	private static final long numOfRocks = 1_000_000_000_000L;
	private static final int minRowsToKeep = 50;
	private static final int numRowsToTriggerGC = 10_000;
	private static final int numGridCols = 7;
	private static final int numGridRows = numRowsToTriggerGC + minRowsToKeep;
	private static final int startSampleAt = 3000;

	private static final boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		List<String> lines = ResourceLoader.readStrings("aoc22/Day17_input.txt");
		char[] horizontalShiftsAry = lines.get(0).toCharArray();
		int shiftIndex = -1;
		int shapeIndex = -1;

		//fill grid
		char[][] grid = newGrid();

		long rockCtr = 0L;
		long cumulativeTopOfTower;
		long cumulativeNumRowsPurged = 0L;
		int topOfTower = -1; //floor = -1 (first row is 0)
		Coordinates rockCoordinate; //indicates the location of the rock's bottom right on the grid

		int maxNumberOfDownMovesForAnyRock = 0;

		StateSignature sampleSignature = null;
		boolean sampleTaken = false;
		boolean sampleMatched = false;


		while (rockCtr < numOfRocks) {

			//check if grid needs garbage collection
			if (topOfTower > numRowsToTriggerGC) {
				printRuntime(startTime);
				//copy top 'minRowsToKeep' from grid to newGrid
				char[][] newGrid = compactGrid(grid, topOfTower);

				//calculate how many rows are being removed (i.e. topOfTower - minRowsToKeep) = numRowsPurged
				int numRowsPurged = topOfTower - minRowsToKeep + 1; //topOfTower is zero offset based
				double pctComplete = (double) rockCtr / numOfRocks * 100;
				cumulativeNumRowsPurged+= numRowsPurged;
				int prevTopOfTower = topOfTower;
				topOfTower = topOfTower - numRowsPurged;
				cumulativeTopOfTower = cumulativeNumRowsPurged + topOfTower;

				System.out.printf("Compacting... prevTopOfTower=%s, topOfTower=%s, minRowsToKeep=%s, " +
								"numRowsPurged=%s, cumulativeNumRowsPurged=%s, cumulativeTopOfTower=%s, " +
								"numRocks=%s, maxMovesDown=%s, pctComplete=%f%n",
						prevTopOfTower, topOfTower, minRowsToKeep, numRowsPurged, cumulativeNumRowsPurged,
						cumulativeTopOfTower, rockCtr, maxNumberOfDownMovesForAnyRock, pctComplete);

				grid = newGrid;
			}

			//check if sample needs to be taken
			if (!sampleTaken && rockCtr > startSampleAt) {
				cumulativeTopOfTower = cumulativeNumRowsPurged + topOfTower;
				sampleSignature =
						getStateSignature(shiftIndex, shapeIndex, topOfTower, grid, minRowsToKeep - 10,
								rockCtr, cumulativeTopOfTower);

				if (DEBUG) {
					System.out.printf("Sample State Signature: %s%n", sampleSignature);
				}
				sampleTaken = true;
			}

			//if sampleTaken, findMatch (if not already found)
			if (sampleTaken && !sampleMatched && rockCtr > sampleSignature.getNumRocks()) {
				cumulativeTopOfTower = cumulativeNumRowsPurged + topOfTower;
				StateSignature checkSignature =
						getStateSignature(shiftIndex, shapeIndex, topOfTower, grid, minRowsToKeep - 10,
								rockCtr, cumulativeTopOfTower);

				//when match found, skip ahead (both rocks and towerIncrements) n cycles to just before the limit (numOfRocks)
				if (checkSignature.getSignature().equals(sampleSignature.getSignature())) {
					if (DEBUG) {
						System.out.printf("Matching State Signature Found: %s%n", checkSignature);
					}
					sampleMatched = true;

					//skip ahead
					long rocksPerCycle = checkSignature.getNumRocks() - sampleSignature.getNumRocks();
					long towerIncrPerCycle = checkSignature.getTopOfTower() - sampleSignature.getTopOfTower();

					long numCyclesToSkip = (numOfRocks - rockCtr) / rocksPerCycle;
					rockCtr+= (rocksPerCycle * numCyclesToSkip);
					cumulativeNumRowsPurged+= (towerIncrPerCycle * numCyclesToSkip);
				}
			}

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
			int downMovesForThisRockCtr = 0;
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
							downMovesForThisRockCtr++;
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

			maxNumberOfDownMovesForAnyRock = Math.max(maxNumberOfDownMovesForAnyRock, downMovesForThisRockCtr);
		}

		if (DEBUG) {
			printBottomOfGrid(grid, topOfTower + 3, 30);
		}
		cumulativeTopOfTower = cumulativeNumRowsPurged + topOfTower;
		long answer = cumulativeTopOfTower + 1;
		System.out.printf("Tower Height = %s%n", answer);

		long expected = 1591860465110L;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}

		if (DEBUG) {
			System.out.printf("Max down moves for any rock = %s%n", maxNumberOfDownMovesForAnyRock);
			if (maxNumberOfDownMovesForAnyRock + 10 > minRowsToKeep) {
				System.err.println("Not enough rows kept!!");
			}
			printRuntime(startTime);
		}
	}

	private static StateSignature getStateSignature(int shiftIndex, int shapeIndex, int topOfTower, char[][] grid,
													int topRowsCount, long rockCtr, long cumulativeTopOfTower) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < topRowsCount; i++) {
			sb.append("|");
			sb.append(String.valueOf(grid[topOfTower-i]));
		}
		sb.append("|");
		return new StateSignature(shiftIndex, shapeIndex, sb.toString(), rockCtr, cumulativeTopOfTower);
	}

	private static void printRuntime(long startTime) {
		long seconds = (System.currentTimeMillis() - startTime) / 1000L;
		System.out.printf("Running for %d seconds%n", seconds);
	}

	private static char[][] compactGrid(char[][] oldGrid, int topOfTower) {
		char[][] newGrid = newGrid();

		int numRowsToPurge = topOfTower - minRowsToKeep + 1; //topOfTower is 0 offset based
		System.arraycopy(oldGrid, numRowsToPurge, newGrid, 0, minRowsToKeep);
		return newGrid;
	}

	private static char[][] newGrid() {
		char[][] grid = new char[numGridRows][numGridCols];
		for (int i = 0; i < numGridRows; i++) {
			for (int j = 0; j < numGridCols; j++) {
				grid[i][j] = '.';
			}
		}
		return grid;
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

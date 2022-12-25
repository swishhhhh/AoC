package aoc22;

import static aoc22.datastructs.Direction.*;
import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;
import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24Part1 {
	static class Blizzard {
		private final int id;
		private Coordinates location;
		private final char sign;
		private final Direction direction;

		Blizzard(int id, Coordinates location, char sign) {
			this.id = id;
			this.location = location;
			this.sign = sign;
			switch (sign) {
				case '>' -> direction = EAST;
				case '<' -> direction = WEST;
				case '^' -> direction = NORTH;
				case 'v' -> direction = SOUTH;
				default -> throw new IllegalArgumentException("Not a valid sign " + sign);
			}
		}

		public int getId() {
			return id;
		}
		public Coordinates getLocation() {
			return location;
		}
		public char getSign() {
			return sign;
		}
		public Direction getDirection() {
			return direction;
		}
		public void setLocation(Coordinates location) {
			this.location = location;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Blizzard blizzard = (Blizzard) o;

			return id == blizzard.id;
		}
		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public String toString() {
			return "Blizzard{" +
					"id=" + id +
					", location=" + location +
					", sign=" + sign +
					", direction=" + direction +
					'}';
		}
	}

	private static char[][] emptyGrid;
	private static char[][] grid;
	static Map<Integer, Blizzard> blizzards = new HashMap<>();
	static Map<Coordinates, Integer> blizzardsByLocationCnt = new HashMap<>();
	static Coordinates startLocation;
	static Coordinates targetLocation;
	static Coordinates cursor;
	static final Set<Character> blizzardMarkers = Set.of('<', '>', '^', 'v');
//	static final int CYCLE_REPEAT_CNT = 12; //LCD of dimensions of inner grid (4 X 6 = 24, LCD=12)
	static final int CYCLE_REPEAT_CNT = 300; //LCD of dimensions of inner grid (20 X 150 = 3000, LCD=300)

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day24_input.txt");

		//setup grids, start and target locations, cursor
		grid = new char[lines.size()][lines.get(0).length()];
		emptyGrid = new char[lines.size()][lines.get(0).length()];
		startLocation = new Coordinates(1, 0);
		targetLocation = new Coordinates(lines.get(0).length() - 2, lines.size() - 1);
		cursor = new Coordinates(startLocation.x(), startLocation.y());

		int blizzardCtr = 0;
		int row = -1;

		//set up base/empty grid
		for (String line: lines) {
			row++;
			char[] charAry = line.toCharArray();
			for (int col = 0; col < charAry.length; col++) {
				char c = charAry[col] == '#' ? '#' : '.';
				emptyGrid[row][col] = c;

				if (blizzardMarkers.contains(charAry[col])) {
					blizzardCtr++;
					Blizzard blizz = new Blizzard(blizzardCtr, new Coordinates(col, row), charAry[col]);
					blizzards.put(blizz.getId(), blizz);
					Integer cnt = blizzardsByLocationCnt.get(blizz.getLocation());
					if (cnt == null) {
						cnt = 0;
					}
					blizzardsByLocationCnt.put(blizz.getLocation(), ++cnt);
				}
			}
		}

		redrawGrid();
		Helper.printArray2D(grid);

		//------------------

		int minutesCtr = 0;
		Set<String> statesVisited = new HashSet<>();
		Queue<List<Coordinates>> queue = new LinkedBlockingQueue<>();
		queue.add(List.of(cursor.clone()));

		List<Coordinates> pathFound = null;

		while (!queue.isEmpty()) {
			List<Coordinates> incomingPath = new ArrayList<>(queue.poll());

			//only advance blizzards once per minute, check path.size() to determine if need to advance
			if (minutesCtr < incomingPath.size()) {
				advanceBlizzards();
				minutesCtr++;
				System.out.printf("Minutes = %s, q-size=%s%n", minutesCtr, queue.size());
				redrawGrid();
			}

			cursor = incomingPath.get(incomingPath.size() - 1); //set cursor to last node in the path

			//check if already visited this state
			String stateSignature = (minutesCtr % CYCLE_REPEAT_CNT) + cursor.toString();
			if (statesVisited.contains(stateSignature)) {
				System.out.printf("State %s already visited%n", stateSignature);
				continue;
			}

			//check availability for each of the 4 directions + same cell
			List<Coordinates> cellsToCheck = new ArrayList<>();
			cellsToCheck.add(cursor); //same cell
			cellsToCheck.addAll(getNeighboringCells(cursor));

			boolean optionFound = false, targetFound = false;
			for (Coordinates cell: cellsToCheck) {
				if (!blizzardsByLocationCnt.containsKey(cell)) {
					optionFound = true;
					List<Coordinates> newPath = new ArrayList<>(incomingPath);
					newPath.add(cell);

					if (cell.equals(targetLocation)) {
						targetFound = true; //target found!
						pathFound = newPath;
						break;
					}

					statesVisited.add(stateSignature);
					queue.add(newPath);
				}
			}

			//if nothing available, continue (path is dead-end (literally!!))
			if (!optionFound) {
				continue;
			}

			if (targetFound) {
				cursor = targetLocation;
				break;
			}

		}

		redrawGrid();
		Helper.printArray2D(grid);

		System.out.printf("Number of minutes = %s%n", pathFound.size() - 1);

	}

	private static List<Coordinates> getNeighboringCells(Coordinates cursor) {
		//West
		return Stream.of(
					new Coordinates(cursor.x(), cursor.y()-1), //North
					new Coordinates(cursor.x(), cursor.y()+1), //South
					new Coordinates(cursor.x()+1, cursor.y()), //East
					new Coordinates(cursor.x()-1, cursor.y()))
				.filter(coord -> coord.y() >= 0 && coord.y() < grid.length) //avoid out-of-bounds
				.filter(coord -> coord.x() >= 0 && coord.x() < grid[0].length) // ditto
				.filter(coord -> grid[coord.y()][coord.x()] != '#') //avoid hitting a wall..
				.collect(Collectors.toList());
	}

	private static void advanceBlizzards() {
		Coordinates prevLocation, newLocation;
		int x=0, y=0;
		for (Blizzard bliz: blizzards.values()) {
			prevLocation = bliz.getLocation();
			switch (bliz.getDirection()) {
				case NORTH -> {
					x = prevLocation.x();
					y = prevLocation.y()-1;
					if (grid[y][x] == '#') y = grid.length - 2; //wrap around to bottom
				}
				case SOUTH -> {
					x = prevLocation.x();
					y = prevLocation.y()+1;
					if (grid[y][x] == '#') y = 1; //wrap around to top
				}
				case EAST -> {
					x = prevLocation.x()+1;
					y = prevLocation.y();
					if (grid[y][x] == '#') x = 1; //wrap around to left side
				}
				case WEST -> {
					x = prevLocation.x()-1;
					y = prevLocation.y();
					if (grid[y][x] == '#') x = grid[0].length - 2; //wrap around to right side
				}
			}

			newLocation = new Coordinates(x, y);

			//update blizzards AND blizzardsByLocationCnt
			//remember to remove zero counts from map!

			//update/remove prev location count
			Integer cnt = blizzardsByLocationCnt.remove(prevLocation);
			if (cnt != null) { //should never be null...
				cnt--;
				if (cnt > 0) {
					blizzardsByLocationCnt.put(prevLocation, cnt); //update with decremented count
				}
			}

			//update/add new location count
			cnt = blizzardsByLocationCnt.get(newLocation);
			if (cnt == null) {
				cnt = 0;
			}
			cnt++;
			blizzardsByLocationCnt.put(newLocation, cnt);

			//finally, update blizz's location itself
			bliz.setLocation(newLocation);
		}
	}

	public static void redrawGrid() {
		grid = cloneGrid(emptyGrid);

		//draw blizzards onto grid
		for (Blizzard bliz: blizzards.values()) {
			Coordinates loc = bliz.getLocation();
			//check if there's already more than 1 blizz in that location
			Integer countAtLocation = blizzardsByLocationCnt.get(loc);
			if (countAtLocation != null && countAtLocation > 1) {
				grid[loc.y()][loc.x()] = (char) (countAtLocation + 48); //ASCII value 0 starts at 48
			} else {
				grid[loc.y()][loc.x()] = bliz.getSign();
			}
		}

		//draw cursor onto grid
		grid[cursor.y()][cursor.x()] = 'E';
	}

	private static char[][] cloneGrid(char[][] srcGrid) {
		char [][] targetGrid = new char[srcGrid.length][srcGrid[0].length];
		for (int i = 0; i < srcGrid.length; i++) {
			System.arraycopy(srcGrid[i], 0, targetGrid[i], 0, targetGrid[i].length);
		}
		return targetGrid;
	}
}

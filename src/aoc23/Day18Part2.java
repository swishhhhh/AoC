package aoc23;

import datastructs.Line;
import utils.ResourceLoader;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * <a href="https://adventofcode.com/2023/day/18">Advent of Code 2023 Day 18</a>
 */
public class Day18Part2 {
	private final static boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day18_input.txt");

		long count = process(lines);
		System.out.printf("Trench tiles count: %s%n", count);

		long expected = 147839570293376L;
		if (count != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", count, expected));
		}
	}

	private static long process(List<String> lines) {
		Map<Long, List<Line>> horizontalLines = new TreeMap<>();
		List<Line> verticalLines = new ArrayList<>();

		Line minMaxX = ingestLines(lines, horizontalLines, verticalLines);
		return processLines(horizontalLines, verticalLines, minMaxX.getX1(), minMaxX.getX2());
	}

	/**
	 * @return Line representing the minX (x1) and maxX (x2) of the grid.
	 */
	private static Line ingestLines(List<String> lines, Map<Long, List<Line>> horizontalLines, List<Line> verticalLines) {
		int x = 0, y = 0;
		int minX = 0, maxX = 0, minY = 0, maxY = 0;

		for (String line : lines) {
			String hexString = line.split(" ")[2].replace("(", "").replace(")", "").replace("#", "");
			int steps = Integer.parseInt(hexString.substring(0, 5), 16);
			String direction = hexString.substring(5, 6);
			long x1 = x, x2 = x, y1 = y, y2 = y;

			switch (direction) {
				case "0" -> { //right
					x1 = x;
					x+= steps;
					x2 = x;
				}
				case "1" -> { //down
					y1 = y;
					y+= steps;
					y2 = y;
				}
				case "2" -> { //left
					x2 = x;
					x-= steps;
					x1 = x;
				}
				case "3" -> { //up
					y2 = y;
					y-= steps;
					y1 = y;
				}
			}

			Line gridLine = new Line(x1, x2, y1, y2);
			minX = min(minX, x);
			maxX = max(maxX, x);
			minY = min(minY, y);
			maxY = max(maxY, y);

			if (gridLine.getType().equals("horizontal")) {
				List<Line> horizontalLinesForRow =
						horizontalLines.computeIfAbsent(gridLine.getY1(), k -> new ArrayList<>());
				horizontalLinesForRow.add(gridLine);
			} else if (gridLine.getType().equals("vertical")) {
				verticalLines.add(gridLine);
			}

			if (DEBUG) {
				System.out.printf("Direction: %s, Steps: %s%n", direction, steps);
			}
		}

		//sort all the horizontal lines lists and the vertical lines list
		horizontalLines.values().forEach(Collections::sort);
		Collections.sort(verticalLines);

		return new Line(minX, maxX, 0, 0);
	}

	private static long processLines(Map<Long, List<Line>> horizontalLines, List<Line> verticalLines, long minX, long maxX) {
		long count = 0;
		Long prevRowNum = null;
		for (Map.Entry<Long, List<Line>> entry : horizontalLines.entrySet()) {
			long rowNum = entry.getKey();
			if (prevRowNum != null) {
				count+= processFollowingRowsWithOnlyVerticals(rowNum, prevRowNum, verticalLines, minX, maxX);
			}

			count+= processRowWithHorizontals(rowNum, horizontalLines, verticalLines, minX, maxX);

			prevRowNum = rowNum;
		}
		return count;
	}

	private static long processFollowingRowsWithOnlyVerticals(long rowNum, long prevRowNum,
															  List<Line> verticalLines, long minX, long maxX) {
		long repeatCtr = rowNum - prevRowNum - 1;
		if (repeatCtr == 0) {
			return 0;
		}

		List<Line> linesThisRow = getVerticalLinesIntersectingRow(prevRowNum + 1, verticalLines);
		if (linesThisRow.isEmpty()) { //indicates bottom of grid/shape
			return 0;
		}

		Collections.sort(linesThisRow);

		StringBuilder sb = new StringBuilder();
		long cursor = minX;
		long inCtr = 0, outCtr = 0, barrierCharCtr = 0;
		int barrierCrossingsCtr = 0;
		Line line;
		for (int i = 0; i < linesThisRow.size(); i++) {
			//account for empty space since last line
			line = linesThisRow.get(i);
			long emptyTilesSinceLastLine = line.getX1() - cursor;
			if (emptyTilesSinceLastLine > 0) {
				if (barrierCrossingsCtr % 2 == 0) {
					outCtr+= emptyTilesSinceLastLine;
					sb.append("[O,").append(emptyTilesSinceLastLine).append("] ");
				} else {
					inCtr+= emptyTilesSinceLastLine;
					sb.append("[I,").append(emptyTilesSinceLastLine).append("] ");
				}
			}
			cursor = line.getX1();

			//process vertical line
			barrierCharCtr++;
			barrierCrossingsCtr++;
			sb.append("| ");
			cursor++;
		}

		//add trailing out chars (if any)
		long trailingChars = maxX - cursor + 1;
		if (trailingChars > 0) {
			outCtr+= trailingChars;
			sb.append("[O,").append(trailingChars).append("] ");
		}

		if (DEBUG) {
			System.out.printf("Row num %s: (%s) X %s %n", prevRowNum + 1, sb, repeatCtr);
		}

		return (inCtr + barrierCharCtr) * repeatCtr;
	}

	private static long processRowWithHorizontals(Long rowNum, Map<Long, List<Line>> allHorizontalLines,
												  List<Line> verticalLines, long minX, long maxX) {
		List<Line> horizontalLinesThisRow = allHorizontalLines.get(rowNum);
		List<Line> verticalLinesThisRow = getVerticalLinesIntersectingRow(rowNum, verticalLines);
		List<Line> linesThisRow = new ArrayList<>(horizontalLinesThisRow);
		linesThisRow.addAll(verticalLinesThisRow);
		Collections.sort(linesThisRow); //will sort on x1 then x2, so vertical lines will come before horizontal of equal x1

		StringBuilder sb = new StringBuilder();
		long cursor = minX;
		long inCtr = 0, outCtr = 0, barrierCharCtr = 0;
		int barrierCrossingsCtr = 0;
		Line line;
		for (int i = 0; i < linesThisRow.size(); i++) {
			line = linesThisRow.get(i);
			Line prevLine = i == 0 ? null : linesThisRow.get(i - 1);
			Line nextLine = i >= linesThisRow.size() - 1 ? null : linesThisRow.get(i + 1);

			//account for empty space since last line
			long emptyTilesSinceLastLine = line.getX1() - cursor;
			if (emptyTilesSinceLastLine > 0) {
				if (barrierCrossingsCtr % 2 == 0) {
					outCtr+= emptyTilesSinceLastLine;
					sb.append("[O,").append(emptyTilesSinceLastLine).append("] ");
				} else {
					inCtr+= emptyTilesSinceLastLine;
					sb.append("[I,").append(emptyTilesSinceLastLine).append("] ");
				}
			}
			cursor = line.getX1();

			//if vertical line and no horizontal line to the left (with same x2 value) or right (with same x1 value)
			if (line.getType().equals("vertical")) {
				if (   (prevLine == null || prevLine.getX2() != line.getX1())
						&& (nextLine == null || nextLine.getX1() != line.getX1())) {
					barrierCharCtr++;
					barrierCrossingsCtr++;
					sb.append("| ");
					cursor = line.getX2() + 1;
					continue;
				} else {
					cursor = line.getX2() + 1;
				}
			}

			//if horizontal line
			if (line.getType().equals("horizontal")) {
				long lenOfLine = line.getX2() - line.getX1() + 1;
				barrierCharCtr+= lenOfLine;

				//guaranteed to have vertical line on either side (with same x1 and x2 coords respectively)

				//process left end of horizontal line
				Line leftVerticalLine = prevLine;
				if (leftVerticalLine.getY1() == line.getY1()){ //vertical line points south
					sb.append("F "); //representing NW corner
				} else if (leftVerticalLine.getY2() == line.getY1()) { //vertical line points north
					sb.append("L "); //representing SW corner
					barrierCrossingsCtr++; //only count north pointing vertical lines
				} else {
					throw new RuntimeException(
							String.format("Unexpected left veritcal line %s found at row %s", leftVerticalLine, rowNum));
				}

				//process middle of horizontal line
				long lenOfMiddle = lenOfLine - 2;
				if (lenOfMiddle > 0) {
					sb.append("[-,").append(lenOfMiddle).append("] ");
				}

				//process right end of horizontal line
				Line rightVerticalLine = nextLine;
				if (rightVerticalLine.getY1() == line.getY2()){ //vertical line points south
					sb.append("7 "); //representing NE corner
				} else if (rightVerticalLine.getY2() == line.getY2()) { //vertical line points north
					sb.append("J "); //representing SE corner
					barrierCrossingsCtr++; //only count north pointing vertical lines
				} else {
					throw new RuntimeException(
							String.format("Unexpected left veritcal line %s found at row %s", leftVerticalLine, rowNum));
				}

				cursor = line.getX2() + 1;
			}
		}

		//add trailing out chars (if any)
		long trailingChars = maxX - cursor + 1;
		if (trailingChars > 0) {
			outCtr+= trailingChars;
			sb.append("[O,").append(trailingChars).append("] ");
		}

		//print debug line
		if (DEBUG) {
			System.out.printf("Row num %s: %s%n", rowNum, sb);
		}

		return inCtr + barrierCharCtr;
	}

	private static List<Line> getVerticalLinesIntersectingRow(Long rowNum, List<Line> verticalLines) {
		return verticalLines
				.stream()
				.filter(line -> line.getY1() <= rowNum && line.getY2() >= rowNum)
				.collect(Collectors.toList());
	}
}

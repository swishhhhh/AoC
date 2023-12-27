package aoc23;

import datastructs.Coordinates;
import utils.ResourceLoader;

import java.util.*;

import static utils.GridUtils.*;

/**
 * <a href="https://adventofcode.com/2023/day/23">Advent of Code 2023 Day 23</a>
 */
public class Day23Part2 {

	static class Graph {
		private final Map<Coordinates, Vertex> vertices = new HashMap<>();
		private final List<Edge> edges = new ArrayList<>();

		@Override
		public String toString() {
			return "Graph{" +
					"vertices=" + vertices +
					", edges=" + edges +
					'}';
		}
	}

	static class Vertex {
		Coordinates id;
		Set<Edge> outEdges = new HashSet<>();

		public Vertex(Coordinates id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Vertex vertex)) return false;

			if (!Objects.equals(id, vertex.id)) return false;
			return Objects.equals(outEdges, vertex.outEdges);
		}

		@Override
		public int hashCode() {
			return id != null ? id.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "Vertex{" +
					"id='" + id + '\'' +
					", outEdges=" + outEdges +
					'}';
		}
	}

	static class Edge {
		Vertex fromV;
		Vertex toV;
		long weight;
		List<Coordinates> path;

		public Edge(Vertex fromV, Vertex toV, long weight) {
			this.fromV = fromV;
			this.toV = toV;
			this.weight = weight;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Edge edge)) return false;

			if (weight != edge.weight) return false;
			if (!fromV.equals(edge.fromV)) return false;
			return toV.equals(edge.toV);
		}

		@Override
		public int hashCode() {
			int result = fromV.hashCode();
			result = 31 * result + toV.hashCode();
			result = 31 * result + (int) (weight ^ (weight >>> 32));
			return result;
		}

		@Override
		public String toString() {
			return "Edge{" +
					"fromV=" + fromV.id +
					", toV=" + toV.id +
					", weight=" + weight +
					'}';
		}
	}

	private static final boolean DEBUG = false;
	private static List<Edge> longestPathTrace = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day23_input.txt");

		char[][] grid = new char[lines.size()][];
		for (int i = 0; i < grid.length; i++) {
			grid[i] = lines.get(i).toCharArray();
		}

		Coordinates start = getSource(grid);
		Coordinates end = getTarget(grid);
		Graph graph = loadGraph(grid, start, end);

		long answer = dfs(graph.vertices.get(start), graph.vertices.get(end), new HashSet<>(), new ArrayList<>());

		if (DEBUG) {
			tracePathAndPrintGrid(grid);
		}

		System.out.printf("Answer = %s%n", answer);

		long expected = 6230;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}

	}

	private static long dfs(Vertex incomingV, Vertex target, Set<Coordinates> visitedVertices, List<Edge> incomingEdges) {
		//base case (reached target)
		if (incomingV.equals(target)) {
			long sumToTarget = getSumToTarget(incomingEdges);

			if (DEBUG) {
				updatePathTrace(sumToTarget, incomingEdges);
			}

			return sumToTarget;
		}

		//brute force add every other adjacent vertex (unless already in the visited stack)
		visitedVertices.add(incomingV.id);
		long maxToTarget = 0;
		for (Edge e : incomingV.outEdges) {
			if (visitedVertices.contains(e.toV.id)) {
				continue;
			}

			List<Edge> edgesPath = new ArrayList<>(incomingEdges);
			edgesPath.add(e);
			long result = dfs(e.toV, target, visitedVertices, edgesPath);
			maxToTarget = Math.max(result, maxToTarget);
		}

		visitedVertices.remove(incomingV.id);
		return maxToTarget;
	}

	private static long getSumToTarget(List<Edge> incomingEdges) {
		long sumToTarget = 0;
		for (Edge e : incomingEdges) {
			sumToTarget+= e.weight;
		}
		return sumToTarget;
	}

	private static void updatePathTrace(long sumToTargetCurrent, List<Edge> incomingEdges) {
		long sumToTargetSoFar = getSumToTarget(longestPathTrace);
		if (sumToTargetCurrent > sumToTargetSoFar) {
			longestPathTrace = incomingEdges;
		}
	}

	private static Graph loadGraph(char[][] grid, Coordinates start, Coordinates end) {
		Graph graph = new Graph();
		List<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex(start));
		vertices.add(new Vertex(end));

		//find the rest of the vertices - as determined by any tile that has more than 2 eligible neighbors
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length; col++) {
				if (grid[row][col] != '#') {
					Coordinates candidate = new Coordinates(col, row);
					if (getAllNeighborsExcludingWalls(grid, candidate).size() > 2) {
						vertices.add(new Vertex(candidate));
					}
				}
			}
		}

		//load graph with vertices
		for (Vertex v : vertices) {
			graph.vertices.put(v.id, v);
		}

		// For each vertex create an edge for each of the directions you can traverse from it (i.e. 3 or 4) and set
		//  its weight based on number of steps to next grid intersection (other vertex)
		for (Vertex v : vertices) {
			List<Coordinates> neighbors = getAllNeighborsExcludingWalls(grid, v.id);
			for (Coordinates c : neighbors) {
				List<Coordinates> path = getPathToNextIntersection(c, grid, v);
				//last node in the path is the other vertex at the opposite end of this edge
				Vertex toV = graph.vertices.get(path.get(path.size() - 1));
				if (toV == null) {
					throw new RuntimeException("Unable to find vertex " + path.get(path.size() - 1));
				} else if (toV.equals(v)) {
					throw new RuntimeException("Grid has a loop-back corridor for coordinate " + c);
				}
				Edge edge = new Edge(v, toV, path.size());
				if (DEBUG) {
					edge.path = path;
				}
				v.outEdges.add(edge);
				graph.edges.add(edge);
			}

		}

		return graph;
	}

	private static List<Coordinates> getPathToNextIntersection(Coordinates start, char[][] grid, Vertex fromV) {
		List<Coordinates> path = new ArrayList<>();
		path.add(start);

		List<Coordinates> neighbors = getAllNeighborsExcludingWalls(grid, start);
		while (neighbors.size() == 2) {
			Coordinates selected = neighbors.get(0);
			if (path.contains(selected) || fromV.id.equals(selected)) {
				selected = neighbors.get(1);
			}
			path.add(selected);
			neighbors = getAllNeighborsExcludingWalls(grid, selected);

			if (path.size() > (grid.length * grid[0].length)) {
				throw new RuntimeException("Houston we got a problem!");
			}
		}

		return path;
	}

	private static Coordinates getSource(char[][] grid) {
		//starting point is the first dot on the top row of the grid
		for (int i = 0; i < grid[0].length; i++) {
			if (grid[0][i] == '.') {
				return new Coordinates(i, 0);
			}
		}

		throw new RuntimeException("Unable to find starting point");
	}

	private static Coordinates getTarget(char[][] grid) {
		//ending point is the first dot on the last row of the grid
		int lastRow = grid.length - 1;
		for (int i = 0; i < grid[lastRow].length; i++) {
			if (grid[lastRow][i] == '.') {
				return new Coordinates(i, lastRow);
			}
		}

		throw new RuntimeException("Unable to find ending point");
	}

	private static List<Coordinates> getAllNeighborsExcludingWalls(char[][] grid, Coordinates cursor) {
		return getNeighboringCells(grid, cursor).stream().filter(n -> grid[n.y()][n.x()] != '#').toList();
	}

	private static void tracePathAndPrintGrid(char[][] grid) {
		//replace the puzzle input chars '>' and 'v' (since we want to use them to mark our path) with '-' and '|' respectively
		for (char[] row : grid) {
			for (int i = 0; i < row.length; i++) {
				char c = row[i];
				if (c == '>') row[i] = '-';
				if (c == 'v') row[i] = '|';
			}
		}

		Coordinates prevStep = null;
		for (Edge edge : longestPathTrace) {
			if (prevStep == null) { //first edge
				prevStep = edge.fromV.id;
			}
			for (Coordinates step : edge.path) {
				char c = '?';
				if      (prevStep.x() < step.x()) c = '>';
				else if (prevStep.x() > step.x()) c = '<';
				else if (prevStep.y() < step.y()) c = 'v';
				else if (prevStep.y() > step.y()) c = '^';
				grid[step.y()][step.x()] = c;
				prevStep = step;
			}
		}

		printGrid(grid);
	}
}

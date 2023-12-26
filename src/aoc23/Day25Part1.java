package aoc23;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2023/day/25">Advent of Code 2023 Day 25</a>
 * <P>
 * Solved using Karger's algorithm to find the minimum number of cuts in a graph.
 */
public class Day25Part1 {
	private static class Graph {
		private final Map<String, Vertex> vertices = new TreeMap<>();
		private final List<Edge> edges = new ArrayList<>();

		@Override
		public String toString() {
			return "Graph{" +
					"vertices=" + vertices +
					", edges=" + edges +
					'}';
		}
	}

	private static class Vertex {
		private final String id;
		private final Set<Edge> edges = new HashSet<>();
		private final Set<String> ancestorVertices = new HashSet<>(); //tracks the ancestor edges (if any) this vertex was merged from

		public Vertex(String id) {
			this.id = id;
			this.ancestorVertices.add(id);
		}

		public void addEdge(Edge edge) {
			edges.add(edge);
		}

		@Override
		public String toString() {
			return "Vertex{" +
					"id='" + id + '\'' +
					", # of edges=" + edges.size() +
					'}';
		}
	}

	private static class Edge {
		private final List<Vertex> vertices = new ArrayList<>();

		public Edge(Vertex first, Vertex second) {
			if (first == null || second == null) {
				throw new IllegalArgumentException("Both vertices are required");
			}
			vertices.add(first);
			vertices.add(second);
		}

		public boolean contains(Vertex v1, Vertex v2) {
			return vertices.contains(v1) && vertices.contains(v2);
		}

		public Vertex getOppositeVertex(Vertex v) {
			if (!vertices.contains(v)) {
				throw new IllegalArgumentException("Vertex " + v.id);
			}
			return vertices.get(1 - vertices.indexOf(v));
		}

		public void replaceVertex(Vertex oldV, Vertex newV) {
			if (!vertices.contains(oldV)) {
				throw new IllegalArgumentException("Vertex " + oldV.id);
			}
			vertices.remove(oldV);
			vertices.add(newV);
		}

		@Override
		public String toString() {
			return "Edge{" +
					"vertices=" + vertices +
					'}';
		}
	}

	final static boolean DEBUG = false;

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day25_input.txt");

		Graph graph = process(lines);
		assert graph.edges.size() == 3;
		assert graph.vertices.size() == 2;

		Object[] vertices = graph.vertices.values().toArray();
		int product = ((Vertex)vertices[0]).ancestorVertices.size() * ((Vertex)vertices[1]).ancestorVertices.size();
		System.out.printf("Number of cuts = %s, product = %s%n", graph.edges.size(), product);

		int expected = 569904;
		if (product != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", product, expected));
		}
	}

	private static Graph process(List<String> lines) {
		int minCut = lines.size(); //initialize to max
		Graph selectedGraph = null;
		for (int i = 1; i < 1000; i++) { //might need more than 1000 iterations... but usually enough
			if (DEBUG && i % 10 == 0) {
				System.out.printf("iteration: %s%n", i);
			}

			Graph graph = createGraph(lines);

			if (DEBUG && i == 1) {
				printGraph(graph);
			}

			int m = minCut(graph);
			if (m < minCut) {
				minCut = m;
				selectedGraph = graph;
			}

			if (minCut == 3) {
				System.out.printf("Min cuts found after %s iterations%n", i);
				break; //shortcut, since we know the number of cuts we're looking for...
			}
		}

		return selectedGraph;
	}

	public static int minCut(Graph graph) {
		//Karger's algorithm
		Random rnd = new Random();
		while (graph.vertices.size() > 2) {
			Edge edge = graph.edges.remove(rnd.nextInt(graph.edges.size()));
			Vertex v1 = graph.vertices.remove(edge.vertices.get(0).id);
			Vertex v2 = graph.vertices.remove(edge.vertices.get(1).id);
			Vertex mergedVertex = new Vertex(v1.id);
			mergedVertex.ancestorVertices.addAll(v1.ancestorVertices);
			mergedVertex.ancestorVertices.addAll(v2.ancestorVertices);
			redirectEdges(graph, v1, mergedVertex);
			redirectEdges(graph, v2, mergedVertex);
			graph.vertices.put(v1.id, mergedVertex);
		}
		return graph.edges.size();
	}

	private static void redirectEdges(Graph graph, Vertex fromV, Vertex toV) {
		for (Edge edge : fromV.edges) {
			if (edge.getOppositeVertex(fromV) == toV) {
				//remove edge looping back to self
				toV.edges.remove(edge);
				graph.edges.remove(edge);
			} else {
				edge.replaceVertex(fromV, toV);
				toV.addEdge(edge);
			}
		}
		fromV.edges.clear();
	}

	private static Graph createGraph(List<String> lines) {
		Graph graph = new Graph();

		for (String line: lines) {
			String[] ary = line.split(":");
			String[] ary2 = ary[1].trim().split(" ");

			//add left vertex
			String leftId = ary[0].trim();
			Vertex leftV = graph.vertices.get(leftId);
			if (leftV == null) {
				leftV = new Vertex(leftId);
				graph.vertices.put(leftId, leftV);
			}

			//add all the right vertices
			for (String s : ary2) {
				String rightId = s.trim();
				Vertex rightV = graph.vertices.get(rightId);
				if (rightV == null) {
					rightV = new Vertex(rightId);
					graph.vertices.put(rightId, rightV);
				}

				Edge e = new Edge(leftV, rightV);
				graph.edges.add(e);
				leftV.addEdge(e);
				rightV.addEdge(e);
			}
		}

		return graph;
	}

	private static void printGraph(Graph graph) {
		System.out.println("Graph:");
		for (Vertex v : graph.vertices.values()) {
			System.out.print(v.id + ":");
			for (Edge edge : v.edges) {
				System.out.print(" " + edge.getOppositeVertex(v).id);
			}
			System.out.println();
		}
	}
}

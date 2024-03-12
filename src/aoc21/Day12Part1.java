package aoc21;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/12">Advent of Code 2021 Day 12</a>
 */
public class Day12Part1 {
    static class Node {
        String id;
        List<Node> connectedNodes = new ArrayList<>();

        public Node(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id='" + id + '\'' +
                    ", connectedNodes=" + connectedNodes.stream().map(n -> n.id).toList() +
                    '}';
        }
    }

    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {

        String resourceName = "aoc21/Day12_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        Node start = buildGraph(lines);
        long answer = countPathsDfs(0, start, new ArrayList<>(), new HashSet<>());

        System.out.printf("Number of paths = %s%n", answer);

        long expected = 3713;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long countPathsDfs(long count, Node node, List<String> currentPath, Set<String> visitedSmallCaves) {
        currentPath.add(node.id);
        if (isSmallCave(node)) {
            visitedSmallCaves.add(node.id);
        }

        if (node.id.equals("end")) { //base case
            count++;
            if (DEBUG) {
                System.out.printf("Path #%s: %s%n", count, currentPath);
            }

        } else {
            for (Node n : node.connectedNodes) {
                if (visitedSmallCaves.contains(n.id)) {
                    continue;
                }

                count = countPathsDfs(count, n, currentPath, visitedSmallCaves);
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visitedSmallCaves.remove(node.id);
        return count;
    }

    private static boolean isSmallCave(Node n) {
        return n.id.equals(n.id.toLowerCase());
    }

    private static Node buildGraph(List<String> lines) {
        Map<String, Node> nodesMap = new HashMap<>();

        for (String line : lines) {
            String[] ary = line.split("-");
            Node node1 = nodesMap.get(ary[0]);
            Node node2 = nodesMap.get(ary[1]);

            if (node1 == null) {
                node1 = new Node(ary[0]);
                nodesMap.put(ary[0], node1);
            }
            if (node2 == null) {
                node2 = new Node(ary[1]);
                nodesMap.put(ary[1], node2);
            }

            node1.connectedNodes.add(node2);
            node2.connectedNodes.add(node1);
        }

        return nodesMap.get("start");
    }
}

package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/7">Advent of Code 2020 Day 7</a>
 */
public class Day07Part2 {
    static class Node {
        String id;
        Map<Node, Integer> children = new HashMap<>();
        Set<Node> parents = new HashSet<>();

        public Node(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;

            return Objects.equals(id, node.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id='" + id + '\'' +
                    ", children=" + children.size() +
                    ", parents=" + parents.size() +
                    '}';
        }
    }

    static class StackItem {
        Node node;
        long multiplier;

        public StackItem(Node node, long multiplier) {
            this.node = node;
            this.multiplier = multiplier;
        }

        @Override
        public String toString() {
            return "StackItem{" +
                    "node=" + node +
                    ", multiplier=" + multiplier +
                    '}';
        }
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day7_input.txt");

        long answer = new Day07Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2431;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, Node> allNodes = buildGraph(lines);
        return countDescendants(allNodes.get("shiny gold"));
    }

    private Map<String, Node> buildGraph(List<String> lines) {
        Map<String, Node> nodes = new HashMap<>();
        for (String line : lines) {
            String[] ary = line.split(" contain ");
            String parentId = ary[0].replace("bags", "").trim();

            Node parentNode = nodes.get(parentId);
            if (parentNode == null) {
                parentNode = new Node(parentId);
                nodes.put(parentId, parentNode);
            }

            if (ary[1].trim().equals("no other bags.")) {
                continue;
            }

            String[] ary2 = ary[1].split(",");
            for (String s : ary2) {
                s = s.trim();
                int firstSpace = s.indexOf(' ');
                int qty = Integer.parseInt(s.substring(0, firstSpace));
                String containedId =
                        s.substring(firstSpace).replace("bags", "").replace("bag", "").replace(".", "").trim();

                Node containedNode = nodes.get(containedId);
                if (containedNode == null) {
                    containedNode = new Node(containedId);
                    nodes.put(containedId, containedNode);
                }

                parentNode.children.put(containedNode, qty);
                containedNode.parents.add(parentNode);
            }
        }

        return nodes;
    }

    private long countDescendants(Node src) {
        long count = 0;

        Stack<StackItem> stack = new Stack<>();
        stack.add(new StackItem(src, 1));

        //dfs
        while (!stack.isEmpty()) {
            StackItem si = stack.pop();
            for (Map.Entry<Node, Integer> e : si.node.children.entrySet()) {
                long multiples = si.multiplier * e.getValue();
                stack.add(new StackItem(e.getKey(), multiples));
                count += multiples;
            }
        }

        return count;
    }
}

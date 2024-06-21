package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/7">Advent of Code 2020 Day 7</a>
 */
public class Day07Part1 {
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

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day7_input.txt");

        long answer = new Day07Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 335;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, Node> allNodes = buildGraph(lines);
        return getAncestors(allNodes.get("shiny gold")).size();
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

    private Set<String> getAncestors(Node src) {
        Set<String> ancestors = new HashSet<>();

        Stack<Node> stack = new Stack<>();
        stack.add(src);

        //dfs
        while (!stack.isEmpty()) {
            Node n = stack.pop();

            if (!n.id.equals(src.id)) { //avoid counting srcNode itself
                ancestors.add(n.id);
            }

            stack.addAll(n.parents);
        }

        return ancestors;
    }
}

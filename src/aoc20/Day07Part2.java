package aoc20;

import utils.ResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * <a href="https://adventofcode.com/2020/day/7">Advent of Code 2020 Day 7</a>
 */
public class Day07Part2 {
    static class StackItem {
        Day07Part1.Node node;
        long multiplier;

        public StackItem(Day07Part1.Node node, long multiplier) {
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
        Map<String, Day07Part1.Node> allNodes = Day07Part1.buildGraph(lines);
        return countDescendants(allNodes.get("shiny gold"));
    }

    private long countDescendants(Day07Part1.Node src) {
        long count = 0;

        Stack<StackItem> stack = new Stack<>();
        stack.add(new StackItem(src, 1));

        //dfs
        while (!stack.isEmpty()) {
            StackItem si = stack.pop();
            for (Map.Entry<Day07Part1.Node, Integer> e : si.node.children.entrySet()) {
                long multiples = si.multiplier * e.getValue();
                stack.add(new StackItem(e.getKey(), multiples));
                count += multiples;
            }
        }

        return count;
    }
}

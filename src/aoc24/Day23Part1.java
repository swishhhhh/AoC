package aoc24;

import utils.ResourceLoader;

import java.util.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * <a href="https://adventofcode.com/2024/day/23">Advent of Code 2024 Day 23</a>
 */
public class Day23Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day23_input.txt");

        long answer = new Day23Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 1269;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        Map<String, List<String>> graph = buildGraph(lines);

        long count = 0;

        Collection<String> tNodes = graph.keySet().stream().filter(s -> s.startsWith("t")).toList();
        Set<String> visited = new HashSet<>();

        //for each tNode, find all pair combinations that are also connected to each other
        for (String tNode : tNodes) {
            List<String> tNodeConnections = graph.get(tNode);
            for (int i = 0; i < tNodeConnections.size() - 1; i++) {
                String con1 = tNodeConnections.get(i);
                for (int j = i + 1; j < tNodeConnections.size(); j++) {
                    String con2 = tNodeConnections.get(j);
                    String dedupeKey = new ArrayList<>(Arrays.asList(tNode, con1, con2)).stream().sorted().toList().toString();
                    if (!visited.contains(dedupeKey) && graph.get(con1).contains(con2)) {
                        count++;
                        visited.add(dedupeKey);
                        if (DEBUG) {
                            System.out.printf("%s,%s,%s%n", tNode, con1, con2);
                        }
                    }
                }
            }
        }

        return count;
    }

    private Map<String, List<String>> buildGraph(List<String> lines) {
        Map<String, List<String>> graph = new HashMap<>();

        for (String line : lines) {
            String[] ary = line.split("-");
            String member1 = ary[0], member2 = ary[1];
            graph.computeIfAbsent(member1, k -> new ArrayList<>()).add(member2);
            graph.computeIfAbsent(member2, k -> new ArrayList<>()).add(member1);
        }

        return graph;
    }
}
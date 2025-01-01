package aoc24;

import aoc24.day23.BronKerbosch;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/23">Advent of Code 2024 Day 23</a>
 */
public class Day23Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day23_input.txt");

        String answer = new Day23Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        String expected = "ad,jw,kt,kz,mt,nc,nr,sb,so,tg,vs,wh,yh";
        if (!answer.equals(expected)) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private String execute(List<String> lines) {
        List<Set<String>> cliques = new BronKerbosch<>(buildGraph(lines)).findMaximalCliques();
        Set<String> largestClique = Collections.max(cliques, Comparator.comparingInt(Set::size));

        //return a comma delimited string representing largestClique sorted
        return String.join(",", largestClique.stream().sorted().toList());
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
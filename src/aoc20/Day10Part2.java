package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/10">Advent of Code 2020 Day 10</a>
 */
public class Day10Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day10_input.txt");

        long answer = new Day10Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 56693912375296L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Long> nums = new ArrayList<>(lines.stream().map(Long::parseLong).sorted().toList());
        nums.add(nums.get(nums.size() - 1) + 3); //add last node (always 3 greater than the greatest adapter)

        Map<LinkedList<Long>, Long> combosMap = new HashMap<>();
        combosMap.put(new LinkedList<>(List.of(0L)), 1L); //initial node

        for (long num : nums) {
            Map<LinkedList<Long>, Long> newCombosMap = new HashMap<>();
            for (Map.Entry<LinkedList<Long>, Long> entry: combosMap.entrySet()) {
                LinkedList<Long> combo = entry.getKey();
                long count = entry.getValue();
                addNumToCombo(newCombosMap, num, combo, count);
            }

            combosMap = newCombosMap;
        }

        return combosMap.values().stream().mapToLong(Long::longValue).sum();
    }

    private void addNumToCombo(Map<LinkedList<Long>, Long> newCombosMap, long num, LinkedList<Long> combo, long count) {
        /*
          try adding num as a suffix to each of the (up to) last 2 nodes in combo starting with the last one
             i.e. if combo consists of a,b,c and x is the num then we try a,b,c,x (trimming a), then
             a,b,x
         */
        int iterations = 1;
        for (int i = combo.size() - 1; i >= 0 && iterations <= 2; i--) {
            LinkedList<Long> newCombo = new LinkedList<>(List.of(num));

            for (int j = i; j >= 0; j--) {
                if (newCombo.size() >= 3 || newCombo.get(0) - 3 > combo.get(j)) {
                    break;
                }
                newCombo.addFirst(combo.get(j)); //add element to front of newCombo
            }
            if (newCombo.size() == 1) { //didn't add anything in previous iteration, no need to continue up the chain
                return;
            }

            newCombosMap.merge(newCombo, count, Long::sum);

            iterations++;
        }
    }
}

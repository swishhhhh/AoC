package aoc21;

import utils.ResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2021/day/14">Advent of Code 2021 Day 14</a>
 */
public class Day14Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day14_input.txt");

        long answer = execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 3816397135460L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        String template = lines.get(0);
        if (DEBUG) {
            System.out.printf("Original template: %s%n", template);
        }

        Map<String, String> insertionsMap = new HashMap<>();
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            insertionsMap.put(line.substring(0, 2), line.substring(line.length() - 1));
        }

        Map<String, Long> pairsCntMap = new HashMap<>();

        //count initial pairs for first step
        for (int i = 0; i < template.length() - 1; i++) {
            String pair = template.substring(i, i + 2);
            String insertionChar = insertionsMap.get(pair);
            assert insertionChar != null;

            addCount(pairsCntMap, pair.charAt(0) + insertionChar, 1L);
            addCount(pairsCntMap, insertionChar + pair.charAt(1), 1L);
        }
        if (DEBUG) {
            System.out.printf("After first step: %s%n", pairsCntMap);
        }

        //do subsequent (39) steps
        for (int step = 1; step < 40; step++) {
            Map<String, Long> newPairsCntMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : pairsCntMap.entrySet()) {
                String pair = entry.getKey();
                String insertionChar = insertionsMap.get(pair);
                assert insertionChar != null;

                addCount(newPairsCntMap, pair.charAt(0) + insertionChar, entry.getValue());
                addCount(newPairsCntMap, insertionChar + pair.charAt(1), entry.getValue());
            }

            pairsCntMap = newPairsCntMap;

            if (DEBUG) {
                System.out.printf("After step %s: %s%n", step + 1, pairsCntMap);
            }
        }

        return getResult(pairsCntMap, template);
    }


    private static Long getResult(Map<String, Long> pairsCntMap, String template) {
        Map<String, Long> frequencyMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : pairsCntMap.entrySet()) {
            String pair = entry.getKey();
            Long count = entry.getValue();

            //add count for both (1st and 2nd) chars in the pairs
            addCount(frequencyMap, pair.substring(0, 1), count);
            addCount(frequencyMap, pair.substring(1, 2), count);
        }

        //add 1 for both first and last chars of original template (since those are the only 2 that weren't double-counted)
        addCount(frequencyMap, template.substring(0, 1), 1L);
        addCount(frequencyMap, template.substring(template.length() - 1), 1L);

        //sort frequencies
        List<Long> frequencies = frequencyMap.values().stream().sorted().toList();
        long largest = frequencies.get(frequencies.size() - 1) / 2; //divide by 2 because of double-count
        long smallest = frequencies.get(0) / 2;

        return largest - smallest;
    }

    private static void addCount(Map<String, Long> pairsCntMap, String key, Long increment) {
        Long cnt = pairsCntMap.get(key);
        if (cnt == null) {
            cnt = 0L;
        }
        cnt+= increment;
        pairsCntMap.put(key, cnt);
    }
}

package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/9">Advent of Code 2020 Day 9</a>
 */
public class Day09Part1And2 {
    private static final int PREAMBLE_SIZE = 25;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day9_input.txt");

        Day09Part1And2 exec = new Day09Part1And2();
        long invalidNum = exec.findInvalidNumber(lines);
        System.out.printf("Part 1 (invalid number) = %s%n", invalidNum);

        long expected = 731031916;
        if (invalidNum != expected) {
            throw new RuntimeException(String.format("Invalid-number %s doesn't match expected %s", invalidNum, expected));
        }

        long answer = exec.execPart2(lines, invalidNum);
        System.out.printf("Part 2 answer = %s%n", answer);

        expected = 93396727;
        if (answer != expected) {
            throw new RuntimeException(String.format("Part 2 answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long findInvalidNumber(List<String> lines) {
        LinkedHashMap<Long, List<Long>> cache = new LinkedHashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            long n = Long.parseLong(lines.get(i));

            if (i >= PREAMBLE_SIZE) {
                //check if n is valid
                boolean valid = false;
                for (long key : cache.keySet()) {
                    List<Long> values = cache.get(key);
                    if (values.contains(n - key)) {
                        valid = true;
                        break;
                    }
                }

                if (!valid) {
                    return n;
                }
            }

            if (i > PREAMBLE_SIZE) {
                //remove old/expired cache entry
                cache.remove(cache.keySet().iterator().next());
            }

            //add n to each cache element
            for (Map.Entry<Long, List<Long>> entry : cache.entrySet()) {
                entry.getValue().add(n);
            }

            //add new cache element
            cache.put(n, new ArrayList<>());
        }

        throw new RuntimeException("Unable to solve part 1");
    }

    private long execPart2(List<String> lines, long part1Num) {
        LinkedList<Long> queue = new LinkedList<>();
        long sum = 0;

        for (String s : lines) {
            long n = Long.parseLong(s);
            sum += n;
            queue.add(n);

            while (sum >= part1Num) {
                if (sum == part1Num) {
                    return calculatePart2Answer(queue);
                }
                sum -= queue.removeFirst();
            }
        }

        throw new RuntimeException("Unable to solve part 2");
    }

    private long calculatePart2Answer(LinkedList<Long> queue) {
        Collections.sort(queue);
        return queue.getFirst() + queue.getLast();
    }
}

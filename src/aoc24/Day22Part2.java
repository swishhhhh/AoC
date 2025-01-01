package aoc24;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/22">Advent of Code 2024 Day 22</a>
 */
public class Day22Part2 {
    private static final boolean DEBUG = false;
    private static final int NUM_ITERATIONS = 2000;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day22_input.txt");

        long answer = new Day22Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2042;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        //key = sequence, value = array of values one array per input line
        Map<String, int[]> sequenceValues = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            Map<String, Integer> seqsToPricesMap = getSequencesToPriceChanges(Long.parseLong(lines.get(i)));

            for (Map.Entry<String, Integer> entry : seqsToPricesMap.entrySet()) {
                int[] values = sequenceValues.computeIfAbsent(entry.getKey(), k -> new int[lines.size()]);
                values[i] = entry.getValue();
            }
        }

        //find the sequence with maximum sum
        long bestSum = Long.MIN_VALUE;
        String bestSequence = null;

        for (Map.Entry<String, int[]> entry : sequenceValues.entrySet()) {
            long sum = Arrays.stream(entry.getValue()).sum();
            if (sum > bestSum) {
                bestSum = sum;
                bestSequence = entry.getKey();
            }
        }

        if (DEBUG) {
            System.out.printf("Best sequence: %s, sum: %s%n", bestSequence, bestSum);
        }

        return bestSum;
    }

    private Map<String, Integer> getSequencesToPriceChanges(long num) {
        Map<String, Integer> map = new HashMap<>();
        ArrayDeque<Integer> sequenceList = new ArrayDeque<>(4);
        StringBuilder sequenceBuilder = new StringBuilder(15);

        int lastDigit = (int) (num % 10);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            long next = next(num);
            int nextLastDigit = (int) (next % 10);
            int change = nextLastDigit - lastDigit;
            sequenceList.add(change);

            if (sequenceList.size() > 4) {
                sequenceList.removeFirst();
            }

            if (sequenceList.size() == 4) {
                sequenceBuilder.setLength(0); //reset
                sequenceList.forEach(s -> sequenceBuilder.append(',').append(s));
                map.putIfAbsent(sequenceBuilder.toString(), nextLastDigit);
            }

            num = next;
            lastDigit = nextLastDigit;
        }

        return map;
    }

    private long next(long num) {
        /*
         Each number evolves into the next secret number in the sequence via the following process:
            1) Calculate the result of multiplying the secret number by 64. Then, mix this result into the secret number.
               Finally, prune the secret number.
            2) Calculate the result of dividing the secret number by 32. Round the result down to the nearest integer.
               Then, mix this result into the secret number. Finally, prune the secret number.
            3) Calculate the result of multiplying the secret number by 2048. Then, mix this result into the secret number.
               Finally, prune the secret number.

         Each step of the above process involves mixing and pruning:
            - To mix a value into the secret number, calculate the bitwise XOR of the given value and the secret number.
               Then, the secret number becomes the result of that operation. (If the secret number is 42, and you were
               to mix 15 into the secret number, the secret number would become 37.)
            - To prune the secret number, calculate the value of the secret number modulo 16777216. Then, the secret
               number becomes the result of that operation. (If the secret number is 100000000, and you were to prune
               the secret number, the secret number would become 16113920.)
         */

        long answer = prune(mix(num << 6, num));  //multiply by 64
        answer = prune(mix(answer >> 5, answer)); //divide by 32
        return prune(mix(answer << 11, answer));  //multiply by 2048
    }

    private long mix(long num, long val) {
        return num ^ val;
    }

    private long prune(long num) {
        return num % 16777216;
    }
}
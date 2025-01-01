package aoc24;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/22">Advent of Code 2024 Day 22</a>
 */
public class Day22Part1 {
    private static final boolean DEBUG = false;
    private static final int NUM_ITERATIONS = 2000;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day22_input.txt");

        long answer = new Day22Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 17960270302L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long sum = 0;

        for (String line : lines) {
            long num = Long.parseLong(line);
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                num = next(num);
            }
            
            sum += num;

            if (DEBUG) {
                System.out.printf("line=%s, num=%s%n", line, num);
            }
        }

        return sum;
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
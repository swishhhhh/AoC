package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/13">Advent of Code 2020 Day 13</a>
 */
public class Day13Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day13_input.txt");

        long answer = new Day13Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 404517869995362L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long answer = 0;
        long increment = -1;
        String[] ary = lines.get(1).split(",");
        for (int i = 0; i < ary.length; i++) {
            if (Helper.isNumeric(ary[i])) {
                long num = Long.parseLong(ary[i]);
                if (increment == -1) {
                    increment = num; //initialize first time
                    continue;
                }

                while (true) {
                    answer += increment;
                    if ((answer + i) % num == 0) {
                        increment *= num;
                        break;
                    }
                }
            }
        }

        return answer;
    }
}

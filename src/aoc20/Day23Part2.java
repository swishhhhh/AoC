package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/23">Advent of Code 2020 Day 23</a>
 */
public class Day23Part2 {
    private static final int NUMBER_OF_CUPS = 1_000_000;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day23_input.txt");

        long answer = new Day23Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 90481418730L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        int[] cups = loadCups(lines.get(0));
        /*
          Each element in the cups array represents an edge between 2 cups. The source/left cup is represented by the
          index, and the destination/right cup is represented by the value at that index.
          For example, if cups[3] = 8 -> then cup 3 is pointing to cup 8.
         */

        //initialize to first cup
        int currentCup = Character.getNumericValue(lines.get(0).charAt(0));

        for (int i = 1; i <= 10 * NUMBER_OF_CUPS; i++) {
            currentCup = playRound(cups, currentCup);
        }

        //get the 2 cups immediately following the cup labeled 1 and return the product of their labels
        int firstCup = cups[1];
        int secondCup = cups[firstCup];
        return (long) firstCup * secondCup;
    }

    private int[] loadCups(String input) {
        int[] cups = new int[NUMBER_OF_CUPS + 1];

        int firstCup = Character.getNumericValue(input.charAt(0));

        int source, target = 0;
        // Process initial input string
        for (int i = 0; i < input.length() - 1; i++) {
            source = Character.getNumericValue(input.charAt(i));
            target = Character.getNumericValue(input.charAt(i + 1));
            cups[source] = target;
        }

        //link last cup of initial input to first cup of the rest of the input
        cups[target] = input.length() + 1;

        //rest of the cups...
        for (int i = input.length() + 1; i < NUMBER_OF_CUPS; i++) {
            cups[i] = i + 1;
        }

        //link last cup back around to first cup to complete the circle
        cups[NUMBER_OF_CUPS] = firstCup;

        return cups;
    }

    private int playRound(int[] cups, int currentCup) {
        int cupA = cups[currentCup];
        int cupB = cups[cupA];
        int cupC = cups[cupB];

        int destinationCup = currentCup;
        do {
            destinationCup = (destinationCup > 1) ? destinationCup - 1 : NUMBER_OF_CUPS;
        } while (destinationCup == cupA || destinationCup == cupB || destinationCup == cupC);

        // Rearrange cups with minimal operations
        cups[currentCup] = cups[cupC];     //connect current cup to the one after C
        cups[cupC] = cups[destinationCup]; //connect cup C to what was after the destination cup
        cups[destinationCup] = cupA;       //connect the destination cup to cup A

        return cups[currentCup]; //next cup
    }
}

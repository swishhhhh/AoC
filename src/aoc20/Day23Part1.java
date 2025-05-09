package aoc20;

import utils.ResourceLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/23">Advent of Code 2020 Day 23</a>
 */
public class Day23Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day23_input.txt");

        long answer = new Day23Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 36472598;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        LinkedList<Integer> cups = loadCups(lines.get(0));
        Integer currentCup = cups.getFirst();

        for (int i = 1; i <= 100; i++) {
            currentCup = playRound(cups, currentCup);
        }

        // starting after the cup labeled 1, collect the other cups' labels clockwise into a single string
        LinkedList<Integer> orderedCups = selectCups(cups, 1, cups.size() - 1);
        StringBuilder sb = new StringBuilder();
        for (Integer cup : orderedCups) {
            sb.append(cup);
        }

        return Long.parseLong(sb.toString());
    }

    private LinkedList<Integer> loadCups(String input) {
        LinkedList<Integer> cups = new LinkedList<>();
        for (char c : input.toCharArray()) {
            cups.add(Integer.parseInt("" + c));
        }
        return cups;
    }

    private Integer playRound(LinkedList<Integer> cups, Integer currentCup) {
        //pick up the three cups that are immediately clockwise of the current cup
        LinkedList<Integer> selected3 = selectCups(cups, currentCup, 3);

        //remove the 3 selected cups from the main list
        for (Integer cup : selected3) {
            cups.remove(cup);
        }

        //place the selected 3 cups immediately after the destination cup. Keep the same order they were picked up in.
        int index = cups.indexOf(getDestinationCup(cups, currentCup));
        cups.addAll(index + 1, selected3);

        //select a new current cup: the cup which is immediately after the current cup.
        return selectCups(cups, currentCup, 1).getFirst();
    }

    private LinkedList<Integer> selectCups(LinkedList<Integer> cups, Integer currentCup, int numberOfCupsToSelect) {
        LinkedList<Integer> selected = new LinkedList<>();
        int index = cups.indexOf(currentCup);
        for (int i = 0; i < numberOfCupsToSelect; i++) {
            index = (index + 1) % cups.size();
            selected.add(cups.get(index));
        }

        return selected;
    }

    private static int getDestinationCup(LinkedList<Integer> cups, Integer currentCup) {
        /*
           Select a destination cup: the cup with a label equal to the current cup's label minus one.
            If this results in a number/label that isn't present in the cups list, keep subtracting 1 until you find a
            label that is present in the list. If at any point in this process the value goes below 1, wrap around to
            the highest number/label (9).
         */
        int destinationCup = currentCup - 1;
        while (!cups.contains(destinationCup)) {
            destinationCup--;
            if (destinationCup < 1) {
                destinationCup = 9;
            }
        }
        return destinationCup;
    }
}

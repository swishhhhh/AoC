package aoc20;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/23">Advent of Code 2020 Day 23</a>
 */
public class Day23Part2 {
    private static class Cup {
        int label;
        Cup next;
        Cup prev;

        public Cup(int label) {
            this.label = label;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cup cup)) return false;

            return label == cup.label;
        }

        @Override
        public int hashCode() {
            return label;
        }

        @Override
        public String toString() {
            return "Cup{" +
                    "label=" + label +
                    '}';
        }
    }

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
        Map<Integer, Cup> cups = loadCups(lines.get(0));
        Cup currentCup = cups.values().stream().findFirst().orElse(null);

        for (int i = 1; i <= 10_000_000; i++) {
            currentCup = playRound(cups, currentCup);
        }

        //get the 2 cups immediately following the cup labeled 1 and return the product of their labels
        Cup firstCup = cups.get(1).next;
        Cup secondCup = firstCup.next;
        return (long) firstCup.label * secondCup.label;
    }

    private Map<Integer, Cup> loadCups(String input) {
        Map<Integer, Cup> cups = new LinkedHashMap<>();
        // Initialize first cup separately to avoid null checks in loop
        int firstLabel = Character.getNumericValue(input.charAt(0));
        Cup firstCup = new Cup(firstLabel);
        cups.put(firstLabel, firstCup);
        Cup previousCup = firstCup;

        // Process initial input string
        for (int i = 1; i < input.length(); i++) {
            int label = Character.getNumericValue(input.charAt(i));
            Cup newCup = new Cup(label);
            previousCup.next = newCup;
            newCup.prev = previousCup;
            cups.put(label, newCup);
            previousCup = newCup;
        }

        for (int i = input.length() + 1; i <= 1_000_000; i++) {
            Cup newCup = new Cup(i);
            previousCup.next = newCup;
            newCup.prev = previousCup;
            cups.put(newCup.label, newCup);
            previousCup = newCup;
        }

        //link last cup back around to first cup to complete the circle
        previousCup.next = firstCup;
        firstCup.prev = previousCup;

        return cups;
    }

    private Cup playRound(Map<Integer, Cup> cups, Cup currentCup) {
        //pick up the three cups that are immediately clockwise of the current cup
        List<Cup> selected3 = selectNext3Cups(currentCup);

        //remove the 3 selected cups from the main list
        for (Cup cup : selected3) {
            removeCup(cup, cups);
        }

        //place the selected 3 cups immediately after the destination cup. Keep the same order they were picked up in.
        Cup destinationCup = getDestinationCup(cups, currentCup);
        for (Cup cup : selected3) {
            cup.prev = destinationCup;
            cup.next = destinationCup.next;
            destinationCup.next.prev = cup;
            destinationCup.next = cup;
            cups.put(cup.label, cup);
            destinationCup = cup;
        }

        //select a new current cup: the cup which is immediately after the current cup.
        return currentCup.next;
    }

    private void removeCup(Cup cup, Map<Integer, Cup> cups) {
        cup.prev.next = cup.next;
        cup.next.prev = cup.prev;
        cup.next = null;
        cup.prev = null;
        cups.remove(cup.label);
    }

    private List<Cup> selectNext3Cups(Cup currentCup) {
        List<Cup> selected = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            currentCup = currentCup.next;
            selected.add(currentCup);
        }

        return selected;
    }

    private static Cup getDestinationCup(Map<Integer, Cup> cups, Cup currentCup) {
        /*
           Select a destination cup: the cup with a label equal to the current cup's label minus one.
            If this results in a number/label that isn't present in the cups maps, keep subtracting 1 until you find a
            label that is present in the map. If at any point in this process the value goes below 1, wrap around to
            the highest number/label (1_000_000).
         */
        int destinationCupLabel = currentCup.label - 1;
        while (!cups.containsKey(destinationCupLabel)) {
            destinationCupLabel--;
            if (destinationCupLabel < 1) {
                destinationCupLabel = 1_000_000;
            }
        }
        return cups.get(destinationCupLabel);
    }
}

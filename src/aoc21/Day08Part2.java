package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2021/day/8">Advent of Code 2021 Day 8</a>
 */
public class Day08Part2 {

    public static void main(String... args) throws Exception {
        String resourceName = "aoc21/Day8_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        long answer = 0;
        for (String line : lines) {
            answer+= getLineValue(line);
        }

        System.out.printf("Number of selected digits %s%n", answer);

        long expected = 983030;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static long getLineValue(String line) {
        Map<String, Integer> signatureToDigitsMap = new HashMap<>();
        Map<Integer, List<Character>> digitsToCharGroupsMap = new HashMap<>();
        List<List<Character>> fiveSegmentGroups = new ArrayList<>();
        List<List<Character>> sixSegmentGroups = new ArrayList<>();

        String[] ary = line.split("\\|");
        for (String s : ary[0].trim().split(" ")) {
            List<Character> group = Helper.charArrayToList(s.toCharArray());
            String signature = getGroupSignature(group);

            // identify the easy digits {1,4,7,8} based on their unique segments count
            // and collect the 5 and 6 segment groups into separate buckets
            switch (s.length()) {
                case 2 -> { // digital 1
                    signatureToDigitsMap.put(signature, 1);
                    digitsToCharGroupsMap.put(1, group);
                }
                case 3 -> { // digital 7
                    signatureToDigitsMap.put(signature, 7);
                    digitsToCharGroupsMap.put(7, group);
                }
                case 4 -> { // digital 4
                    signatureToDigitsMap.put(signature, 4);
                    digitsToCharGroupsMap.put(4, group);
                }
                case 5 -> { // digital 2, 3 or 5
                    fiveSegmentGroups.add(group);
                }
                case 6 -> { // digital 0, 6 or 9
                    sixSegmentGroups.add(group);
                }
                case 7 -> { // digital 8
                    signatureToDigitsMap.put(signature, 8);
                    digitsToCharGroupsMap.put(8, group);
                }
            }
        }

        Collection<Character> one = getCloneOfGroup(digitsToCharGroupsMap, 1);
        Collection<Character> four = getCloneOfGroup(digitsToCharGroupsMap, 4);
        Collection<Character> seven = getCloneOfGroup(digitsToCharGroupsMap, 7);
        Collection<Character> lShapedDigit = getCloneOfGroup(digitsToCharGroupsMap, 8);
        lShapedDigit.removeAll(four);
        lShapedDigit.removeAll(seven);
        //at this point lShapedDigit is left with 2 segments: lower left vertical and bottom horizontal

        //decode the sixSegmentGroups
        for (List<Character> sixGroup : sixSegmentGroups) {
            List<Character> tmp = new ArrayList<>(lShapedDigit);
            String signature = getGroupSignature(sixGroup);

            tmp.removeAll(sixGroup); //segment is 0, 6 or 9
            if (tmp.size() == 1) { //segment is 9
                signatureToDigitsMap.put(signature, 9);
                digitsToCharGroupsMap.put(9, sixGroup);

            } else { //tmp.size() == 0 -> segment is 0 or 6
                tmp = getCloneOfGroup(digitsToCharGroupsMap, 8);
                tmp.removeAll(sixGroup);
                //at this point tmp is left with 1 segment: either middle horizontal or upper right vertical
                tmp.removeAll(one);
                if (tmp.size() == 0) { //segment is 6
                    signatureToDigitsMap.put(signature, 6);
                    digitsToCharGroupsMap.put(6, sixGroup);

                } else { //tmp.size() == 1 -> segment is 0
                    signatureToDigitsMap.put(signature, 0);
                    digitsToCharGroupsMap.put(0, sixGroup);
                }
            }
        }

        //decode the fiveSegmentGroups
        for (List<Character> fiveGroup : fiveSegmentGroups) {
            List<Character> tmp = new ArrayList<>(lShapedDigit);
            String signature = getGroupSignature(fiveGroup);

            tmp.removeAll(fiveGroup); //segment is 2, 3 or 5
            if (tmp.size() == 0) { //segment is 2
                signatureToDigitsMap.put(signature, 2);
                digitsToCharGroupsMap.put(2, fiveGroup);

            } else { //tmp.size() == 1 -> segment is 3 or 5
                tmp = getCloneOfGroup(digitsToCharGroupsMap, 8);
                Collection<Character> six = getCloneOfGroup(digitsToCharGroupsMap, 6);
                tmp.removeAll(six); //at this point tmp is left with only the upper right vertical segment
                tmp.removeAll(fiveGroup);
                if (tmp.size() == 0) { //segment is 3
                    signatureToDigitsMap.put(signature, 3);
                    digitsToCharGroupsMap.put(3, fiveGroup);

                } else { //tmp.size() == 1 -> segment is 5
                    signatureToDigitsMap.put(signature, 5);
                    digitsToCharGroupsMap.put(5, fiveGroup);
                }
            }
        }

        //all digits identified, time to decode the 2nd half of the line
        StringBuilder sb = new StringBuilder();
        for (String s : ary[1].trim().split(" ")) {
            List<Character> group = Helper.charArrayToList(s.toCharArray());
            String signature = getGroupSignature(group);
            sb.append(signatureToDigitsMap.get(signature));
        }

        return Integer.parseInt(sb.toString());
    }

    private static List<Character> getCloneOfGroup(Map<Integer, List<Character>> digitsToCharGroupsMap, int digit) {
        return new ArrayList<>(digitsToCharGroupsMap.get(digit));
    }

    private static String getGroupSignature(List<Character> group) {
        Collections.sort(group);
        return group.toString();
    }
}

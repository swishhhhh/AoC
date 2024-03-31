package aoc21;

import utils.ResourceLoader;

import java.util.List;

import static utils.Helper.isDigit;
import static utils.Helper.isNumeric;

/**
 * <a href="https://adventofcode.com/2021/day/18">Advent of Code 2021 Day 18</a>
 */
public class Day18Part1 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day18_input.txt");

        long answer = execute(lines);

        System.out.printf("Answer = %s%n", answer);

        long expected = 4120;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s%n", answer, expected));
        }
    }

    private static long execute(List<String> lines) {
        String result = lines.get(0);
        for (int i = 1; i < lines.size(); i++) {
            result = add(result, lines.get(i));
        }

        return getMagnitude(result);
    }

    private static String add(String left, String right) {
        //addition
        String result = "[" + left + "," + right + "]";

        //reduction
        while (true) {
            String s = explode(result); //attempt explosion
            if (!s.equals(result)) { //successful explosion
                result = s;
                continue;
            }

            result = s;
            s = split(result);        //attempt split
            if (!s.equals(result)) { //successful split
                result = s;
                continue;
            }

            break; //no explosions or splits left, reduction complete
        }

        if (DEBUG) {
            System.out.printf("Adding %s and %s = %s%n", left, right, result);
        }

        return result;
    }

    private static String explode(String input) {
        /*
          If any pair is nested inside four pairs, the leftmost such pair explodes.

          To explode a pair, the pair's left value is added to the first regular number to the left of the exploding
          pair (if any), and the pair's right value is added to the first regular number to the right of the exploding
          pair (if any). Exploding pairs will always consist of two regular numbers. Then, the entire exploding pair is
          replaced with the regular number 0.

          For example:
          - [[[[[9,8],1],2],3],4] becomes [[[[0,9],2],3],4] (the 9 has no regular number to its left, so it is not added
            to any regular number).
          - [7,[6,[5,[4,[3,2]]]]] becomes [7,[6,[5,[7,0]]]] (the 2 has no regular number to its right, and so it is
            not added to any regular number).
         */

        int nestedCtr = 0;
        for (int idx = 0; idx < input.length(); idx++) {
            char c = input.charAt(idx);
            if (c == '[') {
                nestedCtr++;
            } else if (c == ']') {
                nestedCtr--;
            }

            if (nestedCtr <= 4) {
                continue;
            }

            //get left number (should immediately follow the open brace)
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                i++;
                c = input.charAt(idx + i);
                if (isDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            int leftValue = Integer.parseInt(sb.toString());

            assert c == ',';

            //get right number (should immediately follow the comma)
            sb = new StringBuilder();
            while (true) {
                i++;
                c = input.charAt(idx + i);
                if (isDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            int rightValue = Integer.parseInt(sb.toString());

            String leftPartOfString = input.substring(0, idx);
            String rightPartOfString = input.substring(idx + i + 1);

            //the pair's left value is added to the first regular number to the left of the exploding pair (if any)
            StringBuilder numToLeftAsString = new StringBuilder();
            for (int j = leftPartOfString.length() - 1; j >= 0; j--) {
                c = leftPartOfString.charAt(j);
                if (isDigit(c)) {
                    numToLeftAsString.insert(0, c);
                } else if (numToLeftAsString.length() > 0) {
                    int numToLeft = Integer.parseInt(numToLeftAsString.toString()) + leftValue;
                    leftPartOfString = leftPartOfString.substring(0, j + 1) + numToLeft +
                            leftPartOfString.substring(j + 1 + numToLeftAsString.length());
                    break;
                }
            }

            //the pair's right value is added to the first regular number to the right of the exploding pair (if any)
            StringBuilder numToRightAsString = new StringBuilder();
            for (int j = 0; j < rightPartOfString.length(); j++) {
                c = rightPartOfString.charAt(j);
                if (isDigit(c)) {
                    numToRightAsString.append(c);
                } else if (numToRightAsString.length() > 0) {
                    int numToRight = Integer.parseInt(numToRightAsString.toString()) + rightValue;
                    rightPartOfString = rightPartOfString.substring(0, j - numToRightAsString.length()) + numToRight +
                            rightPartOfString.substring(j);
                    break;
                }
            }

            return leftPartOfString + "0" + rightPartOfString;
        }

        return input; //if the above for loop completed then nothing to explode
    }

    private static String split(String input) {
        /*
          If any regular number is 10 or greater, the leftmost such regular number splits.

          To split a regular number, replace it with a pair; the left element of the pair should be the regular number
          divided by two and rounded down, while the right element of the pair should be the regular number divided by
          two and rounded up. For example, 10 becomes [5,5], 11 becomes [5,6], 12 becomes [6,6], and so on.
         */

        StringBuilder sb = new StringBuilder();
        StringBuilder digits = new StringBuilder();
        for (int idx = 0; idx < input.length(); idx++) {
            char c = input.charAt(idx);
            if (isDigit(c)) {
                digits.append(c);
            } else if (digits.length() > 1) {
                //split
                int num = Integer.parseInt(digits.toString());
                String newPair = "[" + (num / 2) + "," + (int) Math.ceil((double) num / 2) + "]";
                sb.append(newPair);

                //only the first (leftmost) number is split for each call to this method, so add the remainder of the input string and exit
                sb.append(input.substring(idx));
                break;
            } else {
                if (digits.length() == 1) {
                    sb.append(digits);
                    digits = new StringBuilder();
                }
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static long getMagnitude(String input) {
        /*
          The magnitude of a pair is 3 times the magnitude of its left element plus 2 times the magnitude of its
          right element. The magnitude of a regular number is just that number.

          For example, the magnitude of [9,1] is 3*9 + 2*1 = 29; the magnitude of [1,9] is 3*1 + 2*9 = 21.
          Magnitude calculations are recursive: the magnitude of [[9,1],[1,9]] is 3*29 + 2*21 = 129.
         */

        assert input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']';

        int nestedCtr = 0;
        int idx = 0;
        while (true) {
            //find first comma in input string that is nested only 1 layer deep; that's the pivot/split point
            char c = input.charAt(idx);
            if (c == '[') {
                nestedCtr++;
            } else if (c == ']') {
                nestedCtr--;
            } else if (c == ',' && nestedCtr == 1) {
                break;
            }

            idx++;
        }

        String leftStr = input.substring(1, idx);
        long leftVal = isNumeric(leftStr) ? Long.parseLong(leftStr) : getMagnitude(leftStr);

        String rightStr = input.substring(idx + 1, input.length() - 1);
        long rightVal = isNumeric(rightStr) ? Long.parseLong(rightStr) : getMagnitude(rightStr);

        return (3 * leftVal) + (2 * rightVal);
    }
}

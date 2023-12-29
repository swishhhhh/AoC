package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    /**
     * example usage:
     *      LinkedHashSet<Character> set = Helper.convertCharArrayToSet(s.toCharArray());
     * 		set.forEach(c -> System.out.print(c + ","));
     */
    public static HashSet<Character> convertCharArrayToSet(char[] charArray) {
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        for (char c: charArray) {
            set.add(c);
        }
        return set;
    }

    /**
     * example usage:
     *      LinkedHashSet<String> set = Helper.convertStringArrayToSet(new String[]{"abc", "xyz", "123"});
     * 		set.forEach(s -> System.out.println(s));
     */
    public static LinkedHashSet<String> convertStringArrayToSet(String[] array) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        Collections.addAll(set, array);
        return set;
    }

    /**
     * example usage:
     *      LinkedHashSet<Integer> set = Helper.convertIntArrayToSet(new int[]{1, 2, 3});
     * 		set.forEach(i -> System.out.println(i));
     */
    public static LinkedHashSet<Integer> convertIntArrayToSet(int[] array) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        for (int i: array) {
            set.add(i);
        }
        return set;
    }

    public static Character[] unboxCharArray(char[] ary) {
        Character[] returnAry = new Character[ary.length];
        for (int i = 0; i < ary.length; i++) {
            returnAry[i] = ary[i];
        }
        return returnAry;
    }

    /**
     * example usage:
     *      String s = "xyz";
     * 		List<Character> list = Helper.charArrayToList(s.toCharArray());
     * 		list.forEach(c -> System.out.println(c));
     */
    public static List<Character> charArrayToList(char[] ary) {
        return Arrays.asList(unboxCharArray(ary));
    }

    /**
     * example usage:
     *      String text = "Hello, my name is Joe, I'm 25 years old. I live at 123 Main st, Lakewood NJ, 08701.";
     * 		List<Integer> ints = Helper.extractIntsFromText(text);
     * 		ints.forEach(i -> System.out.println(i));
     * 	// prints
     * 	    25
     *      17
     *      8701
     */
    public static List<Integer> extractIntsFromText(String text, boolean withMinusSign) {
        String pattern = withMinusSign ? "-?\\d+" : "\\d+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        List<Integer> list = new ArrayList<>();
        while(m.find()) {
            list.add(Integer.parseInt(m.group()));
        }
        return list;
    }

    public static List<Integer> extractIntsFromText(String text) {
        return extractIntsFromText(text, false);
    }

    public static List<Long> extractLongsFromText(String text, boolean withMinusSign) {
        String pattern = withMinusSign ? "-?\\d+" : "\\d+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        List<Long> list = new ArrayList<>();
        while(m.find()) {
            list.add(Long.parseLong(m.group()));
        }
        return list;
    }

    public static List<Long> extractLongsFromText(String text) {
        return extractLongsFromText(text, false);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * fills a 2D array with a char
     * @param ary 2D-array to fill
     * @param c character to fill every cell with
     */
    public static void fillCharArray2D(char[][] ary, char c) {
        for (char[] chars : ary) {
            Arrays.fill(chars, c);
        }
    }

    public static void printArray2D(char[][] ary) {
        for (char[] outer: ary) {
            for (char c: outer) {
                System.out.print(c);
            }
            System.out.println();
        }
    }

    public static long greatestCommonDenominator(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    public static long greatestCommonDenominator(Long[] input) {
        return greatestCommonDenominator(Arrays.asList(input));
    }

    public static long greatestCommonDenominator(List<Long> input) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            result = greatestCommonDenominator(result, input.get(i));
        }
        return result;
    }

    public static long lowestCommonMultiplier(long a, long b) {
        return a * (b / greatestCommonDenominator(a, b));
    }

    public static long lowestCommonMultiplier(Long[] input) {
        return lowestCommonMultiplier(Arrays.asList(input));
    }

    public static long lowestCommonMultiplier(List<Long> input) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            result = lowestCommonMultiplier(result, input.get(i));
        }
        return result;
    }

    public static int countChars(String input, char charToCount) {
        return input.length() - input.replaceAll("[" + charToCount + "]", "").length();
    }
}

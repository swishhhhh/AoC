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
     * 		List<Integer> ints = Helper.exractIntsFromText(text);
     * 		ints.forEach(i -> System.out.println(i));
     * 	// prints
     * 	    25
     *      17
     *      8701
     */
    public static List<Integer> exractIntsFromText(String text) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(text);
        List<Integer> list = new ArrayList<>();
        while(m.find()) {
            list.add(Integer.parseInt(m.group()));
        }
        return list;
    }
}

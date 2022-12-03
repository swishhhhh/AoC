package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3Part1 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day3_input.txt");

		int total = 0;

		for (String line: lines) {
			int len = line.length();
			String firstHalf = line.substring(0, len / 2);

			char[] lineChars = firstHalf.toCharArray();
			Set<Character> charSet = new HashSet<>();
			for (char c: lineChars) {
				charSet.add(c);
			}

			//2nd half
			char foundChar = '0';
			for (int i = len / 2; i < len; i++) {
				char c = line.charAt(i);
				if (charSet.contains(c)) {
					foundChar = c;
					break;
				}
			}

			if (Character.isUpperCase(foundChar)) {
				total+= (foundChar - 38); //'A' should equal 27, ascii 'A' = 65 (diff of 38)
			} else {
				total+= (foundChar - 96); //'a' should equal 1, ascii 'a' = 97 (diff of 96)
			}
		}

		System.out.printf("Total = %s%n", total);
	}
}

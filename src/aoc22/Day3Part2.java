package aoc22;

import utils.ResourceLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3Part2 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day3_input.txt");

		int total = 0;

		String firstLine, secondLine, thirdLine;
		for (int i = 0; i < lines.size(); i += 3) {
			firstLine = lines.get(i);
			secondLine = lines.get(i + 1);
			thirdLine = lines.get(i + 2);

			Set<Character> charSet1 = new HashSet<>();
			for (char c : firstLine.toCharArray()) {
				charSet1.add(c);
			}

			Set<Character> charSet2 = new HashSet<>();
			for (char c : secondLine.toCharArray()) {
				if (charSet1.contains(c)) {
					charSet2.add(c);
				}
			}

			Set<Character> charSet3 = new HashSet<>();
			for (char c : thirdLine.toCharArray()) {
				if (charSet2.contains(c)) {
					charSet3.add(c);
				}
			}

			Character foundChar = (Character) charSet3.toArray()[0];
			if (Character.isUpperCase(foundChar)) {
				total += (foundChar - 38); //'A' should equal 27, ascii 'A' = 65 (diff of 38)
			} else {
				total += (foundChar - 96); //'a' should equal 1, ascii 'a' = 97 (diff of 96)
			}
		}

		System.out.printf("Total = %s%n", total);
	}
}

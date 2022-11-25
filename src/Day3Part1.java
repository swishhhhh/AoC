import utils.ResourceLoader;

import java.util.List;

public class Day3Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("Day3Part1_input.txt");

        int EXPECTED_LINE_LENGTH = 12;
        int[] bitCounters = new int[EXPECTED_LINE_LENGTH]; //increment each counter for 1s and decrement for 0s

        int lineCtr = 0;
        for (String line: lines) {
            lineCtr++;
            char[] lineChars = line.toCharArray();
            if (lineChars.length != EXPECTED_LINE_LENGTH) {
                throw new IllegalArgumentException(String.format("Line length != %s (line number %s%n) ", EXPECTED_LINE_LENGTH, lineCtr));
            }

            int idx = 0;
            for (char c: line.toCharArray()) {
                if (c == '0') {
                    bitCounters[idx]--;
                } else {
                    bitCounters[idx]++;
                }
                idx++;
            }
        }

        StringBuilder gamma = new StringBuilder();
        StringBuilder epsilon = new StringBuilder();
        for (int bitCounter : bitCounters) {
            if (bitCounter > 0) {
                gamma.append("1");
                epsilon.append("0");
            } else {
                gamma.append("0");
                epsilon.append("1");
            }
        }

        int gammaN = Integer.parseInt(gamma.toString(), 2);
        int epsilonN = Integer.parseInt(epsilon.toString(), 2);
        System.out.printf("gamma = %s, epsilon = %s, product = %s%n", gammaN, epsilonN, gammaN * epsilonN);
    }
}

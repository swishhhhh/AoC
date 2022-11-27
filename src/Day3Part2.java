import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

public class Day3Part2 {
    public static void main(String... args) throws Exception {
        String resourceName = "Day3Part2_input.txt";
        List<String> lines = ResourceLoader.readStrings(resourceName);

        int EXPECTED_LINE_LENGTH = 12;

        //oxygen generator rating
        for (int i = 0; i < EXPECTED_LINE_LENGTH; i++) {
            List<String> ones = new ArrayList<>();
            List<String> zeros = new ArrayList<>();

            splitOnesAndZeros(lines, i, ones, zeros);

            if (ones.size() >= zeros.size()) lines = ones;
            else lines = zeros;

            if (lines.size() <= 1) {
                break;
            }
        }

        int oxyRating = Integer.parseInt(lines.get(0), 2);

        //------------
        //CO2 scrubber rating
        lines = ResourceLoader.readStrings(resourceName);
        for (int i = 0; i < EXPECTED_LINE_LENGTH; i++) {
            List<String> ones = new ArrayList<>();
            List<String> zeros = new ArrayList<>();

            splitOnesAndZeros(lines, i, ones, zeros);

            if (zeros.size() <= ones.size()) lines = zeros;
            else lines = ones;

            if (lines.size() <= 1) {
                break;
            }
        }

        int co2Rating = Integer.parseInt(lines.get(0), 2);

        //------------
        System.out.printf("oxy rating = %s, co2 rating = %s, product = %s%n", oxyRating, co2Rating, oxyRating * co2Rating);
    }

    private static void splitOnesAndZeros (List<String> lines, final int idx, List<String> ones, List<String> zeros) {
        lines.forEach(line -> {
            if (line.charAt(idx) == '1') ones.add(line);
            else zeros.add(line);
        });
    }
}

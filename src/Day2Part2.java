import utils.ResourceLoader;

import java.util.List;

public class Day2Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("Day2Part2_input.txt");

        int vertical = 0;
        int horizontal = 0;
        int aim = 0;
        String direction;
        int unit;

        for (String line: lines) {
            String[] ary = line.split(" ");
            direction = ary[0];
            unit = Integer.parseInt(ary[1]);
            switch (direction) {
                case "forward" -> {
                    horizontal += unit;
                    vertical += (aim * unit);
                }
                case "up" -> aim -= unit;
                case "down" -> aim += unit;
                default -> throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        }

        System.out.printf("horizontal = %s, vertical = %s, product = %s%n", horizontal, vertical, horizontal * vertical);
    }
}

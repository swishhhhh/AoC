package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/17">Advent of Code 2021 Day 17</a>
 */
public class Day17Part2 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day17_input.txt");

        List<Integer> nums = Helper.extractIntsFromText(lines.get(0), true);
        int leftEdgeOfTargetZone = nums.get(0);
        int rightEdgeOfTargetZone = nums.get(1);
        int bottomOfTargetZone = nums.get(2);
        int topOfTargetZone = nums.get(3);

        int minVelocityX = 1;
        int maxVelocityX = rightEdgeOfTargetZone + 1;
        int minVelocityY = bottomOfTargetZone;
        int maxVelocityY = Math.abs(bottomOfTargetZone);

        long answer = 0L;
        for (int x = minVelocityX; x <= maxVelocityX; x++) {
            for (int y = minVelocityY; y <= maxVelocityY; y++) {
                if (reachesTargetZone(x, y, leftEdgeOfTargetZone, rightEdgeOfTargetZone, bottomOfTargetZone, topOfTargetZone)) {
                    answer++;
                }
            }
        }

        System.out.printf("Total distinct velocity values = %s%n", answer);

        long expected = 4433;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private static boolean reachesTargetZone(int initialVelocityX, int initialVelocityY,
                                             int leftEdgeOfZone, int rightEdgeOfZone, int bottomOfZone, int topOfZone) {
        int x = 0, y = 0;
        int xVel = initialVelocityX;
        int yVel = initialVelocityY;

        while (x <= rightEdgeOfZone && y >= bottomOfZone) {
            x += xVel;
            y += yVel;

            if (x >= leftEdgeOfZone && x <= rightEdgeOfZone && y <= topOfZone && y >= bottomOfZone) {
                if (DEBUG) {
                    System.out.printf("Velocity pair {%s, %s} found, in-zone target hit at point {%s, %s}%n",
                            initialVelocityX, initialVelocityY, x, y);
                }
                return true;
            }

            if (xVel > 0) {
                xVel -= 1;
            }
            yVel -= 1;
        }

        return false;
    }
}

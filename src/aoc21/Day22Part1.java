package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/22">Advent of Code 2021 Day 22</a>
 */
public class Day22Part1 {
    private int xShift = Integer.MAX_VALUE;
    private int yShift = Integer.MAX_VALUE;
    private int zShift = Integer.MAX_VALUE;
    private boolean[][][] grid;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day22_input.txt");

        long answer = new Day22Part1().execute(lines);
        System.out.printf("Number of cubes on = %s%n", answer);

        long expected = 600458;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        setup(lines);
        applySteps(lines);
        return countCellsThatAreOn();
    }

    private long countCellsThatAreOn() {
        long count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                for (int k = 0; k < grid[0][0].length; k++) {
                    if (grid[i][j][k]) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private void applySteps(List<String> lines) {
        for (String line : lines) {
            List<Integer> nums = Helper.extractIntsFromText(line, true);
            if (Math.abs(nums.get(0)) > 50) {
                continue;
            }

            int x1 = nums.get(0), x2 = nums.get(1), y1 = nums.get(2), y2 = nums.get(3), z1 = nums.get(4), z2 = nums.get(5);
            boolean onOrOff = line.startsWith("on ");

            for (int i = x1 - this.xShift; i <= x2 - this.xShift; i++) {
                for (int j = y1 - this.yShift; j <= y2 - this.yShift; j++) {
                    for (int k = z1 - this.zShift; k <= z2 - this.zShift; k++) {
                        this.grid[i][j][k] = onOrOff;
                    }
                }
            }
        }
    }

    private void setup(List<String> lines) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (String line : lines) {
            List<Integer> nums = Helper.extractIntsFromText(line, true);
            if (Math.abs(nums.get(0)) > 50) {
                continue;
            }

            minX = Math.min(minX, nums.get(0));
            maxX = Math.max(maxX, nums.get(1));
            minY = Math.min(minY, nums.get(2));
            maxY = Math.max(maxY, nums.get(3));
            minZ = Math.min(minZ, nums.get(4));
            maxZ = Math.max(maxZ, nums.get(5));
        }

        this.xShift = minX;
        this.yShift = minY;
        this.zShift = minZ;

        this.grid = new boolean[maxX - minX + 1][maxY - minY + 1][maxZ - minZ + 1];
    }
}

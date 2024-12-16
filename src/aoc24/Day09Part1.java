package aoc24;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/9">Advent of Code 2024 Day 9</a>
 */
public class Day09Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day9_input.txt");

        long answer = new Day09Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 6471961544878L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        //example of line: 2333133121414131402

        int[] blocks = unPack(lines.get(0));
        //example of unpacked blocks: 00...111...2...333.44.5555.6666.777.888899

        compact(blocks);
        //example of compacted blocks: 0099811188827773336446555566..............

        return calculateChecksum(blocks);
    }

    private int[] unPack(String input) {
        List<Integer> blocks = new ArrayList<>();

        int blockId = 0;
        for (int i = 0; i < input.length(); i++) {
            int len = Character.getNumericValue(input.charAt(i));
            int value = -1; //default, -1 signifies free space

            if (i % 2 == 0) { //file
                value = blockId;
                blockId++;
            }

            for (int j = 0; j < len; j++) {
                blocks.add(value);
            }
        }

        //return an int array representation of the blocks
        return blocks.stream().mapToInt(Integer::intValue).toArray();
    }

    private void compact(int[] blocks) {
        //move file blocks one at a time from the end of the disk to the leftmost free space block (until there are no gaps remaining between file blocks)
        int targetIdx = 0, sourceIdx = blocks.length - 1;
        while (sourceIdx > targetIdx) {
            if (blocks[sourceIdx] != -1) {
                //find next free block
                while (blocks[targetIdx] != -1) {
                    targetIdx++;
                    if (targetIdx >= sourceIdx) {
                        return;
                    }
                }
                blocks[targetIdx] = blocks[sourceIdx];
                targetIdx++;
            }
            blocks[sourceIdx] = 0;
            sourceIdx--;
        }
    }

    private long calculateChecksum(int[] blocks) {
        long checksum = 0;
        for (int i = 0; i < blocks.length; i++) {
            checksum += ((long) i * blocks[i]);
        }
        return checksum;
    }
}

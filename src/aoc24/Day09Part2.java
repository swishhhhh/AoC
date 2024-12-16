package aoc24;

import org.apache.commons.math3.util.Pair;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2024/day/9">Advent of Code 2024 Day 9</a>
 */
public class Day09Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day9_input.txt");

        long answer = new Day09Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 6511178035564L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        //example of line: 2333133121414131402

        //map with the key being the index of the free space block and the value being the length of the free space block
        TreeMap<Integer, Integer> freeSpaceMap = new TreeMap<>();

        //map with the key being the index of the file block and the value being a Pair comprised of the id of the file block and its length
        TreeMap<Integer, Pair<Integer, Integer>> fileMap = new TreeMap<>();

        int[] blocks = unPackAndPopulateMaps(lines.get(0), freeSpaceMap, fileMap);
        //example of unpacked blocks: 00...111...2...333.44.5555.6666.777.888899
        
        compact(blocks, freeSpaceMap, fileMap);
        //example of compacted blocks: 00992111777.44.333....5555.6666.....8888..

        return calculateChecksum(blocks);
    }

    private int[] unPackAndPopulateMaps(String input, TreeMap<Integer, Integer> freeSpaceMap,
                                        TreeMap<Integer, Pair<Integer, Integer>> fileMap) {
        List<Integer> blocks = new ArrayList<>();

        int blockId = 0, idx = 0;
        for (int i = 0; i < input.length(); i++) {
            int len = Character.getNumericValue(input.charAt(i));
            int value = -1; //default, -1 signifies free space

            if (i % 2 == 0) { //file
                value = blockId;
                fileMap.put(idx, new Pair<>(blockId, len));
                blockId++;
            } else { //free space
                freeSpaceMap.put(idx, len);
            }

            for (int j = 0; j < len; j++) {
                blocks.add(value);
                idx++;
            }
        }

        //return an int array representation of the blocks
        return blocks.stream().mapToInt(Integer::intValue).toArray();
    }

    private void compact(int[] blocks, TreeMap<Integer, Integer> freeSpaceMap,
                         TreeMap<Integer, Pair<Integer, Integer>> fileMap) {
        /*
          Move files one at a time from the end of the disk to the leftmost free space that is large enough to fit the
          file. If no free spaces available, leave file in its original place.
         */
        List<Integer> fileIndexInReverseOrder = new ArrayList<>(fileMap.keySet());
        Collections.reverse(fileIndexInReverseOrder);

        for (int fileIdx : fileIndexInReverseOrder) {
            Pair<Integer, Integer> fileInfo = fileMap.get(fileIdx);
            int fileId = fileInfo.getFirst();
            int fileLen = fileInfo.getSecond();

            //headMap gets subset of the freeSpaceMap containing only entries before (to the left of) fileIdx
            Map<Integer, Integer> possibleSpaces = freeSpaceMap.headMap(fileIdx);

            //find first suitable space (if any)
            for (Map.Entry<Integer, Integer> entry : possibleSpaces.entrySet()) {
                int freeSpaceIdx = entry.getKey();
                int freeSpaceLen = entry.getValue();

                if (freeSpaceLen >= fileLen) {
                    Arrays.fill(blocks, fileIdx, fileIdx + fileLen, -1);  //clear old space
                    Arrays.fill(blocks, freeSpaceIdx, freeSpaceIdx + fileLen, fileId);  //fill new space

                    //update freeSpace map
                    freeSpaceMap.remove(freeSpaceIdx);
                    if (freeSpaceLen > fileLen) {
                        freeSpaceMap.put(freeSpaceIdx + fileLen, freeSpaceLen - fileLen);
                    }
                    break;
                }
            }
        }
    }

    private long calculateChecksum(int[] blocks) {
        long checksum = 0;
        for (int i = 0; i < blocks.length; i++) {
            checksum += ((long) i * Math.max(blocks[i], 0)); //treat -1 as 0
        }
        return checksum;
    }
}

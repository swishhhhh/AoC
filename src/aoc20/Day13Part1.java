package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/13">Advent of Code 2020 Day 13</a>
 */
public class Day13Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day13_input.txt");

        long answer = new Day13Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 156;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long startMinute = Long.parseLong(lines.get(0));
        List<Long> busIds = Helper.extractLongsFromText(lines.get(1));

        // Find the bus with the smallest wait time
        return busIds.stream()
                .map(id -> Map.entry(getNextBusFromNowInMinutes(startMinute, id), id))
                .min(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() * entry.getValue())
                .orElseThrow();
    }

    private Long getNextBusFromNowInMinutes(long startMinute, Long busId) {
        return (busId - (startMinute % busId)) % busId;
    }
}

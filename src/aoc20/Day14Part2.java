package aoc20;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2020/day/14">Advent of Code 2020 Day 14</a>
 */
public class Day14Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day14_input.txt");

        long answer = new Day14Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 3296185383161L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        String mask = "";
        Map<Long, Long> memory = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("mask")) {
                mask = line.split("=")[1].trim();
                continue;
            }

            List<Long> nums = Helper.extractLongsFromText(line);
            long address = nums.get(0);
            String addressInBinary = Helper.padLeft(Long.toBinaryString(address), '0', 36);
            List<String> addressesToUpdate = applyMaskToAddress(addressInBinary, mask);

            long value = nums.get(1);
            addressesToUpdate.forEach(a -> memory.put(Long.parseLong(a, 2), value));
        }

        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    private List<String> applyMaskToAddress(String inputAddress, String mask) {
        List<String> addresses = new ArrayList<>();
        addresses.add(inputAddress);

        //explode the addresses list by applying the mask
        for (int i = 0; i < mask.length(); i++) {
            List<String> newAddresses = new ArrayList<>();
            for (String address : addresses) {
                char[] chars = address.toCharArray();
                switch (mask.charAt(i)) {
                    case '0' -> newAddresses.add(address);  // Keep as is
                    case '1' -> {
                        chars[i] = '1';  //set the bit to 1
                        newAddresses.add(new String(chars));
                    }
                    case 'X' -> {  //generate both possibilities (0 and 1)
                        chars[i] = '0';
                        newAddresses.add(new String(chars));
                        chars[i] = '1';
                        newAddresses.add(new String(chars));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + mask.charAt(i));
                }
            }

            addresses = newAddresses;
        }

        return addresses;
    }
}

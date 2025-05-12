package aoc20;

import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/25">Advent of Code 2020 Day 25</a>
 */
public class Day25 {
    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day25_input.txt");

        long answer = new Day25().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 17673381;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long cardPublicKey = Long.parseLong(lines.get(0));
        long doorPublicKey = Long.parseLong(lines.get(1));
        
        long cardLoopSize = getLoopSize(cardPublicKey);
        long doorLoopSize = getLoopSize(doorPublicKey);

        if (DEBUG) {
            System.out.printf("cardLoopSize = %s%n", cardLoopSize);
            System.out.printf("doorLoopSize = %s%n", doorLoopSize);
        }

        long loopSize = Math.min(cardLoopSize, doorLoopSize); //either will do, so take lower loopSize
        long publicKey = (cardLoopSize < doorLoopSize) ? doorPublicKey : cardPublicKey; //and pair with other's public key
        long encryptionKey = 1;
        for (int i = 0; i < loopSize; i++) {
            encryptionKey = transform(publicKey, encryptionKey);
        }

        return encryptionKey;
    }

    private long getLoopSize(long cardPublicKey) {
        long value = 1;
        long loopSize = 0;
        while (value != cardPublicKey) {
            value = transform(7, value);
            loopSize++;
        }

        return loopSize;
    }

    private long transform(long subjectNumber, long value) {
        return (value * subjectNumber) % 20201227;
    }

}

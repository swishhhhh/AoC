package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2021/day/16">Advent of Code 2021 Day 16</a>
 */
public class Day16Part1And2 {
    private static class Packet {
        Packet parent;
        int startAt;
        int endAt = -1;
        String binaryVersion;
        int version;
        String binaryTypeId;
        int typeId;
        long literalValue = -1;
        String lenTypeId;
        int lenInBits = -1;
        int numSubPackets = -1;
        List<Packet> subPackets = new ArrayList<>();
        long packetValue = -1;

        public Packet(Packet parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "parent=" + parent +
                    ", startAt=" + startAt +
                    ", endAt=" + endAt +
                    ", binaryVersion='" + binaryVersion + '\'' +
                    ", version=" + version +
                    ", binaryTypeId='" + binaryTypeId + '\'' +
                    ", typeId=" + typeId +
                    ", literalValue=" + literalValue +
                    ", lenTypeId='" + lenTypeId + '\'' +
                    ", lenInBits=" + lenInBits +
                    ", numSubPackets=" + numSubPackets +
                    ", packetValue=" + packetValue +
                    '}';
        }
    }

    private static final boolean DEBUG = false;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day16_input.txt");

        Packet root = execute(lines.get(0));
        long sum = sumUpVersions(root);
        System.out.printf("Sum of versions = %s, Value of outer packet = %s%n", sum, root.packetValue);

        long expectedSum = 891, expectedValue = 673042777597L;
        if (sum != expectedSum || root.packetValue != expectedValue) {
            throw new RuntimeException(
                    String.format("Sum %s or value %s doesn't match expected %s, %s",
                            sum, root.packetValue, expectedSum, expectedValue));
        }
    }

    private static Packet execute(String input) {
        String binary = hexToBinary(input);

        if (DEBUG) {
            System.out.printf("Hex input: %s%nBinary: %s%n", input, binary);
        }

        Packet root = new Packet(null);
        parsePacket(binary, 0, root);

        return root;
    }

    private static int parsePacket(String input, int startAt, Packet p) {
        if (input.length() < startAt + 6) { //not enough for another packet
            return startAt;
        }

        p.startAt = startAt;
        int idx = startAt;

        //version
        p.binaryVersion = input.substring(idx, idx + 3);
        p.version = binaryToInt(p.binaryVersion);
        idx += 3;

        //type id
        p.binaryTypeId = input.substring(idx, idx + 3);
        p.typeId = binaryToInt(p.binaryTypeId);
        idx += 3;

        //literal value type packets
        if (p.typeId == 4) {
            StringBuilder sb = new StringBuilder();
            boolean lastGroup = false;
            while (!lastGroup) {
                sb.append(input, idx + 1, idx + 5);
                lastGroup = input.charAt(idx) == '0';
                idx += 5;
            }
            p.literalValue = binaryToLong(sb.toString());
            p.endAt = idx;
        }

        //length type id (only for non-literal (type == 4) packets)
        if (p.typeId != 4) {
            p.lenTypeId = input.substring(idx, idx + 1);
            idx += 1;

            if (p.lenTypeId.equals("0")) {
                //the next 15 bits are a number that represents the total length in bits of the sub-packets contained by this packet
                String binLenInBits = input.substring(idx, idx + 15);
                p.lenInBits = binaryToInt(binLenInBits);
                idx += 15;

                int newIdx = idx;
                while (newIdx < idx + p.lenInBits) {
                    Packet child = new Packet(p);
                    p.subPackets.add(child);
                    newIdx = parsePacket(input, newIdx, child);
                }
                idx = newIdx;
            } else {
                //the next 11 bits are a number that represents the number of sub-packets immediately contained by this packet
                String binNumSubPackets = input.substring(idx, idx + 11);
                p.numSubPackets = binaryToInt(binNumSubPackets);
                idx += 11;

                for (int i = 0; i < p.numSubPackets; i++) {
                    Packet child = new Packet(p);
                    p.subPackets.add(child);
                    idx = parsePacket(input, idx, child);
                }
            }

            p.endAt = idx;
        }

        calculatePacketValue(p);

        return idx;
    }

    private static void calculatePacketValue(Packet p) {
        switch (p.typeId) {
            case 0 -> {
                //sum of children
                p.packetValue = 0;
                p.subPackets.forEach(child -> p.packetValue += child.packetValue);
            }
            case 1 -> {
                //product of children
                p.packetValue = 1;
                p.subPackets.forEach(child -> p.packetValue *= child.packetValue);
            }
            case 2 -> {
                //min of children
                p.packetValue = Long.MAX_VALUE;
                p.subPackets.forEach(child -> p.packetValue = Math.min(p.packetValue, child.packetValue));
            }
            case 3 -> {
                //max of children
                p.packetValue = Long.MIN_VALUE;
                p.subPackets.forEach(child -> p.packetValue = Math.max(p.packetValue, child.packetValue));
            }
            case 4 -> {
                //literal value
                p.packetValue = p.literalValue;
            }
            case 5 -> {
                //value is 1 if the value of the 1st sub-packet is greater than the value of the 2nd sub-packet, otherwise 0
                p.packetValue = p.subPackets.get(0).packetValue > p.subPackets.get(1).packetValue ? 1 : 0;
            }
            case 6 -> {
                //value is 1 if the value of the 1st sub-packet is less than the value of the 2nd sub-packet, otherwise 0
                p.packetValue = p.subPackets.get(0).packetValue < p.subPackets.get(1).packetValue ? 1 : 0;
            }
            case 7 -> {
                //value is 1 if the value of the 1st sub-packet == the value of the 2nd sub-packet, otherwise 0
                p.packetValue = p.subPackets.get(0).packetValue == p.subPackets.get(1).packetValue ? 1 : 0;
            }
        }
    }

    private static long sumUpVersions(Packet parent) {
        long sum = 0;
        for (Packet child : parent.subPackets) {
            sum+= sumUpVersions(child);
        }
        return parent.version + sum;
    }

    private static String hexToBinary(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            sb.append(Helper.padLeft(Integer.toBinaryString(Integer.parseInt(hex.substring(i, i + 1), 16)), '0', 4));
        }
        return sb.toString();
    }

    private static int binaryToInt(String binary) {
        return Integer.parseInt(binary, 2);
    }

    private static long binaryToLong(String binary) {
        return Long.parseLong(binary, 2);
    }
}

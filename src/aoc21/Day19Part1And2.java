package aoc21;

import aoc21.day19.Beacon;
import aoc21.day19.Edge;
import aoc21.day19.Scanner;
import aoc21.day19.Transposition;
import datastructs.Coordinates3D;
import utils.Helper;
import utils.ResourceLoader;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <a href="https://adventofcode.com/2021/day/19">Advent of Code 2021 Day 19</a>
 */
public class Day19Part1And2 {
    private final Map<Coordinates3D, List<String>> masterBeaconsMap = new HashMap<>();
    private final Map<Integer, Coordinates3D> masterScannerOffsetsMap = new HashMap<>();

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day19_input.txt");

        Day19Part1And2 exec = new Day19Part1And2();
        long numBeacons = exec.getNumberOfBeacons(lines);

        System.out.printf("Number of Beacons = %s%n", numBeacons);

        long expected = 353;
        if (numBeacons != expected) {
            throw new RuntimeException(String.format("Number of Beacons %s doesn't match expected %s", numBeacons, expected));
        }

        long largestDistance = exec.getLargestManhattanDistance();
        System.out.printf("Largest Manhattan Distance = %s%n", largestDistance);

        expected = 10832;
        if (largestDistance != expected) {
            throw new RuntimeException(String.format("Largest Manhattan Distance %s doesn't match expected %s", largestDistance, expected));
        }
    }

    private long getNumberOfBeacons(List<String> lines) {
        Queue<Scanner> unprocessedScanners = loadScanners(lines);
        List<Scanner> processedScanners = new ArrayList<>();

        //make first scanner (scanner 0) the base - i.e. orient masterMap according to it
        Scanner scanner = unprocessedScanners.poll();
        assert scanner != null;
        scanner.setTransposition(new Transposition(0, 1, 2, 1, 1, 1, new Coordinates3D(0, 0, 0)));
        transposeBeaconsAndPlaceOnMasterMap(scanner);
        processedScanners.add(scanner);

        while (!unprocessedScanners.isEmpty()) {
            scanner = unprocessedScanners.poll();

            Scanner adjacentProcessedScanner = getAdjacentScanner(scanner, processedScanners);
            if (adjacentProcessedScanner == null) {
                unprocessedScanners.add(scanner); //add to back of queue and continue with next unprocessed scanner
                continue;
            }

            scanner.setTransposition(getTransposition(scanner, adjacentProcessedScanner));
            transposeBeaconsAndPlaceOnMasterMap(scanner);

            processedScanners.add(scanner);
        }

        return masterBeaconsMap.keySet().size();
    }

    private Queue<Scanner> loadScanners(List<String> lines) {
        Queue<Scanner> scanners = new LinkedBlockingQueue<>();

        int scannerNum = -1;
        int beaconNum = -1;
        Scanner scanner = null;
        for (String line : lines) {
            if (line.startsWith("--- scanner")) {
                scannerNum = Helper.extractIntsFromText(line).get(0);
                scanner = new Scanner(scannerNum);
                beaconNum = -1;
                continue;
            }

            if (line.isBlank()) {
                assert scanner != null;
                populateEdges(scanner);
                scanners.add(scanner);
                continue;
            }

            List<Integer> rawCoords = Helper.extractIntsFromText(line, true);
            beaconNum++;
            Beacon b = new Beacon("S" + scannerNum + "|" + "B" + beaconNum + "|RC" + rawCoords,
                    rawCoords.stream().mapToInt(i -> i).toArray());
            assert scanner != null;
            scanner.addBeacon(b);
        }

        //last scanner
        assert scanner != null;
        populateEdges(scanner);
        scanners.add(scanner);

        return scanners;
    }

    private void populateEdges(Scanner scanner) {
        for (int i = 0; i < scanner.getBeacons().size() - 1; i++) {
            Beacon b1 = scanner.getBeacons().get(i);
            for (int j = i + 1; j < scanner.getBeacons().size(); j++) {
                Beacon b2 = scanner.getBeacons().get(j);
                scanner.addEdge(new Edge(b1, b2));
            }
        }
    }

    private void transposeBeaconsAndPlaceOnMasterMap(Scanner scanner) {
        for (Beacon b : scanner.getBeacons()) {
            int[] rawCoords = b.getRawCoordinates();
            Transposition t = scanner.getTransposition();
            int x = (rawCoords[t.xCoordPos()] * t.xSign()) - t.offsetFromOrigin().x();
            int y = (rawCoords[t.yCoordPos()] * t.ySign()) - t.offsetFromOrigin().y();
            int z = (rawCoords[t.zCoordPos()] * t.zSign()) - t.offsetFromOrigin().z();
            Coordinates3D c = new Coordinates3D(x, y, z);
            b.setTransposedCoords(c);

            List<String> beaconIds = this.masterBeaconsMap.get(c);
            if (beaconIds == null) {
                beaconIds = new ArrayList<>();
            }
            beaconIds.add(b.getId());
            this.masterBeaconsMap.put(c, beaconIds);
        }

        //add offset coordinates to masterScannerOffsetsMap
        this.masterScannerOffsetsMap.put(scanner.getId(), scanner.getTransposition().offsetFromOrigin());
    }

    private Scanner getAdjacentScanner(Scanner s1, List<Scanner> processedScanners) {
        /*
           adjacent scanner == another scanner (in processedScanners) that has at least 12 beacons in common with s1)
         */
        Set<Long> s1Signatures = s1.getSignaturesToEdgesMap().keySet();

        for (Scanner s2 : processedScanners) {
            Set<Beacon> commonBeacons = new HashSet<>();
            Set<Long> commonSignatures = new HashSet<>();

            s2.getEdges().stream()
                    .filter(e -> s1Signatures.contains(e.getDistanceSignature()))
                    .forEach(e -> {
                        commonBeacons.add(e.getBeacon1());
                        commonBeacons.add(e.getBeacon2());
                        commonSignatures.add(e.getDistanceSignature());
                    });

            if (commonBeacons.size() >= 12) {
                s1.setCommonSignatures(commonSignatures);
                return s2;
            }
        }

        return null;
    }

    private Transposition getTransposition(Scanner scanner, Scanner adjacentProcessedScanner) {
        List<Long> commonSignatures = scanner.getCommonSignatures().stream().toList();
        Transposition[] transPair1 = getTranspositionPairForSignature(scanner, adjacentProcessedScanner, commonSignatures.get(0));
        Transposition t1a = transPair1[0];
        Transposition t1b = transPair1[1];

        Transposition[] transPair2 = getTranspositionPairForSignature(scanner, adjacentProcessedScanner, commonSignatures.get(1));
        Transposition t2a = transPair2[0];
        Transposition t2b = transPair2[1];

        //2 of the 4 transpositions should match, that's the one we're looking for
        if (t1a.equals(t2a) || t1a.equals(t2b)) {
            return t1a;
        } else if (t1b.equals(t2a) || t1b.equals(t2b)) {
            return t1b;
        } else {
            throw new RuntimeException(String.format("Unable to determine transposition for scanner %s", scanner));
        }
    }

    private Transposition[] getTranspositionPairForSignature(Scanner scanner, Scanner adjacentProcessedScanner, long sig) {
        Edge adjEdge = adjacentProcessedScanner.getSignaturesToEdgesMap().get(sig);
        Beacon adjB1 = adjEdge.getBeacon1();
        Beacon adjB2 = adjEdge.getBeacon2();

        Edge edge = scanner.getSignaturesToEdgesMap().get(sig);
        Beacon beaconA = edge.getBeacon1();
        Beacon beaconB = edge.getBeacon2();

        //figure out the x,y,z coordinate positions
        long xDiff = adjB1.getTransposedCoords().x() - adjB2.getTransposedCoords().x();
        long yDiff = adjB1.getTransposedCoords().y() - adjB2.getTransposedCoords().y();
        long zDiff = adjB1.getTransposedCoords().z() - adjB2.getTransposedCoords().z();
        int xCoordPos = -1, yCoordPos = -1, zCoordPos = -1;

        for (int i = 0; i < 3; i++) {
            long absRawDiff = Math.abs(beaconA.getRawCoordinates()[i] - beaconB.getRawCoordinates()[i]);
            if (absRawDiff == Math.abs(xDiff)) {
                xCoordPos = i;
            } else if (absRawDiff == Math.abs(yDiff)) {
                yCoordPos = i;
            } else if (absRawDiff == Math.abs(zDiff)) {
                zCoordPos = i;
            }
        }

        assert xCoordPos != -1 && yCoordPos != -1 && zCoordPos != -1;

        //figure out the 2 sets of potential x,y,z signs (2 sets because we're not sure if adjB1 matches beaconA or beaconB)
        long rawDiffX1 = beaconA.getRawCoordinates()[xCoordPos] - beaconB.getRawCoordinates()[xCoordPos];
        int xSign1 = xDiff > 0 && rawDiffX1 > 0 || xDiff < 0 && rawDiffX1 < 0 ? 1 : -1;
        int xSign2 = -1 * xSign1;

        long rawDiffY1 = beaconA.getRawCoordinates()[yCoordPos] - beaconB.getRawCoordinates()[yCoordPos];
        int ySign1 = yDiff > 0 && rawDiffY1 > 0 || yDiff < 0 && rawDiffY1 < 0 ? 1 : -1;
        int ySign2 = -1 * ySign1;

        long rawDiffZ1 = beaconA.getRawCoordinates()[zCoordPos] - beaconB.getRawCoordinates()[zCoordPos];
        int zSign1 = zDiff > 0 && rawDiffZ1 > 0 || zDiff < 0 && rawDiffZ1 < 0 ? 1 : -1;
        int zSign2 = -1 * zSign1;

        //figure out 2 sets of 3D-coordinates to represent offsetFromOrigin (2 because we're not sure... (see above))
        int offsetX1 = (xSign1 * beaconA.getRawCoordinates()[xCoordPos]) - adjB1.getTransposedCoords().x();
        int offsetY1 = (ySign1 * beaconA.getRawCoordinates()[yCoordPos]) - adjB1.getTransposedCoords().y();
        int offsetZ1 = (zSign1 * beaconA.getRawCoordinates()[zCoordPos]) - adjB1.getTransposedCoords().z();

        int offsetX2 = (xSign2 * beaconB.getRawCoordinates()[xCoordPos]) - adjB1.getTransposedCoords().x();
        int offsetY2 = (ySign2 * beaconB.getRawCoordinates()[yCoordPos]) - adjB1.getTransposedCoords().y();
        int offsetZ2 = (zSign2 * beaconB.getRawCoordinates()[zCoordPos]) - adjB1.getTransposedCoords().z();

        //create the pair of transpositions
        Transposition t1 = new Transposition(xCoordPos, yCoordPos, zCoordPos, xSign1, ySign1, zSign1,
                new Coordinates3D(offsetX1, offsetY1, offsetZ1));
        Transposition t2 = new Transposition(xCoordPos, yCoordPos, zCoordPos, xSign2, ySign2, zSign2,
                new Coordinates3D(offsetX2, offsetY2, offsetZ2));

        return new Transposition[]{t1, t2};
    }

    private long getLargestManhattanDistance() {
        List<Coordinates3D> offsets = this.masterScannerOffsetsMap.values().stream().toList();
        long maxDist = 0;
        for (int i = 0; i < offsets.size() - 1; i++) {
            for (int j = i + 1; j < offsets.size(); j++) {
                maxDist = Math.max(maxDist,
                        Math.abs(offsets.get(i).x() - offsets.get(j).x()) +
                                Math.abs(offsets.get(i).y() - offsets.get(j).y()) +
                                Math.abs(offsets.get(i).z() - offsets.get(j).z())
                );
            }
        }

        return maxDist;
    }
}

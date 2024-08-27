package aoc20;

import datastructs.Coordinates;
import datastructs.Direction;
import utils.ResourceLoader;

import java.util.List;

import static datastructs.Direction.*;
import static java.lang.Math.abs;

/**
 * <a href="https://adventofcode.com/2020/day/12">Advent of Code 2020 Day 12</a>
 */
public class Day12Part2 {
    private static final List<Direction> QUADRANT = List.of(NE, SE, SW, NW);

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day12_input.txt");

        long answer = new Day12Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 52069;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        long xPosShip = 0, yPosShip = 0;
        long xPosWaypoint = 10, yPosWaypoint = 1; //waypoint positions are relative to the ship, starting at 10 (east), 1 (north)

        for (String line : lines) {
            char instruction = line.charAt(0);
            int units = Integer.parseInt(line.substring(1));

            switch (instruction) {
                case 'N' -> yPosWaypoint += units;
                case 'S' -> yPosWaypoint -= units;
                case 'E' -> xPosWaypoint += units;
                case 'W' -> xPosWaypoint -= units;
                case 'F' -> {
                    xPosShip = xPosShip + (units * xPosWaypoint);
                    yPosShip = yPosShip + (units * yPosWaypoint);
                }
                case 'R', 'L' -> {
                    Coordinates c =
                            rotateWaypoint(new Coordinates((int) xPosWaypoint, (int) yPosWaypoint), instruction, units);
                    xPosWaypoint = c.x();
                    yPosWaypoint = c.y();
                }
            }
        }

        return abs(xPosShip) + abs(yPosShip);
    }

    private static Coordinates rotateWaypoint(Coordinates waypoint, char rightOrLeft, int degrees) {
        int ninetyDegreeTurns = degrees / 90;

        //for every 90-degree turn the x and y coords switch with each other
        int newWpPosX = ninetyDegreeTurns % 2 == 0 ? abs(waypoint.x()) : abs(waypoint.y());
        int newWpPosY = ninetyDegreeTurns % 2 == 0 ? abs(waypoint.y()) : abs(waypoint.x());

        //get current waypoint quadrant
        Direction quadrant;
        if (waypoint.x() >= 0 && waypoint.y() >= 0) {
            quadrant = NE;
        } else if (waypoint.x() >= 0) {
            quadrant = SE;
        } else if (waypoint.y() < 0) {
            quadrant = SW;
        } else {
            quadrant = NW;
        }

        //rotate quadrant
        int idx = QUADRANT.indexOf(quadrant); //current quadrant
        if (rightOrLeft == 'R') {
            idx = (idx + ninetyDegreeTurns) % 4;
        } else { // rightOrLeft == 'L'
            idx -= ninetyDegreeTurns;
            if (idx < 0) {
                idx = 4 + idx;
            }
        }
        quadrant = QUADRANT.get(idx);        //new quadrant

        if (quadrant == NW || quadrant == SW) {
            newWpPosX = -1 * newWpPosX;
        }
        if (quadrant == SE || quadrant == SW) {
            newWpPosY = -1 * newWpPosY;
        }

        return new Coordinates(newWpPosX, newWpPosY);
    }
}

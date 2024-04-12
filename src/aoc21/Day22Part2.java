package aoc21;

import datastructs.Cuboid;
import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2021/day/22">Advent of Code 2021 Day 22</a>
 */
public class Day22Part2 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day22_input.txt");

        long answer = new Day22Part2().execute(lines);
        System.out.printf("Number of cubes on = %s%n", answer);

        long expected = 1334275219162622L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        /*
          Algo: Keep constructing cuboids for each step in the input and adding them to the list (if "on").
                Each time a new cuboid (c1) is created, it is compared against all other cuboids (cn) already in the list
                and for each one that it (c1) overlaps with, it splits the other ones (cn) into 0 to 6 new sub-cuboids
                such that at no time do any of the cuboids in the list overlap with any other ones.
                Each step will at most add 6 additional cuboids to the list (representing the max of 6 outer layers).
         */

        Set<Cuboid> cuboids = new HashSet<>();

        for (String line : lines) {
            List<Integer> nums = Helper.extractIntsFromText(line, true);
            int x1 = nums.get(0), x2 = nums.get(1), y1 = nums.get(2), y2 = nums.get(3), z1 = nums.get(4), z2 = nums.get(5);
            Cuboid c1 = new Cuboid(x1, x2, y1, y2, z1, z2);

            boolean onOrOff = line.startsWith("on ");
            Set<Cuboid> nextCuboids = new HashSet<>();

            if (onOrOff) {
                nextCuboids.add(c1);
            }

            //iterate through cuboids and split up any that overlap such that we're left with only non-overlapping ones
            for (Cuboid c2 : cuboids) {
                if (!c1.overlapsWith(c2)) {
                    nextCuboids.add(c2);
                    continue;
                }

                nextCuboids.addAll(extractCuboid(c1, c2));
            }

            cuboids = nextCuboids;
        }

        //add up the volume (# of cells in each cuboid) of all the cuboids
        long total = 0;
        for (Cuboid c : cuboids) {
            total += c.getVolume();
        }

        return total;
    }

    private List<Cuboid> extractCuboid(Cuboid c1, Cuboid c2) {
        /*
          Returns List of 0..6 cuboids that represent the 6 sides/outer-layers of c2 after extracting (cutting out) c1
          from it.
            - if c2 fully envelops c1 (i.e. c1 is fully inside c2 and none of the x,y,z boundaries are in common/touching)
               then the number of cuboids returned will be 6 (for each of the 6 sides)
            - conversely if c2 is fully inside c1, 0 will be returned
            - if c1 and c2 partially overlap then 1..5 will be returned depending on how the cuboids overlap

            To help visualize - imagine a standard 3x3 Rubik's Cube. The 6 sides can be labeled {Right, Left, Bottom, Top
            Front, Back}. From an orientation perspective, we'll say the x-axis runs from left to right, the y-axis from
            bottom/down to top/up, and the z axis from front to back. We can categorize each cell into 1 of 3 categories:
              - corner  -> cells with 3 sides exposed
              - edge    -> cells with 2 sides exposed
              - regular -> cells with 0 or 1 sides exposed

            Let's then say we want to extract the core cell (at x,y,z coordinates [1,1,1]) from the rest of the 3x2 cube
            by cutting away the 6 sides. The minimum number of cuts is 6 but the pieces cut away will be of different
            sizes because of the "edge" and "corner" pieces that shouldn't be double counted.
              - Our first 2 cuts will cut away the left and right sides respectively. The size of these 2 pieces
                (cuboids) will be 9 cells (x=0..0, y=0..2, z=0..2 for the left side, and x=2..2, y=0..2, z=0..2 for the
                right side).
              - Our next 2 cuts are for the bottom and top. Those 2 cuts will produce cuboids of 3 cells each (x=1..1,
                y=0..0, z=0..2 for the bottom side, and x=1..1, y=2..2, z=0..2 for the top side). The right and left
                edges and corners have already been accounted for (cut away) with the first 2 cuts, so we don't include
                them again here.
              - Our final 2 cuts will cut away a single cube each in the front (x=1, y=1, z=0) and back (x=1, y=1, z=2)
                respectively. All other edges and corners are already accounted for by the first 4 cuts.

             In the example above, the 3x3 cube/oid is c2, and c1 is the core cell left after cutting away the 6 sides.
             This algo also works for different sizes of overlapping c1/c2 cuboids including partial overlaps where
             there isn't anything to cut away for one or more of the 6 sides.
         */
        List<Cuboid> subCuboids = new ArrayList<>();

        //left side
        if (c1.x1() > c2.x1()) {
            subCuboids.add(new Cuboid(
                    c2.x1(), c1.x1() - 1,
                    c2.y1(), c2.y2(),
                    c2.z1(), c2.z2()));
        }

        //right side
        if (c1.x2() < c2.x2()) {
            subCuboids.add(new Cuboid(
                    c1.x2() + 1, c2.x2(),
                    c2.y1(), c2.y2(),
                    c2.z1(), c2.z2()));
        }

        //bottom
        if (c1.y1() > c2.y1()) {
            subCuboids.add(new Cuboid(
                    Math.max(c1.x1(), c2.x1()), Math.min(c1.x2(), c2.x2()),
                    c2.y1(), c1.y1() - 1,
                    c2.z1(), c2.z2()));
        }

        //top
        if (c1.y2() < c2.y2()) {
            subCuboids.add(new Cuboid(
                    Math.max(c1.x1(), c2.x1()), Math.min(c1.x2(), c2.x2()),
                    c1.y2() + 1, c2.y2(),
                    c2.z1(), c2.z2()));
        }

        //front
        if (c1.z1() > c2.z1()) {
            subCuboids.add(new Cuboid(
                    Math.max(c1.x1(), c2.x1()), Math.min(c1.x2(), c2.x2()),
                    Math.max(c1.y1(), c2.y1()), Math.min(c1.y2(), c2.y2()),
                    c2.z1(), c1.z1() - 1));
        }

        //back
        if (c1.z2() < c2.z2()) {
            subCuboids.add(new Cuboid(
                    Math.max(c1.x1(), c2.x1()), Math.min(c1.x2(), c2.x2()),
                    Math.max(c1.y1(), c2.y1()), Math.min(c1.y2(), c2.y2()),
                    c1.z2() + 1, c2.z2()));
        }

        return subCuboids;
    }
}

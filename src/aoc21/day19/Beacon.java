package aoc21.day19;

import datastructs.Coordinates3D;

import java.util.Objects;

public class Beacon {
    private final String id; //composed of: scanner-id | beacon-id | raw-coordinates, e.g.: "S0|B0|RC{5,6,-4}"
    private final int[] rawCoordinates; //relative to parent scanner and its orientation
    private Coordinates3D transposedCoords; //relative to master map (based on scanner 0's orientation)

    public Beacon(String id, int[] rawCoordinates) {
        this.id = id;
        this.rawCoordinates = rawCoordinates;
    }

    public String getId() {
        return id;
    }

    public int[] getRawCoordinates() {
        return rawCoordinates;
    }

    public Coordinates3D getTransposedCoords() {
        return transposedCoords;
    }

    public void setTransposedCoords(Coordinates3D transposedCoords) {
        this.transposedCoords = transposedCoords;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "id='" + id + '\'' +
                ", transposedCoords=" + transposedCoords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beacon beacon)) return false;

        return Objects.equals(id, beacon.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

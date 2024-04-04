package aoc21.day19;

import datastructs.Coordinates3D;

import java.util.Objects;

public record Transposition(int xCoordPos, int yCoordPos, int zCoordPos, int xSign, int ySign, int zSign,
                            Coordinates3D offsetFromOrigin) {

    @Override
    public String toString() {
        return "Transposition{" +
                "xCoordPos=" + xCoordPos +
                ", yCoordPos=" + yCoordPos +
                ", zCoordPos=" + zCoordPos +
                ", xSign=" + xSign +
                ", ySign=" + ySign +
                ", zSign=" + zSign +
                ", offsetFromOrigin=" + offsetFromOrigin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transposition that)) return false;

        if (xCoordPos != that.xCoordPos) return false;
        if (yCoordPos != that.yCoordPos) return false;
        if (zCoordPos != that.zCoordPos) return false;
        if (xSign != that.xSign) return false;
        if (ySign != that.ySign) return false;
        if (zSign != that.zSign) return false;
        return Objects.equals(offsetFromOrigin, that.offsetFromOrigin);
    }

    @Override
    public int hashCode() {
        int result = xCoordPos;
        result = 31 * result + yCoordPos;
        result = 31 * result + zCoordPos;
        result = 31 * result + xSign;
        result = 31 * result + ySign;
        result = 31 * result + zSign;
        result = 31 * result + (offsetFromOrigin != null ? offsetFromOrigin.hashCode() : 0);
        return result;
    }
}

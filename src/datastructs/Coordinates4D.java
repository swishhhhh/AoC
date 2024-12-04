package datastructs;

public record Coordinates4D(int x, int y, int z, int w) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates4D that = (Coordinates4D) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return w == that.w;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + w;
        return result;
    }

    @Override
    public String toString() {
        return "Coordinates3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}

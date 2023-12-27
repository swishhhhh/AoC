package aoc23.datastructs;

public record Coordinates(int x, int y) {

    @Override
    public Coordinates clone() {
        return new Coordinates(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ",y=" + y +
                '}';
    }
}

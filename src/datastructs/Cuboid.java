package datastructs;

public record Cuboid(int x1, int x2, int y1, int y2, int z1, int z2) {
    public int getWidth() {
        return x2 - x1 + 1;
    }

    public int getHeight() {
        return y2 - y1 + 1;
    }

    public int getDepth() {
        return z2 - z1 + 1;
    }

    public long getVolume() {
        return (long) this.getWidth() * this.getHeight() * this.getDepth();
    }

    public boolean overlapsWith(Cuboid other) {
        return (this.x2 >= other.x1 && this.x1 <= other.x2)
            && (this.y2 >= other.y1 && this.y1 <= other.y2)
            && (this.z2 >= other.z1 && this.z1 <= other.z2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuboid cuboid)) return false;

        if (x1 != cuboid.x1) return false;
        if (x2 != cuboid.x2) return false;
        if (y1 != cuboid.y1) return false;
        if (y2 != cuboid.y2) return false;
        if (z1 != cuboid.z1) return false;
        return z2 == cuboid.z2;
    }

    @Override
    public int hashCode() {
        int result = x1;
        result = 31 * result + x2;
        result = 31 * result + y1;
        result = 31 * result + y2;
        result = 31 * result + z1;
        result = 31 * result + z2;
        return result;
    }

    @Override
    public String toString() {
        return "Cuboid{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", z1=" + z1 +
                ", z2=" + z2 +
                '}';
    }
}

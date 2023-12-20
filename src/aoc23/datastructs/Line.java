package aoc23.datastructs;

public class Line implements Comparable<Line> {
    private final long x1;
    private final long x2;
    private final long y1;
    private final long y2;
    private final String type;

    public Line(long x1, long x2, long y1, long y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        if (x1 == x2) {
            this.type = "vertical";
        } else if (y1 == y2) {
            this.type = "horizontal";
        } else {
            this.type = "diagonal";
        }
    }

    public long getX1() {
        return x1;
    }

    public long getX2() {
        return x2;
    }

    public long getY1() {
        return y1;
    }

    public long getY2() {
        return y2;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Line{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line line)) return false;

        if (x1 != line.x1) return false;
        if (x2 != line.x2) return false;
        if (y1 != line.y1) return false;
        return y2 == line.y2;
    }

    @Override
    public int hashCode() {
        int result = (int) (x1 ^ (x1 >>> 32));
        result = 31 * result + (int) (x2 ^ (x2 >>> 32));
        result = 31 * result + (int) (y1 ^ (y1 >>> 32));
        result = 31 * result + (int) (y2 ^ (y2 >>> 32));
        return result;
    }

    @Override
    public int compareTo(Line o) {
        long ret =       this.x1 - o.x1;
        ret = ret == 0 ? this.x2 - o.x2 : ret;
        ret = ret == 0 ? this.y1 - o.y1 : ret;
        ret = ret == 0 ? this.y2 - o.y2 : ret;
        return ret < 0 ? -1 : (ret == 0 ? 0 : 1);
    }
}

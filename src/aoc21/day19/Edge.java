package aoc21.day19;

public class Edge {
    private final Beacon beacon1;
    private final Beacon beacon2;
    private final long distanceSignature;

    public Edge(Beacon b1, Beacon b2) {
        this.beacon1 = b1;
        this.beacon2 = b2;
        int[] coords1 = b1.getRawCoordinates();
        int[] coords2 = b2.getRawCoordinates();
        this.distanceSignature = //sum of (squares of the coord pair differences)
                (long) (Math.pow(coords1[0] - coords2[0], 2)
                      + Math.pow(coords1[1] - coords2[1], 2)
                      + Math.pow(coords1[2] - coords2[2], 2));
    }

    public Beacon getBeacon1() {
        return beacon1;
    }

    public Beacon getBeacon2() {
        return beacon2;
    }

    public long getDistanceSignature() {
        return distanceSignature;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "beacon1=" + beacon1 +
                ", beacon2=" + beacon2 +
                ", distanceSignature=" + distanceSignature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge edge)) return false;

        if (distanceSignature != edge.distanceSignature) return false;
        if (!beacon1.equals(edge.beacon1)) return false;
        return beacon2.equals(edge.beacon2);
    }

    @Override
    public int hashCode() {
        int result = beacon1.hashCode();
        result = 31 * result + beacon2.hashCode();
        result = 31 * result + (int) (distanceSignature ^ (distanceSignature >>> 32));
        return result;
    }
}

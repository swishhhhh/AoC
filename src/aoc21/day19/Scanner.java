package aoc21.day19;

import java.util.*;

public class Scanner {
    private final int id;
    private final List<Beacon> beacons = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<Long, Edge> signaturesToEdgesMap = new HashMap<>();
    private Transposition transposition;
    private Set<Long> commonSignatures = new HashSet<>();

    public Scanner(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void addBeacon(Beacon beacon) {
        this.beacons.add(beacon);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
        Long signature = edge.getDistanceSignature();
        if (this.signaturesToEdgesMap.containsKey(signature)) {
            throw new IllegalArgumentException(String.format("Already have edge (%s) with signature %s",
                    this.signaturesToEdgesMap.get(signature), signature));
        }
        this.signaturesToEdgesMap.put(edge.getDistanceSignature(), edge);
    }

    public Map<Long, Edge> getSignaturesToEdgesMap() {
        return signaturesToEdgesMap;
    }

    public Set<Long> getCommonSignatures() {
        return commonSignatures;
    }

    public void setCommonSignatures(Set<Long> commonSignatures) {
        this.commonSignatures = commonSignatures;
    }

    public Transposition getTransposition() {
        return transposition;
    }

    public void setTransposition(Transposition transposition) {
        this.transposition = transposition;
    }

    @Override
    public String toString() {
        return "Scanner{" +
                "id=" + id +
                ", beacons=" + beacons +
                ", edges=" + edges +
                ", transposition=" + transposition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scanner scanner)) return false;

        return id == scanner.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

package aoc24.day23;

import java.util.*;

public class BronKerbosch<T> {
    private final List<Set<T>> maximalCliques;
    private final Map<T, List<T>> adjacencyGraph;

    /**
     * Initializes a new BronKerbosch algorithm instance.
     * @param adjacencyGraph An undirected graph represented as an adjacency list
     */
    public BronKerbosch(Map<T, List<T>> adjacencyGraph) {
        this.maximalCliques = new ArrayList<>();
        this.adjacencyGraph = Map.copyOf(adjacencyGraph);
    }

    /**
     * Finds all maximal cliques in the graph using the Bron-Kerbosch algorithm.
     * @return List of all maximal cliques, where each clique is a Set of vertices
     */
    public List<Set<T>> findMaximalCliques() {
        Set<T> candidates = new HashSet<>(adjacencyGraph.keySet());
        bronKerbosch(new HashSet<>(), candidates, new HashSet<>());
        return List.copyOf(maximalCliques);
    }

    /**
     * Recursive implementation of the Bron-Kerbosch algorithm.
     * @param potentialClique the current potential clique being built (R)
     * @param candidates vertices that could extend the potential clique (P)
     * @param excluded vertices that have already been processed (X)
     */
    private void bronKerbosch(Set<T> potentialClique,
                              Set<T> candidates,
                              Set<T> excluded) {
        if (candidates.isEmpty() && excluded.isEmpty()) {
            maximalCliques.add(new HashSet<>(potentialClique));
            return;
        }

        Set<T> candidatesCopy = new HashSet<>(candidates);
        for (T vertex : candidatesCopy) {
            Set<T> newPotentialClique = new HashSet<>(potentialClique);
            newPotentialClique.add(vertex);

            Set<T> newCandidates = new HashSet<>(candidates);
            newCandidates.retainAll(adjacencyGraph.get(vertex));

            Set<T> newExcluded = new HashSet<>(excluded);
            newExcluded.retainAll(adjacencyGraph.get(vertex));

            bronKerbosch(newPotentialClique, newCandidates, newExcluded);

            candidates.remove(vertex);
            excluded.add(vertex);
        }
    }
}

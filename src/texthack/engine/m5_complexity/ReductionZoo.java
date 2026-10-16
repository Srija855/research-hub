package texthack.engine.m5_complexity;

import texthack.engine.ds.CustomArrayList;

/**
 * The Canonical Reduction Zoo:
 * 1. 3-SAT -> CLIQUE (Clause Gadgets & Consistency Edges)
 * 2. CLIQUE -> INDEPENDENT SET (Graph Complementation G -> G_bar)
 * 3. INDEPENDENT SET -> VERTEX COVER (|S| + |C| = |V| duality)
 * 4. SUBSET-SUM -> 0/1 KNAPSACK (Values = Weights)
 * 5. HAMILTONIAN CYCLE -> METRIC TSP (Edge weights 1 if edge exists, 2 otherwise)
 * Zero java.util.* usage.
 */
public class ReductionZoo {

    public static class Clause {
        public int l1, l2, l3; // Signed integers: +1 for x1, -1 for not(x1)

        public Clause(int l1, int l2, int l3) {
            this.l1 = l1;
            this.l2 = l2;
            this.l3 = l3;
        }
    }

    public static class ReductionGraph {
        public int numVertices;
        public boolean[][] adj;

        public ReductionGraph(int numVertices) {
            this.numVertices = numVertices;
            this.adj = new boolean[numVertices][numVertices];
        }

        public void addEdge(int u, int v) {
            if (u != v) {
                adj[u][v] = true;
                adj[v][u] = true;
            }
        }
    }

    /**
     * Reduces 3-SAT to CLIQUE:
     * For k clauses, creates 3*k vertices (one per literal occurrence).
     * Connects two literal vertices if:
     * - They are in different clauses, AND
     * - They are not negations of each other (i.e. consistent).
     * Satisfiable iff graph contains a clique of size k.
     */
    public static ReductionGraph reduce3SatToClique(CustomArrayList<Clause> formula) {
        int k = formula.size();
        int totalVertices = 3 * k;
        ReductionGraph g = new ReductionGraph(totalVertices);

        for (int c1 = 0; c1 < k; c1++) {
            Clause cl1 = formula.get(c1);
            int[] lits1 = {cl1.l1, cl1.l2, cl1.l3};

            for (int c2 = c1 + 1; c2 < k; c2++) {
                Clause cl2 = formula.get(c2);
                int[] lits2 = {cl2.l1, cl2.l2, cl2.l3};

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        // Consistent: not negations of each other
                        if (lits1[i] != -lits2[j]) {
                            int u = c1 * 3 + i;
                            int v = c2 * 3 + j;
                            g.addEdge(u, v);
                        }
                    }
                }
            }
        }
        return g;
    }

    /**
     * Reduces CLIQUE to INDEPENDENT SET via Graph Complementation.
     * S is a Clique in G iff S is an Independent Set in G_complement.
     */
    public static ReductionGraph reduceCliqueToIndependentSet(ReductionGraph g) {
        int n = g.numVertices;
        ReductionGraph complement = new ReductionGraph(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!g.adj[i][j]) {
                    complement.addEdge(i, j);
                }
            }
        }
        return complement;
    }

    /**
     * Duality: S is an Independent Set in G of size k iff
     * V \ S is a Vertex Cover in G of size |V| - k.
     */
    public static boolean verifyVertexCoverDual(ReductionGraph g, CustomArrayList<Integer> independentSet, CustomArrayList<Integer> vertexCover) {
        if (independentSet.size() + vertexCover.size() != g.numVertices) return false;

        // Check vertex cover validity: every edge must have at least one endpoint in VC
        for (int u = 0; u < g.numVertices; u++) {
            for (int v = u + 1; v < g.numVertices; v++) {
                if (g.adj[u][v]) {
                    if (!vertexCover.contains(u) && !vertexCover.contains(v)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Reduces Hamiltonian Cycle to TSP:
     * Sets dist(u, v) = 1 if (u, v) in E, else 2.
     * Hamiltonian cycle exists iff min TSP tour cost == |V|.
     */
    public static int[][] reduceHamCycleToTSP(ReductionGraph g) {
        int n = g.numVertices;
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else if (g.adj[i][j]) {
                    dist[i][j] = 1;
                } else {
                    dist[i][j] = 2; // Non-edge penalty
                }
            }
        }
        return dist;
    }
}

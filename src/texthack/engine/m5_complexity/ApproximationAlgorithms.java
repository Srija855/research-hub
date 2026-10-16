package texthack.engine.m5_complexity;

import texthack.engine.ds.CustomArrayList;
import texthack.engine.ds.CustomDisjointSet;
import texthack.engine.ds.CustomSort;
import texthack.engine.ds.CustomComparator;

/**
 * Approximation Algorithms with Provable Performance Guarantees:
 * 1. Vertex Cover 2-Approximation via Maximal Matching (Cost <= 2 * OPT)
 * 2. Metric TSP 2-Approximation via MST Doubling & Shortcutting
 * 3. Greedy Set Cover (Approximation Ratio: H_n <= ln n + 1)
 * 4. Taxonomy: PTAS, FPTAS, APX
 * Zero java.util.* usage.
 */
public class ApproximationAlgorithms {

    public static class ApproxEdge {
        public int u;
        public int v;
        public int weight;

        public ApproxEdge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    /**
     * 2-Approximation for Minimum Vertex Cover via Maximal Matching:
     * While there exists an uncovered edge (u, v):
     *   Add both u and v to vertex cover C
     *   Remove all edges incident to u or v
     * Guarantee: Any valid vertex cover must pick at least 1 vertex from each matched edge.
     * Since edges are disjoint, |OPT| >= |M|.
     * Our algorithm picks 2|M| vertices, so |C| = 2|M| <= 2 * |OPT|.
     */
    public static CustomArrayList<Integer> vertexCover2Approx(boolean[][] adj, int n) {
        CustomArrayList<Integer> cover = new CustomArrayList<>();
        boolean[] covered = new boolean[n];

        for (int u = 0; u < n; u++) {
            if (covered[u]) continue;
            for (int v = u + 1; v < n; v++) {
                if (adj[u][v] && !covered[v]) {
                    // Pick both endpoints of this maximal matching edge
                    cover.add(u);
                    cover.add(v);
                    covered[u] = true;
                    covered[v] = true;
                    break;
                }
            }
        }
        return cover;
    }

    /**
     * 2-Approximation for Metric TSP via MST Doubling and DFS Shortcutting:
     * 1. Compute MST T of graph G. Cost(T) < OPT(TSP).
     * 2. Double every edge to form Eulerian graph. Cost = 2 * Cost(T).
     * 3. Traverse Eulerian tour with shortcuts bypassing previously visited vertices.
     * 4. By triangle inequality, shortcut cost <= walk cost <= 2 * Cost(T) < 2 * OPT.
     */
    public static CustomArrayList<Integer> metricTSP2Approx(int[][] dist, int n) {
        // Collect edges for Kruskal's MST
        CustomArrayList<ApproxEdge> edges = new CustomArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                edges.add(new ApproxEdge(i, j, dist[i][j]));
            }
        }

        // Sort edges
        ApproxEdge[] edgeArr = new ApproxEdge[edges.size()];
        for (int i = 0; i < edges.size(); i++) edgeArr[i] = edges.get(i);
        CustomSort.mergeSort(edgeArr, new CustomComparator<ApproxEdge>() {
            @Override
            public int compare(ApproxEdge a, ApproxEdge b) {
                return a.weight - b.weight;
            }
        });

        // Kruskal MST
        CustomDisjointSet dsu = new CustomDisjointSet(n);
        CustomArrayList<CustomArrayList<Integer>> mstAdj = new CustomArrayList<>(n);
        for (int i = 0; i < n; i++) mstAdj.add(new CustomArrayList<>());

        for (int i = 0; i < edgeArr.length; i++) {
            ApproxEdge e = edgeArr[i];
            if (dsu.union(e.u, e.v)) {
                mstAdj.get(e.u).add(e.v);
                mstAdj.get(e.v).add(e.u);
            }
        }

        // DFS preorder walk gives shortcutting tour
        boolean[] visited = new boolean[n];
        CustomArrayList<Integer> tour = new CustomArrayList<>();
        dfsTour(0, visited, mstAdj, tour);
        tour.add(0); // return to start

        return tour;
    }

    private static void dfsTour(int u, boolean[] visited, CustomArrayList<CustomArrayList<Integer>> mstAdj, CustomArrayList<Integer> tour) {
        visited[u] = true;
        tour.add(u);
        CustomArrayList<Integer> nbrs = mstAdj.get(u);
        for (int i = 0; i < nbrs.size(); i++) {
            int v = nbrs.get(i);
            if (!visited[v]) {
                dfsTour(v, visited, mstAdj, tour);
            }
        }
    }

    /**
     * Greedy Set Cover:
     * Iteratively selects the subset that covers the maximum number of currently uncovered elements.
     * Ratio: <= H(max_set_size) <= ln(n) + 1.
     */
    public static CustomArrayList<Integer> greedySetCover(int universeSize, CustomArrayList<CustomArrayList<Integer>> subsets) {
        CustomArrayList<Integer> chosen = new CustomArrayList<>();
        boolean[] covered = new boolean[universeSize];
        int numCovered = 0;

        while (numCovered < universeSize) {
            int bestSubIdx = -1;
            int maxNewElements = 0;

            for (int i = 0; i < subsets.size(); i++) {
                int countNew = 0;
                CustomArrayList<Integer> set = subsets.get(i);
                for (int j = 0; j < set.size(); j++) {
                    int elem = set.get(j);
                    if (elem < universeSize && !covered[elem]) {
                        countNew++;
                    }
                }
                if (countNew > maxNewElements) {
                    maxNewElements = countNew;
                    bestSubIdx = i;
                }
            }

            if (bestSubIdx == -1 || maxNewElements == 0) {
                break; // Impossible to cover full universe
            }

            chosen.add(bestSubIdx);
            CustomArrayList<Integer> bestSet = subsets.get(bestSubIdx);
            for (int j = 0; j < bestSet.size(); j++) {
                int elem = bestSet.get(j);
                if (elem < universeSize && !covered[elem]) {
                    covered[elem] = true;
                    numCovered++;
                }
            }
        }
        return chosen;
    }
}

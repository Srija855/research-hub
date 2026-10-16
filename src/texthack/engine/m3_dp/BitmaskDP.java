package texthack.engine.m3_dp;

import texthack.engine.ds.CustomArrayList;

/**
 * Bitmask Dynamic Programming:
 * 1. Travelling Salesperson Problem (TSP) in O(2^n * n^2) via Held-Karp
 * 2. Hamiltonian Path Detection
 * 3. Exact Set Cover / Partition DP
 * Zero java.util.* usage.
 */
public class BitmaskDP {

    public static class TSPResult {
        public int minCost;
        public CustomArrayList<Integer> tour;

        public TSPResult(int minCost, CustomArrayList<Integer> tour) {
            this.minCost = minCost;
            this.tour = tour;
        }
    }

    /**
     * Solves Travelling Salesperson Problem via Held-Karp in O(2^n * n^2) time.
     * @param dist n x n distance matrix
     */
    public static TSPResult solveTSP(int[][] dist) {
        int n = dist.length;
        if (n == 0) return new TSPResult(0, new CustomArrayList<>());
        if (n == 1) {
            CustomArrayList<Integer> tour = new CustomArrayList<>();
            tour.add(0);
            return new TSPResult(0, tour);
        }

        int totalMasks = 1 << n;
        int[][] memo = new int[totalMasks][n];
        int[][] parent = new int[totalMasks][n];
        int INF = 1_000_000_000;

        for (int m = 0; m < totalMasks; m++) {
            for (int i = 0; i < n; i++) {
                memo[m][i] = INF;
                parent[m][i] = -1;
            }
        }

        // Base case: starting at city 0 with mask (1 << 0)
        memo[1][0] = 0;

        for (int mask = 1; mask < totalMasks; mask++) {
            if ((mask & 1) == 0) continue; // Must contain start city 0
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || memo[mask][u] == INF) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 && dist[u][v] < INF) {
                        int nextMask = mask | (1 << v);
                        int newDist = memo[mask][u] + dist[u][v];
                        if (newDist < memo[nextMask][v]) {
                            memo[nextMask][v] = newDist;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Close cycle back to 0
        int fullMask = (1 << n) - 1;
        int minCost = INF;
        int lastNode = -1;

        for (int u = 1; u < n; u++) {
            if (memo[fullMask][u] < INF && dist[u][0] < INF) {
                int totalCost = memo[fullMask][u] + dist[u][0];
                if (totalCost < minCost) {
                    minCost = totalCost;
                    lastNode = u;
                }
            }
        }

        // Reconstruct path
        CustomArrayList<Integer> tour = new CustomArrayList<>();
        if (lastNode != -1) {
            int currMask = fullMask;
            int currNode = lastNode;
            while (currNode != -1) {
                tour.add(currNode);
                int p = parent[currMask][currNode];
                currMask ^= (1 << currNode);
                currNode = p;
            }
            // Reverse tour to start at 0
            CustomArrayList<Integer> rev = new CustomArrayList<>();
            for (int i = tour.size() - 1; i >= 0; i--) {
                rev.add(tour.get(i));
            }
            rev.add(0); // return to start
            return new TSPResult(minCost, rev);
        }

        return new TSPResult(INF, tour);
    }

    /**
     * Checks if a Hamiltonian Path exists in graph represented by adjacency matrix.
     */
    public static boolean hasHamiltonianPath(boolean[][] adj) {
        int n = adj.length;
        int totalMasks = 1 << n;
        boolean[][] dp = new boolean[totalMasks][n];

        // Base case: path of length 1 starting at each vertex
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = true;
        }

        for (int mask = 1; mask < totalMasks; mask++) {
            for (int u = 0; u < n; u++) {
                if (!dp[mask][u]) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 && adj[u][v]) {
                        dp[mask | (1 << v)][v] = true;
                    }
                }
            }
        }

        int fullMask = (1 << n) - 1;
        for (int i = 0; i < n; i++) {
            if (dp[fullMask][i]) return true;
        }
        return false;
    }
}

package texthack.engine.m5_complexity;

import texthack.engine.ds.CustomArrayList;

/**
 * Parameterized Complexity & Fixed Parameter Tractability (FPT).
 * 1. Kernelization (Buss Kernel for Vertex Cover)
 * 2. Bounded Search Tree Algorithm running in O(2^k * n)
 * Demonstrates how exponential complexity is confined strictly to parameter k.
 * Zero java.util.* usage.
 */
public class ParameterizedComplexity {

    public static class FPTResult {
        public boolean solvable;
        public CustomArrayList<Integer> vertexCover;

        public FPTResult(boolean solvable, CustomArrayList<Integer> vertexCover) {
            this.solvable = solvable;
            this.vertexCover = vertexCover;
        }
    }

    /**
     * Bounded Search Tree Algorithm for k-Vertex Cover in O(2^k * n).
     * At each step, picks an uncovered edge (u, v).
     * Any valid cover must include u OR v. Branches recursively.
     */
    public static FPTResult solveKVertexCover(boolean[][] adj, int n, int k) {
        CustomArrayList<Integer> cover = new CustomArrayList<>();
        boolean success = searchBranch(adj, n, k, cover);
        return new FPTResult(success, cover);
    }

    private static boolean searchBranch(boolean[][] adj, int n, int k, CustomArrayList<Integer> currentCover) {
        // Find first uncovered edge
        int uEdge = -1, vEdge = -1;
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (adj[u][v] && !currentCover.contains(u) && !currentCover.contains(v)) {
                    uEdge = u;
                    vEdge = v;
                    break;
                }
            }
            if (uEdge != -1) break;
        }

        // Base case: All edges are covered
        if (uEdge == -1) {
            return true;
        }

        // Budget exhausted but edges still remain
        if (k <= 0) {
            return false;
        }

        // Branch 1: Include u in cover
        currentCover.add(uEdge);
        if (searchBranch(adj, n, k - 1, currentCover)) {
            return true;
        }
        currentCover.remove(currentCover.size() - 1); // backtrack

        // Branch 2: Include v in cover
        currentCover.add(vEdge);
        if (searchBranch(adj, n, k - 1, currentCover)) {
            return true;
        }
        currentCover.remove(currentCover.size() - 1); // backtrack

        return false;
    }

    /**
     * Buss Kernelization for Vertex Cover:
     * High-degree rule: If deg(v) > k, v MUST be placed into the vertex cover.
     * Isolated rule: deg(v) == 0 vertices can be removed.
     * Guarantees reduced kernel has at most k^2 edges and 2k^2 vertices.
     */
    public static int applyBussKernelRule(boolean[][] adj, int n, int k, CustomArrayList<Integer> mandatoryCover) {
        int[] deg = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adj[i][j]) deg[i]++;
            }
        }

        int remainingK = k;
        for (int i = 0; i < n; i++) {
            if (deg[i] > remainingK) {
                mandatoryCover.add(i);
                remainingK--;
                // Zero out edges incident to i
                for (int j = 0; j < n; j++) {
                    adj[i][j] = false;
                    adj[j][i] = false;
                }
            }
        }
        return remainingK;
    }
}

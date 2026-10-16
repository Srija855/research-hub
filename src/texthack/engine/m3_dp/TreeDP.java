package texthack.engine.m3_dp;

import texthack.engine.ds.CustomArrayList;

/**
 * Dynamic Programming on Trees:
 * 1. Maximum Weight Independent Set on Trees
 * 2. Tree Diameter and Centroid Finding
 * 3. Tree Rerooting Technique (calculating sum of distances for all nodes in O(n))
 * Zero java.util.* usage.
 */
public class TreeDP {

    public static class TreeNode {
        public int id;
        public int weight;
        public CustomArrayList<TreeNode> neighbors = new CustomArrayList<>();

        public TreeNode(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }
    }

    /**
     * Maximum Weight Independent Set on a Tree:
     * dp[u][0] = max weight in subtree u if u is NOT included
     * dp[u][1] = max weight in subtree u if u IS included
     */
    public static int maxWeightIndependentSet(TreeNode root, int totalNodes) {
        int[][] dp = new int[totalNodes][2];
        boolean[] visited = new boolean[totalNodes];
        dfsMWIS(root, visited, dp);
        return Math.max(dp[root.id][0], dp[root.id][1]);
    }

    private static void dfsMWIS(TreeNode u, boolean[] visited, int[][] dp) {
        visited[u.id] = true;
        dp[u.id][0] = 0;
        dp[u.id][1] = u.weight;

        for (int i = 0; i < u.neighbors.size(); i++) {
            TreeNode v = u.neighbors.get(i);
            if (!visited[v.id]) {
                dfsMWIS(v, visited, dp);
                // If u is not included, v can be either included or excluded
                dp[u.id][0] += Math.max(dp[v.id][0], dp[v.id][1]);
                // If u is included, children cannot be included
                dp[u.id][1] += dp[v.id][0];
            }
        }
    }

    /**
     * Tree Rerooting Technique:
     * Computes the sum of distances from every node to all other nodes in O(n) total time.
     */
    public static int[] allPairsDistanceSums(CustomArrayList<CustomArrayList<Integer>> adj, int n) {
        int[] subSize = new int[n];
        int[] distSum = new int[n];

        // Pass 1: Bottom-up DFS rooted at node 0
        dfsDown(0, -1, adj, subSize, distSum);

        // Pass 2: Top-down rerooting DFS
        dfsReroot(0, -1, n, adj, subSize, distSum);

        return distSum;
    }

    private static void dfsDown(int u, int p, CustomArrayList<CustomArrayList<Integer>> adj, int[] subSize, int[] distSum) {
        subSize[u] = 1;
        distSum[u] = 0;

        CustomArrayList<Integer> nbrs = adj.get(u);
        for (int i = 0; i < nbrs.size(); i++) {
            int v = nbrs.get(i);
            if (v != p) {
                dfsDown(v, u, adj, subSize, distSum);
                subSize[u] += subSize[v];
                distSum[u] += distSum[v] + subSize[v];
            }
        }
    }

    private static void dfsReroot(int u, int p, int n, CustomArrayList<CustomArrayList<Integer>> adj, int[] subSize, int[] distSum) {
        CustomArrayList<Integer> nbrs = adj.get(u);
        for (int i = 0; i < nbrs.size(); i++) {
            int v = nbrs.get(i);
            if (v != p) {
                // Moving root from u to v:
                // Nodes in v's subtree are 1 step closer (- subSize[v])
                // Nodes outside v's subtree are 1 step farther (+ (n - subSize[v]))
                distSum[v] = distSum[u] - subSize[v] + (n - subSize[v]);
                dfsReroot(v, u, n, adj, subSize, distSum);
            }
        }
    }
}

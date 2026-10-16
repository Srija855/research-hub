package texthack.engine.m3_dp;

/**
 * Interval Dynamic Programming:
 * 1. Matrix-Chain Multiplication (MCM) - O(n^3)
 * 2. Optimal Binary Search Tree (OBST) - O(n^3)
 * Demonstrates optimal subproblem division over sub-intervals [i, j].
 * Zero java.util.* usage.
 */
public class IntervalDP {

    public static class MCMResult {
        public int minScalarMultiplications;
        public String parenthesization;

        public MCMResult(int minScalarMultiplications, String parenthesization) {
            this.minScalarMultiplications = minScalarMultiplications;
            this.parenthesization = parenthesization;
        }
    }

    /**
     * Matrix Chain Multiplication DP in O(n^3) time.
     * @param dims dimensions array where matrix i has size dims[i-1] x dims[i]
     */
    public static MCMResult solveMatrixChain(int[] dims) {
        int n = dims.length - 1; // Number of matrices
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Chain length L from 2 to n
        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int cost = m[i][k] + m[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }
        return new MCMResult(m[1][n], buildParenthesization(s, 1, n));
    }

    private static String buildParenthesization(int[][] s, int i, int j) {
        if (i == j) {
            return "A" + i;
        }
        return "(" + buildParenthesization(s, i, s[i][j]) + " x " + buildParenthesization(s, s[i][j] + 1, j) + ")";
    }

    /**
     * Optimal Binary Search Tree in O(n^3) time.
     * @param keys sorted keys
     * @param p search frequencies for keys[1..n]
     * @param q unsuccesful search frequencies for intervals[0..n]
     */
    public static double solveOBST(String[] keys, double[] p, double[] q) {
        int n = keys.length;
        double[][] e = new double[n + 2][n + 1];
        double[][] w = new double[n + 2][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j - 1] + q[j];
                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                    }
                }
            }
        }
        return e[1][n];
    }
}

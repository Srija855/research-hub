package texthack.engine.m3_dp;

/**
 * Edit Distance Algorithms:
 * 1. Standard Levenshtein (Wagner-Fischer)
 * 2. Damerau-Levenshtein (allows adjacent transposition)
 * 3. Weighted Edit Distance (custom cost matrix)
 * Zero java.util.* usage.
 */
public class EditDistance {

    /**
     * Classic Levenshtein Distance via Wagner-Fischer in O(nm) time, O(min(n,m)) space.
     */
    public static int levenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) return -1;
        int n = s1.length();
        int m = s2.length();
        if (n == 0) return m;
        if (m == 0) return n;

        // Space optimization: keep only previous and current row
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                int del = prev[j] + 1;
                int ins = curr[j - 1] + 1;
                int sub = prev[j - 1] + cost;
                curr[j] = Math.min(del, Math.min(ins, sub));
            }
            System.arraycopy(curr, 0, prev, 0, m + 1);
        }
        return prev[m];
    }

    /**
     * Damerau-Levenshtein Distance:
     * Adds support for adjacent transposition (e.g. "teh" -> "the").
     */
    public static int damerauLevenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) return -1;
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(dp[i - 1][j] + 1,        // deletion
                           Math.min(dp[i][j - 1] + 1,        // insertion
                                    dp[i - 1][j - 1] + cost)); // substitution

                // Transposition
                if (i > 1 && j > 1 && s1.charAt(i - 1) == s2.charAt(j - 2) && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Weighted Edit Distance:
     * Assigns custom costs for insertions, deletions, and character substitutions.
     */
    public static int weightedEditDistance(String s1, String s2, int insertCost, int deleteCost, int subCost) {
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        dp[0][0] = 0;
        for (int i = 1; i <= n; i++) dp[i][0] = i * deleteCost;
        for (int j = 1; j <= m; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int del = dp[i - 1][j] + deleteCost;
                    int ins = dp[i][j - 1] + insertCost;
                    int sub = dp[i - 1][j - 1] + subCost;
                    dp[i][j] = Math.min(del, Math.min(ins, sub));
                }
            }
        }
        return dp[n][m];
    }
}

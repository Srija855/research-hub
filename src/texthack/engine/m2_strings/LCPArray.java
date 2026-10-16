package texthack.engine.m2_strings;

/**
 * Longest Common Prefix (LCP) Array using Kasai's Algorithm in linear O(n) time.
 * Given text T and its Suffix Array SA, computes LCP between consecutive suffixes in SA.
 * Zero java.util.* usage.
 */
public class LCPArray {

    /**
     * Computes the LCP array in O(n) time using Kasai's algorithm.
     * lcp[i] = length of longest common prefix between suffix sa[i] and suffix sa[i-1].
     */
    public static int[] computeLCP(String text, int[] sa) {
        int n = text.length();
        if (n == 0) return new int[0];

        int[] lcp = new int[n];
        int[] rank = new int[n];

        // rank[i] is the position of suffix starting at i in the suffix array
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0; // Length of previously matched prefix minus 1
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // Preceding suffix in suffix array order
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--; // Next suffix starting at i+1 has at least h-1 characters in common
                }
            } else {
                lcp[0] = 0;
            }
        }
        return lcp;
    }

    /**
     * Computes the Longest Common Substring (LCS) between two documents using Suffix Array + LCP.
     */
    public static String computeLongestCommonSubstring(String doc1, String doc2) {
        if (doc1 == null || doc2 == null || doc1.isEmpty() || doc2.isEmpty()) return "";

        // Concatenate using unique sentinel separator
        char sentinel = '\u0001';
        String combined = doc1 + sentinel + doc2;
        int doc1Len = doc1.length();

        SuffixArray saObj = new SuffixArray(combined);
        int[] sa = saObj.getSuffixArray();
        int[] lcp = computeLCP(combined, sa);

        int maxLen = 0;
        int bestStart = 0;

        for (int i = 1; i < combined.length(); i++) {
            int idx1 = sa[i - 1];
            int idx2 = sa[i];

            // Check if one suffix belongs to doc1 and the other belongs to doc2
            boolean oneInDoc1 = (idx1 < doc1Len) != (idx2 < doc1Len);
            if (oneInDoc1) {
                if (lcp[i] > maxLen) {
                    maxLen = lcp[i];
                    bestStart = Math.min(idx1, idx2);
                }
            }
        }

        if (maxLen == 0) return "";
        return combined.substring(bestStart, bestStart + maxLen);
    }
}

package texthack.engine.m2_strings;

import texthack.engine.ds.IntArrayList;

/**
 * String Matching: Naive O(nm) baseline and Knuth-Morris-Pratt (KMP) O(n + m).
 * Demonstrates the transition from quadratic worst-case to linear time matching.
 * Zero java.util.* usage.
 */
public class KMPSearch {

    /**
     * Naive string matching in O(n * m) time.
     */
    public static IntArrayList naiveSearch(String text, String pattern) {
        IntArrayList occurrences = new IntArrayList();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return occurrences;

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == m) {
                occurrences.add(i);
            }
        }
        return occurrences;
    }

    /**
     * Computes the KMP failure function (pi-table / prefix function).
     * pi[i] is the length of the longest proper prefix of pattern[0..i]
     * that is also a suffix of pattern[0..i].
     * Time: O(m), Space: O(m).
     */
    public static int[] computePiTable(String pattern) {
        int m = pattern.length();
        int[] pi = new int[m];
        if (m == 0) return pi;

        pi[0] = 0;
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
                k = pi[k - 1]; // Roll back to previous longest prefix
            }
            if (pattern.charAt(k) == pattern.charAt(i)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Knuth-Morris-Pratt search in linear O(n + m) time.
     */
    public static IntArrayList search(String text, String pattern) {
        IntArrayList occurrences = new IntArrayList();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return occurrences;

        int[] pi = computePiTable(pattern);
        int q = 0; // Number of characters matched

        for (int i = 0; i < n; i++) {
            while (q > 0 && pattern.charAt(q) != text.charAt(i)) {
                q = pi[q - 1]; // Shift without moving text pointer backwards
            }
            if (pattern.charAt(q) == text.charAt(i)) {
                q++;
            }
            if (q == m) {
                occurrences.add(i - m + 1); // Match found at 0-indexed position
                q = pi[q - 1];
            }
        }
        return occurrences;
    }
}

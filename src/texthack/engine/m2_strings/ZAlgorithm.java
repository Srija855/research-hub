package texthack.engine.m2_strings;

import texthack.engine.ds.IntArrayList;

/**
 * Z-Algorithm (Modern linear-time alternative to KMP).
 * Computes Z-array in O(n) time using the [l, r] match interval.
 * Zero java.util.* usage.
 */
public class ZAlgorithm {

    /**
     * Computes the Z-array for string S.
     * Z[i] is the length of the longest common prefix between S and the suffix starting at i.
     * Time: O(n), Space: O(n).
     */
    public static int[] computeZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;

        z[0] = n;
        int l = 0, r = 0; // Current [l, r] segment that matches prefix S[0..r-l]

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                // Inside the existing Z-box: copy previously computed value or boundary
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            // Try to expand beyond the current boundary
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            // Update [l, r] box if we extended beyond r
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Searches for pattern P in text T using Z-algorithm.
     * Constructs concatenated string S = P + "\u0000" + T and computes Z-values.
     */
    public static IntArrayList search(String text, String pattern) {
        IntArrayList occurrences = new IntArrayList();
        int m = pattern.length();
        int n = text.length();
        if (m == 0 || n < m) return occurrences;

        String combined = pattern + "\u0000" + text;
        int[] z = computeZ(combined);

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                occurrences.add(i - (m + 1));
            }
        }
        return occurrences;
    }
}

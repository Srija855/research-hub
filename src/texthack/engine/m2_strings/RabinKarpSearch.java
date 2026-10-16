package texthack.engine.m2_strings;

import texthack.engine.ds.IntArrayList;

/**
 * Rabin-Karp String Search using Double Polynomial Rolling Hash.
 * Employs two large primes to guarantee collision probability < 10^-18.
 * Zero java.util.* usage.
 */
public class RabinKarpSearch {

    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE1 = 313L;
    private static final long BASE2 = 353L;

    public static IntArrayList search(String text, String pattern) {
        IntArrayList occurrences = new IntArrayList();
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || n < m) return occurrences;

        // Compute high-order multiplier (BASE^(m-1) % MOD)
        long power1 = 1;
        long power2 = 1;
        for (int i = 0; i < m - 1; i++) {
            power1 = (power1 * BASE1) % MOD1;
            power2 = (power2 * BASE2) % MOD2;
        }

        // Compute hash of pattern and initial window of text
        long patHash1 = 0, patHash2 = 0;
        long textHash1 = 0, textHash2 = 0;

        for (int i = 0; i < m; i++) {
            patHash1 = (patHash1 * BASE1 + pattern.charAt(i)) % MOD1;
            patHash2 = (patHash2 * BASE2 + pattern.charAt(i)) % MOD2;

            textHash1 = (textHash1 * BASE1 + text.charAt(i)) % MOD1;
            textHash2 = (textHash2 * BASE2 + text.charAt(i)) % MOD2;
        }

        // Slide window across text
        for (int i = 0; i <= n - m; i++) {
            if (textHash1 == patHash1 && textHash2 == patHash2) {
                // Double hash match - verify characters to be 100% sound
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    occurrences.add(i);
                }
            }

            // Rolling hash update for next position
            if (i < n - m) {
                textHash1 = (textHash1 - text.charAt(i) * power1) % MOD1;
                if (textHash1 < 0) textHash1 += MOD1;
                textHash1 = (textHash1 * BASE1 + text.charAt(i + m)) % MOD1;

                textHash2 = (textHash2 - text.charAt(i) * power2) % MOD2;
                if (textHash2 < 0) textHash2 += MOD2;
                textHash2 = (textHash2 * BASE2 + text.charAt(i + m)) % MOD2;
            }
        }
        return occurrences;
    }
}

package texthack.engine.m3_dp;

/**
 * Sum Over Subsets (SOS) Dynamic Programming.
 * Computes F[mask] = sum_{submask in mask} A[submask] for all masks in O(n * 2^n) time
 * instead of the naive O(3^n) submask iteration.
 * Crucial for fast inclusion-exclusion and multi-topic document co-occurrence.
 * Zero java.util.* usage.
 */
public class SOSDP {

    /**
     * Fast Walsh-Hadamard / SOS DP transform in O(n * 2^n).
     * @param a Array of initial values indexed by bitmask
     * @param n Number of bits (dimension)
     * @return Transformed array where result[mask] = sum_{s in mask} a[s]
     */
    public static int[] computeSOS(int[] a, int n) {
        int totalMasks = 1 << n;
        int[] sos = new int[totalMasks];
        System.arraycopy(a, 0, sos, 0, Math.min(a.length, totalMasks));

        for (int i = 0; i < n; i++) {
            for (int mask = 0; mask < totalMasks; mask++) {
                if ((mask & (1 << i)) != 0) {
                    sos[mask] += sos[mask ^ (1 << i)];
                }
            }
        }
        return sos;
    }

    /**
     * Supersets SOS DP: F[mask] = sum_{superset containing mask} A[superset].
     */
    public static int[] computeSupersetSOS(int[] a, int n) {
        int totalMasks = 1 << n;
        int[] sos = new int[totalMasks];
        System.arraycopy(a, 0, sos, 0, Math.min(a.length, totalMasks));

        for (int i = 0; i < n; i++) {
            for (int mask = totalMasks - 1; mask >= 0; mask--) {
                if ((mask & (1 << i)) == 0) {
                    sos[mask] += sos[mask | (1 << i)];
                }
            }
        }
        return sos;
    }
}

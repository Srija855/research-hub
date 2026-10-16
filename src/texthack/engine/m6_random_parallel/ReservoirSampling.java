package texthack.engine.m6_random_parallel;

import texthack.engine.ds.CustomArrayList;

/**
 * Reservoir Sampling for Massive Streaming Data.
 * Samples k uniform random items from an unknown or infinite stream of size N in O(N) time and O(k) space.
 * Proof: Each item has exact probability k / N of being in final sample.
 * Zero java.util.* usage.
 */
public class ReservoirSampling {

    private static long lcgSeed = 543216789L;

    private static synchronized int nextRandomInt(int bound) {
        if (bound <= 0) return 0;
        lcgSeed = (lcgSeed * 6364136223846793005L + 1442695040888963407L);
        long pos = lcgSeed & 0x7fffffffffffffffL;
        return (int) (pos % bound);
    }

    /**
     * Samples k elements uniformly from stream.
     */
    public static <T> CustomArrayList<T> sampleFromStream(CustomArrayList<T> stream, int k) {
        CustomArrayList<T> reservoir = new CustomArrayList<>(k);
        int n = stream.size();
        if (k <= 0 || n == 0) return reservoir;

        // Fill reservoir with first k elements
        int initialLimit = Math.min(n, k);
        for (int i = 0; i < initialLimit; i++) {
            reservoir.add(stream.get(i));
        }

        // Process elements from index k to n - 1
        for (int i = k; i < n; i++) {
            int j = nextRandomInt(i + 1); // Random index between 0 and i inclusive
            if (j < k) {
                // Replace reservoir element at index j
                reservoir.set(j, stream.get(i));
            }
        }

        return reservoir;
    }

    public static void displayMathematicalProof() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  INDUCTION PROOF: UNIFORMITY OF RESERVOIR SAMPLING (Alan G. Waterman / Knuth)  ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  Theorem: At any step N >= k, every item from 1 to N is in the reservoir with");
        System.out.println("           probability exactly k / N.");
        System.out.println("  Base Case (N = k):");
        System.out.println("     The first k elements are placed in the reservoir with probability k / k = 1.");
        System.out.println("  Inductive Step (Assume holds for N - 1, prove for N):");
        System.out.println("     1. The N-th item is chosen with probability k / N.");
        System.out.println("     2. Any previous item x (1 <= x < N) was in reservoir at step N - 1 with prob k / (N-1).");
        System.out.println("     3. Item x survives step N if either:");
        System.out.println("        - The N-th item is NOT chosen (prob: 1 - k / N), OR");
        System.out.println("        - The N-th item IS chosen, but item x is NOT the one evicted (prob: 1 - 1/k).");
        System.out.println("     4. Survival probability:");
        System.out.println("        P(survive) = (1 - k/N) + (k/N) * (1 - 1/k) = 1 - k/N + k/N - 1/N = 1 - 1/N = (N - 1) / N.");
        System.out.println("     5. Total probability x is in reservoir at step N:");
        System.out.println("        P(in reservoir) = [k / (N - 1)] * [(N - 1) / N] = k / N.  Q.E.D.");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

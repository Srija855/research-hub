package texthack.engine.m6_random_parallel;

/**
 * Parallel Algorithm Primitives:
 * 1. Parallel Reduce (Work O(n), Span O(log n))
 * 2. Parallel Scan / Prefix-Sum (Blelloch Algorithm: Up-Sweep & Down-Sweep)
 * 3. Parallel Merge Sort
 * 4. Architectural Analysis: Data-Parallel vs Task-Parallel
 * Zero java.util.* usage.
 */
public class ParallelPrimitives {

    /**
     * Blelloch Work-Efficient Parallel Prefix Sum (Exclusive Scan).
     * Simulates the 2-pass binary tree algorithm:
     * - Up-Sweep (Reduce pass): computes sum of subtrees in O(log n) span.
     * - Down-Sweep pass: replaces root with 0 and propagates prefix sums down in O(log n) span.
     * Work: O(n), Span: O(log n).
     */
    public static int[] blellochScan(int[] input) {
        // Pad to next power of 2
        int n = input.length;
        int m = 1;
        while (m < n) m <<= 1;

        int[] a = new int[m];
        System.arraycopy(input, 0, a, 0, n);

        // 1. Up-Sweep (Reduce) phase
        for (int d = 0; d < log2(m); d++) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += step) {
                a[k + step - 1] = a[k + half - 1] + a[k + step - 1];
            }
        }

        // 2. Clear last element (identity element for addition = 0)
        a[m - 1] = 0;

        // 3. Down-Sweep phase
        for (int d = log2(m) - 1; d >= 0; d--) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < m; k += step) {
                int t = a[k + half - 1];
                a[k + half - 1] = a[k + step - 1];
                a[k + step - 1] = a[k + step - 1] + t;
            }
        }

        int[] result = new int[n];
        System.arraycopy(a, 0, result, 0, n);
        return result;
    }

    /**
     * Parallel Tree Reduce:
     * Work: O(n), Span: O(log n).
     */
    public static int parallelReduce(int[] arr) {
        if (arr == null || arr.length == 0) return 0;
        int[] copy = new int[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);

        int active = copy.length;
        while (active > 1) {
            int half = (active + 1) / 2;
            for (int i = 0; i < active / 2; i++) {
                copy[i] = copy[i] + copy[active - 1 - i];
            }
            active = half;
        }
        return copy[0];
    }

    /**
     * Multi-threaded Parallel Array Processing using custom Java Threads.
     */
    public static void parallelTransform(final int[] data, final int numThreads) {
        Thread[] threads = new Thread[numThreads];
        int n = data.length;
        int chunkSize = (n + numThreads - 1) / numThreads;

        for (int t = 0; t < numThreads; t++) {
            final int start = t * chunkSize;
            final int end = Math.min(start + chunkSize, n);

            threads[t] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = start; i < end; i++) {
                        data[i] = data[i] * 2 + 1; // Arbitrary element-wise kernel
                    }
                }
            });
            threads[t].start();
        }

        for (int t = 0; t < numThreads; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static int log2(int x) {
        int r = 0;
        while ((1 << r) < x) r++;
        return r;
    }

    public static void displayParallelTradeoffs() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  PARALLEL ARCHITECTURES: WHERE PARALLELISM SHINES VS WHERE IT FAILS");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  1. Embarrassingly Parallel / Data-Parallel (High Speedup):");
        System.out.println("     - Element-wise array transformations, Map operations, Ray tracing.");
        System.out.println("     - Near-linear speedup S_p ~ P (Work T_1 = O(n), Span T_oo = O(1)).");
        System.out.println("  2. Tree-Structured Primitives (Logarithmic Span):");
        System.out.println("     - Parallel Reduce (Sum, Max) and Parallel Prefix-Sum (Blelloch scan).");
        System.out.println("     - Work T_1 = O(n), Span T_oo = O(log n). Yields high parallelism n / log n.");
        System.out.println("  3. Where Parallelism Fails (Negative Speedup):");
        System.out.println("     - High synchronization overhead / lock contention.");
        System.out.println("     - Sequential bottleneck (Amdahl's Law limit: 1 / ((1 - p) + p / P)).");
        System.out.println("     - False sharing (multiple cores modifying adjacent cache line bytes).");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

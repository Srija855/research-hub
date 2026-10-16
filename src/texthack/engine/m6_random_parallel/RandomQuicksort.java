package texthack.engine.m6_random_parallel;

/**
 * Randomised Quicksort (Las Vegas Algorithm).
 * Hand-built Linear Congruential Generator (LCG) for pivot randomization.
 * Eliminates adversarial O(n^2) worst-case degradation.
 * Zero java.util.* usage.
 */
public class RandomQuicksort {

    // Hand-built LCG Random generator parameters (Knuth MMIX)
    private static long lcgSeed = 123456789L;

    private static synchronized int nextRandomInt(int bound) {
        if (bound <= 0) return 0;
        lcgSeed = (lcgSeed * 6364136223846793005L + 1442695040888963407L);
        long positive = lcgSeed & 0x7fffffffffffffffL;
        return (int) (positive % bound);
    }

    public static class SortStats {
        public long comparisons;
        public long durationNanos;

        public SortStats(long comparisons, long durationNanos) {
            this.comparisons = comparisons;
            this.durationNanos = durationNanos;
        }
    }

    public static SortStats sort(int[] a) {
        if (a == null || a.length <= 1) return new SortStats(0, 0);
        long startTime = System.nanoTime();
        long[] compCounter = new long[1];
        quickSortRandom(a, 0, a.length - 1, compCounter);
        long elapsed = System.nanoTime() - startTime;
        return new SortStats(compCounter[0], elapsed);
    }

    private static void quickSortRandom(int[] a, int low, int high, long[] compCounter) {
        if (low < high) {
            // Pick uniformly random pivot in [low, high]
            int randomIndex = low + nextRandomInt(high - low + 1);
            swap(a, randomIndex, high);

            int p = partition(a, low, high, compCounter);
            quickSortRandom(a, low, p - 1, compCounter);
            quickSortRandom(a, p + 1, high, compCounter);
        }
    }

    private static int partition(int[] a, int low, int high, long[] compCounter) {
        int pivot = a[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            compCounter[0]++;
            if (a[j] <= pivot) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}

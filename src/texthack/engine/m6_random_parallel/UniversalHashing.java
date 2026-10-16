package texthack.engine.m6_random_parallel;

/**
 * Universal Hash Families & 2-Independent Randomised Hashing.
 * Implements Carter-Wegman 2-independent hash family:
 * h_{a,b}(x) = ((a * x + b) % p) % m
 * Guarantees collision probability: Pr[h(x) == h(y)] <= 1/m for any distinct x, y.
 * Zero java.util.* usage.
 */
public class UniversalHashing {

    private final long a;
    private final long b;
    private final long prime;
    private final int numBuckets;

    // Mersenne prime 2^31 - 1
    public static final long MERSENNE_PRIME_31 = 2147483647L;

    public UniversalHashing(long a, long b, int numBuckets) {
        this.prime = MERSENNE_PRIME_31;
        this.a = (a % (prime - 1)) + 1; // a in [1, prime - 1]
        this.b = b % prime;             // b in [0, prime - 1]
        this.numBuckets = numBuckets;
    }

    public int hash(long key) {
        long nonNegativeKey = key & 0x7fffffffffffffffL;
        long val = (MillerRabinPrimality.mulMod(a, nonNegativeKey % prime, prime) + b) % prime;
        return (int) (val % numBuckets);
    }

    public static double benchmarkCollisionRate(long[] keys, int buckets, int trials) {
        int totalCollisions = 0;
        long totalPairs = (long) keys.length * (keys.length - 1) / 2;

        for (int t = 0; t < trials; t++) {
            long a = 1 + (t * 6364136223846793005L + 1) % (MERSENNE_PRIME_31 - 1);
            long b = (t * 1442695040888963407L) % MERSENNE_PRIME_31;
            if (a < 0) a = -a;
            if (b < 0) b = -b;
            if (a == 0) a = 1;

            UniversalHashing uh = new UniversalHashing(a, b, buckets);
            int[] bucketCounts = new int[buckets];
            for (int k = 0; k < keys.length; k++) {
                int h = uh.hash(keys[k]);
                bucketCounts[h]++;
            }

            for (int i = 0; i < buckets; i++) {
                int c = bucketCounts[i];
                if (c > 1) {
                    totalCollisions += (c * (c - 1)) / 2;
                }
            }
        }

        return (double) totalCollisions / (trials * totalPairs);
    }
}

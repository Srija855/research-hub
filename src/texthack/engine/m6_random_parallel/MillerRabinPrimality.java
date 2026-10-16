package texthack.engine.m6_random_parallel;

import texthack.engine.ds.CustomArrayList;

/**
 * Miller-Rabin Primality Test (Monte Carlo Algorithm).
 * Solves the Carmichael-number challenge and provides witness certificates.
 * Error probability <= (1/4)^k for k independent random bases.
 * Zero java.util.* usage.
 */
public class MillerRabinPrimality {

    private static long lcgSeed = 987654321L;

    private static synchronized long nextRandomLong(long min, long max) {
        lcgSeed = (lcgSeed * 6364136223846793005L + 1442695040888963407L);
        long pos = lcgSeed & 0x7fffffffffffffffL;
        return min + (pos % (max - min + 1));
    }

    public static class PrimalityResult {
        public long number;
        public boolean isPrime;
        public int iterationsTested;
        public double errorProbabilityUpperLimit;
        public Long witnessDisproof; // Witness to compositeness if found
        public CustomArrayList<Long> testedBases;

        public PrimalityResult(long number, boolean isPrime, int iterationsTested,
                               double errorProbabilityUpperLimit, Long witnessDisproof,
                               CustomArrayList<Long> testedBases) {
            this.number = number;
            this.isPrime = isPrime;
            this.iterationsTested = iterationsTested;
            this.errorProbabilityUpperLimit = errorProbabilityUpperLimit;
            this.witnessDisproof = witnessDisproof;
            this.testedBases = testedBases;
        }

        @Override
        public String toString() {
            if (isPrime) {
                return String.format("Number %d is PROBABLY PRIME (Rounds: %d, Error bound: <= %.2e)",
                        number, iterationsTested, errorProbabilityUpperLimit);
            } else {
                return String.format("Number %d is COMPOSITE (Witness disproof: base a = %s)",
                        number, witnessDisproof != null ? witnessDisproof.toString() : "deterministic");
            }
        }
    }

    /**
     * Russian Peasant binary multiplication to compute (a * b) % mod safely for 64-bit longs.
     */
    public static long mulMod(long a, long b, long mod) {
        long res = 0;
        a %= mod;
        b %= mod;
        while (b > 0) {
            if ((b & 1) != 0) {
                res = (res + a) % mod;
            }
            a = (a + a) % mod;
            b >>= 1;
        }
        return res;
    }

    /**
     * Binary modular exponentiation: (base^exp) % mod
     */
    public static long powerMod(long base, long exp, long mod) {
        long res = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) != 0) {
                res = mulMod(res, base, mod);
            }
            base = mulMod(base, base, mod);
            exp >>= 1;
        }
        return res;
    }

    /**
     * Naive Fermat Test to illustrate vulnerability to Carmichael numbers.
     */
    public static boolean fermatTest(long n, long a) {
        if (n <= 1 || a >= n) return false;
        return powerMod(a, n - 1, n) == 1;
    }

    /**
     * Executes Miller-Rabin Primality Test for k iterations.
     */
    public static PrimalityResult isPrime(long n, int k) {
        if (n < 2) return new PrimalityResult(n, false, 0, 0, null, new CustomArrayList<>());
        if (n == 2 || n == 3) return new PrimalityResult(n, true, k, 0, null, new CustomArrayList<>());
        if (n % 2 == 0) return new PrimalityResult(n, false, 0, 0, 2L, new CustomArrayList<>());

        // Factor n - 1 as 2^s * d with d odd
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        CustomArrayList<Long> bases = new CustomArrayList<>();

        // Test with deterministic bases first for small numbers, or random bases
        for (int iter = 0; iter < k; iter++) {
            long a = nextRandomLong(2, n - 2);
            bases.add(a);

            long x = powerMod(a, d, n);
            if (x == 1 || x == n - 1) {
                continue; // Passes this round
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = mulMod(x, x, n);
                if (x == n - 1) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                // 'a' is a Miller-Rabin witness that n is definitely composite!
                return new PrimalityResult(n, false, iter + 1, 0, a, bases);
            }
        }

        // Passed all k rounds
        double errorRate = Math.pow(0.25, k);
        return new PrimalityResult(n, true, k, errorRate, null, bases);
    }
}

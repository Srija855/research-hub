package texthack.engine.m6_random_parallel;

/**
 * Theoretical Framework: Randomised & Parallel Algorithms Canon.
 * Encapsulates:
 * - Las Vegas vs Monte Carlo paradigm
 * - Brent's Theorem & Work-Span analysis
 * - Uniform Reservoir Sampling proof
 * Zero java.util.* usage.
 */
public class RandomAnalysis {

    public static void displayFoundations() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("       MODULE 6: RANDOMISED & PARALLEL ALGORITHMS THEORETICAL CANON             ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("1. LAS VEGAS vs MONTE CARLO TAXONOMY:");
        System.out.println("   * Las Vegas Algorithms:");
        System.out.println("     - Correctness is GUARANTEED 100% deterministic (never produces a wrong answer).");
        System.out.println("     - Execution time is a random variable.");
        System.out.println("     - Canonical Example: Randomised QuickSort (Expected time O(n log n), worst O(n^2)).");
        System.out.println("   * Monte Carlo Algorithms:");
        System.out.println("     - Execution time is strictly DETERMINISTIC and BOUNDED.");
        System.out.println("     - Correctness holds with high probability (bounded error rate epsilon).");
        System.out.println("     - Canonical Example: Miller-Rabin Primality Test (Error probability <= (1/4)^k).");
        System.out.println();
        System.out.println("2. THE CARMICHAEL NUMBER CHALLENGE & MILLER-RABIN WITNESS:");
        System.out.println("   * Fermat's Little Theorem states: if p is prime and gcd(a, p) = 1, then a^(p-1) == 1 (mod p).");
        System.out.println("   * Failure of Fermat: Carmichael numbers (e.g. 561 = 3 * 11 * 17, 1105, 1729) satisfy");
        System.out.println("     a^(n-1) == 1 (mod n) for ALL coprime bases 'a', fooling Fermat primality testing!");
        System.out.println("   * Miller-Rabin Solution: Factoring n - 1 = 2^s * d. By Euclid's lemma, the only square roots");
        System.out.println("     of 1 modulo a prime are +1 and -1. If we encounter x^2 == 1 (mod n) where x != +-1,");
        System.out.println("     we have discovered a non-trivial witness proving n is composite.");
        System.out.println();
        System.out.println("3. PARALLEL WORK-SPAN MODEL & BRENT'S THEOREM:");
        System.out.println("   * T_1 (Work): Total sequential operations.");
        System.out.println("   * T_infinity (Span / Critical Path): Longest dependency chain (minimum execution time on oo cores).");
        System.out.println("   * Parallelism: Ratio T_1 / T_infinity.");
        System.out.println("   * Brent's Theorem: On P physical processors, runtime T_P satisfies:");
        System.out.println("     T_P <= (T_1 / P) + T_infinity");
        System.out.println("   * Speedup Ceiling: S_P = T_1 / T_P <= min(P, T_1 / T_infinity).");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

package texthack.engine.m3_dp;

/**
 * Diagnostic Reference: When Dynamic Programming is the WRONG Tool.
 * Explains failure modes and architectural boundaries:
 * 1. Independent Non-overlapping subproblems -> Use Divide & Conquer
 * 2. Matroid / Greedy-choice property holds -> Use Greedy (O(n log n))
 * 3. Unbounded / Continuous state spaces -> Use Mathematical Programming or Branch & Bound
 * Zero java.util.* usage.
 */
public class DPDecisionGuide {

    public static void displayDecisionGuide() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  CRITICAL ARCHITECTURE: WHEN DYNAMIC PROGRAMMING IS THE WRONG TOOL");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  1. No Overlapping Subproblems:");
        System.out.println("     - Example: MergeSort, Fast Fourier Transform (FFT).");
        System.out.println("     - Subproblems are strictly disjoint trees. Memoization introduces useless table");
        System.out.println("       overhead with 0 cache hits. Use standard Divide & Conquer.");
        System.out.println("  2. Greedy-Choice Property Holds (Matroid Structure):");
        System.out.println("     - Example: Minimum Spanning Tree (Kruskal/Prim), Fractional Knapsack, Huffman Coding.");
        System.out.println("     - DP would take O(V^2) or O(nW) where a greedy choice solves it in O(E log V)");
        System.out.println("       or O(n log n) with provable optimality.");
        System.out.println("  3. Exponential State Space Without Bitmask Compression:");
        System.out.println("     - Example: 0/1 Knapsack with weight W = 10^15; General TSP with N = 100.");
        System.out.println("     - Pseudo-polynomial DP table size O(n * 10^15) exceeds memory. Must turn to");
        System.out.println("       Branch-and-Bound, Meet-in-the-Middle O(2^(n/2)), or FPTAS approximations.");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

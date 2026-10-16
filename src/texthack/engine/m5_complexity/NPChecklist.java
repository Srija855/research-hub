package texthack.engine.m5_complexity;

/**
 * Computational Complexity Foundations & Practical Engineer's NP-Hardness Checklist.
 * Explores:
 * - Decision vs Optimization problems
 * - P, NP, co-NP via polynomial witnesses/certificates
 * - Cook-Levin Theorem & SAT
 * - Engineer's Structural Checklist
 * Zero java.util.* usage.
 */
public class NPChecklist {

    public static void displayChecklist() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  ENGINEER'S NP-HARDNESS RECOGNITION CHECKLIST & COMPLEXITY CANON");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  1. Formal Definitions:");
        System.out.println("     * Decision Problem: Output is YES or NO (e.g., 'Is there a clique of size >= k?').");
        System.out.println("     * Optimization Problem: Find best configuration (e.g., 'Find maximum clique size').");
        System.out.println("     * Polynomial Reductions (Karp): A <=_p B means an oracle for B solves A in poly-time.");
        System.out.println("     * P: Problems solvable in polynomial time O(n^c).");
        System.out.println("     * NP: Problems with polynomial-time verifiable YES-witnesses/certificates.");
        System.out.println("     * co-NP: Problems with polynomial-time verifiable NO-witnesses/disproofs.");
        System.out.println("     * NP-Complete: In NP and every problem in NP reduces to it (Cook-Levin: SAT).");
        System.out.println();
        System.out.println("  2. Practical Engineer's Checklist (Warning signs of NP-Hardness in System Design):");
        System.out.println("     [!] 1. Subset Selection with Capacity: Does your feature require choosing a subset");
        System.out.println("            of items summing exactly or maximally to a threshold? (Subset-Sum / Knapsack)");
        System.out.println("     [!] 2. Exact Coverage: Do you need to cover all elements using minimum subsets? (Set Cover)");
        System.out.println("     [!] 3. Conflict Minimization: Must mutually incompatible tasks be segregated? (Graph Coloring)");
        System.out.println("     [!] 4. Ordering / Route Optimization: Must an agent visit all locations once? (TSP / Ham-Cycle)");
        System.out.println("     [!] 5. Clustered Subgraphs: Are you searching for densely interconnected clusters? (Max Clique)");
        System.out.println("     [!] 6. Multi-Resource Scheduling: Assigning tasks with non-preemptive deadlines to multiple cores?");
        System.out.println("     => IF YES: Stop trying to design an exact polynomial greedy/DP algorithm!");
        System.out.println("        Turn immediately to Approximation, FPT (Parameterized), or Heuristics.");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

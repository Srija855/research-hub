# ResearchHub: TextHack Advanced DSA Engine

**A High-Performance Text Analytics & Algorithmic Problem-Solving Engine for Indian-Language Research Corpora**  
*Curriculum Mapping: CLRS Part VII | Kleinberg-Tardos | Erickson | DSA-3 Full Syllabus Coverage*

---

## 📌 Executive Summary & Problem Statement

Modern text-analytics and knowledge-graph processing engines face strict algorithmic bottlenecks:
1. **Linear and Basic Tree/Graph Algorithms (from DSA-1 / DSA-2) Fail**:
   - Substring searching with naive methods explodes to $O(n \cdot m)$ across large multilingual archives.
   - Greedy methods fail to compute optimal sequence alignments with gap penalties.
   - Standard shortest path algorithms cannot model capacity-constrained citation propagation or dual bottleneck cuts.
   - Brute-force state space exploration fails on combinatorial scheduling and coverage tasks.
   - Deterministic prime testing and hashing require exponential time or huge memory tables.
2. **TextHack's Objective**: Hand-build a zero-dependency, advanced analytics engine that executes:
   - **Pattern Search**: Linear-time exact and multi-pattern string matching.
   - **Fuzzy Matching**: Alignment and distance algorithms handling phonetic variations and OCR noise.
   - **Document Similarity**: Cache-efficient suffix array indexing and linear-time longest common prefix (LCP) analysis.
   - **Citation Flow**: Network max-flow and dual min-cut capacity bottlenecks.
   - **Project Scheduling**: Reductions to Min-Cut and $2$-approximation algorithms for NP-hard resource allocation.
   - **Cryptographic Primality**: Monte Carlo randomized testing handling Carmichael pseudoprimes.
3. **Core Engine Constraint**: **STRICTLY ZERO `java.util.*` IMPORTS**. Every data structure (`CustomArrayList`, `IntArrayList`, `CustomHashMap`, `CustomQueue`, `IntQueue`, `CustomStack`, `CustomMinHeap`, `CustomDisjointSet`, `CustomSort`, `CustomStringBuilder`) has been designed and implemented from first principles.

---

## 🗺️ Comprehensive DSA-3 Curriculum Mapping (Modules M1 – M6)

| Module | Core Domain | Algorithmic Concepts Implemented | Project Use Case & Datasets | Asymptotic Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **M1** | **Foundations & Architecture** | Clean API design, Asymptotic complexity matrix, Hand-built Custom DS | Public API (`TextHackEngine`), Custom data structures with zero `java.util.*` | Space: $O(N)$<br>Zero stdlib |
| **M2** | **Advanced String Algorithms** | KMP ($\pi$-table), Z-Algorithm ($[l,r]$ box), Rabin-Karp (Double Hash), Aho-Corasick Automaton, Suffix Arrays (Prefix Doubling), Kasai LCP Array | Full-text substring search, multi-term index, document similarity on Indian Wikipedia articles (`corpus.dat`) | Preproc: $O(m)$ / $O(n \log^2 n)$<br>Query: $O(n+m)$ |
| **M3** | **Advanced Dynamic Programming** | Wagner-Fischer Levenshtein, Damerau-Levenshtein (transposition), Needleman-Wunsch & Smith-Waterman, Interval DP (MCM, OBST), Bitmask DP (Held-Karp TSP), Tree DP with Rerooting, SOS DP | Fuzzy search on transliterated titles, phonological alignment, survey routing, topic subset co-occurrence | Levenshtein: $O(nm)$<br>MCM: $O(n^3)$<br>TSP: $O(2^n n^2)$<br>SOS: $O(n 2^n)$ |
| **M4** | **Network Flow & Reductions** | Ford-Fulkerson, Edmonds-Karp ($O(VE^2)$), Dinic’s Blocking Flow ($O(V^2E)$), Min-Cut Dual, Bipartite Matching (König's Theorem), Min-Cost Max-Flow (SSP/SPFA), Project Selection Reduction | Citation flow bottlenecks (`citations.dat`), student-project assignment, optimal research portfolio (`projects.dat`) | Dinic: $O(V^2 E)$<br>Min-Cut: $O(V^2 E)$<br>Matching: $O(E\sqrt{V})$ |
| **M5** | **NP-Completeness & Approximations** | Decision vs Optimization, Karp Reductions (3-SAT $\to$ Clique $\to$ IS $\to$ VC, Ham-Cycle $\to$ TSP), Engineer's NP Checklist, Maximal Matching 2-Approx for Vertex Cover, Metric TSP 2-Approx, Greedy Set Cover, Buss Kernel & Bounded Search Tree FPT | Resource conflict resolution, metric scheduling tour, fixed-parameter tractable (FPT) scheduling ($k=3$) | Approx Ratio $\le 2 \cdot \text{OPT}$<br>FPT: $O(2^k \cdot n)$ |
| **M6** | **Randomised & Parallel Algorithms** | Las Vegas vs Monte Carlo, Randomised QuickSort (expected $O(n \log n)$), Miller-Rabin Primality, Carmichael Number Disproof, Universal Hashing, Uniform Reservoir Sampling, Blelloch Parallel Prefix Scan | Fast streaming corpus sampling, cryptographic key verification (`test_primes.dat`), parallel prefix sums | Miller-Rabin: $O(k \log^3 n)$<br>Error: $\le (1/4)^k$<br>Blelloch: Work $O(n)$, Span $O(\log n)$ |

---

## 🏗️ System Architecture & Data Directory

All datasets reside exclusively in `.dat` format under the `data/` directory:
* [`data/corpus.dat`](file:///Users/garimellasrija/Downloads/reasearch%20hub/data/corpus.dat): 15 articles covering historical and scientific Indian treatises (Aryabhata, Panini, Pingala, Bhaskara, Nalanda, Madhava, Ramanujan, Modern NLP, Quantum Theory, Network Flow).
* [`data/citations.dat`](file:///Users/garimellasrija/Downloads/reasearch%20hub/data/citations.dat): Directed citation graph with forward capacities and unit transmission costs.
* [`data/projects.dat`](file:///Users/garimellasrija/Downloads/reasearch%20hub/data/projects.dat): 8 research initiatives with positive revenue rewards, negative engineering costs, and prerequisite dependencies.
* [`data/test_primes.dat`](file:///Users/garimellasrija/Downloads/reasearch%20hub/data/test_primes.dat): Benchmarks including 7 Carmichael numbers ($561, 1105, 1729, 2465, 2821, 6601, 8911$), large primes ($10^9+7, 10^{18}+3, Mersenne\ 2^{31}-1$), and large composites ($10^{18}+1, 10^{18}+7$).

---

## ⚡ How to Run

### Option 1: In Eclipse IDE
1. Launch **Eclipse IDE**.
2. Select **File $\to$ Open Projects from File System...** (or **Import... $\to$ Existing Projects into Workspace**).
3. Browse and select the project directory:  
   `/Users/garimellasrija/Downloads/reasearch hub`
4. The `.project` and `.classpath` configurations will automatically configure `src/` as the build source and `bin/` as the target.
5. In the Project Explorer, open `src/texthack/TextHackApp.java`.
6. Right-click $\to$ **Run As $\to$ Java Application**.
7. The automated diagnostic benchmark suite and interactive menu will display in the Eclipse Console.

### Option 2: From Terminal
```bash
# Make scripts executable and build binaries into bin/
chmod +x build.sh run.sh
./build.sh

# Run the complete automated diagnostic benchmark suite
./run.sh --all

# Launch the interactive console explorer menu
./run.sh
```

---

## 📊 Sample Test Results & Verifications

```text
================================================================================
[QUERY: CITATION_FLOW_ANALYSIS] | Input: "DOC001 -> DOC015"
Algorithm: Dinic's Algorithm (Level Graph & Blocking Flow)
Complexity: Time O(V^2 * E) | Space O(V + E)
  * Maximum Citation Flow: 15 units
  * Max-Flow Min-Cut Verification: Cut Capacity = 15 units (Exact Dual Match)
  * Bottleneck Citation: [DOC009] -> [DOC012] (Capacity: 15)
================================================================================

--- OPTIMAL RESEARCH PROJECT SELECTION VIA MIN-CUT REDUCTION ---
  * Maximum Optimal Net Profit: +$320K
  * Selected Feasible Project Portfolio: PRJ01, PRJ02, PRJ03, PRJ07, PRJ08

--- MILLER-RABIN PRIMALITY TEST (CARMICHAEL CHALLENGE) ---
  * 561 (First Carmichael Number 3*11*17): COMPOSITE (Witness base a = 412)
  * 1729 (Hardy-Ramanujan Number 7*13*19): COMPOSITE (Witness base a = 38)
  * 1000000007 (Competitive Programming Prime): PROBABLY PRIME (Error bound <= 9.54e-07)
  * 1000000000000000003 (Large 19-digit prime 10^18 + 3): PROBABLY PRIME (Error bound <= 9.54e-07)
```

---

## 🎓 Viva & Technical Evaluation Quick Reference

* **Why is `java.util.*` forbidden?**  
  To ensure mastery of core algorithms and cache-level data structures. All dynamic arrays, hash tables with chaining, queues, stacks, disjoint sets, and heaps were written from scratch.
* **Why does Miller-Rabin succeed where Fermat fails?**  
  Fermat's test ($a^{n-1} \equiv 1 \pmod n$) is fooled by Carmichael numbers because they satisfy the congruence for all coprime bases. Miller-Rabin detects non-trivial square roots of $1 \pmod n$ during binary exponentiation ($x^2 \equiv 1 \pmod n \implies x \equiv \pm 1$). Any other value certifies compositeness.
* **How does Project Selection reduce to Min-Cut?**  
  Positive-profit projects connect from source $S$ with capacity equal to profit. Negative-cost projects connect to sink $T$ with capacity equal to absolute cost. Dependencies have infinite capacity $\infty$. The minimum cut partitions chosen vs rejected projects, maximizing $\sum \text{Profits} - \text{Cut Capacity}$.

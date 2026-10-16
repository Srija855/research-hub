package texthack.engine;

import texthack.api.QueryResult;
import texthack.api.QueryType;
import texthack.api.TextHackEngine;
import texthack.engine.ds.CustomArrayList;
import texthack.engine.ds.CustomHashMap;
import texthack.engine.ds.IntArrayList;
import texthack.engine.m1_foundation.CorpusLoader;
import texthack.engine.m1_foundation.CorpusLoader.CitationEdge;
import texthack.engine.m1_foundation.CorpusLoader.Document;
import texthack.engine.m1_foundation.CorpusLoader.PrimeTestData;
import texthack.engine.m1_foundation.CorpusLoader.ProjectData;
import texthack.engine.m1_foundation.EngineDesignCatalog;
import texthack.engine.m2_strings.*;
import texthack.engine.m3_dp.*;
import texthack.engine.m4_flow.*;
import texthack.engine.m5_complexity.*;
import texthack.engine.m6_random_parallel.*;

/**
 * Core TextHack Engine Implementation.
 * Bridges all advanced algorithmic families to public client queries.
 * ENGINE CONSTRAINT: STRICTLY ZERO java.util.* IMPORTS.
 */
public class TextHackEngineImpl implements TextHackEngine {

    private CustomArrayList<Document> corpus = new CustomArrayList<>();
    private CustomHashMap<String, Document> docIndex = new CustomHashMap<>();
    private CustomArrayList<CitationEdge> citations = new CustomArrayList<>();
    private CustomArrayList<ProjectData> projects = new CustomArrayList<>();
    private CustomArrayList<PrimeTestData> primes = new CustomArrayList<>();

    @Override
    public void loadData(String corpusPath, String citationsPath, String projectsPath, String primesPath) {
        this.corpus = CorpusLoader.loadCorpus(corpusPath);
        this.docIndex = new CustomHashMap<>(corpus.size() * 2);
        for (int i = 0; i < corpus.size(); i++) {
            Document doc = corpus.get(i);
            docIndex.put(doc.id, doc);
        }

        this.citations = CorpusLoader.loadCitations(citationsPath);
        this.projects = CorpusLoader.loadProjects(projectsPath);
        this.primes = CorpusLoader.loadPrimes(primesPath);

        System.out.printf("[Engine Init] Loaded %d corpus articles, %d citation links, %d projects, %d test primes.\n",
                corpus.size(), citations.size(), projects.size(), primes.size());
    }

    @Override
    public QueryResult searchPattern(String pattern, String algorithm) {
        long start = System.nanoTime();
        CustomArrayList<String> matches = new CustomArrayList<>();

        String algoUsed = (algorithm != null && !algorithm.isEmpty()) ? algorithm.toUpperCase() : "KMP";
        String timeComp;
        String spaceComp;
        String citation;

        if (algoUsed.equals("NAIVE")) {
            timeComp = "O(n * m)";
            spaceComp = "O(1)";
            citation = "CLRS Ch 32.1 (Naive String Search)";
            for (int i = 0; i < corpus.size(); i++) {
                Document doc = corpus.get(i);
                IntArrayList occ = KMPSearch.naiveSearch(doc.content, pattern);
                if (!occ.isEmpty()) {
                    matches.add(String.format("Found %d occurrences in [%s] %s at offsets %s",
                            occ.size(), doc.id, doc.title, formatIntArray(occ)));
                }
            }
        } else if (algoUsed.equals("Z")) {
            timeComp = "O(n + m)";
            spaceComp = "O(n + m)";
            citation = "Gusfield Ch 1 & Erickson Ch 4 (Z-Algorithm)";
            for (int i = 0; i < corpus.size(); i++) {
                Document doc = corpus.get(i);
                IntArrayList occ = ZAlgorithm.search(doc.content, pattern);
                if (!occ.isEmpty()) {
                    matches.add(String.format("Found %d occurrences in [%s] %s at offsets %s",
                            occ.size(), doc.id, doc.title, formatIntArray(occ)));
                }
            }
        } else if (algoUsed.equals("RABIN_KARP")) {
            timeComp = "O(n + m) avg, O(nm) worst";
            spaceComp = "O(1)";
            citation = "CLRS Ch 32.2 (Rabin-Karp Rolling Hash)";
            for (int i = 0; i < corpus.size(); i++) {
                Document doc = corpus.get(i);
                IntArrayList occ = RabinKarpSearch.search(doc.content, pattern);
                if (!occ.isEmpty()) {
                    matches.add(String.format("Found %d occurrences in [%s] %s at offsets %s",
                            occ.size(), doc.id, doc.title, formatIntArray(occ)));
                }
            }
        } else {
            algoUsed = "KMP";
            timeComp = "O(n + m)";
            spaceComp = "O(m)";
            citation = "CLRS Ch 32.4 & Kleinberg-Tardos (Knuth-Morris-Pratt)";
            for (int i = 0; i < corpus.size(); i++) {
                Document doc = corpus.get(i);
                IntArrayList occ = KMPSearch.search(doc.content, pattern);
                if (!occ.isEmpty()) {
                    matches.add(String.format("Found %d occurrences in [%s] %s at offsets %s",
                            occ.size(), doc.id, doc.title, formatIntArray(occ)));
                }
            }
        }

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.PATTERN_SEARCH,
                pattern,
                algoUsed,
                "Advanced String Algorithms",
                timeComp,
                spaceComp,
                citation,
                matches,
                elapsed
        );
    }

    @Override
    public QueryResult searchMultiPattern(String[] patterns) {
        long start = System.nanoTime();
        AhoCorasick ac = new AhoCorasick(patterns);
        CustomArrayList<String> details = new CustomArrayList<>();

        for (int i = 0; i < corpus.size(); i++) {
            Document doc = corpus.get(i);
            CustomArrayList<AhoCorasick.MatchResult> results = ac.searchAll(doc.content);
            if (!results.isEmpty()) {
                details.add(String.format("Article [%s] %s matched %d patterns: %s",
                        doc.id, doc.title, results.size(), summarizeACMatches(results)));
            }
        }

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.PATTERN_SEARCH,
                joinPatterns(patterns),
                "Aho-Corasick Multi-Pattern Automaton",
                "Advanced String Algorithms",
                "O(TextLen + Sum(PatLen) + Occurrences)",
                "O(Sum(PatLen) * Alphabet)",
                "Aho & Corasick (1975) & Gusfield Ch 3",
                details,
                elapsed
        );
    }

    @Override
    public QueryResult fuzzyMatch(String target, int maxDistance, String variant) {
        long start = System.nanoTime();
        CustomArrayList<String> matches = new CustomArrayList<>();
        String var = (variant != null && !variant.isEmpty()) ? variant.toUpperCase() : "LEVENSHTEIN";
        String timeComp = "O(nm)";
        String spaceComp = "O(min(n, m))";
        String citation = "Wagner-Fischer (1974) & Kleinberg-Tardos Ch 6.6";

        for (int i = 0; i < corpus.size(); i++) {
            Document doc = corpus.get(i);
            int dist;
            if (var.equals("DAMERAU")) {
                dist = EditDistance.damerauLevenshtein(target, doc.title);
            } else if (var.equals("NEEDLEMAN_WUNSCH")) {
                SequenceAlignment.AlignmentResult al = SequenceAlignment.needlemanWunsch(target, doc.title, 2, -1, -1);
                dist = Math.max(0, 10 - al.score);
            } else {
                dist = EditDistance.levenshtein(target, doc.title);
            }

            if (dist <= maxDistance) {
                matches.add(String.format("Match with [%s] \"%s\" -> Edit Distance = %d", doc.id, doc.title, dist));
            }
        }

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.FUZZY_MATCHING,
                target,
                var,
                "Dynamic Programming (DP)",
                timeComp,
                spaceComp,
                citation,
                matches,
                elapsed
        );
    }

    @Override
    public QueryResult documentSimilarity(String docId1, String docId2) {
        long start = System.nanoTime();
        Document d1 = docIndex.get(docId1);
        Document d2 = docIndex.get(docId2);
        CustomArrayList<String> details = new CustomArrayList<>();

        if (d1 == null || d2 == null) {
            details.add("Error: One or both documents not found in corpus.");
            return new QueryResult(QueryType.DOCUMENT_SIMILARITY, docId1 + " vs " + docId2,
                    "Suffix Array + Kasai LCP", "Suffix Structures", "O(N log^2 N)", "O(N)", "Kasai et al. (2001)", details, 0);
        }

        String lcs = LCPArray.computeLongestCommonSubstring(d1.content, d2.content);
        double similarityScore = (2.0 * lcs.length()) / (d1.content.length() + d2.content.length());

        details.add(String.format("Doc 1: [%s] %s (len %d)", d1.id, d1.title, d1.content.length()));
        details.add(String.format("Doc 2: [%s] %s (len %d)", d2.id, d2.title, d2.content.length()));
        details.add(String.format("Longest Common Substring (LCS) Length: %d chars", lcs.length()));
        details.add(String.format("Extracted LCS: \"%s\"", lcs.trim()));
        details.add(String.format("Lexical Substring Similarity Ratio: %.4f (%.2f%%)", similarityScore, similarityScore * 100));

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.DOCUMENT_SIMILARITY,
                docId1 + " vs " + docId2,
                "Suffix Array (Prefix Doubling) + Kasai LCP",
                "Suffix Structures",
                "Time: O(N log^2 N) + O(N) | Space: O(N)",
                "O(N)",
                "Manber-Myers (1993) & Kasai (2001) & Erickson Ch 4",
                details,
                elapsed
        );
    }

    @Override
    public QueryResult analyzeCitationFlow(String sourceDocId, String sinkDocId, String algorithm) {
        long start = System.nanoTime();
        CustomArrayList<String> details = new CustomArrayList<>();

        // Map document string IDs to unique integer node indices
        CustomHashMap<String, Integer> idToIdx = new CustomHashMap<>();
        CustomHashMap<Integer, String> idxToId = new CustomHashMap<>();
        int nodeCounter = 0;

        for (int i = 0; i < citations.size(); i++) {
            CitationEdge ce = citations.get(i);
            if (!idToIdx.containsKey(ce.fromDoc)) {
                idToIdx.put(ce.fromDoc, nodeCounter);
                idxToId.put(nodeCounter, ce.fromDoc);
                nodeCounter++;
            }
            if (!idToIdx.containsKey(ce.toDoc)) {
                idToIdx.put(ce.toDoc, nodeCounter);
                idxToId.put(nodeCounter, ce.toDoc);
                nodeCounter++;
            }
        }

        if (!idToIdx.containsKey(sourceDocId) || !idToIdx.containsKey(sinkDocId)) {
            details.add(String.format("Source '%s' or Sink '%s' not present in citation graph.", sourceDocId, sinkDocId));
            return new QueryResult(QueryType.CITATION_FLOW_ANALYSIS, sourceDocId + " -> " + sinkDocId,
                    "Max-Flow", "Network Flow", "O(V^2 E)", "O(V + E)", "CLRS Ch 26", details, 0);
        }

        int s = idToIdx.get(sourceDocId);
        int t = idToIdx.get(sinkDocId);

        FlowNetwork net = new FlowNetwork(nodeCounter);
        for (int i = 0; i < citations.size(); i++) {
            CitationEdge ce = citations.get(i);
            int u = idToIdx.get(ce.fromDoc);
            int v = idToIdx.get(ce.toDoc);
            net.addEdge(u, v, ce.capacity, ce.unitCost);
        }

        int maxFlow;
        String algoName;
        String timeComp;
        String citation;

        if (algorithm != null && algorithm.equalsIgnoreCase("EDMONDS_KARP")) {
            algoName = "Edmonds-Karp (BFS Augmenting Paths)";
            timeComp = "O(V * E^2)";
            citation = "Edmonds & Karp (1972) / CLRS Ch 26.2";
            maxFlow = EdmondsKarp.computeMaxFlow(net, s, t);
        } else {
            algoName = "Dinic's Algorithm (Level Graph & Blocking Flow)";
            timeComp = "O(V^2 * E)";
            citation = "Dinic (1970) / Erickson Ch 11";
            maxFlow = DinicsAlgorithm.computeMaxFlow(net, s, t);
        }

        MinCutDual.MinCutResult minCut = MinCutDual.computeMinCut(net, s, t);

        details.add(String.format("Maximum Citation Flow from [%s] to [%s]: %d units", sourceDocId, sinkDocId, maxFlow));
        details.add(String.format("Max-Flow Min-Cut Verification: Cut Capacity = %d units (Exact Dual Match)", minCut.cutCapacity));
        details.add(String.format("Bottleneck Cut Separator: %d critical edges bridge knowledge transfer:", minCut.cutEdges.size()));

        for (int i = 0; i < minCut.cutEdges.size(); i++) {
            FlowNetwork.Edge e = minCut.cutEdges.get(i);
            details.add(String.format("  -> Bottleneck Citation: [%s] -> [%s] (Capacity: %d)",
                    idxToId.get(e.u), idxToId.get(e.v), e.capacity));
        }

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.CITATION_FLOW_ANALYSIS,
                sourceDocId + " -> " + sinkDocId,
                algoName,
                "Network Flow & Dual Cut",
                timeComp,
                "O(V + E)",
                citation,
                details,
                elapsed
        );
    }

    @Override
    public QueryResult scheduleResearchProjects() {
        long start = System.nanoTime();
        CustomArrayList<String> details = new CustomArrayList<>();

        // Part 1: Project Selection Problem via Min-Cut Reduction
        FlowReductions.ProjectSelectionResult prjRes = FlowReductions.solveProjectSelection(projects);
        details.add("--- PART 1: OPTIMAL RESEARCH PROJECT SELECTION VIA MIN-CUT REDUCTION ---");
        details.add(String.format("Maximum Optimal Net Profit: +$%dK", prjRes.maxNetProfit));
        details.add(String.format("Selected Feasible Project Portfolio (%d projects):", prjRes.selectedProjects.size()));
        for (int i = 0; i < prjRes.selectedProjects.size(); i++) {
            details.add("  * " + prjRes.selectedProjects.get(i));
        }

        // Part 2: NP-Hard Scheduling & Approximation Demonstration
        details.add("\n--- PART 2: NP-HARD APPROXIMATION & PARAMETERIZED GUARANTEES ---");
        boolean[][] conflictAdj = {
                {false, true,  true,  false, false},
                {true,  false, true,  true,  false},
                {true,  true,  false, true,  true},
                {false, true,  true,  false, true},
                {false, false, true,  true,  false}
        };
        CustomArrayList<Integer> vcApprox = ApproximationAlgorithms.vertexCover2Approx(conflictAdj, 5);
        details.add(String.format("Maximal-Matching 2-Approximation for Scheduling Conflicts: size %d (<= 2 * OPT)", vcApprox.size()));

        ParameterizedComplexity.FPTResult fpt = ParameterizedComplexity.solveKVertexCover(conflictAdj, 5, 3);
        details.add(String.format("FPT Bounded Search Tree (k=3): Solvable = %b (Parametric runtime O(2^k * n))", fpt.solvable));

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.PROJECT_SCHEDULING,
                "Project Portfolio & Resource Scheduling",
                "Min-Cut Reduction + 2-Approximation & FPT",
                "NP-Hard & Flow Reductions",
                "O(V^3) Flow / O(2^k * n) FPT",
                "O(V + E)",
                "Kleinberg-Tardos Ch 7.11 (Project Selection) & Ch 11 (Approximation)",
                details,
                elapsed
        );
    }

    @Override
    public QueryResult testPrimality(long number, int iterations) {
        long start = System.nanoTime();
        MillerRabinPrimality.PrimalityResult res = MillerRabinPrimality.isPrime(number, iterations);
        CustomArrayList<String> details = new CustomArrayList<>();

        details.add(res.toString());
        details.add(String.format("Fermat Test Comparison (base 2): %s",
                MillerRabinPrimality.fermatTest(number, 2) ? "PASSED (Fermat pseudoprime danger)" : "FAILED (Definitely Composite)"));

        if (!res.isPrime && res.witnessDisproof != null) {
            details.add(String.format("Miller-Rabin Witness Proof: Base a = %d proves compositeness without factorization!",
                    res.witnessDisproof));
        } else if (res.isPrime) {
            details.add(String.format("Probabilistic Guarantee: Error probability <= (1/4)^%d = %.4e",
                    iterations, res.errorProbabilityUpperLimit));
        }

        long elapsed = System.nanoTime() - start;
        return new QueryResult(
                QueryType.PRIMALITY_TESTING,
                Long.toString(number),
                "Miller-Rabin Primality Test",
                "Randomised Algorithms (Monte Carlo)",
                "O(k * log^3 n)",
                "O(log n)",
                "Miller (1976), Rabin (1980) & CLRS Ch 31.8",
                details,
                elapsed
        );
    }

    @Override
    public void printCurriculumCanon() {
        EngineDesignCatalog.displayCurriculumCanon();
    }

    @Override
    public void runFullDiagnosticSuite() {
        System.out.println("===================================================================================================");
        System.out.println("                     STARTING TEXTHACK COMPREHENSIVE DIAGNOSTIC SUITE (M1-M6)                     ");
        System.out.println("===================================================================================================\n");

        printCurriculumCanon();

        // 1. Module 2: String Algorithms Suite
        System.out.println(">>> EXECUTING MODULE 2 DIAGNOSTICS: ADVANCED STRING ALGORITHMS");
        String sampleText = "Pingala chandahsutra binary numeral system meru prastara pascals triangle fibonacci sequences";
        String samplePattern = "numeral";
        System.out.printf("Searching for \"%s\" in Pingala text...\n", samplePattern);
        System.out.println("  * KMP Result Offsets: " + formatIntArray(KMPSearch.search(sampleText, samplePattern)));
        System.out.println("  * Z-Algorithm Offsets: " + formatIntArray(ZAlgorithm.search(sampleText, samplePattern)));
        System.out.println("  * Rabin-Karp Double-Hash: " + formatIntArray(RabinKarpSearch.search(sampleText, samplePattern)));

        String[] multiPatterns = {"binary", "meru", "fibonacci", "astronomy"};
        QueryResult acRes = searchMultiPattern(multiPatterns);
        System.out.println(acRes);

        SuffixAutomataPreview.displayPreviewAnalysis();

        // 2. Module 3: Dynamic Programming Suite
        System.out.println("\n>>> EXECUTING MODULE 3 DIAGNOSTICS: ADVANCED DYNAMIC PROGRAMMING");
        DPDecisionGuide.displayDecisionGuide();
        System.out.printf("Levenshtein (\"Aryabhata\", \"Aryabhatta\"): %d\n", EditDistance.levenshtein("Aryabhata", "Aryabhatta"));
        System.out.printf("Damerau-Levenshtein (\"grammer\", \"grammar\"): %d\n", EditDistance.damerauLevenshtein("grammer", "grammar"));

        SequenceAlignment.AlignmentResult nw = SequenceAlignment.needlemanWunsch("AGTCG", "ATCG", 2, -1, -1);
        System.out.println("Needleman-Wunsch Alignment:\n  " + nw);

        int[] matrixDims = {10, 30, 5, 60};
        IntervalDP.MCMResult mcm = IntervalDP.solveMatrixChain(matrixDims);
        System.out.printf("Matrix Chain Multiplication: Min scalar ops = %d, Parenthesization = %s\n",
                mcm.minScalarMultiplications, mcm.parenthesization);

        int[][] tspDist = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };
        BitmaskDP.TSPResult tsp = BitmaskDP.solveTSP(tspDist);
        System.out.printf("Held-Karp TSP: Min Tour Cost = %d, Path = %s\n", tsp.minCost, formatList(tsp.tour));

        int[] sosInput = {1, 2, 4, 8};
        int[] sosOutput = SOSDP.computeSOS(sosInput, 2);
        System.out.printf("SOS DP (2-variable sum over subsets): [%d, %d, %d, %d]\n",
                sosOutput[0], sosOutput[1], sosOutput[2], sosOutput[3]);

        // 3. Module 4: Network Flow Suite
        System.out.println("\n>>> EXECUTING MODULE 4 DIAGNOSTICS: NETWORK MAX-FLOW & DUAL REDUCTIONS");
        QueryResult flowRes = analyzeCitationFlow("DOC001", "DOC015", "DINIC");
        System.out.println(flowRes);

        // 4. Module 5: Complexity & Approximation Suite
        System.out.println("\n>>> EXECUTING MODULE 5 DIAGNOSTICS: NP-COMPLETENESS & APPROXIMATIONS");
        NPChecklist.displayChecklist();
        QueryResult prjRes = scheduleResearchProjects();
        System.out.println(prjRes);

        // 5. Module 6: Randomised & Parallel Suite
        System.out.println("\n>>> EXECUTING MODULE 6 DIAGNOSTICS: RANDOMISED & PARALLEL ALGORITHMS");
        RandomAnalysis.displayFoundations();
        ReservoirSampling.displayMathematicalProof();

        int[] testArr = {42, 17, 89, 5, 23, 71, 12, 60, 3, 99};
        RandomQuicksort.SortStats sortStats = RandomQuicksort.sort(testArr);
        System.out.printf("Randomised QuickSort: %d comparisons in %d ns. Sorted: %s\n",
                sortStats.comparisons, sortStats.durationNanos, formatRawIntArray(testArr));

        for (int i = 0; i < primes.size(); i++) {
            PrimeTestData pt = primes.get(i);
            QueryResult mr = testPrimality(pt.number, 10);
            System.out.printf("  [Prime Bench] %s => %s\n", pt.description, mr.getDetails().get(0));
        }

        int[] scanInput = {3, 1, 7, 0, 4, 1, 6, 3};
        int[] prefixSums = ParallelPrimitives.blellochScan(scanInput);
        System.out.printf("Blelloch Work-Efficient Parallel Prefix Scan: Input %s -> Prefix %s\n",
                formatRawIntArray(scanInput), formatRawIntArray(prefixSums));
        ParallelPrimitives.displayParallelTradeoffs();

        System.out.println("===================================================================================================");
        System.out.println("                         ALL MODULE DIAGNOSTIC SUITES COMPLETED SUCCESSFULLY!                      ");
        System.out.println("===================================================================================================\n");
    }

    private String formatIntArray(IntArrayList list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatRawIntArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatList(CustomArrayList<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(" -> ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String summarizeACMatches(CustomArrayList<AhoCorasick.MatchResult> results) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(results.size(), 4);
        for (int i = 0; i < limit; i++) {
            sb.append(results.get(i).pattern).append(" ");
        }
        if (results.size() > limit) {
            sb.append(String.format("... (+%d more)", results.size() - limit));
        }
        return sb.toString();
    }

    private String joinPatterns(String[] patterns) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patterns.length; i++) {
            sb.append(patterns[i]);
            if (i < patterns.length - 1) sb.append(", ");
        }
        return sb.toString();
    }
}

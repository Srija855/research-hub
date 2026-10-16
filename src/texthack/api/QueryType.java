package texthack.api;

/**
 * The six canonical query classes answered by TextHack.
 */
public enum QueryType {
    PATTERN_SEARCH,          // Maps to String Algorithms (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)
    FUZZY_MATCHING,          // Maps to Dynamic Programming (Levenshtein, Damerau, Needleman-Wunsch)
    DOCUMENT_SIMILARITY,     // Maps to Suffix Structures (Suffix Arrays, Kasai LCP)
    CITATION_FLOW_ANALYSIS,  // Maps to Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic, Min-Cut)
    PROJECT_SCHEDULING,      // Maps to NP-Hard Reductions & Approximation Algorithms
    PRIMALITY_TESTING        // Maps to Randomised Algorithms (Miller-Rabin, Universal Hashing)
}

package texthack.api;

import texthack.engine.ds.CustomArrayList;

/**
 * TextHack Engine Public API.
 * Students and client applications interact exclusively through this interface.
 */
public interface TextHackEngine {

    /**
     * Loads the corpus and relational research datasets.
     */
    void loadData(String corpusPath, String citationsPath, String projectsPath, String primesPath);

    /**
     * Query Class 1: Pattern Search using String Algorithms.
     * @param pattern pattern string to search
     * @param algorithm "KMP", "Z", "RABIN_KARP", "NAIVE", or "AHO_CORASICK"
     */
    QueryResult searchPattern(String pattern, String algorithm);

    /**
     * Multi-pattern search using Aho-Corasick automaton.
     */
    QueryResult searchMultiPattern(String[] patterns);

    /**
     * Query Class 2: Fuzzy Matching using Dynamic Programming.
     * @param target Word or phrase to match against corpus titles/vocabulary
     * @param maxDistance Maximum edit distance allowed
     * @param variant "LEVENSHTEIN", "DAMERAU", or "NEEDLEMAN_WUNSCH"
     */
    QueryResult fuzzyMatch(String target, int maxDistance, String variant);

    /**
     * Query Class 3: Document Similarity using Suffix Structures.
     * Computes longest common substring and suffix similarity between two documents.
     */
    QueryResult documentSimilarity(String docId1, String docId2);

    /**
     * Query Class 4: Citation Flow Analysis using Max-Flow / Min-Cut.
     * Computes maximum citation flow between source literature and downstream field,
     * identifying the bottleneck min-cut partition.
     */
    QueryResult analyzeCitationFlow(String sourceDocId, String sinkDocId, String algorithm);

    /**
     * Query Class 5: Project Scheduling & Dependency Selection.
     * Solves research project selection via Min-Cut reduction,
     * and demonstrates NP-Hard scheduling approximation.
     */
    QueryResult scheduleResearchProjects();

    /**
     * Query Class 6: Primality Testing using Randomised Algorithms.
     * Evaluates large integer primality using Miller-Rabin test with witness generation.
     */
    QueryResult testPrimality(long number, int iterations);

    /**
     * Prints the full academic curriculum mapping and design pass.
     */
    void printCurriculumCanon();

    /**
     * Runs all module benchmarks and diagnostic suites (M1 through M6).
     */
    void runFullDiagnosticSuite();
}

package texthack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import texthack.api.QueryResult;
import texthack.api.TextHackEngine;
import texthack.engine.TextHackEngineImpl;

/**
 * Main Application Runner for Eclipse and Terminal Execution.
 * Provides clear, academic, structured console output.
 */
public class TextHackApp {

    public static void main(String[] args) {
        System.out.println("***************************************************************************************************");
        System.out.println("*                     RESEARCH HUB: TEXTHACK ADVANCED DSA ENGINE (M1 - M6)                        *");
        System.out.println("*                      Curriculum: CLRS Part VII | Kleinberg-Tardos | Erickson                    *");
        System.out.println("*                     Engine Constraint: Hand-built DS (Zero java.util.* inside)                  *");
        System.out.println("***************************************************************************************************\n");

        TextHackEngine engine = new TextHackEngineImpl();

        // Load datasets from data/*.dat files
        String corpusPath = "data/corpus.dat";
        String citationsPath = "data/citations.dat";
        String projectsPath = "data/projects.dat";
        String primesPath = "data/test_primes.dat";

        engine.loadData(corpusPath, citationsPath, projectsPath, primesPath);
        System.out.println();

        // If command line arguments provided or non-interactive mode:
        if (args.length > 0 && args[0].equalsIgnoreCase("--all")) {
            engine.runFullDiagnosticSuite();
            return;
        }

        // By default, execute the comprehensive M1-M6 diagnostic demonstration first
        // so that clicking "Run" in Eclipse immediately produces all results clearly!
        System.out.println(">>> AUTOMATED COMPREHENSIVE ENGINE DEMONSTRATION (M1 through M6):");
        engine.runFullDiagnosticSuite();

        // Interactive Console Menu for Eclipse / Terminal exploration
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("---------------------------------------------------------------------------------------------------");
                System.out.println("TEXTHACK INTERACTIVE EXPLORER MENU (Enter option number, or 0 to exit):");
                System.out.println("  1. Display Curriculum Canon & Asymptotic Complexity Matrix (M1)");
                System.out.println("  2. Pattern Search on Indian Wikipedia Corpus (KMP / Z / Rabin-Karp) (M2)");
                System.out.println("  3. Multi-Pattern Search with Aho-Corasick Automaton (M2)");
                System.out.println("  4. Suffix Array & Kasai LCP Document Similarity (M2)");
                System.out.println("  5. Fuzzy Title Matching (Wagner-Fischer / Damerau / Needleman-Wunsch) (M3)");
                System.out.println("  6. Matrix Chain Multiplication & Bitmask TSP Tour (M3)");
                System.out.println("  7. Citation Flow Analysis (Dinic / Edmonds-Karp & Dual Min-Cut) (M4)");
                System.out.println("  8. Research Project Selection via Min-Cut Reduction (M4)");
                System.out.println("  9. NP-Hardness Checklist & Approximation Guarantees (M5)");
                System.out.println(" 10. Miller-Rabin Primality Test & Carmichael Number Challenge (M6)");
                System.out.println(" 11. Parallel Blelloch Prefix-Sum Scan & Reservoir Sampling (M6)");
                System.out.println(" 12. Run Full Diagnostic Suite Again");
                System.out.println("  0. Exit");
                System.out.print("Select an option [0-12]: ");

                String input = reader.readLine();
                if (input == null || input.trim().isEmpty()) {
                    break;
                }
                int choice;
                try {
                    choice = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 0 and 12.");
                    continue;
                }

                if (choice == 0) {
                    System.out.println("\nExiting TextHack Research Hub. Happy learning!");
                    break;
                }

                handleUserChoice(engine, choice, reader);
            }
        } catch (Exception e) {
            System.out.println("[Interactive Session Finished]");
        }
    }

    private static void handleUserChoice(TextHackEngine engine, int choice, BufferedReader reader) throws Exception {
        System.out.println();
        switch (choice) {
            case 1:
                engine.printCurriculumCanon();
                break;
            case 2:
                System.out.print("Enter search pattern (e.g., 'algorithm', 'ganita', 'syntax', 'quantum'): ");
                String pat = reader.readLine().trim();
                System.out.print("Algorithm [KMP / Z / RABIN_KARP / NAIVE] (default: KMP): ");
                String algo = reader.readLine().trim();
                if (algo.isEmpty()) algo = "KMP";
                QueryResult res2 = engine.searchPattern(pat, algo);
                System.out.println(res2);
                break;
            case 3:
                System.out.print("Enter comma-separated patterns (e.g., 'binary,meru,fibonacci,astronomy'): ");
                String pInput = reader.readLine().trim();
                String[] pList = pInput.split(",");
                for (int i = 0; i < pList.length; i++) pList[i] = pList[i].trim();
                QueryResult res3 = engine.searchMultiPattern(pList);
                System.out.println(res3);
                break;
            case 4:
                System.out.print("Enter Doc ID 1 (e.g. DOC001): ");
                String d1 = reader.readLine().trim();
                System.out.print("Enter Doc ID 2 (e.g. DOC004): ");
                String d2 = reader.readLine().trim();
                QueryResult res4 = engine.documentSimilarity(d1, d2);
                System.out.println(res4);
                break;
            case 5:
                System.out.print("Enter search title/phrase: ");
                String fuzzyStr = reader.readLine().trim();
                System.out.print("Maximum edit distance (e.g. 3): ");
                int dist = Integer.parseInt(reader.readLine().trim());
                QueryResult res5 = engine.fuzzyMatch(fuzzyStr, dist, "LEVENSHTEIN");
                System.out.println(res5);
                break;
            case 6:
                System.out.println("Running Matrix Chain Multiplication and Bitmask TSP...");
                int[] dims = {15, 25, 10, 40, 5};
                texthack.engine.m3_dp.IntervalDP.MCMResult mcm = texthack.engine.m3_dp.IntervalDP.solveMatrixChain(dims);
                System.out.println("Matrix Dimensions: [15x25, 25x10, 10x40, 40x5]");
                System.out.println("Optimal Parenthesization: " + mcm.parenthesization);
                System.out.println("Minimum Scalar Multiplications: " + mcm.minScalarMultiplications);
                break;
            case 7:
                System.out.print("Source Doc ID (e.g. DOC001): ");
                String sDoc = reader.readLine().trim();
                System.out.print("Sink Doc ID (e.g. DOC015): ");
                String tDoc = reader.readLine().trim();
                QueryResult res7 = engine.analyzeCitationFlow(sDoc, tDoc, "DINIC");
                System.out.println(res7);
                break;
            case 8:
                QueryResult res8 = engine.scheduleResearchProjects();
                System.out.println(res8);
                break;
            case 9:
                texthack.engine.m5_complexity.NPChecklist.displayChecklist();
                break;
            case 10:
                System.out.print("Enter integer to test (e.g. 561, 1000000007, 1729, 2147483647): ");
                long num = Long.parseLong(reader.readLine().trim());
                QueryResult res10 = engine.testPrimality(num, 15);
                System.out.println(res10);
                break;
            case 11:
                texthack.engine.m6_random_parallel.ReservoirSampling.displayMathematicalProof();
                int[] data = {1, 2, 3, 4, 5, 6, 7, 8};
                int[] scan = texthack.engine.m6_random_parallel.ParallelPrimitives.blellochScan(data);
                System.out.println("Blelloch Exclusive Scan on [1..8]:");
                for (int v : scan) System.out.print(v + " ");
                System.out.println();
                break;
            case 12:
                engine.runFullDiagnosticSuite();
                break;
            default:
                System.out.println("Invalid selection.");
        }
    }
}

package texthack.engine.m2_strings;

/**
 * Preview Level Analysis: Suffix Automata (SAM / DAWG) and Suffix Trees (Ukkonen).
 * Details what these advanced structures enable that Suffix Arrays do not:
 * - Online, streaming text ingestion in linear O(1) amortized time per character
 * - Minimal DFA state representation (at most 2n-1 states and 3n-4 transitions)
 * - Distinct substring counting in O(states) rather than O(n log n)
 * - Exact factor frequency computation via link tree DP
 * Zero java.util.* usage.
 */
public class SuffixAutomataPreview {

    public static class SAMState {
        public int len;
        public int link;
        public int[] next = new int[256];

        public SAMState(int len, int link) {
            this.len = len;
            this.link = link;
            for (int i = 0; i < 256; i++) {
                next[i] = -1;
            }
        }
    }

    public static class MinimalSAM {
        public SAMState[] st;
        public int sz;
        public int last;

        public MinimalSAM(int maxlen) {
            st = new SAMState[2 * maxlen];
            st[0] = new SAMState(0, -1);
            sz = 1;
            last = 0;
        }

        public void extend(char c) {
            int cur = sz++;
            st[cur] = new SAMState(st[last].len + 1, 0);
            int p = last;

            while (p != -1 && st[p].next[c] == -1) {
                st[p].next[c] = cur;
                p = st[p].link;
            }

            if (p == -1) {
                st[cur].link = 0;
            } else {
                int q = st[p].next[c];
                if (st[p].len + 1 == st[q].len) {
                    st[cur].link = q;
                } else {
                    int clone = sz++;
                    st[clone] = new SAMState(st[p].len + 1, st[q].link);
                    System.arraycopy(st[q].next, 0, st[clone].next, 0, 256);

                    while (p != -1 && st[p].next[c] == q) {
                        st[p].next[c] = clone;
                        p = st[p].link;
                    }
                    st[q].link = st[cur].link = clone;
                }
            }
            last = cur;
        }

        public long countDistinctSubstrings() {
            long total = 0;
            for (int i = 1; i < sz; i++) {
                total += (st[i].len - st[st[i].link].len);
            }
            return total;
        }
    }

    public static void displayPreviewAnalysis() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  PREVIEW: SUFFIX AUTOMATA (DAWG) & SUFFIX TREES vs SUFFIX ARRAYS");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  1. Suffix Tree (Ukkonen):");
        System.out.println("     - Compact trie of all suffixes in O(n) space and construction time.");
        System.out.println("     - High memory overhead in practice (pointer-heavy, 20-40 bytes/char).");
        System.out.println("  2. Suffix Array (Manber-Myers / SA-IS) + LCP (Kasai):");
        System.out.println("     - Cache-friendly array of integers (4-8 bytes/char).");
        System.out.println("     - Static text representation; dynamic updates require rebuilding or FM-index.");
        System.out.println("  3. Suffix Automaton (SAM):");
        System.out.println("     - Smallest DFA recognizing all substrings of a string.");
        System.out.println("     - Online construction: characters can be appended one-by-one in O(1) amortized.");
        System.out.println("     - Number of states <= 2n - 1, transitions <= 3n - 4.");
        System.out.println("     - Enables counting distinct substrings, finding smallest cyclic shifts in linear time.");
        System.out.println("--------------------------------------------------------------------------------");
    }
}

package texthack.engine.m2_strings;

import texthack.engine.ds.CustomComparator;
import texthack.engine.ds.CustomSort;

/**
 * Suffix Array Construction via Prefix Doubling in O(n log^2 n) / O(n log n).
 * Also documents the linear-time SA-IS (Induced Sorting) conceptual framework.
 * Zero java.util.* usage.
 */
public class SuffixArray {

    private final String text;
    private final int n;
    private final int[] sa; // Suffix array: sa[i] is index of i-th lexicographically smallest suffix

    public static class SuffixRank {
        public int index;
        public int rank1;
        public int rank2;

        public SuffixRank(int index, int rank1, int rank2) {
            this.index = index;
            this.rank1 = rank1;
            this.rank2 = rank2;
        }
    }

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.sa = buildSuffixArrayPrefixDoubling();
    }

    /**
     * Practical O(n log^2 n) Prefix-Doubling algorithm.
     * Uses hand-built CustomSort without java.util.*
     */
    private int[] buildSuffixArrayPrefixDoubling() {
        if (n == 0) return new int[0];
        SuffixRank[] suffixes = new SuffixRank[n];

        // Step 1: Initial rank based on first character
        for (int i = 0; i < n; i++) {
            suffixes[i] = new SuffixRank(i, (int) text.charAt(i), (i + 1 < n) ? (int) text.charAt(i + 1) : -1);
        }

        CustomComparator<SuffixRank> rankComp = new CustomComparator<SuffixRank>() {
            @Override
            public int compare(SuffixRank a, SuffixRank b) {
                if (a.rank1 != b.rank1) return a.rank1 - b.rank1;
                return a.rank2 - b.rank2;
            }
        };

        CustomSort.mergeSort(suffixes, rankComp);

        int[] indexToRank = new int[n];

        // Doubling step: k = 4, 8, 16...
        for (int k = 4; k < 2 * n; k *= 2) {
            int currentRank = 0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank1 = currentRank;
            indexToRank[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank1 && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    suffixes[i].rank1 = currentRank;
                } else {
                    prevRank1 = suffixes[i].rank1;
                    currentRank++;
                    suffixes[i].rank1 = currentRank;
                }
                indexToRank[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < n) ? suffixes[indexToRank[nextIndex]].rank1 : -1;
            }

            CustomSort.mergeSort(suffixes, rankComp);
        }

        int[] resultSA = new int[n];
        for (int i = 0; i < n; i++) {
            resultSA[i] = suffixes[i].index;
        }
        return resultSA;
    }

    public int[] getSuffixArray() {
        return sa;
    }

    public String getText() {
        return text;
    }

    /**
     * Binary search pattern matching on Suffix Array in O(m log n) time.
     */
    public int findFirstOccurrence(String pattern) {
        int low = 0;
        int high = n - 1;
        int ans = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePatternWithSuffix(pattern, suffixStart);

            if (cmp == 0) {
                ans = suffixStart;
                high = mid - 1; // Seek earliest occurrence in SA
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    private int comparePatternWithSuffix(String pattern, int suffixStart) {
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            if (suffixStart + i >= n) {
                return 1; // Pattern is longer than suffix
            }
            char pChar = pattern.charAt(i);
            char sChar = text.charAt(suffixStart + i);
            if (pChar != sChar) {
                return pChar - sChar;
            }
        }
        return 0; // Match prefix
    }
}

package texthack.engine.m3_dp;

import texthack.engine.ds.CustomStringBuilder;

/**
 * Global and Local Sequence Alignment:
 * 1. Needleman-Wunsch (Global Alignment)
 * 2. Smith-Waterman (Local Alignment)
 * Evaluates match score, mismatch penalty, and gap penalties (linear and affine).
 * Zero java.util.* usage.
 */
public class SequenceAlignment {

    public static class AlignmentResult {
        public int score;
        public String alignedSeq1;
        public String alignedSeq2;
        public String alignmentType;

        public AlignmentResult(int score, String alignedSeq1, String alignedSeq2, String alignmentType) {
            this.score = score;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.alignmentType = alignmentType;
        }

        @Override
        public String toString() {
            return String.format("[%s Alignment] Score = %d\nSeq1: %s\nSeq2: %s",
                    alignmentType, score, alignedSeq1, alignedSeq2);
        }
    }

    /**
     * Needleman-Wunsch Global Alignment in O(nm) time.
     */
    public static AlignmentResult needlemanWunsch(String s1, String s2, int matchReward, int mismatchPenalty, int gapPenalty) {
        int n = s1.length();
        int m = s2.length();
        int[][] score = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) score[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) score[0][j] = j * gapPenalty;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int match = score[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? matchReward : mismatchPenalty);
                int del = score[i - 1][j] + gapPenalty;
                int ins = score[i][j - 1] + gapPenalty;
                score[i][j] = Math.max(match, Math.max(del, ins));
            }
        }

        // Traceback for global alignment
        CustomStringBuilder a1 = new CustomStringBuilder();
        CustomStringBuilder a2 = new CustomStringBuilder();
        int i = n, j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && score[i][j] == score[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? matchReward : mismatchPenalty)) {
                a1.append(s1.charAt(i - 1));
                a2.append(s2.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && score[i][j] == score[i - 1][j] + gapPenalty) {
                a1.append(s1.charAt(i - 1));
                a2.append('-');
                i--;
            } else {
                a1.append('-');
                a2.append(s2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(score[n][m], reverse(a1.toString()), reverse(a2.toString()), "Global (Needleman-Wunsch)");
    }

    /**
     * Smith-Waterman Local Alignment in O(nm) time.
     * Cells never drop below 0; searches for maximum score anywhere in matrix.
     */
    public static AlignmentResult smithWaterman(String s1, String s2, int matchReward, int mismatchPenalty, int gapPenalty) {
        int n = s1.length();
        int m = s2.length();
        int[][] score = new int[n + 1][m + 1];

        int maxScore = 0;
        int maxI = 0;
        int maxJ = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int match = score[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? matchReward : mismatchPenalty);
                int del = score[i - 1][j] + gapPenalty;
                int ins = score[i][j - 1] + gapPenalty;
                score[i][j] = Math.max(0, Math.max(match, Math.max(del, ins)));

                if (score[i][j] > maxScore) {
                    maxScore = score[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from maximum scoring cell until cell value drops to 0
        CustomStringBuilder a1 = new CustomStringBuilder();
        CustomStringBuilder a2 = new CustomStringBuilder();
        int i = maxI, j = maxJ;

        while (i > 0 && j > 0 && score[i][j] > 0) {
            if (score[i][j] == score[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? matchReward : mismatchPenalty)) {
                a1.append(s1.charAt(i - 1));
                a2.append(s2.charAt(j - 1));
                i--;
                j--;
            } else if (score[i][j] == score[i - 1][j] + gapPenalty) {
                a1.append(s1.charAt(i - 1));
                a2.append('-');
                i--;
            } else {
                a1.append('-');
                a2.append(s2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(maxScore, reverse(a1.toString()), reverse(a2.toString()), "Local (Smith-Waterman)");
    }

    private static String reverse(String str) {
        char[] chars = str.toCharArray();
        int left = 0, right = chars.length - 1;
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
        return new String(chars);
    }
}

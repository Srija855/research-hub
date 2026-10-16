package texthack.engine.m2_strings;

import texthack.engine.ds.CustomArrayList;
import texthack.engine.ds.CustomHashMap;
import texthack.engine.ds.CustomQueue;
import texthack.engine.ds.IntArrayList;

/**
 * Aho-Corasick Multi-Pattern Automaton.
 * Constructs Trie + Failure Links + Output Dictionary Links via BFS.
 * Matches multiple patterns simultaneously in O(text_length + total_pattern_lengths + matches).
 * Zero java.util.* usage.
 */
public class AhoCorasick {

    public static class Node {
        public CustomHashMap<Character, Node> children = new CustomHashMap<>();
        public Node failLink = null;
        public Node dictLink = null; // Suffix link to next matching output node
        public CustomArrayList<Integer> matchedPatternIndices = new CustomArrayList<>();
        public int depth = 0;
    }

    public static class MatchResult {
        public int patternIndex;
        public String pattern;
        public int position; // End position in text

        public MatchResult(int patternIndex, String pattern, int position) {
            this.patternIndex = patternIndex;
            this.pattern = pattern;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("Match [Pattern: \"%s\" (idx %d) at end-pos %d]", pattern, patternIndex, position);
        }
    }

    private Node root;
    private String[] patterns;

    public AhoCorasick(String[] patterns) {
        this.patterns = patterns;
        this.root = new Node();
        buildTrie();
        buildFailureLinks();
    }

    private void buildTrie() {
        for (int i = 0; i < patterns.length; i++) {
            String pat = patterns[i];
            Node curr = root;
            for (int cIdx = 0; cIdx < pat.length(); cIdx++) {
                char c = pat.charAt(cIdx);
                Node next = curr.children.get(c);
                if (next == null) {
                    next = new Node();
                    next.depth = curr.depth + 1;
                    curr.children.put(c, next);
                }
                curr = next;
            }
            curr.matchedPatternIndices.add(i);
        }
    }

    private void buildFailureLinks() {
        CustomQueue<Node> queue = new CustomQueue<>();

        // Level 1: failure links point to root
        CustomArrayList<Character> rootKeys = root.children.keys();
        for (int i = 0; i < rootKeys.size(); i++) {
            Node child = root.children.get(rootKeys.get(i));
            child.failLink = root;
            queue.enqueue(child);
        }

        // BFS for deeper levels
        while (!queue.isEmpty()) {
            Node curr = queue.dequeue();
            CustomArrayList<Character> keys = curr.children.keys();

            for (int i = 0; i < keys.size(); i++) {
                char ch = keys.get(i);
                Node child = curr.children.get(ch);

                Node f = curr.failLink;
                while (f != null && !f.children.containsKey(ch)) {
                    f = f.failLink;
                }
                child.failLink = (f != null) ? f.children.get(ch) : root;

                // Dict link: if failure node has output, point to it, otherwise inherit failure's dict link
                if (child.failLink != null && !child.failLink.matchedPatternIndices.isEmpty()) {
                    child.dictLink = child.failLink;
                } else if (child.failLink != null) {
                    child.dictLink = child.failLink.dictLink;
                }

                queue.enqueue(child);
            }
        }
    }

    public CustomArrayList<MatchResult> searchAll(String text) {
        CustomArrayList<MatchResult> results = new CustomArrayList<>();
        Node curr = root;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // Follow failure transitions if child does not exist
            while (curr != root && !curr.children.containsKey(ch)) {
                curr = curr.failLink;
            }

            if (curr.children.containsKey(ch)) {
                curr = curr.children.get(ch);
            } else {
                curr = root;
            }

            // Collect direct matches at this node
            for (int k = 0; k < curr.matchedPatternIndices.size(); k++) {
                int patIdx = curr.matchedPatternIndices.get(k);
                results.add(new MatchResult(patIdx, patterns[patIdx], i));
            }

            // Traverse dictionary suffix links to collect sub-pattern matches
            Node out = curr.dictLink;
            while (out != null) {
                for (int k = 0; k < out.matchedPatternIndices.size(); k++) {
                    int patIdx = out.matchedPatternIndices.get(k);
                    results.add(new MatchResult(patIdx, patterns[patIdx], i));
                }
                out = out.dictLink;
            }
        }
        return results;
    }
}

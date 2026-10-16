package texthack.engine.m4_flow;

import texthack.engine.ds.CustomArrayList;

/**
 * Maximum Bipartite Matching via Max-Flow Reduction & König's Theorem.
 * Establishes: Max Cardinality Matching == Min Vertex Cover in bipartite graphs.
 * Zero java.util.* usage.
 */
public class BipartiteMatching {

    public static class MatchingResult {
        public int maxMatchingSize;
        public CustomArrayList<int[]> matchingPairs; // pairs [u, v]
        public CustomArrayList<Integer> minVertexCover;

        public MatchingResult(int maxMatchingSize,
                              CustomArrayList<int[]> matchingPairs,
                              CustomArrayList<Integer> minVertexCover) {
            this.maxMatchingSize = maxMatchingSize;
            this.matchingPairs = matchingPairs;
            this.minVertexCover = minVertexCover;
        }
    }

    /**
     * Solves maximum bipartite matching between Left set (0..L-1) and Right set (0..R-1).
     * @param leftSize Number of left nodes
     * @param rightSize Number of right nodes
     * @param leftToRightEdges Adjacency of edges from left to right
     */
    public static MatchingResult solveBipartiteMatching(int leftSize, int rightSize, CustomArrayList<int[]> leftToRightEdges) {
        int source = 0;
        int sink = leftSize + rightSize + 1;
        int totalVertices = sink + 1;

        FlowNetwork net = new FlowNetwork(totalVertices);

        // Edges from source to left nodes (capacity 1)
        for (int i = 1; i <= leftSize; i++) {
            net.addEdge(source, i, 1);
        }

        // Edges from right nodes to sink (capacity 1)
        for (int j = 1; j <= rightSize; j++) {
            net.addEdge(leftSize + j, sink, 1);
        }

        // Bipartite edges (capacity 1)
        for (int i = 0; i < leftToRightEdges.size(); i++) {
            int[] edge = leftToRightEdges.get(i);
            int u = edge[0] + 1;             // 1-indexed left
            int v = leftSize + edge[1] + 1;   // right node offset
            net.addEdge(u, v, 1);
        }

        int maxMatch = DinicsAlgorithm.computeMaxFlow(net, source, sink);

        // Collect matching pairs
        CustomArrayList<int[]> pairs = new CustomArrayList<>();
        for (int i = 1; i <= leftSize; i++) {
            for (int k = 0; k < net.getEdgesFrom(i).size(); k++) {
                FlowNetwork.Edge e = net.getEdgesFrom(i).get(k);
                if (e.flow == 1 && e.v > leftSize && e.v <= leftSize + rightSize) {
                    pairs.add(new int[]{i - 1, e.v - leftSize - 1});
                }
            }
        }

        // König's Theorem: Min Vertex Cover derived from residual reachability
        MinCutDual.MinCutResult cut = MinCutDual.computeMinCut(net, source, sink);
        CustomArrayList<Integer> minVertexCover = new CustomArrayList<>();

        // Left vertices NOT reachable from source are in min vertex cover
        for (int i = 1; i <= leftSize; i++) {
            if (!cut.sourcePartitionS.contains(i)) {
                minVertexCover.add(i - 1); // left node
            }
        }
        // Right vertices reachable from source are in min vertex cover
        for (int j = 1; j <= rightSize; j++) {
            int rightNode = leftSize + j;
            if (cut.sourcePartitionS.contains(rightNode)) {
                minVertexCover.add(leftSize + j - 1);
            }
        }

        return new MatchingResult(maxMatch, pairs, minVertexCover);
    }
}

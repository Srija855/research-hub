package texthack.engine.m4_flow;

import texthack.engine.ds.CustomArrayList;
import texthack.engine.ds.IntQueue;

/**
 * Min-Cut as the Dual of Max-Flow.
 * Computes the minimum capacity S-T cut separating source and sink in the residual graph.
 * Cut Capacity == Maximum Flow (Max-Flow Min-Cut Theorem).
 * Zero java.util.* usage.
 */
public class MinCutDual {

    public static class MinCutResult {
        public int cutCapacity;
        public CustomArrayList<Integer> sourcePartitionS;
        public CustomArrayList<Integer> sinkPartitionT;
        public CustomArrayList<FlowNetwork.Edge> cutEdges;

        public MinCutResult(int cutCapacity,
                            CustomArrayList<Integer> sourcePartitionS,
                            CustomArrayList<Integer> sinkPartitionT,
                            CustomArrayList<FlowNetwork.Edge> cutEdges) {
            this.cutCapacity = cutCapacity;
            this.sourcePartitionS = sourcePartitionS;
            this.sinkPartitionT = sinkPartitionT;
            this.cutEdges = cutEdges;
        }
    }

    public static MinCutResult computeMinCut(FlowNetwork graph, int source, int sink) {
        // Step 1: Run Dinic's Algorithm to achieve maximum flow
        int maxFlow = DinicsAlgorithm.computeMaxFlow(graph, source, sink);

        // Step 2: Find all vertices reachable from source in the residual graph
        int n = graph.getNumVertices();
        boolean[] reachable = new boolean[n];
        IntQueue queue = new IntQueue(n);

        reachable[source] = true;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (int i = 0; i < graph.getEdgesFrom(u).size(); i++) {
                FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
                if (!reachable[edge.v] && edge.remainingCapacity() > 0) {
                    reachable[edge.v] = true;
                    queue.enqueue(edge.v);
                }
            }
        }

        // Step 3: Partition nodes into S (reachable) and T (non-reachable)
        CustomArrayList<Integer> partitionS = new CustomArrayList<>();
        CustomArrayList<Integer> partitionT = new CustomArrayList<>();
        for (int i = 0; i < n; i++) {
            if (reachable[i]) {
                partitionS.add(i);
            } else {
                partitionT.add(i);
            }
        }

        // Step 4: Identify cut edges (edges from S to T in original graph)
        CustomArrayList<FlowNetwork.Edge> cutEdges = new CustomArrayList<>();
        int computedCutCap = 0;

        for (int u = 0; u < n; u++) {
            if (reachable[u]) {
                for (int i = 0; i < graph.getEdgesFrom(u).size(); i++) {
                    FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
                    if (!reachable[edge.v] && edge.capacity > 0) {
                        cutEdges.add(edge);
                        computedCutCap += edge.capacity;
                    }
                }
            }
        }

        return new MinCutResult(computedCutCap, partitionS, partitionT, cutEdges);
    }
}

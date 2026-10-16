package texthack.engine.m4_flow;

import texthack.engine.ds.IntQueue;

/**
 * Min-Cost Max-Flow (MCMF) via Successive Shortest Path (SSP) with SPFA.
 * Finds the maximum flow from source to sink that minimizes total linear cost.
 * Zero java.util.* usage.
 */
public class MinCostMaxFlow {

    public static class MCMFResult {
        public int maxFlow;
        public int minCost;

        public MCMFResult(int maxFlow, int minCost) {
            this.maxFlow = maxFlow;
            this.minCost = minCost;
        }

        @Override
        public String toString() {
            return String.format("MCMF Result [MaxFlow = %d, MinCost = %d]", maxFlow, minCost);
        }
    }

    public static MCMFResult computeMCMF(FlowNetwork graph, int source, int sink) {
        int n = graph.getNumVertices();
        int totalFlow = 0;
        int totalCost = 0;

        int[] dist = new int[n];
        FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[n];
        boolean[] inQueue = new boolean[n];
        int INF = 1_000_000_000;

        while (true) {
            for (int i = 0; i < n; i++) {
                dist[i] = INF;
                edgeTo[i] = null;
                inQueue[i] = false;
            }

            IntQueue queue = new IntQueue(n);
            dist[source] = 0;
            queue.enqueue(source);
            inQueue[source] = true;

            // SPFA (Shortest Path Faster Algorithm) on residual costs
            while (!queue.isEmpty()) {
                int u = queue.dequeue();
                inQueue[u] = false;

                for (int i = 0; i < graph.getEdgesFrom(u).size(); i++) {
                    FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
                    int v = edge.v;

                    if (edge.remainingCapacity() > 0 && dist[u] + edge.cost < dist[v]) {
                        dist[v] = dist[u] + edge.cost;
                        edgeTo[v] = edge;
                        if (!inQueue[v]) {
                            queue.enqueue(v);
                            inQueue[v] = true;
                        }
                    }
                }
            }

            if (dist[sink] == INF) {
                break; // No augmenting path exists
            }

            // Find bottleneck capacity along shortest cost augmenting path
            int bottleNeck = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = edgeTo[v].u) {
                bottleNeck = Math.min(bottleNeck, edgeTo[v].remainingCapacity());
            }

            // Augment along path
            for (int v = sink; v != source; v = edgeTo[v].u) {
                edgeTo[v].augment(bottleNeck);
                totalCost += bottleNeck * edgeTo[v].cost;
            }

            totalFlow += bottleNeck;
        }

        return new MCMFResult(totalFlow, totalCost);
    }
}

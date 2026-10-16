package texthack.engine.m4_flow;

import texthack.engine.ds.IntQueue;

/**
 * Edmonds-Karp Max-Flow Algorithm.
 * Implements the Ford-Fulkerson method using BFS shortest augmenting paths.
 * Guarantees polynomial running time: O(V * E^2).
 * Zero java.util.* usage.
 */
public class EdmondsKarp {

    public static int computeMaxFlow(FlowNetwork graph, int source, int sink) {
        int n = graph.getNumVertices();
        int maxFlow = 0;

        while (true) {
            FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[n];
            boolean[] visited = new boolean[n];
            IntQueue queue = new IntQueue(n);

            visited[source] = true;
            queue.enqueue(source);

            while (!queue.isEmpty() && !visited[sink]) {
                int u = queue.dequeue();
                for (int i = 0; i < graph.getEdgesFrom(u).size(); i++) {
                    FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
                    int v = edge.v;
                    if (!visited[v] && edge.remainingCapacity() > 0) {
                        visited[v] = true;
                        edgeTo[v] = edge;
                        queue.enqueue(v);
                    }
                }
            }

            if (!visited[sink]) {
                break; // No augmenting path exists in residual network
            }

            // Find bottleneck capacity along path
            int bottleNeck = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = edgeTo[v].u) {
                bottleNeck = Math.min(bottleNeck, edgeTo[v].remainingCapacity());
            }

            // Augment flow
            for (int v = sink; v != source; v = edgeTo[v].u) {
                edgeTo[v].augment(bottleNeck);
            }

            maxFlow += bottleNeck;
        }

        return maxFlow;
    }
}

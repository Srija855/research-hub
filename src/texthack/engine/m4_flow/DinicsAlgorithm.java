package texthack.engine.m4_flow;

import texthack.engine.ds.IntQueue;

/**
 * Dinic's Algorithm for Maximum Network Flow.
 * Computes blocking flows on BFS Level Graphs in O(V^2 * E) time.
 * On unit networks / bipartite graphs, achieves O(E * sqrt(V)).
 * Zero java.util.* usage.
 */
public class DinicsAlgorithm {

    public static int computeMaxFlow(FlowNetwork graph, int source, int sink) {
        int n = graph.getNumVertices();
        int maxFlow = 0;
        int[] level = new int[n];
        int[] ptr = new int[n];

        while (bfsLevelGraph(graph, source, sink, level)) {
            for (int i = 0; i < n; i++) ptr[i] = 0; // Reset current-arc pointers
            while (true) {
                int pushed = dfsBlockingFlow(graph, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed == 0) break;
                maxFlow += pushed;
            }
        }
        return maxFlow;
    }

    private static boolean bfsLevelGraph(FlowNetwork graph, int source, int sink, int[] level) {
        int n = graph.getNumVertices();
        for (int i = 0; i < n; i++) level[i] = -1;

        IntQueue queue = new IntQueue(n);
        level[source] = 0;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (int i = 0; i < graph.getEdgesFrom(u).size(); i++) {
                FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
                if (edge.remainingCapacity() > 0 && level[edge.v] == -1) {
                    level[edge.v] = level[u] + 1;
                    queue.enqueue(edge.v);
                }
            }
        }
        return level[sink] != -1;
    }

    private static int dfsBlockingFlow(FlowNetwork graph, int u, int sink, int flow, int[] level, int[] ptr) {
        if (u == sink || flow == 0) return flow;

        for (int i = ptr[u]; i < graph.getEdgesFrom(u).size(); i = ++ptr[u]) {
            FlowNetwork.Edge edge = graph.getEdgesFrom(u).get(i);
            if (level[edge.v] == level[u] + 1 && edge.remainingCapacity() > 0) {
                int pushed = dfsBlockingFlow(graph, edge.v, sink, Math.min(flow, edge.remainingCapacity()), level, ptr);
                if (pushed > 0) {
                    edge.augment(pushed);
                    return pushed;
                }
            }
        }
        return 0;
    }
}

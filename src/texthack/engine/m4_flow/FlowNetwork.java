package texthack.engine.m4_flow;

import texthack.engine.ds.CustomArrayList;

/**
 * Residual Flow Network Representation.
 * Stores directed edges with forward and backward capacities.
 * Zero java.util.* usage.
 */
public class FlowNetwork {

    public static class Edge {
        public int u;
        public int v;
        public int capacity;
        public int flow;
        public int cost; // For min-cost max-flow
        public Edge residual;

        public Edge(int u, int v, int capacity, int cost) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0;
            this.cost = cost;
        }

        public int remainingCapacity() {
            return capacity - flow;
        }

        public void augment(int bottleNeck) {
            this.flow += bottleNeck;
            this.residual.flow -= bottleNeck;
        }
    }

    private final int numVertices;
    private final CustomArrayList<CustomArrayList<Edge>> adj;

    public FlowNetwork(int numVertices) {
        this.numVertices = numVertices;
        this.adj = new CustomArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adj.add(new CustomArrayList<>());
        }
    }

    public void addEdge(int u, int v, int capacity) {
        addEdge(u, v, capacity, 0);
    }

    public void addEdge(int u, int v, int capacity, int cost) {
        Edge forward = new Edge(u, v, capacity, cost);
        Edge backward = new Edge(v, u, 0, -cost);
        forward.residual = backward;
        backward.residual = forward;
        adj.get(u).add(forward);
        adj.get(v).add(backward);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public CustomArrayList<Edge> getEdgesFrom(int u) {
        return adj.get(u);
    }

    public CustomArrayList<CustomArrayList<Edge>> getAdj() {
        return adj;
    }
}

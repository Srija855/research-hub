package texthack.engine.m4_flow;

import texthack.engine.ds.CustomArrayList;
import texthack.engine.m1_foundation.CorpusLoader.ProjectData;

/**
 * Practical Reductions to Max-Flow / Min-Cut:
 * 1. Project Selection Problem (Dependencies + Profits) reduced to Min-Cut
 * 2. Processor Job Scheduling with Deadlines reduced to Flow
 * 3. Document / Image Segmentation reduced to Min-Cut
 * Zero java.util.* usage.
 */
public class FlowReductions {

    public static class ProjectSelectionResult {
        public int maxNetProfit;
        public CustomArrayList<String> selectedProjects;

        public ProjectSelectionResult(int maxNetProfit, CustomArrayList<String> selectedProjects) {
            this.maxNetProfit = maxNetProfit;
            this.selectedProjects = selectedProjects;
        }
    }

    /**
     * Solves the Project Selection Problem via Min-Cut Reduction.
     * Given projects with revenues (>0) or costs (<0) and dependency prerequisites (u requires v):
     * - Source S connected to each positive project u with capacity profit[u]
     * - Each negative project v connected to Sink T with capacity -profit[v]
     * - Infinite capacity edge from u to v if u requires v
     * Maximum Net Profit = Total Positive Profits - Min-Cut Capacity.
     */
    public static ProjectSelectionResult solveProjectSelection(CustomArrayList<ProjectData> projects) {
        int n = projects.size();
        int source = 0;
        int sink = n + 1;
        FlowNetwork net = new FlowNetwork(n + 2);

        int totalPositiveProfit = 0;
        int INF = 1_000_000_000;

        for (int i = 0; i < n; i++) {
            ProjectData p = projects.get(i);
            int node = i + 1;

            if (p.profit > 0) {
                net.addEdge(source, node, p.profit);
                totalPositiveProfit += p.profit;
            } else if (p.profit < 0) {
                net.addEdge(node, sink, -p.profit);
            }
        }

        // Add infinite capacity dependency edges
        for (int i = 0; i < n; i++) {
            ProjectData p = projects.get(i);
            int u = i + 1;
            for (int d = 0; d < p.dependencies.size(); d++) {
                String depId = p.dependencies.get(d);
                for (int j = 0; j < n; j++) {
                    if (projects.get(j).id.equals(depId)) {
                        int v = j + 1;
                        net.addEdge(u, v, INF);
                        break;
                    }
                }
            }
        }

        MinCutDual.MinCutResult cut = MinCutDual.computeMinCut(net, source, sink);
        int maxProfit = totalPositiveProfit - cut.cutCapacity;

        CustomArrayList<String> chosen = new CustomArrayList<>();
        for (int i = 0; i < n; i++) {
            int node = i + 1;
            if (cut.sourcePartitionS.contains(node)) {
                chosen.add(projects.get(i).id + " (" + projects.get(i).name + ")");
            }
        }

        return new ProjectSelectionResult(maxProfit, chosen);
    }

    /**
     * Solves Processor Job Scheduling with Deadlines via Max-Flow.
     * Jobs with execution times and deadlines mapped into discrete time slots.
     */
    public static boolean canScheduleJobs(int[] jobDurations, int[] jobDeadlines, int numSlots) {
        int numJobs = jobDurations.length;
        int source = 0;
        int sink = numJobs + numSlots + 1;
        FlowNetwork net = new FlowNetwork(sink + 1);

        int totalRequiredWork = 0;
        for (int i = 0; i < numJobs; i++) {
            totalRequiredWork += jobDurations[i];
            net.addEdge(source, i + 1, jobDurations[i]);

            // Connect job to valid time slots before deadline
            for (int t = 1; t <= Math.min(jobDeadlines[i], numSlots); t++) {
                net.addEdge(i + 1, numJobs + t, 1);
            }
        }

        // Connect each time slot to sink (capacity 1 job per time slot)
        for (int t = 1; t <= numSlots; t++) {
            net.addEdge(numJobs + t, sink, 1);
        }

        int maxFlow = DinicsAlgorithm.computeMaxFlow(net, source, sink);
        return maxFlow == totalRequiredWork;
    }
}

package texthack.api;

import texthack.engine.ds.CustomArrayList;

/**
 * TextHack Query Result object encapsulating answer metrics,
 * algorithm taxonomy mapping, textbook canon citations, and asymptotic analysis.
 */
public class QueryResult {
    private final QueryType queryType;
    private final String queryInput;
    private final String algorithmUsed;
    private final String algorithmFamily;
    private final String asymptoticTimeComplexity;
    private final String asymptoticSpaceComplexity;
    private final String textbookCitation;
    private final CustomArrayList<String> details;
    private final long executionTimeNanos;

    public QueryResult(QueryType queryType,
                       String queryInput,
                       String algorithmUsed,
                       String algorithmFamily,
                       String asymptoticTimeComplexity,
                       String asymptoticSpaceComplexity,
                       String textbookCitation,
                       CustomArrayList<String> details,
                       long executionTimeNanos) {
        this.queryType = queryType;
        this.queryInput = queryInput;
        this.algorithmUsed = algorithmUsed;
        this.algorithmFamily = algorithmFamily;
        this.asymptoticTimeComplexity = asymptoticTimeComplexity;
        this.asymptoticSpaceComplexity = asymptoticSpaceComplexity;
        this.textbookCitation = textbookCitation;
        this.details = details;
        this.executionTimeNanos = executionTimeNanos;
    }

    public QueryType getQueryType() { return queryType; }
    public String getQueryInput() { return queryInput; }
    public String getAlgorithmUsed() { return algorithmUsed; }
    public String getAlgorithmFamily() { return algorithmFamily; }
    public String getAsymptoticTimeComplexity() { return asymptoticTimeComplexity; }
    public String getAsymptoticSpaceComplexity() { return asymptoticSpaceComplexity; }
    public String getTextbookCitation() { return textbookCitation; }
    public CustomArrayList<String> getDetails() { return details; }
    public long getExecutionTimeNanos() { return executionTimeNanos; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("[QUERY: %s] | Input: \"%s\"\n", queryType, queryInput));
        sb.append(String.format("Algorithm: %s (%s)\n", algorithmUsed, algorithmFamily));
        sb.append(String.format("Complexity: Time %s | Space %s\n", asymptoticTimeComplexity, asymptoticSpaceComplexity));
        sb.append(String.format("Canon Citation: %s\n", textbookCitation));
        sb.append(String.format("Execution Time: %.3f ms\n", executionTimeNanos / 1_000_000.0));
        sb.append("--------------------------------------------------------------------------------\n");
        for (int i = 0; i < details.size(); i++) {
            sb.append("  * ").append(details.get(i)).append("\n");
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }
}

package conflict_analysers;

import data_structures.Node;
import data_structures.Variable;

import java.util.HashSet;
import java.util.Set;

public abstract class ResolutionBasedConflictAnalyser implements ConflictAnalyser {
    /**
     * Do resolution on the candidate nodes as a clause, and clause as another clause.
     *
     * @param candidates First clause
     * @param clause Second clause
     * @param resolvableVariable The variable being removed from both clauses for resolution
     * @return The resulting clause after resolution as a set of nodes
     */
    public Set<Node> resolve(Set<Node> candidates, Set<Node> clause, Variable resolvableVariable) {
        Set<Node> resolvedClause = new HashSet<>();
        for (Node candidate : candidates) {
            if (!candidate.getVariable().equals(resolvableVariable)) {
                resolvedClause.add(candidate);
            }
        }
        for (Node clauseNode : clause) {
            if (!clauseNode.getVariable().equals(resolvableVariable)) {
                resolvedClause.add(clauseNode);
            }
        }
        return resolvedClause;
    }

    /**
     * Count the number of nodes assigned at the given decision level
     *
     * @param nodes
     * @param decisionLevel
     * @return Number of nodes assigned at the given decision level
     */
    public int countNodesAtDecisionLevel(Set<Node> nodes, int decisionLevel) {
        int numAtDecisionLevel = 0;
        for (Node node : nodes) {
            if (node.getDecisionLevel() == decisionLevel) {
                numAtDecisionLevel++;
            }
        }
        return numAtDecisionLevel;
    }
}

package conflict_analysers;

import data_structures.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ExtendedConflictAnalyser implements ConflictAnalyser {
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

    public ConflictAnalyserResult buildConflictAnalyserResult(Set<Node> candidates, Assignments assignments, Set<Variable> variablesResolved) {
        // Generate new learnt clause
        int maxLevel = -1;
        int assertionLevel = -1;
        Set<Literal> learntLiterals = new HashSet<>();
        for (Node candidate : candidates) {
            learntLiterals.add(new Literal(candidate.getVariable(), assignments.getVariableAssignment(candidate.getVariable())));
            if (candidate.getDecisionLevel() > maxLevel) {
                assertionLevel = maxLevel;
                maxLevel = candidate.getDecisionLevel();
            } else if (candidate.getDecisionLevel() < maxLevel && candidate.getDecisionLevel() > assertionLevel) {
                assertionLevel = candidate.getDecisionLevel();
            }
        }
        Clause learntClause = new Clause(new ArrayList<>(learntLiterals));

        // Default assertion level when only one literal in learnt clause or all literals have same decision level
        if (learntClause.size() == 1 || (maxLevel != -1 && assertionLevel == -1)) {
            assertionLevel = 0;
        }

        return new ConflictAnalyserResult(assertionLevel, learntClause, variablesResolved);
    }

    public ConflictAnalyserResult buildConflictAnalyserResult(Set<Node> candidates, Assignments assignments) {
        return buildConflictAnalyserResult(candidates, assignments, new HashSet<>());
    }

    public void removeConflictingNodeFromGraph(Node inferredNode) {
        List<Edge> inEdges = inferredNode.getInEdges();
        for (Edge inEdge : inEdges) {
            inEdge.getFromNode().removeOutEdge(inEdge);
        }
    }
}

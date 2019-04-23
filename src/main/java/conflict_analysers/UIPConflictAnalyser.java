package conflict_analysers;

import data_structures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UIPConflictAnalyser extends ResolutionBasedConflictAnalyser {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIPConflictAnalyser.class);

    @Override
    public ConflictAnalyserResult learnClause(UnitResolutionResult conflict, Assignments assignments) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();
        int conflictDecisionLevel = conflict.getConflictDecisionLevel();

        // Remove all outgoing edges from the conflicting nodes
        inferredNode.removeSubtree();
        conflictingNode.removeSubtree();

        // Collect edges that directly lead to the conflict
        Queue<Edge> cutEdges = new LinkedList<>(conflictingNode.getInEdges());
        cutEdges.addAll(inferredNode.getInEdges());

        // Find the nodes leading to conflict site
        Set<Node> candidates = new HashSet<>();
        while (!cutEdges.isEmpty()) {
            candidates.add(cutEdges.poll().getFromNode());
        }

        // Do resolution to find learnt clause
        int numLiteralsAtDecisionLevel = countNodesAtDecisionLevel(candidates, conflictDecisionLevel);
        for (Node candidate : candidates) {
            cutEdges.addAll(candidate.getInEdges());
        }
        Set<Node> visited = new HashSet<>();
        while (numLiteralsAtDecisionLevel != 1 && !cutEdges.isEmpty()) {
            Edge cutEdge = cutEdges.poll();
            if (cutEdge.getToNode().equals(conflictingNode) || cutEdge.getToNode().equals(inferredNode)) {
                // Cannot do resolution with conflicting nodes
                continue;
            }
            if (cutEdge.getToNode().getDecisionLevel() > conflictDecisionLevel) {
                // Don't consider learning literals assigned at decision level higher than conflict level
                continue;
            }
            if (!visited.add(cutEdge.getToNode())) {
                // Don't resolve with previously resolved with nodes
                continue;
            }
            candidates = resolve(candidates, assignments.getNodes(cutEdge.getDueToClause()), cutEdge.getToNode().getVariable());
            numLiteralsAtDecisionLevel = countNodesAtDecisionLevel(candidates, conflictDecisionLevel);
            cutEdges.addAll(cutEdge.getFromNode().getInEdges());
        }

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
        LOGGER.debug("LEARNT new clause {}", learntClause.toString());

        // Remove assignment of the conflicting node which came second
        List<Edge> inEdges = inferredNode.getInEdges();
        for (Edge inEdge : inEdges) {
            inEdge.getFromNode().removeOutEdge(inEdge);
        }

        // Default assertion level when only one literal in learnt clause or all literals have same decision level
        if (learntClause.size() == 1 || (maxLevel != -1 && assertionLevel == -1)) {
            return new ConflictAnalyserResult(0, learntClause);
        }

        return new ConflictAnalyserResult(assertionLevel, learntClause);
    }
}

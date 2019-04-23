package conflict_analysers;

import data_structures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public abstract class UIPConflictAnalyser extends ExtendedConflictAnalyser {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIPConflictAnalyser.class);

    abstract int getUIPCriteria();

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
        while (numLiteralsAtDecisionLevel != getUIPCriteria() && !cutEdges.isEmpty()) {
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

        // Build result with learnt clause and assertion level
        ConflictAnalyserResult result = buildConflictAnalyserResult(candidates, assignments);
        LOGGER.debug("LEARNT new clause {}", result.getLearntClause().toString());

        // Remove assignment of the conflicting node which came second
        removeConflictingNodeFromGraph(inferredNode);

        return result;
    }
}

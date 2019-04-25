package conflict_analysers;

import data_structures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class DirectCutConflictAnalyser extends ExtendedConflictAnalyser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectCutConflictAnalyser.class);

    @Override
    public ConflictAnalyserResult learnClause(UnitResolutionResult conflict, Assignments assignments) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();

        // Collect edges that directly lead to the conflict
        Queue<Edge> cutEdges = new LinkedList<>(conflictingNode.getInEdges());
        cutEdges.addAll(inferredNode.getInEdges());

        // Find the nodes leading to conflict site
        Set<Node> candidates = new HashSet<>();
        while (!cutEdges.isEmpty()) {
            candidates.add(cutEdges.poll().getFromNode());
        }

        // Build result with learnt clause and assertion level
        ConflictAnalyserResult result = buildConflictAnalyserResult(candidates, assignments);
        LOGGER.info("LEARNT new clause {}", result.getLearntClause().toString());

        // Remove assignment of the conflicting node which came second
        removeConflictingNodeFromGraph(inferredNode);

        return result;
    }
}

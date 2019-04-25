package conflict_analysers;

import data_structures.Assignments;
import data_structures.ConflictAnalyserResult;
import data_structures.Node;
import data_structures.UnitResolutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RootsCutConflictAnalyser extends ExtendedConflictAnalyser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootsCutConflictAnalyser.class);

    @Override
    public ConflictAnalyserResult learnClause(UnitResolutionResult conflict, Assignments assignments) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();

        Set<Node> candidates = inferredNode.getAncestors();
        candidates.addAll(conflictingNode.getAncestors());

        // Build result with learnt clause and assertion level
        ConflictAnalyserResult result = buildConflictAnalyserResult(candidates, assignments);
        LOGGER.debug("LEARNT new clause {}", result.getLearntClause().toString());

        // Remove assignment of the conflicting node which came second
        removeConflictingNodeFromGraph(inferredNode);

        return result;
    }
}

package conflict_analysers;

import data_structures.Assignments;
import data_structures.ConflictAnalyserResult;
import data_structures.Node;
import data_structures.UnitResolutionResult;

import java.util.Set;

public class RootsConflictAnalyser implements ConflictAnalyser {
    @Override
    public ConflictAnalyserResult learnClause(UnitResolutionResult conflict, Assignments assignments) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();

        Set<Node> candidates = inferredNode.getAncestors();
        candidates.addAll(conflictingNode.getAncestors());


        return null;
    }
}

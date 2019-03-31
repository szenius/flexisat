package branching_heuristics;

import data_structures.Assignments;
import data_structures.Clauses;

public class RandomVariable extends PickBranchingVariableHeuristic{
    @Override
    public int pickBranchingVariable(Clauses clauses, Assignments assignments) {
        return (int) assignments.getUnassignedVarIds().toArray()[0];
    }
}

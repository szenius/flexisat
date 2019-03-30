package branching_heuristics;

import data_structures.Assignments;
import data_structures.Clauses;

public interface PickBranchingVariableHeuristic {
    int pickBranchingVariable(Clauses clauses, Assignments assignments);
}

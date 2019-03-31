package branching_heuristics;

import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Clauses;

public abstract class PickBranchingVariableHeuristic {

    public abstract int pickBranchingVariable(Clauses clauses, Assignments assignments);

    // Only applicable to Two-Literal Clause heuristics. The other heuristics will just do nothing when this is called
    // to speed things up.
    public void updateTwoClauseStatus(Assignments assignments, Clause clause) {
    }

}

package data_structures;

import java.util.ArrayList;
import java.util.List;

public class Clauses {
    private List<Clause> clauses;

    public Clauses(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public List<Clause> getClauses() {
        return this.clauses;
    }

    public boolean hasEmptyClause() {
        for (Clause clause : clauses) {
            if (clause.getLiterals().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given the current assignment, try to infer new assignments.
     *
     * @param assignment current assignments
     * @param decisionLevel decision level at which this resolution is being invoked
     * @return false if any UNSAT in assignments. true otherwise.
     */
    public boolean resolve(Assignment assignment, int decisionLevel) {
        int currentClauseIndex = 0;
        while (currentClauseIndex != clauses.size() - 1) {
            Assignment.AssignmentStatus status =
                    assignment.findAndAssignVariable(clauses.get(currentClauseIndex), decisionLevel);
            if (status == Assignment.AssignmentStatus.SUCCESS) {
                currentClauseIndex = 0;
            } else if (status == Assignment.AssignmentStatus.NO_UNIT_CLAUSES){
                currentClauseIndex++;
            } else {
                return false;
            }
        }
        return true;
    }
}
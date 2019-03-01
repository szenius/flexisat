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

    public List<Clause> getUnitClauses() {
        List<Clause> result = new ArrayList<>();
        for (Clause clause : clauses) {
            if (clause.isUnitClause()) {
                result.add(clause);
            }
        }
        return result;
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
     * Given the current assignment, remove the assigned variables and try to infer new assignments.
     *
     * @param assignment current assignments
     * @param decisionLevel decision level at which this resolution is being invoked
     */
    public void resolve(Assignment assignment, int decisionLevel) {
        for (Clause clause : clauses) {
            for (Literal literal : clause.getLiterals()) {
                if (assignment.getAssignedVarIds().contains(literal.getVariable().getId())) {
                    if (clause.getLiterals().size() == 2) {
                        // todo: stub omg this makes my head hurt
                    }
                }
            }
        }
    }
}
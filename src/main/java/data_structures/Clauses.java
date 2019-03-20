package data_structures;

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
     * Check if the entire formula is SAT given the current assignments.
     *
     * @param assignments current assignments
     * @param decisionLevel decision level at which this resolution is being invoked
     * @return false if any UNSAT in assignments. true otherwise.
     */
    public boolean resolve(Assignments assignments, int decisionLevel) {
        for (Clause clause : clauses) {
            boolean sat = clause.checkSAT(assignments);
            if (!sat) return false;
        }
        return true;
    }
}
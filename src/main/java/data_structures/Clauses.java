package data_structures;

import java.util.Set;

public class Clauses {
    private Set<Clause> clauses;

    public Clauses(Set<Clause> clauses) {
        this.clauses = clauses;
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public Set<Clause> getClauses() {
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
     * Check if the entire formula is VALID given the current assignments.
     *
     * @param assignments current assignments
     * @return false if any UNSAT in assignments. true otherwise.
     */
    public boolean checkVALID(Assignments assignments) {
        for (Clause clause : clauses) {
            boolean valid = clause.checkVALID(assignments);
            if (!valid) return false;
        }
        return true;
    }
}
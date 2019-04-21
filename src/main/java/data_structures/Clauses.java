package data_structures;

import java.util.Set;

public class Clauses {
    private Set<Clause> clauses;
    private Clause lastAddedClause;
    public Clauses(Set<Clause> clauses) {
        this.clauses = clauses;
        this.lastAddedClause = null;
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
        this.lastAddedClause = clause;
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
            boolean valid = clause.checkSAT(assignments);
            if (!valid) return false;
        }
        return true;
    }

    public Clause getLastAddedClause() {
        return lastAddedClause;
    }
}
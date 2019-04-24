package data_structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Clauses {
    private Set<Clause> clauses;
    public Clauses(Set<Clause> clauses) {
        this.clauses = clauses;
    }

    public List<List<Literal>> collectTwoClauses(Assignments assignments) {
        List<List<Literal>> twoClauses = new ArrayList<>();
        for (Clause clause : clauses) {
            List<Literal> unassignedLiterals = clause.getUnassignedLiterals(assignments);
            if (unassignedLiterals.size() == 2) {
                twoClauses.add(unassignedLiterals);
            }
        }
        return twoClauses;
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
}
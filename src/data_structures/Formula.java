package data_structures;

import java.util.List;

public class Formula {
    private List<Clause> clauses;

    public Formula(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public List<Clause> getClauses() {
        return this.clauses;
    }
}
package com.data_structures;

class Formula {
    private List<Clause> clauses;

    public Formula(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public List<Clauses> getClauses() {
        return this.clauses;
    }
}
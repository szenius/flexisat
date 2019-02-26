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

    public void resolve(int unitLiteralVarId, boolean b, int decisionLevel) {
        // todo: stub
    }
}
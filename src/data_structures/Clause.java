package data_structures;

import java.util.List;

public class Clause {
    private List<Literal> literals;

    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public boolean isUnitClause() {
        return this.literals.size() == 1;
    }
}
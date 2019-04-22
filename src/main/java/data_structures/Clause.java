package data_structures;

import java.util.*;

public class Clause {
    private List<Literal> literals;

    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public Literal getUnitLiteral(Assignments assignments) {
        Literal unitLiteral = null;
        boolean clauseValue = false;
        for (Literal literal : literals) {
            if (!assignments.hasAssignedVariable(literal)) {
                // Found unassigned literal
                if (unitLiteral != null) {
                    // Found more than one unassigned literal, so there is no unit literal
                    return null;
                }
                unitLiteral = literal;
            } else {
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));
            }
        }
        if (clauseValue) {
            return null;
        }
        return unitLiteral;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (Literal literal : literals) {
            int id = literal.getVariable().getId();
            if (literal.isNegated()) id *= -1;
            joiner.add(String.valueOf(id));
        }
        return "[[" + joiner.toString() + "]]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public int size() {
        return this.getLiterals().size();
    }

    public Literal findUnitLiteral(Variable variable, Assignments assignments) {
        // Count number of literals assigned other than the one corr to the input variable
        int numAssigned = 0;
        boolean clauseValue = false;
        Literal unitLiteral = null;
        for (Literal literal : getLiterals()) {
            if (assignments.hasAssignedVariable(literal)) {
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));
                if (literal.getVariable().equals(variable)) {
                    unitLiteral = literal;
                    continue;
                }
                numAssigned++;
            }
        }
        // If clause evaluates to true, no unit literal can be inferred
        if (clauseValue) {
            return null;
        }
        // Return the literal corr to the input variable if it's the only literal which has not been assigned
        if (numAssigned == literals.size() - 1) {
            return unitLiteral;
        }
        return null;
    }
}
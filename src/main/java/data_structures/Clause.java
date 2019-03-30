package data_structures;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Clause {
    private List<Literal> literals;

    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public Literal getUnitLiteral(Assignments2 assignments) {
        Literal unitLiteral = null;
        boolean clauseValue = false;
        for (Literal literal : literals) {
            if (!assignments.hasAssignedVariable(literal)) {
                if (unitLiteral != null) {
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

    public boolean checkSAT(Assignments assignments) {
        boolean clauseVal = false;
        for (Literal literal : literals) {
            if (assignments.getUnassignedVarIds().contains(literal.getVariable().getId())) {
                // There are still unassigned variables, cannot determine SAT
                return true;
            }
            clauseVal |= literal.getValue(assignments.getAssignmentValue(literal.getVariable().getId()));
        }
        System.out.println("Clause: Checked clause " + toString() + "... sat? " + clauseVal);
        return clauseVal;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (Literal literal : literals) {
            int id = literal.getVariable().getId();
            if (literal.isNegated()) id *= -1;
            joiner.add(String.valueOf(id));
        }
        return joiner.toString();
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
        Clause clause = (Clause) obj;
        List<Literal> literalsCopy = new ArrayList<>(this.getLiterals());
        for (Literal literal : clause.getLiterals()) {
            if (!literalsCopy.remove(literal)) {
                return false;
            }
        }
        return literalsCopy.isEmpty();
    }
}
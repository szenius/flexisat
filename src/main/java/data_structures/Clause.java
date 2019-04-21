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

    public boolean evaluatesToFalse(Assignments2 assignments, Variable newlyAssignedVariable, boolean newlyAssignedValue) {
        for (Literal literal : literals) {
            if (literal.getVariable().equals(newlyAssignedVariable)) {
                if (literal.isNegated() ^ newlyAssignedValue) {
                    return false;
                }
                continue;
            }
            if (!assignments.hasAssignedVariable(literal)) {
                return false;
            }
            if ((literal.isNegated() ^ assignments.getVariableAssignment(literal))) {
                return false;
            }
        }
        return true;
    }

    public Literal getUnitLiteral(Assignments2 assignments, Variable lastAssignedVariable) {
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
        if (unitLiteral == null && lastAssignedVariable != null) {
            for (Literal literal : getLiterals()) {
                if (literal.getVariable().equals(lastAssignedVariable)) {
                    return literal;
                }
            }
        }
        return unitLiteral;
    }

    public boolean checkVALID(Assignments assignments) {
        boolean clauseVal = false;
        for (Literal literal : literals) {
            if (assignments.getUnassignedVarIds().contains(literal.getVariable().getId())) {
                // There are still unassigned variables, cannot determine VALID
                return true;
            }
            clauseVal |= literal.getValue(assignments.getAssignmentValue(literal.getVariable().getId()));
        }
        //System.out.println("Clause: Checked clause " + toString() + "... valid? " + clauseVal);
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
}
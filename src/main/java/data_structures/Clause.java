package data_structures;

import java.util.List;
import java.util.StringJoiner;

// TODO: equals method. Requires Clause to have ID else it will be very troublesome.
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
            if (!assignments.hasAssignedVariable(literal.getVariable().getId())) {
                if (unitLiteral != null) {
                    return null;
                }
                unitLiteral = literal;
            } else {
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getAssignmentValue(literal.getVariable().getId()));
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
}
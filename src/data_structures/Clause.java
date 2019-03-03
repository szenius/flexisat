package data_structures;

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

    public boolean checkSAT(Assignment assignment) {
        boolean clauseVal = false;
        for (Literal literal : literals) {
            if (assignment.getUnassignedVarIds().contains(literal.getVariable().getId())) {
                // There are still unassigned variables, cannot determine SAT
                return true;
            }
            clauseVal |= (assignment.getAssignment(literal.getVariable().getId()) ^ literal.isNegated());
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
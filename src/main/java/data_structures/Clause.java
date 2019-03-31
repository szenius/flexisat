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

    // These two extra information is used for the 2-literals clause heuristics
    private boolean isTwoClause;
    private List<Integer> variablesInTwoClause;

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

    public void setIsTwoClause(boolean isTwoClause) {
        this.isTwoClause = isTwoClause;
    }

    public boolean isTwoClause() {
        return this.isTwoClause;
    }

    public void setVariablesInTwoClause(List<Integer> variablesInTwoClause) {
        this.variablesInTwoClause = variablesInTwoClause;
    }

    public List<Integer> getVariablesInTwoClause() {
        return this.variablesInTwoClause;
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
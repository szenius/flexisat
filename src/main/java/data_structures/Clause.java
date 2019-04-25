package data_structures;

import java.util.*;

public class Clause {
    private List<Literal> literals;
    private boolean isLearnt;

    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    public Clause(List<Literal> literals, boolean isLearnt) {
        this.literals = literals;
        this.isLearnt = isLearnt;
    }

    public List<Literal> getLiterals() {
        return literals;
<<<<<<< HEAD
=======
    }

    public boolean isLearnt() {
        return isLearnt;
>>>>>>> master
    }

    public int size() {
        return this.getLiterals().size();
    }

    /**
     * Tries to find an unassigned unit literal in this clause and returns it.
     *
     * @param assignments
     * @return unit literal in this clause if found. Else return null.
     */
    public Literal findUnitLiteral(Assignments assignments) {
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
                // This literal has been assigned. We add it to the evaluation of this clause's value.
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));
            }
        }
        if (clauseValue) {
            return null;
        }
        return unitLiteral;
    }

    /**
     * Tries to find an assigned unit literal in this clause which corresponds to the given target variable.
     * E.g. If we are looking for the variable 1, and this clause is [[1, 3, 5]] with 3 and 5 already assigned to false.
     *      Then the literal corresponding to 1 will be returned.
     *
     * @param target
     * @param assignments
     * @return unit literal corresponding to the target variable, if it exists. Else null.
     */
    public Literal findTargetUnitLiteral(Variable target, Assignments assignments) {
        // Count number of literals assigned other than the one corr to the input variable
        int numAssigned = 0;
        boolean clauseValue = false;
        Literal unitLiteral = null;
        for (Literal literal : literals) {
            if (assignments.hasAssignedVariable(literal)) {
                // Found assigned literal
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));

                if (literal.getVariable().equals(target)) {
                    // This assigned literal is the literal we are looking for
                    unitLiteral = literal;
                    continue;
                }
                numAssigned++;
            }
        }
        if (clauseValue) {
            // If clause evaluates to true, no unit literal can be inferred
            return null;
        }
        if (numAssigned == literals.size() - 1) {
            // Return the literal corr to the input variable if it's the only literal which has not been assigned
            return unitLiteral;
        }
        return null;
    }

    public List<Literal> getUnassignedLiterals(Assignments assignments) {
        List<Literal> unassignedLiterals = new ArrayList<>();
        for (Literal literal : getLiterals()) {
            if (!assignments.hasAssignedVariable(literal)) {
                unassignedLiterals.add(literal);
            }
        }
        return unassignedLiterals;
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
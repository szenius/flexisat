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
//        System.out.println("Trying to find unit literal from clause " + toString());
        Literal unitLiteral = null;
        boolean clauseValue = false;
        for (Literal literal : literals) {
            if (!assignments.hasAssignedVariable(literal)) {
                // Found unassigned literal
                if (unitLiteral != null) {
                    // Found more than one unassigned literal, so there is no unit literal
//                    System.out.println("Found more than one unassigned literal, so there is no unit literal");
                    return null;
                }
                unitLiteral = literal;
            } else {
                //System.out.println("Found assigned variable " + literal.getVariable().getId() + ", checking if was last assigned " + (lastAssignedVariable == null ? "null" : lastAssignedVariable.getId()));
                if (literal.getVariable().equals(lastAssignedVariable)) {
                    // Found assigned literal
                    List<Edge> edges = assignments.getNode(literal.getVariable()).getInEdges();
                    boolean foundEdgeDueToThisClause = false;
                    for (Edge edge : edges) {
                        if (edge.getDueToClause().equals(this)) {
                            foundEdgeDueToThisClause = true;
//                            System.out.println("Found edge due to clause " + toString() + " for variable " + literal.getVariable().getId());
                            break;
                        }
                    }
                    if (!foundEdgeDueToThisClause) {
                        // Assigned literal was not due to this clause
//                        System.out.println("Variable " + literal.getVariable().getId() + " was assigned but has no edge due to clause " + toString());
                        unitLiteral = literal;
                    }
                }
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));
            }
        }
        if (clauseValue) {
//            System.out.println("Clause " + toString() + " evaluates to true, so cannot infer unit literal");
            return null;
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
package data_structures;

import java.util.*;

/**
 * TODO: make assignment and decision level the same variable so we don't have to maintain two equivalent maps
 */
public class Assignment {
    private Set<Integer> varIds;
    private Map<Integer, Boolean> assignments;
    private Map<Integer, Integer> decisionLevels;

    public Assignment(Set<Integer> varIds) {
        this.varIds = varIds;
        this.assignments = new HashMap<>();
        this.decisionLevels = new HashMap<>();
    }

    /**
     * Tries to add an assignment to a variable by its integer ID.
     *
     * @param varId integer ID corresponding to a variable
     * @param assignment true/false
     * @param decisionLevel decision level of assignment
     * @return false if an alternative assignments already exists, otherwise true.
     */
    public boolean addAssignment(int varId, boolean assignment, int decisionLevel) {
        if (assignments.containsKey(varId)) {
            return assignments.get(varId) == assignment;
        }
        assignments.put(varId, assignment);
        decisionLevels.put(varId, decisionLevel);
        return true;
    }

    /**
     * Tries to change the add an assignment if it doesn't already exist.
     * If it already exists and is a true assignment, change it to false.
     * if it already exists and is a false assignment, it indicates we have tried both assignments.
     *  So return false to indicate conflict.
     *
     * @param varId integer ID corresponding to a variable
     * @param decisionLevel decision level of assignment
     * @return false if there is an existing false assignment. True otherwise.
     */
    public boolean changeAssignment(int varId, int decisionLevel) {
        if (assignments.containsKey(varId)) {
            boolean existingAssignment = assignments.get(varId);
            if (!existingAssignment) {
                return false;
            }
            assignments.replace(varId, false);
            decisionLevels.replace(varId, decisionLevel);
        } else {
            assignments.put(varId, true);
            decisionLevels.put(varId, decisionLevel);
        }
        return true;
    }


    /**
     * Iterates through the literals in a clause to check if it is a
     * unit clause (1 literal left unassigned).
     * @param clause clause to be checked
     * @return null if clause is not a unit clause. Variable otherwise.
     */
    public boolean findAndAssignVariable(Clause clause, int decisionLevel) {
        List<Literal> clauseLiterals = clause.getLiterals();
        boolean isUnitClause = false;
        Literal literalToBeAssigned = null;
        for (int i = 0 ; i < clauseLiterals.size(); i++) {
            Literal literal = clauseLiterals.get(i);
            // Literal not assigned yet.
            if (!this.assignments.containsKey(literal.getVariable().getId())) {
                if (isUnitClause == false ){
                    isUnitClause = true;
                    literalToBeAssigned = literal;
                } else {    // >= 2 Literals are unassigned.
                    return false;
                }
            } else if (this.assignments.containsKey(literal.getVariable().getId()) ) {
                if (literal.isTrue(this.assignments.get(literal.getVariable().getId()))) {
                    return false;
                }
            }
        }
        assignVariable(literalToBeAssigned, decisionLevel);
        return true;
    }


    private void assignVariable(Literal literal, int decisionLevel) {
        if (literal.isNegated()) {
            addAssignment(literal.getVariable().getId(),false, decisionLevel);
        } else {
            addAssignment(literal.getVariable().getId(), true, decisionLevel);
        }
    }


    /**
     * @return All varIDs without an assignment
     */
    public Set<Integer> getUnassignedVarIds() {
        Set<Integer> result = new HashSet<>();
        for (Integer id : varIds) {
            if (!assignments.containsKey(id)) {
                result.add(id);
            }
        }
        return result;
    }

    /**
     * @return All varIDs with an assignment
     */
    public Set<Integer> getAssignedVarIds() {
        return assignments.keySet();
    }
}

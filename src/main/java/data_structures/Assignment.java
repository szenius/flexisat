package data_structures;

import java.util.*;

/**
 * TODO: make assignment and decision level the same variable so we don't have to maintain two equivalent maps
 */
public class Assignment {
    private Set<Integer> varIds;
    private Map<Integer, Integer> decisionLevels;
    private Map<Integer, AssignmentUnit> assignments;
    private Stack<Integer> lastAssignedIds;

    public Assignment(Set<Integer> varIds) {
        this.varIds = varIds;
        this.assignments = new HashMap<>();
        this.decisionLevels = new HashMap<>();
        this.lastAssignedIds = new Stack<>();
    }

    /**
     * Tries to add an assignment to a variable by its integer ID.
     *
     * @param varId integer ID corresponding to a variable
     * @param assignment true/false
     * @param decisionLevel decision level of assignment
     * @return false if an alternative assignments already exists, otherwise true.
     */
    public boolean addAssignment(int varId, boolean assignment, int decisionLevel, List<AssignmentUnit> impliedBy) {
        System.out.println("Assignment: Trying to add assignment of " + varId + "@" + decisionLevel + "=" + String.valueOf(assignment));
        if (assignments.containsKey(varId)) {
            return assignments.get(varId).getAssignment() == assignment;
        }
        AssignmentUnit assignmentUnit = new AssignmentUnit(varId, assignment, decisionLevel);
        if (impliedBy != null) {
            assignmentUnit.addImpliedBy(impliedBy);
        }
        assignments.put(varId, assignmentUnit);
        decisionLevels.put(varId, decisionLevel);
        lastAssignedIds.push(varId);
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
        System.out.println("Assignment: Trying to change assignment of " + varId);
        if (assignments.containsKey(varId)) {
            boolean existingAssignment = assignments.get(varId).getAssignment();
            if (!existingAssignment) {
                return false;
            }
            assignments.get(varId).replaceAssignment(false);
            decisionLevels.replace(varId, decisionLevel);
        } else {
            AssignmentUnit assignmentUnit = new AssignmentUnit(varId, true, decisionLevel);
            assignments.put(varId, assignmentUnit);
            decisionLevels.put(varId, decisionLevel);
        }
        lastAssignedIds.push(varId);
        return true;
    }

    /**
     * Tries to assign any unit literal in this clause so that its value is TRUE.
     * This function will only ever be called during assignment inference.
     *
     * @param clause
     * @return True if an unit literal was assigned, False otherwise.
     */
    public boolean assignUnitClause(Clause clause, int decisionLevel) {
        // Find unit literal, if any
        Literal unitLiteral = null;
        List<AssignmentUnit> impliedBy = new ArrayList<>();
        for (Literal literal : clause.getLiterals()) {
            if (getUnassignedVarIds().contains(literal.getVariable().getId())) {
                if (unitLiteral == null) {
                    unitLiteral = literal;
                } else {
                    // This is not a unit clause
                    return false;
                }
            } else {
                impliedBy.add(assignments.get(literal.getVariable().getId()));
            }
        }

        if (unitLiteral == null) {
            // Did not find any unassigned variable
            return false;
        }

        // Assign the literal so its value is true
        return addAssignment(unitLiteral.getVariable().getId(), !unitLiteral.isNegated(), decisionLevel, impliedBy);
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

    public boolean getAssignment(int id) {
        return assignments.get(id).getAssignment();
    }

    public int getLastAssignment() {
        return lastAssignedIds.peek();
    }

    public void revertLastAssignment() {
        int lastAssignedId = lastAssignedIds.pop();
        assignments.remove(lastAssignedId);
        decisionLevels.remove(lastAssignedId);
    }
}

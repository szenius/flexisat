package data_structures;

import java.util.*;

/**
 * TODO: make assignment and decision level the same variable so we don't have to maintain two equivalent maps
 */
public class Assignment {
    private Set<Integer> varIds;
    private Map<Integer, AssignmentUnit> assignments;
    // This map is for quick access to each variable's decision level
    private Map<Integer, List<Integer>> decisionLevelToVariables;
    private Stack<Integer> lastAssignedIds;

    public Assignment(Set<Integer> varIds) {
        this.varIds = varIds;
        this.assignments = new HashMap<>();
        this.decisionLevelToVariables = new HashMap<>();
        this.lastAssignedIds = new Stack<>();
    }

    /**
     * Tries to add an assignment to a variable by its integer ID.
     *
     * @param assignmentUnit assignmentUnit corresponding to a variable and its assignment
     * @return false if an alternative assignments already exists, otherwise true.
     */
    public boolean addAssignment(AssignmentUnit assignmentUnit, List<AssignmentUnit> impliedBy) {
        System.out.println("Assignment: Trying to add assignment of " + assignmentUnit.getVarId() + "@" +
                assignmentUnit.getDecisionLevel() + "=" + String.valueOf(assignmentUnit.getAssignment()));
        if (assignments.containsKey(assignmentUnit.getVarId())) {
            return assignments.get(assignmentUnit.getVarId()).getAssignment() ==
                    assignmentUnit.getAssignment();
        }
        if (impliedBy != null) {
            assignmentUnit.addImpliedBy(impliedBy);
        }
        assignments.put(assignmentUnit.getVarId(), assignmentUnit);
        if (decisionLevelToVariables.containsKey(assignmentUnit.getDecisionLevel())) {
            decisionLevelToVariables.get(assignmentUnit.getDecisionLevel()).add(assignmentUnit.getVarId());
        } else {
            List<Integer> varIds = new ArrayList<>();
            varIds.add(assignmentUnit.getVarId());
            decisionLevelToVariables.put(assignmentUnit.getDecisionLevel(), varIds);
        }
        lastAssignedIds.push(assignmentUnit.getVarId());
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
            assignments.get(varId).replaceDecisionLevel(decisionLevel);
        } else {
            AssignmentUnit assignmentUnit = new AssignmentUnit(varId, true, decisionLevel);
            assignments.put(varId, assignmentUnit);
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

        // Update the variables that implied the assignment of this variable
        AssignmentUnit assignmentUnit = new AssignmentUnit(unitLiteral.getVariable().getId(),
                                            !unitLiteral.isNegated(), decisionLevel);
        // TODO: This might not be the most efficient as we are
        //  reiterating through the clause.
        for (Literal literal : clause.getLiterals()) {
            if (!getUnassignedVarIds().contains(literal.getVariable().getId())) {
                assignments.get(literal.getVariable().getId()).addImpliedAssignment(assignmentUnit);
            }
        }

        // Assign the literal so its value is true
        return addAssignment(assignmentUnit, impliedBy);
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

    public AssignmentUnit getAssignmentUnit(int id) {
        return assignments.get(id);
    }

    public int getLastAssignment() {
        return lastAssignedIds.peek();
    }

    public int getHighestDecisionLevel() {
        if (this.decisionLevelToVariables.size() == 0) {
            return 0;
        }
        return Collections.max(this.decisionLevelToVariables.keySet());
    }

    public List<Integer> getVariablesInDecisionLevel(Integer decisionLevel) {
        return this.decisionLevelToVariables.get(decisionLevel);
    }

    public void revertLastAssignment() {
        int lastAssignedId = lastAssignedIds.pop();
        assignments.remove(lastAssignedId);
    }

    /**
     * Removes all assignments above a particular decision level.
     * @param decisionLevel
     */
    public void removeAssignmentsAboveDecisionLevel(Integer decisionLevel) {
        Integer maxDecisionLevel = getHighestDecisionLevel();
        System.out.println("Max decision leve = " + maxDecisionLevel);
        for (int i = decisionLevel; i <= maxDecisionLevel; i++ ){
            System.out.println("Decision level = " + i);
            List<Integer> varIds = this.decisionLevelToVariables.get(i);
            for (Integer varId : varIds) {
                this.assignments.remove(varId);
            }
            this.decisionLevelToVariables.remove(decisionLevel);
        }
    }
}

package data_structures;

import java.util.*;

/**
 * TODO: make assignment and decision level the same variable so we don't have to maintain two equivalent maps
 */
public class Assignments {
    private Set<Integer> varIds;
    private Map<Integer, Assignment> assignments;
    // This map is for quick access to each variable's decision level
    private Map<Integer, List<Integer>> decisionLevelToVariables;
    private Stack<Integer> lastAssignedIds;

    public Assignments(Set<Integer> varIds) {
        this.varIds = varIds;
        this.assignments = new HashMap<>();
        this.decisionLevelToVariables = new HashMap<>();
        this.lastAssignedIds = new Stack<>();
    }

    /**
     * Tries to add an assignment to a variable by its variable ID.
     *
     * @param assignment assignment corresponding to a variable and its assignment
     * @return false if an alternative assignments already exists, otherwise true.
     */
    public boolean addAssignment(Assignment assignment) {
        System.out.println("Assignment: Trying to add assignment of " + assignment.getVarId() + "@" +
                assignment.getDecisionLevel() + "=" + String.valueOf(assignment.getAssignmentValue()));
        // Variable has already been assigned. We have to check if it is a conflicting assignment.
        if (assignments.containsKey(assignment.getVarId())) {
            return assignments.get(assignment.getVarId()).getAssignmentValue() ==
                    assignment.getAssignmentValue();
        }

        assignments.put(assignment.getVarId(), assignment);
        // We will add the variable to decisionLevelToVariable to 'cache' it at the particular decision level key.
        addVarIdToDecisionLevelMap(assignment.getVarId(), assignment.getDecisionLevel());

        lastAssignedIds.push(assignment.getVarId());
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
            boolean existingAssignment = assignments.get(varId).getAssignmentValue();
            if (!existingAssignment) {
                return false;
            }
            assignments.get(varId).swapAssignment();
            assignments.get(varId).setDecisionLevel(decisionLevel);
        } else {
            Assignment assignment = new Assignment(varId, true, decisionLevel, null);
            assignments.put(varId, assignment);

        }
        lastAssignedIds.push(varId);
        return true;
    }

    private void updateAssignmentAndDecision(int varId, int decisionLevel) {
        Assignment assignment = new Assignment(varId, true, decisionLevel, null);
        assignments.put(varId, assignment);
        addVarIdToDecisionLevelMap(varId, decisionLevel);
    }

    private void addVarIdToDecisionLevelMap(int varId, int decisionLevel) {
        if (!decisionLevelToVariables.containsKey(decisionLevel)) {
            List<Integer> varIdsOfDecisionLevel = new ArrayList<>();
            varIdsOfDecisionLevel.add(varId);
            decisionLevelToVariables.put(decisionLevel, varIdsOfDecisionLevel);
        } else {
            List<Integer> varIdsOfDecisionLevel = decisionLevelToVariables.get(decisionLevel);
            varIdsOfDecisionLevel.add(varId);
            decisionLevelToVariables.put(decisionLevel, varIdsOfDecisionLevel);
        }
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
        List<Integer> impliedBy = new ArrayList<>();
        for (Literal literal : clause.getLiterals()) {
            if (getUnassignedVarIds().contains(literal.getVariable().getId())) {
                if (unitLiteral == null) {
                    unitLiteral = literal;
                } else {
                    // This is not a unit clause
                    return false;
                }
            } else {
                // Variable has already been assigned. We will have to get all the variables that implied
                // its assignment so that if this clause is a unit clause, we can have a list of all the variables that
                // implied the literal's (the only non-assigned variable in the clause) assignment.
                Assignment unit = assignments.get(literal.getVariable().getId());
                // The variable is a root variable itself. Add its own variable Id into the list.
                if (unit.getImpliedByRootNodeList() == null) {
                    impliedBy.add(literal.getVariable().getId());
                } else {
                    // Retrieve the entire list of root variables that implied this variable's assignment.
                    List<Integer> rootNodes = assignments.get(literal.getVariable().getId()).getImpliedByRootNodeList();
                    impliedBy.addAll(rootNodes);
                }
            }
        }

        if (unitLiteral == null) {
            // Did not find any unassigned variable
            return false;
        }

        // Update the variables that implied the assignment of this variable
        Assignment assignment = new Assignment(unitLiteral.getVariable().getId(),
                                            !unitLiteral.isNegated(), decisionLevel, impliedBy);

        // Assign the literal so its value is true
        return addAssignment(assignment);
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

    public boolean getAssignmentValue(int id) {
        return assignments.get(id).getAssignmentValue();
    }

    public Assignment getAssignment(int id) {
        return assignments.get(id);
    }

    public int getLastAssignment() {
        return lastAssignedIds.peek();
    }

    public int getHighestDecisionLevel() {
        if (this.decisionLevelToVariables.isEmpty()) {
            return 0;
        }
        return Collections.max(this.decisionLevelToVariables.keySet());
    }

    public List<Integer> getVariablesInDecisionLevel(Integer decisionLevel) {
        return this.decisionLevelToVariables.get(decisionLevel);
    }

    public void revertAssignments (List<Literal> literals) {
        int smallestDecisionLevel = Integer.MAX_VALUE;
        for (Literal literal : literals) {
            int assignmentDecisionLevel =
                    getAssignment(literal.getVariable().getId()).getDecisionLevel();
            if (assignmentDecisionLevel < smallestDecisionLevel) {
                smallestDecisionLevel = assignmentDecisionLevel;
            }
        }
        removeAssignmentsAboveDecisionLevel(smallestDecisionLevel);
    }

    /**
     * Removes all assignments above a particular decision level.
     * @param decisionLevel
     */
    public void removeAssignmentsAboveDecisionLevel(int decisionLevel) {
        int maxDecisionLevel = getHighestDecisionLevel();
        System.out.println("Max decision level = " + maxDecisionLevel);
        for (int i = decisionLevel; i <= maxDecisionLevel; i++ ){
            System.out.println("Decision level = " + i);
            List<Integer> varIds = this.decisionLevelToVariables.get(i);
            for (int varId : varIds) {
                this.assignments.remove(varId);
            }
            this.decisionLevelToVariables.remove(decisionLevel);
        }
    }
}

package data_structures;

import java.util.*;

/**
 * TODO: make assignment and decision level the same variable so we don't have to maintain two equivalent maps
 */
public class Assignments {
    private Set<Variable> variables;
    private Map<Integer, Assignment> assignments;
    // This map is for quick access to each variable's decision level
    private Map<Integer, List<Integer>> decisionLevelToVariables;

    public Assignments(Set<Variable> variables) {
        this.variables = variables;
        this.assignments = new HashMap<>();
        this.decisionLevelToVariables = new HashMap<>();
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
            System.out.println("This variable has already been assigned! Id =  " + assignment.getVarId());
            return assignments.get(assignment.getVarId()).getAssignmentValue() ==
                    assignment.getAssignmentValue();
        }

        this.assignments.put(assignment.getVarId(), assignment);
        // We will add the variable to decisionLevelToVariable to 'cache' it at the particular decision level key.
        addVarIdToDecisionLevelMap(assignment.getVarId(), assignment.getDecisionLevel());
        System.out.println("Assignment: Added assignment of " + assignment.getVarId() + "@" +
                assignment.getDecisionLevel() + "=" + String.valueOf(assignment.getAssignmentValue()));
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
            addVarIdToDecisionLevelMap(varId, decisionLevel);
        }
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
        List<Integer> impliedByGraphRoots = new ArrayList<>();
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
                if (unit.getImplicationGraphRoots() == null) {
                    impliedByGraphRoots.add(literal.getVariable().getId());
                } else {
                    // Retrieve the entire list of root variables that implied this variable's assignment.
                    List<Integer> rootNodes = assignments.get(literal.getVariable().getId()).getImplicationGraphRoots();
                    impliedByGraphRoots.addAll(rootNodes);
                }
            }
        }

        if (unitLiteral == null) {
            // Did not find any unassigned variable
            return false;
        }

        // Update the variables that implied the assignment of this variable
        Assignment assignment = new Assignment(unitLiteral.getVariable().getId(),
                                            !unitLiteral.isNegated(), decisionLevel, impliedByGraphRoots);

        // Assign the literal so its value is true
        return addAssignment(assignment);
    }

    /**
     * @return All varIDs without an assignment
     */
    public Set<Integer> getUnassignedVarIds() {
        Set<Integer> result = new HashSet<>();
        for (Variable variable : variables) {
            if (!assignments.containsKey(variable.getId())) {
                result.add(variable.getId());
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
        return this.assignments.get(id);
    }

    public int getLastAssignment() {
        // There is an assumption here that for assignments that are implied, they are
        // the list in each decisionLevelToVariable map's decision level is updated sequentially.
        int highestDecisionLevel = getHighestDecisionLevel();
        List<Integer> variablesInHighestDecisionLevel = decisionLevelToVariables.get(highestDecisionLevel);

        //DEBUG :::::
        System.out.print("Variables in highest decision level " + highestDecisionLevel + " = " );
        for (int varID : variablesInHighestDecisionLevel) {
            System.out.print(varID + " ");
        }
        System.out.println();
        System.out.println("==================================");

        // Getting the last element in the list
        return variablesInHighestDecisionLevel.get(variablesInHighestDecisionLevel.size() - 1);
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

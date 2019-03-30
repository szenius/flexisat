package solvers;

import branching_heuristics.PickBranchingVariableHeuristic;
import branching_heuristics.RandomVariable;
import branching_heuristics.TwoLiteralClause;
import data_structures.*;
import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Clauses;

import java.util.*;

/**
 * TODO: add conflict analysis
 */
public class CDCLSolver implements Solver {

    public enum PickBranchingVariableHeuristics{
        TWO_LITERALS_CLAUSE, RANDOM
    }

    private PickBranchingVariableHeuristic variablePicker;

    public CDCLSolver(PickBranchingVariableHeuristics heuristic) {
        switch (heuristic) {
            case RANDOM:
                variablePicker = new RandomVariable();
                break;
            case TWO_LITERALS_CLAUSE:
                variablePicker = new TwoLiteralClause();
                break;
            // We'll default to the random heuristic for now.
            default:
                variablePicker = new RandomVariable();
        }

    }


    @Override
    public boolean solve(Clauses clauses, Set<Variable> variables, Assignments assignments, int decisionLevel) {
        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            return true;
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            return false;
        }

        // Perform unit resolution
        int unitResolutionDecisionLevel = decisionLevel == 0 ? decisionLevel : decisionLevel - 1;
        if (!performUnitResolution(clauses, variables, assignments, unitResolutionDecisionLevel)) {
            // Conflict resolution should have happened
            return false;
        }

        // Check if any more variables to assign
        if (assignments.getUnassignedVarIds().isEmpty()) {
            return true;
        }

        // Pick a new variable to assign
        int varId = variablePicker.pickBranchingVariable(clauses, assignments);
        System.out.println("Solver: Try assigning " + varId + " to TRUE");
        if(!assignments.addAssignment(new Assignment(varId, true, decisionLevel, null))) {
            return false;
        }
        if (solve(clauses, variables, assignments, decisionLevel + 1)) {
            return true;
        }
        // Decision level might have changed to a lower level
        decisionLevel = assignments.getHighestDecisionLevel();

        // Change assignment of picked variable
        System.out.println("Solver: Try assigning " + varId + " to FALSE");
        if(!assignments.changeAssignment(varId, decisionLevel)) {
            return false;
        }
        if (solve(clauses, variables, assignments, decisionLevel + 1)) {
            return true;
        }
        return false;
    }

    /**
     * Tries to perform unit resolution for whichever unit clause found.
     * After performing unit resolution, check if the assignment is satisfiable. If not, revert the assignment.
     *
     * @param clauses
     * @param assignments
     * @param decisionLevel
     * @return True if unit resolution was done and the resulting assignment is satisfiable,
     *          OR if unit resolution was not done at all. Otherwise return False.
     */
    private boolean performUnitResolution(Clauses clauses, Set<Variable> variables, Assignments assignments, int decisionLevel) {
        boolean performedUnitResolution = true;
        while (performedUnitResolution) {
            performedUnitResolution = false;
            // Try to perform unit resolution until a pass where no unit resolution was performed
            for (Clause clause : clauses.getClauses()) {
                if (assignments.assignUnitClause(clause, decisionLevel)) {
                    performedUnitResolution = true;
                    if (!clauses.resolve(assignments, decisionLevel)) {
                        conflictResolution(clauses, assignments, variables);
                        return false;
                    }
                }
                //TODO: Not sure if this is most efficient place to update the two clause status.
                clause.updateTwoClauseStatus(assignments);
            }
        }
        return true;
    }

    private void conflictResolution(Clauses clauses, Assignments assignments, Set<Variable> variables) {
        int lastAssignedId = assignments.getLastAssignment();
        List<Integer> variablesThatCausedUNSAT = getVariablesThatImpliedUNSATAssignment(clauses, assignments, lastAssignedId);
        List<Literal> literals = createNewClause(variablesThatCausedUNSAT, assignments, variables);
        clauses.addClause(new Clause(literals));
        assignments.revertAssignments(literals);
    }


    /**
     * Get the variable assignments that caused the UNSAT conflict.
     * As of now the affected variables are defined as the root variables.
     *
     * @param clauses
     * @param assignments
     * @param unSatVarId
     * @return
     */
    private List<Integer> getVariablesThatImpliedUNSATAssignment(Clauses clauses, Assignments assignments, Integer unSatVarId) {
        Assignment conflictVariableUnit = assignments.getAssignment(unSatVarId);
        List<Integer> affectedVariables = conflictVariableUnit.getImplicationGraphRoots();
        return affectedVariables;
    }


    private List<Literal> createNewClause(List<Integer> affectedVariables,
                                          Assignments assignments, Set<Variable> variables) {
        List<Literal> clause = new ArrayList<>();
        for (Integer affectedVarId : affectedVariables) {
            Assignment currentAssignment = assignments.getAssignment(affectedVarId);
            if (variables.contains(new Variable(affectedVarId))){
                clause.add(new Literal(new Variable(affectedVarId), !currentAssignment.getAssignmentValue()));
            }
        }
        return clause;
    }
}
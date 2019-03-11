package solvers;

import data_structures.*;
import data_structures.Assignment;
import data_structures.Clause;
import data_structures.Clauses;

/**
 * TODO: add conflict analysis
 */
public class CDCLSolver implements Solver {

    @Override
    public boolean solve(Clauses clauses, Assignment assignment, int decisionLevel) {
        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            return true;
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            return false;
        }

        // Perform unit resolution
        int unitResolutionDecisionLevel = decisionLevel == 0? decisionLevel : decisionLevel - 1;
        if (!performUnitResolution(clauses, assignment, unitResolutionDecisionLevel)) {
            return false;
        }

        // Check if any more variables to assign
        if (assignment.getUnassignedVarIds().isEmpty()) {
            return true;
        }

        // Pick a new variable to assign
        int varId = pickBranchingVariable(assignment);
        System.out.println("Solver: Try assigning " + varId + " to TRUE");
        if(!assignment.addAssignment(varId, true, decisionLevel)) {
            return false;
        }
        if (solve(clauses, assignment, decisionLevel + 1)) {
            return true;
        }

        // Change assignment of picked variable
        System.out.println("Solver: Try assigning " + varId + " to FALSE");
        if(!assignment.changeAssignment(varId, decisionLevel)) {
            return false;
        }
        if (solve(clauses, assignment, decisionLevel + 1)) {
            return true;
        }

        return false;
    }

    /**
     * Tries to perform unit resolution for whichever unit clause found.
     * After performing unit resolution, check if the assignment is satisfiable. If not, revert the assignment.
     *
     * @param clauses
     * @param assignment
     * @param decisionLevel
     * @return True if unit resolution was done and the resulting assignment is satisfiable,
     *          OR if unit resolution was not done at all. Otherwise return False.
     */
    private boolean performUnitResolution(Clauses clauses, Assignment assignment, int decisionLevel) {
        boolean performedUnitResolution = true;
        while (performedUnitResolution) {
            performedUnitResolution = false;
            // Try to perform unit resolution until a pass where no unit resolution was performed
            for (Clause clause : clauses.getClauses()) {
                if (assignment.assignUnitClause(clause, decisionLevel)) {
                    performedUnitResolution = true;
                    if (!clauses.resolve(assignment, decisionLevel)) {
                        assignment.revertLastAssignment();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int pickBranchingVariable(Assignment assignment) {
        return (int) assignment.getUnassignedVarIds().toArray()[0];
    }
}
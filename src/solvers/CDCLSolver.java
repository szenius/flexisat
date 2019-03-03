package solvers;

import data_structures.*;

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

        // Perform unit resolution + infer new assignments
        if (!performUnitResolution(clauses, assignment, decisionLevel)) {
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

    private boolean performUnitResolution(Clauses clauses, Assignment assignment, int decisionLevel) {
        for (Clause clause : clauses.getClauses()) {
            if (assignment.assignUnitClause(clause, decisionLevel)) {
                boolean success = clauses.resolve(assignment, decisionLevel);
                if (!success) {
                    assignment.revertLastAssignment();
                    return false;
                }
            }
        }
        return true;
    }

    private int pickBranchingVariable(Assignment assignment) {
        return (int) assignment.getUnassignedVarIds().toArray()[0];
    }
}
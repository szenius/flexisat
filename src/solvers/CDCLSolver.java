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
        // note(lowjiansheng): Don't think we'll need this here anymore. 
        clauses.resolve(assignment, decisionLevel);

        // Check if any more variables to assign
        if (assignment.getUnassignedVarIds().isEmpty()) {
            return true;
        }

        // Pick a new variable to assign
        int varId = pickBranchingVariable(assignment);
        if(!assignment.addAssignment(varId, true, decisionLevel)) {
            return false;
        }
        if (solve(clauses, assignment, decisionLevel + 1)) {
            return true;
        }

        // Change assignment of picked variable
        if(!assignment.changeAssignment(varId, decisionLevel)) {
            return false;
        }
        if (solve(clauses, assignment, decisionLevel + 1)) {
            return true;
        }

        return false;
    }

    private boolean performUnitResolution(Clauses clauses, Assignment assignment, int decisionLevel) {
        return clauses.resolve(assignment, decisionLevel);
    }

    private int pickBranchingVariable(Assignment assignment) {
        return (int) assignment.getUnassignedVarIds().toArray()[0];
    }
}
package solvers;

import data_structures.*;

import java.util.List;

public class DPLLSolver implements Solver {

    @Override
    public boolean solve(Clauses clauses, Assignment assignment, int decisionLevel) {
        if (clauses.getClauses().isEmpty()) {
            // Empty formula
            return true;
        }

        if (clauses.hasEmptyClause()) {
            // Empty clause
            return false;
        }

        if (!performUnitResolution(clauses, assignment, decisionLevel)) {
            // Perform unit resolution
            return false;
        }

        while(!assignment.getUnassignedVarIds().isEmpty()) {
            if (!pickBranchingVariable(assignment, decisionLevel + 1)) {
                return false;
            }
        }

        return true;
    }

    private boolean performUnitResolution(Clauses clauses, Assignment assignment, int decisionLevel) {
        for (Clause clause : clauses.getUnitClauses()) {
            int unitLiteralVarId = clause.getLiterals().get(0).getVariable().getId();
            boolean success = assignment.addAssignment(unitLiteralVarId, true, decisionLevel);
            if (!success) {
                return false;
            }
            clauses.resolve(unitLiteralVarId, true, decisionLevel);
        }

        return true;
    }

    private boolean pickBranchingVariable(Assignment assignment, int decisionLevel) {
        int varId = (int) assignment.getUnassignedVarIds().toArray()[0];
        return false; // todo: stub
    }

    private int conflictAnalysis(Clauses form, List<Variable> vars) {
        return -1; // todo: stub
    }

    private void backtrack(Clauses clauses, List<Variable> vars, int assertionLevel) {
        // todo:
    }
}
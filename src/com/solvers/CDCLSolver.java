public class CDCLSolver implements Solver {

    @Override
    public boolean solve(Formula form, List<Variable> vars) {
        if (unitPropagation(form, vars).equals(AssignmentStatus.CONFLICT)) {
            return false;
        }

        int decisionLevel = 0;

        while(!allVarAssigned(form, vars)) {
            pickBranchingVariable(form, vars, decisionLevel);
            decisionLevel++;
            if (unitPropagation(form, vars).equals(AssignmentStatus.CONFLICT)) {
                int assertionLevel = conflictAnalysis(form, vars);
                if (assertionLevel < 0) {
                    return false;
                } else {
                    backtrack(formula, vars, assertionLevel);
                    decisionLevel = assertionLevel;
                }
            }
        }

        return true;
    }

    private AssignmentStatus unitPropagation(Formula form, List<Variable> vars) {
        return AssignmentStatus.CONFLICT; // todo: stub
    }

    private boolean allVarAssigned(Formula form, List<Variable> vars) {
        return true; // todo: stub
    }

    private void pickBranchingVariable(Formula formula, List<Variable> vars, int decisionLevel) {
        // todo: Pick Branching Variable
        // todo: Set Decision Level of picked variable to decisionLevel
    }

    private void backtrack(Formula formula, List<Variable> vars, int assertionLevel) {
        // todo:
    }

    private enum AssignmentStatus {
        OKAY,
        CONFLICT;
    }
}
package solvers;

import data_structures.*;

import java.util.*;

public class CDCLSolver2 {
    Clauses clauses;
    Set<Variable> variables;
    Assignments assignments;

    public CDCLSolver2(Clauses clauses, Set<Variable> variables, Assignments assignments) {
        this.clauses = clauses;
        this.variables = variables;
        this.assignments = assignments;
    }

    public boolean solve() {
        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            return true;
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            return false;
        }

        // Perform unit resolution
        int decisionLevel = 0;
        if (!unitPropagation(decisionLevel)) {
            return false;
        }

        while (assignments.getImplicationGraphNodes().size() < variables.size()) {
            Variable pickedVariable = pickBranchingVariable();
            decisionLevel++;
            Node newNode = new Node(pickedVariable, decisionLevel);
            assignments.getImplicationGraphNodes().put(pickedVariable, newNode);
            assignments.getImplicationGraphRoots().put(pickedVariable, newNode);
            if (!unitPropagation(decisionLevel)) {
                int assertionLevel = conflictAnalysis();
                if (assertionLevel < 0) {
                    return false;
                } else {
                    backtrack(assertionLevel);
                    decisionLevel = assertionLevel;
                }
            }
        }
        return true;
    }

    private void backtrack(int assertionLevel) {
        // TODO:
    }

    private int conflictAnalysis() {
        return 0; // TODO:
    }

    private boolean unitPropagation(int decisionLevel) {
        boolean performedUnitResolution = true;

        while (performedUnitResolution) {
            performedUnitResolution = false;

            // Perform unit resolution while possible
            for (Clause clause : clauses.getClauses()) {
                Literal unitLiteral = clause.getUnitLiteral(assignments);
                if (unitLiteral != null) {
                    boolean hasConflict = buildImplicationGraph(clause, unitLiteral);
                    while (hasConflict) {
                        hasConflict = buildImplicationGraph(clause, unitLiteral);
                    }
                    performedUnitResolution = true;
                }
            }
        }
        return true;
    }

    private boolean buildImplicationGraph(Clause clause, Literal unitLiteral) {
        Node existingNode = assignments.getImplicationGraphNodes().getOrDefault(unitLiteral.getVariable(), null);
        if (existingNode != null) {
            // TODO: conflict
        }
        // TODO:

    }

    /**
     * Returns first found variable that has not been assigned
     *
     * @return Variable that has not been assigned
     */
    private Variable pickBranchingVariable() {
        for (Variable variable : variables) {
            if (assignments.getImplicationGraphNodes().containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }
}

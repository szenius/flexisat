package solvers;

import data_structures.*;
import data_structures.Assignment;
import data_structures.Clause;
import data_structures.Clauses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO: add conflict analysis
 */
public class CDCLSolver implements Solver {

    @Override
    public boolean solve(Clauses clauses, Set<Variable> variables, Assignment assignment, int decisionLevel) {
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
        if (!performUnitResolution(clauses, variables, assignment, unitResolutionDecisionLevel)) {
            // DO CONFLICT RESOLUTION HERE

            return false;
        }

        // Check if any more variables to assign
        if (assignment.getUnassignedVarIds().isEmpty()) {
            return true;
        }

        // Pick a new variable to assign
        int varId = pickBranchingVariable(assignment);
        System.out.println("Solver: Try assigning " + varId + " to TRUE");
        if(!assignment.addAssignment(new AssignmentUnit(varId, true, decisionLevel),null)) {
            return false;
        }
        if (solve(clauses, variables, assignment, decisionLevel + 1)) {
            return true;
        }

        // Change assignment of picked variable
        System.out.println("Solver: Try assigning " + varId + " to FALSE");
        if(!assignment.changeAssignment(varId, decisionLevel)) {
            return false;
        }
        if (solve(clauses, variables, assignment, decisionLevel + 1)) {
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
    private boolean performUnitResolution(Clauses clauses, Set<Variable> variables, Assignment assignment, int decisionLevel) {
        boolean performedUnitResolution = true;
        while (performedUnitResolution) {
            performedUnitResolution = false;
            // Try to perform unit resolution until a pass where no unit resolution was performed
            for (Clause clause : clauses.getClauses()) {
                if (assignment.assignUnitClause(clause, decisionLevel)) {
                    performedUnitResolution = true;
                    if (!clauses.resolve(assignment, decisionLevel)) {
                        conflictResolution(clauses, assignment, variables);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // How to track that a variable picked is the one that contributed to the UNSAT.
    private void conflictResolution(Clauses clauses, Assignment assignment, Set<Variable> variables) {
        int lastAssignedId = assignment.getLastAssignment();
        List<Integer> affectedVariables = getAffectedVariables(clauses, assignment, lastAssignedId);
        List<Literal> listOfLiterals = formNewClauseWithAffectedVariables(affectedVariables, assignment, variables);
        Clause clause = new Clause(listOfLiterals);
        clauses.addClause(clause);
        assignment.revertLastAssignment();
    }


    private int pickBranchingVariable(Assignment assignment) {
        return (int) assignment.getUnassignedVarIds().toArray()[0];
    }


    /**
     * Get the affected variable assignments that caused the UNSAT conflict.
     *
     * @param clauses
     * @param assignment
     * @param unSatVarId
     * @return
     */
    private List<Integer> getAffectedVariables(Clauses clauses, Assignment assignment, Integer unSatVarId) {
        // Case 1: All assigned but caused UNSAT
        AssignmentUnit conflictVariableUnit = assignment.getAssignmentUnit(unSatVarId);
        List<Integer> affectedVariables = new ArrayList<>();
        getRootVariables(affectedVariables, conflictVariableUnit);
        return affectedVariables;
    }


    private void getRootVariables(List<Integer> rootVariables, AssignmentUnit assignmentUnit) {
        if (assignmentUnit.getImpliedByList() == null) {
            rootVariables.add(assignmentUnit.getVarId());
            return;
        }
        for (AssignmentUnit unit : assignmentUnit.getImpliedByList()) {
            getRootVariables(rootVariables, unit);
        }
        return;
    }

    private List<Literal> formNewClauseWithAffectedVariables(List<Integer> affectedVariables,
                                                      Assignment assignment, Set<Variable> variables) {
        List<Literal> clause = new ArrayList<>();
        for (Integer affectedVarId : affectedVariables) {
            AssignmentUnit currentAssignment = assignment.getAssignmentUnit(affectedVarId);
            if (variables.contains(new Variable(affectedVarId))){
                clause.add(new Literal(new Variable(affectedVarId), !currentAssignment.getAssignment()));
            }
        }
        return clause;
    }
}
package solvers;

import data_structures.*;
import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Clauses;
import performance.PerformanceTester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CDCLSolver implements Solver {
    Clauses clauses;
    Set<Variable> variables;
    Assignments assignments;


    public CDCLSolver(Clauses clauses, Set<Variable> variables) {
        this.clauses = clauses;
        this.variables = variables;
        this.assignments = new Assignments(variables);
    }

    @Override
    public boolean solve(PerformanceTester perfTester) {
        System.out.println("Running CDCLSolver...");
        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            System.out.println("Found empty formula!");
            return true;
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            System.out.println("Found empty clause!");
            return false;
        }

        int decisionLevel = 0;

        if (performUnitResolution(clauses, variables, assignments, decisionLevel) == unitResolutionResults.FALSE) {
            System.out.println("Conflict from Unit Propagation at decision level " + decisionLevel + "!");
            return false;
        }

        while (!isAllVariablesAssigned(assignments)) {
            // Pick branching variable to assign
            int pickedVariableId = pickBranchingVariable(assignments, perfTester);
            Assignment newNode = new Assignment(pickedVariableId, true, decisionLevel, null);
            if (!assignments.addAssignment(newNode)) {
                System.out.println("UNSAT!!! NOT POSSIBLE.");
                return false;
            }

            boolean success = false;
            while (!success) {
                // Run unit propagation
                unitResolutionResults results = performUnitResolution(clauses, variables, assignments, decisionLevel);
                switch (results) {
                    case UNSAT:
                        return false;
                    case TRUE:
                        success = true;
                        break;
                    case FALSE:
                        success = false;
                        break;
                    default:
                        System.out.println("Not possible.");
                        System.exit(1);
                }

                // Get the new decision level if there was a conflict.
                if (!success) {
                    decisionLevel = assignments.getHighestDecisionLevel();
                    // TODO: DEBUGGING
                   // printStateCheck();
                    // DEBUGGING
                }
            }
            // TODO: DEBUGGING
            // printStateCheck();
            // DEBUGGING

            if (decisionLevel == 0 && assignments.getVariablesInDecisionLevel(decisionLevel) == null ) {
                decisionLevel = 0;
            } else {
                decisionLevel++;
            }

            // This is not possible. Our decision level should never be higher than the total number of variables.
            if (decisionLevel > variables.size()) {
                // TODO: DEBUGGING
                System.out.println("************Error in implementation.*********** It is not possible for the decision level to be higher " +
                        "than the number of variables.");
                printStateCheck();
                System.exit(1);
                // DEBUGGING
            }
        }
        // Final check to prevent implementation errors.
        if (clauses.checkVALID(assignments)) {
            return true;
        } else {
            return false;
        }
    };


    @Override
    public boolean solveWithTimer(PerformanceTester perfTester) {
        perfTester.startTimer();
        boolean isSat = this.solve(perfTester);
        perfTester.stopTimer();

        perfTester.printExecutionTime();
        perfTester.printNumPickBranchingVariablesCalled();
        return isSat;
    }


    /* TODO: Debug print function.
     * This prints the entire state of the decisionLevelToVariableId map.
     */
    private void printStateCheck() {
        System.out.println("====== STATE CHECK=====");
        for (int i = 0 ; i <= assignments.getHighestDecisionLevel(); i++) {
            System.out.println("Decision Level = " + i);
            List<Integer> varsInDecisionLevel = assignments.getVariablesInDecisionLevel(i);
            if (varsInDecisionLevel == null){
                System.out.println("NULL WEIRD.");
                //System.exit(1);
            } else {
                for (Integer var : varsInDecisionLevel) {
                    System.out.print(var + " ");
                    if (assignments.getAssignment(var) == null) {
                        System.out.println("ERROR. " + var + "'s assignment does not exist.");
                        System.exit(1);
                    }
                }
                System.out.println();
            }
        }
        System.out.println("~~~~~~~ STATE CHECK END ~~~~~~~~");
    }

    enum unitResolutionResults {
        UNSAT, TRUE, FALSE
    }

    /**
     * Tries to perform unit resolution for whichever unit clause found.
     * After performing unit resolution, check if the assignment is satisfiable.
     * If its not a valid assignment, we will call conflict analysis to resolve the conflict.
     * @param clauses
     * @param assignments
     * @param decisionLevel
     * @return - unitResolutionResults.TRUE if unit resolution was done and the resulting assignment is satisfiable,
     *          OR if unit resolution was not done at all.
     *         - unitResolutionResults.FALSE if unit resolution was done and there was a conflicting assignment.
     *         - unitResolutionResults.UNSAT if unit resolution was done on a clause with only 1 literal and is
     *         a conflicting assignment.
     */
    private unitResolutionResults performUnitResolution(Clauses clauses, Set<Variable> variables, Assignments assignments, int decisionLevel) {
        boolean performedUnitResolution = true;
        while (performedUnitResolution) {
            performedUnitResolution = false;
            // Try to perform unit resolution until a pass where no unit resolution was performed
            for (Clause clause : clauses.getClauses()) {
                if (assignments.assignUnitClause(clause, decisionLevel)) {
                    performedUnitResolution = true;
                    if (!clauses.checkVALID(assignments)) {
                        // UNSAT. If clause only consists of 1 literal and still invalid.
                        if (clause.getLiterals().size() == 1) {
                            return unitResolutionResults.UNSAT;
                        }
                        conflictAnalysis(clauses, assignments, variables);
                        return unitResolutionResults.FALSE;
                    }
                }
            }
        }
        return unitResolutionResults.TRUE;
    }

    private boolean isAllVariablesAssigned(Assignments assignments) {
        return (assignments.getUnassignedVarIds().size() == 0);
    }

    /**
     * Conflict Analysis will use the root heuristics to get the root variables.
     * It will add the root variables clause that implied the assignments that led to the UNSAT assignment.
     * @param clauses
     * @param assignments
     * @param variables
     */
    private int conflictAnalysis(Clauses clauses, Assignments assignments, Set<Variable> variables) {
        int lastAssignedId = assignments.getLastAssignment();
        Set<Integer> variablesThatCausedUNSAT = getVariablesThatImpliedUNSATAssignment(clauses, assignments, lastAssignedId);
        List<Literal> literals = createNewConflictClause(variablesThatCausedUNSAT, assignments, variables);
        clauses.addClause(new Clause(literals));
        assignments.revertAssignments(literals);
        return assignments.getHighestDecisionLevel();
    }


    private int pickBranchingVariable(Assignments assignments, PerformanceTester performanceTester) {
        performanceTester.pickBranchingVariablesCalled();
        return (int) assignments.getUnassignedVarIds().toArray()[0];
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
    private Set<Integer> getVariablesThatImpliedUNSATAssignment(Clauses clauses, Assignments assignments, Integer unSatVarId) {
        Assignment conflictVariableUnit = assignments.getAssignment(unSatVarId);
        List<Integer> affectedVariables = conflictVariableUnit.getImplicationGraphRoots();

        // TODO: Debug prints for root variables
        /*
        System.out.println("::::::CONFLICT ANALYSIS:::::: root variables that implied UNSAT");
        for (int varId: affectedVariables) {
            System.out.print(varId + " ");
        }
        System.out.println();
        // DEBUG::::*/

        Set<Integer> affectedVariablesDedupped = new HashSet<>();
        for (Integer varIds : affectedVariables) {
            affectedVariablesDedupped.add(varIds);
        }

        return affectedVariablesDedupped;
    }


    /**
     * This method creates a new clause with the values of each literal being
     * the input variables and negating its current assignment.
     * @param affectedVariables
     * @param assignments
     * @param variables
     * @return
     */
    private List<Literal> createNewConflictClause(Set<Integer> affectedVariables,
                                                  Assignments assignments, Set<Variable> variables) {
        List<Literal> clause = new ArrayList<>();
        for (Integer affectedVarId : affectedVariables) {
            Assignment currentAssignment = assignments.getAssignment(affectedVarId);
            if (variables.contains(new Variable(affectedVarId))){
                Literal literalToAdd = new Literal(new Variable(affectedVarId), currentAssignment.getAssignmentValue());
                clause.add(literalToAdd);
            }
        }
        return clause;
    }
}
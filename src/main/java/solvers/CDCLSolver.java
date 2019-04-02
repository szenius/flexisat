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

/**
 * TODO: add conflict analysis
 */
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

        // Perform unit resolution
        int decisionLevel = 0;

        if (!performUnitResolution(clauses, variables, assignments, decisionLevel)) {
            System.out.println("Conflict from Unit Propagation at decision level " + decisionLevel + "!");
            return false;
        }

        while (!isAllVariablesAssigned(assignments)) {
            // Pick branching variable to assign
            int pickedVariableId = pickBranchingVariable(assignments, perfTester);
            Assignment newNode = new Assignment(pickedVariableId, true, decisionLevel, null);

            if (!assignments.addAssignment(newNode)) {
                System.out.println("NOT POSSIBLE.");
                return false;
            }
            System.out.println("PICK VARIABLE: Assigned variable " + pickedVariableId + " at " + newNode.getDecisionLevel() + "!");

            boolean success = false;
            while (!success) {
                // Run unit propagation
                success = performUnitResolution(clauses, variables, assignments, decisionLevel);
                // Conflict analysis already ran. Now need to get the new decision level.
                if (!success) {
                    decisionLevel = assignments.getHighestDecisionLevel();
                    //TODO: definitely wrong here.
                    if (decisionLevel < 0 ) {
                        return false;
                    }

                    // DEBUGGING
                    printStateCheck();
                    // DEBUGGING
                }
            }
            // DEBUGGING
            printStateCheck();
            // DEBUGGING

            decisionLevel++;

            if (decisionLevel > variables.size()) {
                // DEBUGGING
                System.out.println("************Error in implementation.*********** It is not possible for the decision level to be higher " +
                        "than the number of variables.");
                printStateCheck();
                System.exit(1);
                // DEBUGGING
            }
        }
        return true;
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

    /*
     * DEBUG. PRINTS ENTIRE STATE.
     */
    private void printStateCheck() {
        System.out.println("====== STATE CHECK=====");
        for (int i = 0 ; i <= assignments.getHighestDecisionLevel(); i++) {
            System.out.println("Decision Level = " + i);
            List<Integer> varsInDecisionLevel = assignments.getVariablesInDecisionLevel(i);
            if (varsInDecisionLevel == null){
                System.out.println("NULL WEIRD.");
                System.exit(1);
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
                    if (!clauses.checkVALID(assignments)) {
                        System.out.println("==== Running conflict analysis at decision level = " + decisionLevel + "====");
                        conflictAnalysis(clauses, assignments, variables);
                        return false;
                    }
                }
            }
        }
        return true;
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

        System.out.println("Before revert: Highest decision level = " + assignments.getHighestDecisionLevel());
        Set<Integer> variablesThatCausedUNSAT = getVariablesThatImpliedUNSATAssignment(clauses, assignments, lastAssignedId);
        List<Literal> literals = createNewClause(variablesThatCausedUNSAT, assignments, variables);

        // DEBUG
        System.out.println("::::::CONFLICT ANALYSIS new clause added");
        for (Literal literal: literals) {
            System.out.print(literal.toString());
        }
        System.out.println();
        // DEBUG

        clauses.addClause(new Clause(literals));
        assignments.revertAssignments(literals);
        System.out.println("After revert: Highest decision level = " + assignments.getHighestDecisionLevel());
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
        if (conflictVariableUnit == null ){
            System.out.println("NULL conflict variable unit = " + unSatVarId);
        }
        List<Integer> affectedVariables = conflictVariableUnit.getImplicationGraphRoots();

        // DEBUG::::
        System.out.println("::::::CONFLICT ANALYSIS:::::: root variables that implied UNSAT");
        for (int varId: affectedVariables) {
            System.out.print(varId + " ");
        }
        System.out.println();
        // DEBUG::::

        Set<Integer> affectedVariablesDedupped = new HashSet<>();
        for (Integer varIds : affectedVariables) {
            affectedVariablesDedupped.add(varIds);
        }

        return affectedVariablesDedupped;
    }


    private List<Literal> createNewClause(Set<Integer> affectedVariables,
                                          Assignments assignments, Set<Variable> variables) {
        List<Literal> clause = new ArrayList<>();

        System.out.println(":::::::CONFLICT ANALYSIS CREATE NEW CLAUSE:::::::");

        for (Integer affectedVarId : affectedVariables) {
            Assignment currentAssignment = assignments.getAssignment(affectedVarId);
            if (variables.contains(new Variable(affectedVarId))){
                Literal literalToAdd = new Literal(new Variable(affectedVarId), currentAssignment.getAssignmentValue());
                System.out.println("Adding literal = " + literalToAdd);
                clause.add(literalToAdd);
            }
        }
        System.out.println(":::::::CONFLICT ANALYSIS CREATE NEW CLAUSE:::::::");

        return clause;
    }
}
package solvers;

import branch_pickers.BranchPicker;
import conflict_analysers.ConflictAnalyser;
import data_structures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.Parser;

import java.util.*;

public class CDCLSolver implements Solver {
    private static final Logger LOGGER = LoggerFactory.getLogger(CDCLSolver.class);

    private Clauses clauses;
    private Set<Variable> variables;
    private Assignments assignments;

    private BranchPicker branchPicker;
    private ConflictAnalyser conflictAnalyser;
    private int pickBranchingCount;

    public CDCLSolver(Parser parser) {
        this.clauses = parser.getClauses();
        this.variables = parser.getVariables();
        this.branchPicker = parser.getBranchPicker();
        this.conflictAnalyser = parser.getConflictAnalyser();
        this.assignments = new Assignments();
        this.pickBranchingCount = 0;
    }

    /**
     * Check if CNF formula is SAT or UNSAT by CDCL.
     * Generally: Try to do unit propagation wherever possible.
     *            If conflict in inferred assignment during unit propagation, do analysis and learn a new clause.
     *            If it's not possible to do unit propagation, pick a branching variable to assign FALSE.
     *            If the decision level becomes negative, return UNSAT.
     *            Else if all variables are assigned without conflict, return SAT.
     *
     * @return SolverResult object
     */
    @Override
    public SolverResult solve() {
        LOGGER.debug("Running CDCLSolver...");

        long startTime = System.currentTimeMillis();

        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            LOGGER.debug("Found empty formula!");
            return new SolverResult(true, getPickBranchingCount(), computeTimeTaken(startTime));
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            LOGGER.debug("Found empty clause!");
            return new SolverResult(false, getPickBranchingCount(), computeTimeTaken(startTime));
        }

        // Perform unit resolution
        int decisionLevel = 0;
        if (unitPropagation(decisionLevel).isConflict()) {
            return new SolverResult(false, getPickBranchingCount(), computeTimeTaken(startTime));
        }

        while (assignments.getNumAssigned() < variables.size()) {
            // Pick branching variable to assign
            Variable pickedVariable = pickBranchingVariable();
            decisionLevel++;
            Node newNode = new Node(pickedVariable, decisionLevel);
            newNode.addAncestor(newNode);
            assignments.addAssignment(pickedVariable, newNode, false, true);
            LOGGER.debug("ASSIGNED {}={}@{}", pickedVariable.getId(), assignments.getVariableAssignment(pickedVariable),decisionLevel);

            // Run unit propagation
            UnitResolutionResult unitResolutionResult = unitPropagation(decisionLevel);
            while (unitResolutionResult.isConflict()) {
                int assertionLevel = conflictAnalysis(unitResolutionResult);
                LOGGER.debug("ASSERTING at level {}", assertionLevel);
                if (assertionLevel < 0) {
                    return new SolverResult(false, getPickBranchingCount(), computeTimeTaken(startTime));
                } else {
                    backtrack(assertionLevel);
                    LOGGER.debug("BACKTRACKED to decision level {}", assertionLevel);
                    decisionLevel = assertionLevel;
                }
                unitResolutionResult = unitPropagation(decisionLevel);
            }
        }
        return new SolverResult(true, getPickBranchingCount(), computeTimeTaken(startTime));
    }

    /**
     * Remove all nodes which have decision levels higher than the input assertionLevel.
     *
     * @param assertionLevel
     */
    private void backtrack(int assertionLevel) {
        if (assertionLevel == 0) {
            // Remove all assignments
            assignments.clear();
        } else {
            // Remove all assignments made beyond assertion level
            assignments.removeAssignmentsBeyondLevel(assertionLevel);
        }
    }

    /**
     * Analyse conflict in implication graph in order to learn a new clause
     *
     * @param conflict
     * @return Assertion level
     */
    private int conflictAnalysis(UnitResolutionResult conflict) {
        ConflictAnalyserResult result = conflictAnalyser.learnClause(conflict, assignments);

        Clause learntClause = result.getLearntClause();
        clauses.addClause(learntClause);
        branchPicker.updateWeights(result);
        branchPicker.decayWeights();

        return result.getAssertionLevel();
    }

    /**
     * Perform unit resolution while possible.
     * Unit resolution is performed by running through each clause and checking if there is any unit literal which has
     *      to be assigned to true for that clause to be true.
     * Once an unit literal is found and assigned, we run through all clauses again to check if there is a clause
     *      that disagrees with the assignment.
     * If any such disagreeing clause is found, return conflict.
     *
     * @param decisionLevel The decision level at which the unit propagation was triggered.
     * @return
     */
    private UnitResolutionResult unitPropagation(int decisionLevel) {
        boolean performedUnitResolution = true;

        // If for any round of going through all clauses we did unit resolution, we go another round to check
        // for possibility for unit resolution again.
        while (performedUnitResolution) {
            performedUnitResolution = false;

            for (Clause clause : clauses.getClauses()) {
                Literal unitLiteral = clause.findUnitLiteral(assignments);

                if (unitLiteral != null) {
                    // Found unit literal, do unit resolution
                    Variable unitLiteralVariable = unitLiteral.getVariable();
                    boolean inferredNodeAssignment = !unitLiteral.isNegated();
                    LOGGER.debug("INFERRED {}={}@{} by clause {}", unitLiteralVariable.getId(), inferredNodeAssignment, decisionLevel, clause.toString());

                    // Add to implication graph + assign variable
                    Node inferredNode = addToImplicationGraph(clause, unitLiteralVariable, decisionLevel);
                    assignments.addAssignment(unitLiteralVariable, inferredNode, inferredNodeAssignment, assignments.isEmpty());

                    // Try to find conflicting assignment
                    Clause disagreeingClause = propagateAssignment(inferredNode, clause);
                    if (disagreeingClause != null) {
                        // Found conflicting assignment
                        LOGGER.debug("CONFLICT: {}@{} due to clauses {} and {}", unitLiteralVariable.getId(), decisionLevel, clause.toString(), disagreeingClause.toString());
                        Node conflictingNode = addToImplicationGraph(disagreeingClause, inferredNode);
                        return new UnitResolutionResult(inferredNode, conflictingNode, true, decisionLevel);
                    }

                    performedUnitResolution = true;
                }
            }
        }

        return new UnitResolutionResult(null, false, decisionLevel);
    }

    /**
     * Propagate the assignment of a node that was inferred during unit resolution.
     *
     * @param inferredNode the node that was just inferred and assigned
     * @param originalClause the clause which led to the unit resolution of inferredNode
     * @return Any clause which disagrees with the assignment of the node. Null if no such clause is found.
     */
    private Clause propagateAssignment(Node inferredNode, Clause originalClause) {
        for (Clause clause : clauses.getClauses()) {
            if (clause.equals(originalClause)) {
                // Don't consider the clause which led to the inferred node's assignment
                continue;
            }
            Literal unitLiteral = clause.findTargetUnitLiteral(inferredNode.getVariable(), assignments);
            if (unitLiteral != null) {
                // Found another clause which helps us infer the same variable we just inferred
                boolean inferredNodeAssignment = !unitLiteral.isNegated();
                if (conflictsWithExistingAssignment(unitLiteral.getVariable(), inferredNodeAssignment)) {
                    // The two clauses produce conflicting assignments
                    return clause;
                }
            }
        }
        return null;
    }

    /**
     * Given a variable and an assignment, check if it conflicts with an existing assignment
     *
     * @param variable
     * @param assignment
     * @return true if there is a conflict with existing assignment, false otherwise
     */
    private boolean conflictsWithExistingAssignment(Variable variable, boolean assignment) {
        if(assignments.hasConflictingAssignment(variable, assignment)) {
            return true;
        }
        return false;
    }

    /**
     * Given a unit clause, we add edges from each assigned literal in this unit clause to its unit literal.
     *
     * @param dueToClause Unit clause containing the unitLiteralVariable
     * @param unitLiteralVariable Unit literal that was inferred by the dueToClause
     * @param decisionLevel decision level at which the unitLiteralVariable was inferred at
     * @return The last added node in the graph
     */
    private Node addToImplicationGraph(Clause dueToClause, Variable unitLiteralVariable, int decisionLevel) {
        Node lastInferredNode = new Node(unitLiteralVariable, decisionLevel);
        return addToImplicationGraph(dueToClause, lastInferredNode);
    }

    /**
     * Given a unit clause, we add edges from each assigned literal in this unit clause to its unit literal.
     *
     * @param dueToClause Unit clause containing the lastInferredNode
     * @param lastInferredNode The node last inferred by unit resolution
     * @return The last added node in the graph
     */
    private Node addToImplicationGraph(Clause dueToClause, Node lastInferredNode) {
        for (Literal literal : dueToClause.getLiterals()) {
            if (!literal.getVariable().equals(lastInferredNode.getVariable())) {
                Node fromNode = assignments.getNode(literal.getVariable());
                Edge newEdge = new Edge(fromNode, lastInferredNode, dueToClause);
                fromNode.addOutEdge(newEdge);
                lastInferredNode.addInEdge(newEdge);
                lastInferredNode.addAncestors(fromNode.getAncestors());
            }
        }
        return lastInferredNode;
    }

    /**
     * Returns a variable that has not been assigned
     *
     * @return Variable that has not been assigned
     */
    private Variable pickBranchingVariable() {
        incrementPickBranchingCount();
        return branchPicker.pick(assignments);
    }

    private void incrementPickBranchingCount() {
        pickBranchingCount++;
    }

    private int getPickBranchingCount() {
        return pickBranchingCount;
    }

    private long computeTimeTaken(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}

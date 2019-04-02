package solvers;

import data_structures.*;

import java.util.*;

public class CDCLSolver2 {
    Clauses clauses;
    Set<Variable> variables;
    Assignments2 assignments;

    public CDCLSolver2(Clauses clauses, Set<Variable> variables) {
        this.clauses = clauses;
        this.variables = variables;
        this.assignments = new Assignments2();
    }

    /**
     * CDCL Solver
     *
     * @return true if the formula is VALID, false otherwise.
     */
    public boolean solve() {
        System.out.println("Running CDCLSolver2...");
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
        if (unitPropagation(decisionLevel).isConflict()) {
            System.out.println("CONFLICT from Unit Propagation at decision level " + decisionLevel + "!");
            return false;
        }

        while (assignments.getNumAssigned() < variables.size()) {
            // Pick branching variable to assign
            Variable pickedVariable = pickBranchingVariable();
            decisionLevel++;
            Node newNode = new Node(pickedVariable, decisionLevel);
            assignments.addAssignment(pickedVariable, newNode, true, true);
            System.out.println("Assigned variable " + pickedVariable.getId() + " at " + decisionLevel + "!");

            // Run unit propagation
            UnitResolutionResult unitResolutionResult = unitPropagation(decisionLevel);
            if (unitResolutionResult.isConflict()) {
                System.out.println("CONFLICT from Unit Propagation at decision level " + decisionLevel + "!");
                int assertionLevel = conflictAnalysis(unitResolutionResult);
                System.out.println("Asserting at level " + assertionLevel);
                if (assertionLevel < 0) {
                    return false;
                } else {
                    backtrack(assertionLevel);
                    System.out.println("Backtracked to decision level " + assertionLevel);
                    decisionLevel = assertionLevel;
                }
            }
        }
        return true;
    }

    /**
     * Remove all nodes which have decision levels higher than the input assertionLevel.
     *
     * @param assertionLevel
     */
    private void backtrack(int assertionLevel) {
        for (Node node : assignments.getImplicationGraphNodes().values()) {
            if (node.getDecisionLevel() > assertionLevel) {
                for (Edge inEdge : node.getInEdges()) {
                    inEdge.getFromNode().removeOutEdge(inEdge);
                    assignments.removeAssignment(node.getVariable());
                }
            }
        }
    }

    /**
     * Based on the conflicting nodes, cut the implication graph to learn a new clause.
     *
     * @param conflict
     * @return
     */
    private int conflictAnalysis(UnitResolutionResult conflict) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();

        // Collect edges to cut for learning clause. We cut the edges that directly lead to the conflict.
        Set<Edge> cutEdges = new HashSet<>(conflictingNode.getInEdges());
        cutEdges.addAll(inferredNode.getInEdges());

        // Learn new clause from our cut
        List<Literal> learntLiterals = new ArrayList<>();
        int assertionLevel = -1;
        for (Edge cutEdge : cutEdges) {
            Literal learntLiteral = new Literal(cutEdge.getFromNode().getVariable(),
                    assignments.getVariableAssignment(cutEdge.getFromNode().getVariable()));
            learntLiterals.add(learntLiteral);
            assertionLevel = Math.max(cutEdge.getFromNode().getDecisionLevel(), assertionLevel);
        }
        Clause learntClause = new Clause(learntLiterals);
        clauses.addClause(learntClause);
        System.out.println("Learnt new clause " + learntClause.toString());

        // Remove assignment of conflicting nodes
        assignments.removeAssignment(conflictingNode.getVariable());

        return assertionLevel;
    }

    /**
     * Perform unit resolution while possible.
     * Unit resolution is performed by running through each clause and checking if there is any unit literal which has
     * to be assigned to true for that clause to be true.
     * If any such assignment is found but conflicts with the existing assignments, return a conflict.
     *
     * @param decisionLevel The decision level at which the unit propagation was triggered.
     * @return
     */
    private UnitResolutionResult unitPropagation(int decisionLevel) {
        boolean performedUnitResolution = true;
        Node lastInferredNode = null;

        // If for any round of going through all clauses we did unit resolution, we go another round to check
        // for possibility for unit resolution again.
        while (performedUnitResolution) {
            performedUnitResolution = false;

            for (Clause clause : clauses.getClauses()) {
                Literal unitLiteral = clause.getUnitLiteral(assignments);

                if (unitLiteral != null) {
                    // Found unit literal, do unit resolution
                    Variable unitLiteralVariable = unitLiteral.getVariable();
                    boolean inferredNodeAssignment = !unitLiteral.isNegated();

                    // Add to implication graph
                    lastInferredNode = addToImplicationGraph(clause, unitLiteralVariable, decisionLevel);

                    // Check if assignment is conflicting
                    if(isConflict(unitLiteralVariable, inferredNodeAssignment)) {
                        // Conflicting assignment
                        System.out.println("Found conflicting assignment for " + unitLiteralVariable.getId() + " in clause " + clause.toString());
                        Node conflictingNode = assignments.getNode(unitLiteralVariable);
                        return new UnitResolutionResult(lastInferredNode, conflictingNode, true);

                    } else {
                        // Not conflicting, add assignment
                        assignments.addAssignment(unitLiteralVariable, lastInferredNode, inferredNodeAssignment, false);
                    }

                    performedUnitResolution = true;
                }
            }
        }

        return new UnitResolutionResult(lastInferredNode, false);
    }

    private boolean isConflict(Variable unitLiteralVariable, boolean inferredNodeAssignment) {
        return assignments.hasConflictingAssignment(unitLiteralVariable, inferredNodeAssignment);
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
        for (Literal literal : dueToClause.getLiterals()) {
            if (literal.getVariable() != unitLiteralVariable) {
                Node fromNode = assignments.getNode(literal.getVariable());
                Edge newEdge = new Edge(fromNode, lastInferredNode, dueToClause);
                fromNode.addOutEdge(newEdge);
                lastInferredNode.addInEdge(newEdge);
            }
        }
        return lastInferredNode;
    }

    /**
     * Returns first found variable that has not been assigned
     *
     * @return Variable that has not been assigned
     */
    private Variable pickBranchingVariable() {
        for (Variable variable : variables) {
            if (!assignments.getImplicationGraphNodes().containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }
}
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
            assignments.addAssignment(pickedVariable, newNode, false, true);
            System.out.println("Assigned " + pickedVariable.getId() + "=" + assignments.getVariableAssignment(pickedVariable) + "@" + decisionLevel);

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
        System.out.println("Backtracking...");
        for (Node node : assignments.getImplicationGraphNodes().values()) {
            if (node.getDecisionLevel() > assertionLevel) {
                System.out.println("Removing all nodes that led to node " + node.getVariable().getId());
                for (Edge inEdge : node.getInEdges()) {
                    inEdge.getFromNode().removeOutEdge(inEdge);
                }
                assignments.removeAssignment(node.getVariable());
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
        Set<Literal> learntLiterals = new HashSet<>();
        int maxLevel = -1;
        int assertionLevel = -1;
        for (Edge cutEdge : cutEdges) {
            Literal learntLiteral = new Literal(cutEdge.getFromNode().getVariable(),
                    assignments.getVariableAssignment(cutEdge.getFromNode().getVariable()));
            learntLiterals.add(learntLiteral);
            int decisionLevel = cutEdge.getFromNode().getDecisionLevel();
            if (decisionLevel > maxLevel) {
                assertionLevel = maxLevel;
                maxLevel = decisionLevel;
            } else if (decisionLevel < maxLevel && decisionLevel > assertionLevel) {
                assertionLevel = decisionLevel;
            }
            System.out.println("Checked decision level " + decisionLevel + " || assertion@" + assertionLevel + ", max@" + maxLevel);
        }
        Clause learntClause = new Clause(new ArrayList<>(learntLiterals));
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
            Variable lastAssignedVar = null;

            for (Clause clause : clauses.getClauses()) {
                Literal unitLiteral = clause.getUnitLiteral(assignments, lastAssignedVar);

                if (unitLiteral != null) {
                    // Found unit literal, do unit resolution
                    Variable unitLiteralVariable = unitLiteral.getVariable();
                    lastAssignedVar = unitLiteralVariable;
                    boolean inferredNodeAssignment = !unitLiteral.isNegated();

                    // Add to implication graph
                    lastInferredNode = addToImplicationGraph(clause, unitLiteralVariable, decisionLevel);

                    // Add assignment of unit literal
                    System.out.println("Inferred " + unitLiteralVariable.getId() + "=" + inferredNodeAssignment + "@" + decisionLevel + " by clause " + clause.toString());

                    // Check if assignment is conflicting
                    if(conflictsWithExistingAssignment(unitLiteralVariable, inferredNodeAssignment)) {
                        // Conflicting assignment
                        System.out.println("Found conflicting assignment for " + unitLiteralVariable.getId() + " in clause " + clause.toString());
                        Node conflictingNode = assignments.getNode(unitLiteralVariable);
                        return new UnitResolutionResult(lastInferredNode, conflictingNode, true);
                    }

                    assignments.addAssignment(unitLiteralVariable, lastInferredNode, inferredNodeAssignment, false);

                    performedUnitResolution = true;
                }
            }
        }

        return new UnitResolutionResult(lastInferredNode, false);
    }

    private boolean conflictsWithExistingAssignment(Variable unitLiteralVariable, boolean inferredNodeAssignment) {
        System.out.println("Checking if assignment of " + inferredNodeAssignment + " to " + unitLiteralVariable.getId() + " is conflicting...");
        assignments.printVariableAssignments();
        if(assignments.hasConflictingAssignment(unitLiteralVariable, inferredNodeAssignment)) {
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
        for (Literal literal : dueToClause.getLiterals()) {
            if (literal.getVariable() != unitLiteralVariable) {
                Node fromNode = assignments.getNode(literal.getVariable());
                Edge newEdge = new Edge(fromNode, lastInferredNode, dueToClause);
                System.out.println("Added edge from " + fromNode.getVariable().getId() + " to " + lastInferredNode.getVariable().getId() + " using clause " + dueToClause.toString());
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
//        Clause lastAddedClause = clauses.getLastAddedClause();
//        if (lastAddedClause != null) {
//            System.out.println("Picking branching variable from clause " + lastAddedClause.toString());
//            assignments.printVariableAssignments();
//            for (Literal literal : lastAddedClause.getLiterals()) {
//                if (!assignments.hasAssignedVariable(literal)) {
//                    return literal.getVariable();
//                }
//            }
//        }
//        System.out.println("Did not find branching variable from clause, picking next variable...");
        for (Variable variable : variables) {
            if (!assignments.getImplicationGraphNodes().containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }
}

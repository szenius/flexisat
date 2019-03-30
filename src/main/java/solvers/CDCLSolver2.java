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
        if (unitPropagation(decisionLevel).isConflict()) {
            return false;
        }

        while (assignments.getImplicationGraphNodes().size() < variables.size()) {
            Variable pickedVariable = pickBranchingVariable();
            decisionLevel++;
            Node newNode = new Node(pickedVariable, decisionLevel);
            addAssignment(pickedVariable, newNode, true);
            UnitResolutionResult unitResolutionResult = unitPropagation(decisionLevel);
            if (unitResolutionResult.isConflict()) {
                int assertionLevel = conflictAnalysis(unitResolutionResult);
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

    private void addAssignment(Variable variable, Node node, boolean assignment) {
        assignments.getImplicationGraphNodes().put(variable, node);
        assignments.getImplicationGraphRoots().put(variable, node);
        assignments.getVariableAssignments().put(variable, assignment);
    }

    private void backtrack(int assertionLevel) {
        for (Node node : assignments.getImplicationGraphNodes().values()) {
            if (node.getDecisionLevel() > assertionLevel) {
                for (Edge inEdge : node.getInEdges()) {
                    inEdge.getFromNode().removeOutEdge(inEdge);
                }
            }
        }
    }

    private int conflictAnalysis(UnitResolutionResult conflict) {
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();
        Set<Edge> cutEdges = new HashSet<>(conflictingNode.getInEdges());
        cutEdges.addAll(inferredNode.getInEdges());
        List<Literal> learntLiterals = new ArrayList<>();
        int assertionLevel = -1;
        for (Edge cutEdge : cutEdges) {
            learntLiterals.add(new Literal(cutEdge.getFromNode().getVariable(),
                    !assignments.getVariableAssignments().get(cutEdge.getFromNode())));
            assertionLevel = Math.max(cutEdge.getToNode().getDecisionLevel(), assertionLevel);
        }
        clauses.addClause(new Clause(learntLiterals));
        return assertionLevel;
    }

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
                    lastInferredNode = new Node(unitLiteralVariable, decisionLevel);
                    boolean inferredNodeAssignment = !unitLiteral.isNegated();

                    // Add to implication graph
                    for (Literal literal : clause.getLiterals()) {
                        if (literal.getVariable() != unitLiteralVariable) {
                            Node fromNode = assignments.getImplicationGraphNodes().get(unitLiteralVariable);
                            Edge newEdge = new Edge(fromNode, lastInferredNode, clause);
                            fromNode.addOutEdge(newEdge);
                            lastInferredNode.addInEdge(newEdge);
                        }
                    }

                    if(assignments.getVariableAssignments().getOrDefault(unitLiteralVariable, inferredNodeAssignment)
                            != inferredNodeAssignment) {
                        // Conflicting assignment
                        Node conflictingNode = assignments.getImplicationGraphNodes().get(unitLiteralVariable);
                        return new UnitResolutionResult(lastInferredNode, conflictingNode, true);
                    } else {
                        // Not conflicting, add assignment
                        addAssignment(unitLiteralVariable, lastInferredNode, inferredNodeAssignment);
                    }
                }
            }
        }
        return new UnitResolutionResult(lastInferredNode, false);
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

package solvers;

import data_structures.*;

import java.util.*;

public class CDCLSolver implements Solver {
    private Clauses clauses;
    private Set<Variable> variables;
    private Assignments assignments;
    private int pickBranchingCount;

    public CDCLSolver(Clauses clauses, Set<Variable> variables) {
        this.clauses = clauses;
        this.variables = variables;
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
    public SolverResult solve() {
        System.out.println("Running CDCLSolver...");

        long startTime = System.currentTimeMillis();

        // Check if formula is empty
        if (clauses.getClauses().isEmpty()) {
            System.out.println("Found empty formula!");
            return new SolverResult(true, getPickBranchingCount(), computeTimeTaken(startTime));
        }

        // Check if there is any empty clause
        if (clauses.hasEmptyClause()) {
            System.out.println("Found empty clause!");
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
            assignments.addAssignment(pickedVariable, newNode, false, true);
            System.out.println("ASSIGNED " + pickedVariable.getId() + "=" + assignments.getVariableAssignment(pickedVariable) + "@" + decisionLevel);

            // Run unit propagation
            UnitResolutionResult unitResolutionResult = unitPropagation(decisionLevel);
            while (unitResolutionResult.isConflict()) {
                int assertionLevel = conflictAnalysis(unitResolutionResult);
                System.out.println("ASSERTING at level " + assertionLevel);
                if (assertionLevel < 0) {
                    return new SolverResult(false, getPickBranchingCount(), computeTimeTaken(startTime));
                } else {
                    backtrack(assertionLevel);
                    System.out.println("BACKTRACKED to decision level " + assertionLevel);
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
        Node inferredNode = conflict.getInferredNode();
        Node conflictingNode = conflict.getConflictingNode();
        int conflictDecisionLevel = conflict.getConflictDecisionLevel();

        // Remove all outgoing edges from the conflicting nodes
        inferredNode.removeSubtree();
        conflictingNode.removeSubtree();

        // Collect edges that directly lead to the conflict
        Queue<Edge> cutEdges = new LinkedList<>(conflictingNode.getInEdges());
        cutEdges.addAll(inferredNode.getInEdges());

        // Find the nodes leading to conflict site
        Set<Node> candidates = new HashSet<>();
        while (!cutEdges.isEmpty()) {
            candidates.add(cutEdges.poll().getFromNode());
        }

        // Do resolution to find learnt clause
        int numLiteralsAtDecisionLevel = countNodesAtDecisionLevel(candidates, conflictDecisionLevel);
        for (Node candidate : candidates) {
            cutEdges.addAll(candidate.getInEdges());
        }
        Set<Node> visited = new HashSet<>();
        while (numLiteralsAtDecisionLevel != 1 && !cutEdges.isEmpty()) {
            Edge cutEdge = cutEdges.poll();
            if (cutEdge.getToNode().equals(conflictingNode) || cutEdge.getToNode().equals(inferredNode)) {
                // Cannot do resolution with conflicting nodes
                continue;
            }
            if (cutEdge.getToNode().getDecisionLevel() > conflictDecisionLevel) {
                // Don't consider learning literals assigned at decision level higher than conflict level
                continue;
            }
            if (!visited.add(cutEdge.getToNode())) {
                // Don't resolve with previously resolved with nodes
                continue;
            }
            candidates = resolve(candidates, assignments.getNodes(cutEdge.getDueToClause()), cutEdge.getToNode().getVariable());
            numLiteralsAtDecisionLevel = countNodesAtDecisionLevel(candidates, conflictDecisionLevel);
            cutEdges.addAll(cutEdge.getFromNode().getInEdges());
        }

        // Generate new learnt clause
        int maxLevel = -1;
        int assertionLevel = -1;
        Set<Literal> learntLiterals = new HashSet<>();
        for (Node candidate : candidates) {
            learntLiterals.add(new Literal(candidate.getVariable(), assignments.getVariableAssignment(candidate.getVariable())));
            if (candidate.getDecisionLevel() > maxLevel) {
                assertionLevel = maxLevel;
                maxLevel = candidate.getDecisionLevel();
            } else if (candidate.getDecisionLevel() < maxLevel && candidate.getDecisionLevel() > assertionLevel) {
                assertionLevel = candidate.getDecisionLevel();
            }
        }
        Clause learntClause = new Clause(new ArrayList<>(learntLiterals));
        clauses.addClause(learntClause);
        System.out.println("LEARNT new clause " + learntClause.toString());

        // Remove assignment of the conflicting node which came second
        List<Edge> inEdges = inferredNode.getInEdges();
        for (Edge inEdge : inEdges) {
            inEdge.getFromNode().removeOutEdge(inEdge);
        }

        // Default assertion level when only one literal in learnt clause or all literals have same decision level
        if (learntClause.size() == 1 || (maxLevel != -1 && assertionLevel == -1)) {
            return 0;
        }

        return assertionLevel;
    }

    /**
     * Do resolution on the candidate nodes as a clause, and clause as another clause.
     *
     * @param candidates First clause
     * @param clause Second clause
     * @param resolvableVariable The variable being removed from both clauses for resolution
     * @return The resulting clause after resolution as a set of nodes
     */
    private Set<Node> resolve(Set<Node> candidates, Set<Node> clause, Variable resolvableVariable) {
        Set<Node> resolvedClause = new HashSet<>();
        for (Node candidate : candidates) {
            if (!candidate.getVariable().equals(resolvableVariable)) {
                resolvedClause.add(candidate);
            }
        }
        for (Node clauseNode : clause) {
            if (!clauseNode.getVariable().equals(resolvableVariable)) {
                resolvedClause.add(clauseNode);
            }
        }
        return resolvedClause;
    }

    /**
     * Count the number of nodes assigned at the given decision level
     *
     * @param nodes
     * @param decisionLevel
     * @return Number of nodes assigned at the given decision level
     */
    private int countNodesAtDecisionLevel(Set<Node> nodes, int decisionLevel) {
        int numAtDecisionLevel = 0;
        for (Node node : nodes) {
            if (node.getDecisionLevel() == decisionLevel) {
                numAtDecisionLevel++;
            }
        }
        return numAtDecisionLevel;
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
                Literal unitLiteral = clause.getUnitLiteral(assignments);

                if (unitLiteral != null) {
                    // Found unit literal, do unit resolution
                    Variable unitLiteralVariable = unitLiteral.getVariable();
                    boolean inferredNodeAssignment = !unitLiteral.isNegated();
                    System.out.println("INFERRED " + unitLiteralVariable.getId() + "=" + inferredNodeAssignment + "@" + decisionLevel + " by clause " + clause.toString());

                    // Add to implication graph + assign variable
                    Node inferredNode = addToImplicationGraph(clause, unitLiteralVariable, decisionLevel);
                    assignments.addAssignment(unitLiteralVariable, inferredNode, inferredNodeAssignment, assignments.isEmpty());

                    // Try to find conflicting assignment
                    Clause disagreeingClause = propagateAssignment(inferredNode, clause);
                    if (disagreeingClause != null) {
                        // Found conflicting assignment
                        System.out.println("CONFLICT: " + unitLiteralVariable.getId() + "@" + decisionLevel + " due to clauses " + clause.toString() + " and " + disagreeingClause.toString());
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
            Literal unitLiteral = clause.findUnitLiteral(inferredNode.getVariable(), assignments);
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

    private boolean conflictsWithExistingAssignment(Variable unitLiteralVariable, boolean inferredNodeAssignment) {
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
        for (Variable variable : variables) {
            if (!assignments.getImplicationGraphNodes().containsKey(variable)) {
                return variable;
            }
        }
        return null;
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

    /**********************************/
    /** HELPER METHODS FOR DEBUGGING **/
    /**********************************/

    private void printNodeSet(Set<Node> nodes) {
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : nodes) {
            joiner.add((assignments.getVariableAssignment(node.getVariable()) ? "-" : "") + String.valueOf(node.getVariable().getId()));
        }
        System.out.println("[[" + joiner.toString() + "]]");
    }
}

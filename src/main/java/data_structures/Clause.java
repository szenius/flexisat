package data_structures;

import java.util.*;

public class Clause {
    private List<Literal> literals;

    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public Literal findLiteralByNode(Node node) {
        for (Literal literal : literals) {
            if (node.getVariable().equals(literal.getVariable())) {
                return literal;
            }
        }
        return null;
    }

    public Literal getUnitLiteral(Assignments2 assignments, Variable lastAssignedVariable) {
        Literal unitLiteral = null;
        boolean clauseValue = false;
        for (Literal literal : literals) {
            if (!assignments.hasAssignedVariable(literal)) {
                // Found unassigned literal
                if (unitLiteral != null) {
                    // Found more than one unassigned literal, so there is no unit literal
                    return null;
                }
                unitLiteral = literal;
            } else {
                clauseValue = clauseValue | (literal.isNegated() ^ assignments.getVariableAssignment(literal));
            }
        }
        if (clauseValue) {
            return null;
        }
        if (unitLiteral == null && lastAssignedVariable != null) {
            // All variables have been assigned but this clause evaluates to false
            Set<Node> nodes = assignments.getNodes(this);
            Map<Integer, List<Node>> nodesByDecisionLevel = new HashMap<>();
            int maxDecisionLevel = Integer.MIN_VALUE;
            for (Node node : nodes) {
                int decisionLevel = node.getDecisionLevel();
                List<Node> nodesAtDecisionLevel = nodesByDecisionLevel.getOrDefault(decisionLevel, new ArrayList<>());
                nodesAtDecisionLevel.add(node);
                nodesByDecisionLevel.putIfAbsent(decisionLevel, nodesAtDecisionLevel);
                maxDecisionLevel = Math.max(maxDecisionLevel, decisionLevel);
            }
            if (nodesByDecisionLevel.size() == 3) {
                // All literals were assigned at different decision levels. Return the one assigned last.
                return findLiteralByNode(nodesByDecisionLevel.get(maxDecisionLevel).get(0));
            }
            List<Node> candidates = nodesByDecisionLevel.get(maxDecisionLevel);
            for (Node candidate : candidates) {
                if (candidate.getOutEdges().isEmpty()) {
                    // Found a leaf in the implication graph
                    return findLiteralByNode(candidate);
                }
            }
            // Find the literal that was last assigned in the implication graph
            return findLiteralByNode(findDeepestNode(candidates.get(0), candidates.get(0), candidates));
        }
        return unitLiteral;
    }

    private Node findDeepestNode(Node currNode, Node lastFoundNode, List<Node> nodes) {
        if (nodes.contains(currNode)) {
            lastFoundNode = currNode;
        }
        if (currNode.getOutEdges().isEmpty()) {
            return lastFoundNode;
        }
        List<Edge> paths = currNode.getOutEdges();
        for (Edge path : paths) {
            findDeepestNode(path.getToNode(), lastFoundNode, nodes);
        }
        return lastFoundNode;
    }

    public boolean checkVALID(Assignments assignments) {
        boolean clauseVal = false;
        for (Literal literal : literals) {
            if (assignments.getUnassignedVarIds().contains(literal.getVariable().getId())) {
                // There are still unassigned variables, cannot determine VALID
                return true;
            }
            clauseVal |= literal.getValue(assignments.getAssignmentValue(literal.getVariable().getId()));
        }
        //System.out.println("Clause: Checked clause " + toString() + "... valid? " + clauseVal);
        return clauseVal;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (Literal literal : literals) {
            int id = literal.getVariable().getId();
            if (literal.isNegated()) id *= -1;
            joiner.add(String.valueOf(id));
        }
        return "[[" + joiner.toString() + "]]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public int size() {
        return this.getLiterals().size();
    }
}
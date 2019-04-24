package data_structures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Assignments {
    private static final Logger logger = LoggerFactory.getLogger(Assignments.class);

    private Map<Variable, Node> implicationGraphRoots;
    private Map<Variable, Node> implicationGraphNodes;
    private Map<Variable, Boolean> variableAssignments;

    public Assignments() {
        this.implicationGraphRoots = new HashMap<>();
        this.implicationGraphNodes = new HashMap<>();
        this.variableAssignments = new HashMap<>();
    }

    public int getNumAssigned() {
        return variableAssignments.size();
    }

    public Map<Variable, Node> getImplicationGraphNodes() {
        return this.implicationGraphNodes;
    }

    public boolean hasAssignedVariable(Literal literal) {
        return hasAssignedVariable(literal.getVariable());
    }

    public boolean hasAssignedVariable(Variable variable) {
        return variableAssignments.containsKey(variable);
    }

    public boolean getVariableAssignment(Literal literal) {
        return getVariableAssignment(literal.getVariable());
    }

    public boolean getVariableAssignment(Variable variable) {
        return variableAssignments.get(variable);
    }

    public void addAssignment(Variable variable, Node node, boolean assignment, boolean isRoot) {
        addNode(variable, node);
        addVariableAssignment(variable, assignment);
        if (isRoot) addRoot(variable, node);
    }

    public void removeAssignment(Variable variable) {
        this.getImplicationGraphNodes().get(variable).clearAncestors();
        removeNode(variable);
        removeRoot(variable);
        removeVariableAssignment(variable);
    }

    private void removeVariableAssignment(Variable variable) {
        variableAssignments.remove(variable);
    }

    private void removeRoot(Variable variable) {
        implicationGraphRoots.remove(variable);
    }

    private void removeNode(Variable variable) {
        implicationGraphNodes.remove(variable);
    }

    public void addNode(Variable variable, Node node) {
        implicationGraphNodes.put(variable, node);
    }

    public void addVariableAssignment(Variable variable, boolean assignment) {
        addVariableAssignment(variable, assignment, true);
    }

    public void addRoot(Variable variable, Node root) {
        implicationGraphRoots.put(variable, root);
    }

    public boolean hasConflictingAssignment(Variable variable, boolean expected) {
        return variableAssignments.getOrDefault(variable, expected) != expected;
    }

    public Node getNode(Variable variable) {
        return implicationGraphNodes.get(variable);
    }

    public void addVariableAssignment(Variable variable, boolean assignment, boolean shouldOverride) {
        if (shouldOverride) {
            variableAssignments.put(variable, assignment);
        } else {
            variableAssignments.putIfAbsent(variable, assignment);
        }
    }

    public Set<Node> getNodes(Clause dueToClause) {
        Set<Node> nodes = new HashSet<>();
        for (Literal literal : dueToClause.getLiterals()) {
            nodes.add(getNode(literal.getVariable()));
        }
        return nodes;
    }

    public void removeAssignmentsBeyondLevel(int assertionLevel) {
        List<Variable> variables = new ArrayList<>(implicationGraphNodes.keySet());
        for (Variable variable : variables) {
            Node node = implicationGraphNodes.get(variable);
            if (node.getDecisionLevel() > assertionLevel) {
                for (Edge inEdge : node.getInEdges()) {
                    inEdge.getFromNode().removeOutEdge(inEdge);
                }
                node.getInEdges().clear();
                removeAssignment(variable);
            }
        }
    }

    public boolean isEmpty() {
        return this.implicationGraphRoots.isEmpty();
    }

    public void clear() {
        this.implicationGraphRoots = new HashMap<>();
        this.implicationGraphNodes = new HashMap<>();
        this.variableAssignments = new HashMap<>();
    }

    /**********************************/
    /** HELPER METHODS FOR DEBUGGING **/
    /**********************************/

    public void printVariableAssignments() {
        StringBuilder sb = new StringBuilder();
        for (Variable assignedVars : variableAssignments.keySet()) {
            sb.append(assignedVars.getId())
                    .append('=')
                    .append(variableAssignments.get(assignedVars))
                    .append(',');
        }
        logger.debug(sb.toString());
    }

    public void printImplicationGraph() {
        logger.debug("*********************");
        for (Node root : implicationGraphRoots.values()) {
            logger.debug("Exploring root {}", root.toString());
            traverse(root);
        }
        logger.debug("*********************");
    }

    private void traverse(Node root) {
        List<Edge> outgoing = root.getOutEdges();
        for (Edge edge : outgoing) {
            logger.debug(edge.toString());
            traverse(edge.getToNode());
        }
    }
}

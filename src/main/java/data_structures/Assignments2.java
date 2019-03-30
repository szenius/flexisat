package data_structures;

import java.util.HashMap;
import java.util.Map;

public class Assignments2 {
    private Map<Variable, Node> implicationGraphRoots;
    private Map<Variable, Node> implicationGraphNodes;
    private Map<Variable, Boolean> variableAssignments;

    public Assignments2() {
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
        return variableAssignments.containsKey(literal.getVariable());
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
        variableAssignments.put(variable, assignment);
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
}

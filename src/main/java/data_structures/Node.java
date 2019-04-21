package data_structures;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Variable variable;
    private int decisionLevel;
    private List<Edge> inEdges;
    private List<Edge> outEdges;

    public Node(Variable variable, int decisionLevel) {
        this.variable = variable;
        this.decisionLevel = decisionLevel;
        this.inEdges = new ArrayList<>();
        this.outEdges = new ArrayList<>();
    }

    public Variable getVariable() {
        return variable;
    }

    public int getDecisionLevel() {
        return decisionLevel;
    }

    public List<Edge> getInEdges() {
        return inEdges;
    }

    public List<Edge> getOutEdges() {
        return outEdges;
    }

    public void addInEdge(Edge inEdge) {
        this.inEdges.add(inEdge);
    }

    public void addOutEdge(Edge outEdge) {
        this.outEdges.add(outEdge);
    }

    public void removeOutEdge(Edge outEdge) {
        outEdges.remove(outEdge);
    }

    @Override
    public int hashCode() {
        return (String.valueOf(getVariable().hashCode()) + "+" + String.valueOf(getDecisionLevel())).hashCode();
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
        if (((Node) obj).getVariable() == this.getVariable()
                && ((Node) obj).getDecisionLevel() == this.getDecisionLevel()) {
            return true;
        }
        return false;
    }
}

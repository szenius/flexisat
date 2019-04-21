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
    public String toString() {
        return String.valueOf(this.getVariable().getId()) + "@" + String.valueOf(this.getDecisionLevel());
    }

    @Override
    public int hashCode() {
        return getVariable().hashCode();
//        return (String.valueOf(getVariable().hashCode()) + "+" + String.valueOf(getDecisionLevel())).hashCode();
    }

    // NOTE: The equals for Node only cares about whether the variable is the same and not the decision level.
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
        return ((Node) obj).getVariable() == this.getVariable();
    }

    public void removeSubtree() {
        for (Edge out : outEdges) {
            out.getToNode().removeSubtree();
            out.getToNode().getInEdges().clear();
        }
        outEdges.clear();
    }
}

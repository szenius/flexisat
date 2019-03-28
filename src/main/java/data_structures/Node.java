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
}

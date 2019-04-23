package data_structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    private Variable variable;
    private int decisionLevel;
    private List<Edge> inEdges;
    private List<Edge> outEdges;
    private Set<Node> ancestors;

    public Node(Variable variable, int decisionLevel) {
        this.variable = variable;
        this.decisionLevel = decisionLevel;
        this.inEdges = new ArrayList<>();
        this.outEdges = new ArrayList<>();
        this.ancestors = new HashSet<>();
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

    public Set<Node> getAncestors() {
        return ancestors;
    }

    public void addAncestor(Node ancestor) {
        if (ancestor != null) this.ancestors.add(ancestor);
    }

    public void clearAncestors() {
        this.ancestors.clear();
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
        while (!outEdges.isEmpty()) {
            Edge outEdge = outEdges.remove(0);
            outEdge.getToNode().removeInEdge(outEdge.getDueToClause());
        }
    }

    private void removeInEdge(Clause dueToClause) {
        for (int i = 0; i < inEdges.size(); i++) {
            if (inEdges.get(i).getDueToClause().equals(dueToClause)) {
                Edge inEdge = inEdges.remove(i);
                inEdge.getFromNode().removeOutEdge(inEdge);
                i--;
            }
        }
    }

    public void addAncestors(Set<Node> ancestors) {
        this.ancestors.addAll(ancestors);
    }
}

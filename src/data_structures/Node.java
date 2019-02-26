package data_structures;

import com.sun.javafx.geom.Edge;

public class Node {
    private Variable variable;
    private int decisionLevel;
    private int assignment;
    private Edge next;

    public Node(Variable variable, int decisionLevel, int assignment) {
        this.variable = variable;
        this.decisionLevel = decisionLevel;
        this.assignment = assignment;
    }

    public Variable getVariable() {
        return variable;
    }

    public int getDecisionLevel() {
        return decisionLevel;
    }

    public int getAssignment() {
        return assignment;
    }

    public Edge getNext() {
        return next;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public void setAssignment(int assignment) {
        this.assignment = assignment;
    }

    public void setNext(Edge next) {
        this.next = next;
    }
}

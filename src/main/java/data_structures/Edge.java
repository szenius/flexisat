package data_structures;

public class Edge {
    private Clause dueToClause;
    private Node fromEdge;
    private Node toEdge;

    public Edge(Node fromEdge, Node toEdge, Clause dueToClause) {
        this.fromEdge = fromEdge;
        this.toEdge = toEdge;
        this.dueToClause = dueToClause;
    }

    public Clause getDueToClause() {
        return dueToClause;
    }

    public Node getFromEdge() {
        return fromEdge;
    }

    public Node getToEdge() {
        return toEdge;
    }
}

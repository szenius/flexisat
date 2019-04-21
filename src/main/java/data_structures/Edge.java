package data_structures;

public class Edge {
    private Clause dueToClause;
    private Node fromNode;
    private Node toNode;

    public Edge(Node fromNode, Node toNode, Clause dueToClause) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.dueToClause = dueToClause;
    }

    public Clause getDueToClause() {
        return dueToClause;
    }

    public Node getFromNode() {
        return fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    @Override
    public String toString() {
        return fromNode.toString() + "---" + dueToClause.toString() + "-->" + toNode.toString();
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
        Edge edge = (Edge) obj;
        return edge.getFromNode() == this.getFromNode()
                && edge.getToNode() == this.getToNode()
                && edge.getDueToClause() == this.getDueToClause();
    }
}

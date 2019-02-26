package data_structures;

public class Edge {
    private Node prev;
    private Node next;

    public Edge(Node prev, Node next) {
        this.prev = prev;
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

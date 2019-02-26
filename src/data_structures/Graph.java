package data_structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<Integer, Node> nodes;
    private List<Node> startNodes;

    public Graph() {
        this.nodes = new HashMap<>();
        this.startNodes = new ArrayList<>();
    }

    public void addStartNode(Node node) {
        this.startNodes.add(node);
    }

    public List<Node> getStartNodes() {
        return this.startNodes;
    }
}

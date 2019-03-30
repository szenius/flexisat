package data_structures;

public class UnitResolutionResult {
    private Node inferredNode; // last node inferred in the unit resolution
    private Node conflictingNode; // null if no conflict
    private boolean isConflict;

    public UnitResolutionResult(Node inferredNode, boolean isConflict) {
        this.inferredNode = inferredNode;
        this.conflictingNode = null;
        this.isConflict = isConflict;
    }

    public UnitResolutionResult(Node inferredNode, Node conflictingNode, boolean isConflict) {
        this.inferredNode = inferredNode;
        this.conflictingNode = conflictingNode;
        this.isConflict = isConflict;
    }

    public Node getInferredNode() {
        return inferredNode;
    }

    public Node getConflictingNode() {
        return conflictingNode;
    }

    public boolean isConflict() {
        return isConflict;
    }
}

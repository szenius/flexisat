package data_structures;

public class UnitResolutionResult {
    private Node inferredNode; // last node inferred in the unit resolution
    private Node conflictingNode; // null if no conflict
    private boolean isConflict;
    private int conflictDecisionLevel;

    public UnitResolutionResult(Node inferredNode, boolean isConflict, int conflictDecisionLevel) {
        this.inferredNode = inferredNode;
        this.conflictingNode = null;
        this.isConflict = isConflict;
        this.conflictDecisionLevel = conflictDecisionLevel;
    }

    public UnitResolutionResult(Node inferredNode, Node conflictingNode, boolean isConflict, int conflictDecisionLevel) {
        this.inferredNode = inferredNode;
        this.conflictingNode = conflictingNode;
        this.isConflict = isConflict;
        this.conflictDecisionLevel = conflictDecisionLevel;
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

    public int getConflictDecisionLevel() {
        return conflictDecisionLevel;
    }
}

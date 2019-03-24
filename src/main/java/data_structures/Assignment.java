package data_structures;

import java.util.List;

public class Assignment {

    private Integer varId;
    private Boolean assignmentValue;
    private Integer decisionLevel;
    // By our current implementation, we will cut off the tree at the root.
    // TODO: Might have better heuristics that can improve performance.
    private List<Integer> implicationGraphRoots;

    public Assignment(int varId, Boolean assignmentValue, int decisionLevel, List<Integer> implicationGraphRoots) {
        this.varId = varId;
        this.assignmentValue = assignmentValue;
        this.decisionLevel = decisionLevel;
        this.implicationGraphRoots = implicationGraphRoots;
    }


    public void addImplicationGraphRoot(List<Integer> implicationGraphRoot) {
        // There is an assumption here that the implied by list is immutable after this assignmentValue.
        this.implicationGraphRoots = implicationGraphRoot;
    }

    public void swapAssignment() {
        this.assignmentValue = !this.assignmentValue;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public List<Integer> getImplicationGraphRoots() {
        return this.implicationGraphRoots;
    }

    public Boolean getAssignmentValue() {
        return this.assignmentValue;
    }

    public Integer getVarId() {
        return this.varId;
    }

    public Integer getDecisionLevel() {
        return this.decisionLevel;
    }
}

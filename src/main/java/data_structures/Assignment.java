package data_structures;

import java.util.List;

public class Assignment {

    private Integer varId;
    private Boolean assignmentValue;
    private Integer decisionLevel;
    // By our current implementation, we will cut off the tree at the root.
    // TODO: Might have better heuristics that can improve performance.
    private List<Integer> rootImplicationNodes;

    public Assignment(int varId, Boolean assignmentValue, int decisionLevel, List<Integer> impliedByVariables) {
        this.varId = varId;
        this.assignmentValue = assignmentValue;
        this.decisionLevel = decisionLevel;
        this.rootImplicationNodes = impliedByVariables;
    }


    public void addRootImpliedNode(List<Integer> impliedByRootNode) {
        // There is an assumption here that the implied by list is immutable after this assignmentValue.
        this.rootImplicationNodes = impliedByRootNode;
    }

    public void swapAssignment() {
        this.assignmentValue = !this.assignmentValue;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public List<Integer> getImpliedByRootNodeList() {
        return this.rootImplicationNodes;
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

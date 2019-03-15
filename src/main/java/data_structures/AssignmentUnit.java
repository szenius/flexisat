package data_structures;

import java.util.List;

public class AssignmentUnit {

    private Integer varId;
    private Boolean assignment;
    private Integer decisionLevel;
    // By our current implementation, we will cut off the tree at the root.
    // Might have better heuristics that can improve performance.
    private List<Integer> rootImplicationNodes;

    public AssignmentUnit(int varId, Boolean assignment, int decisionLevel) {
        this.varId = varId;
        this.assignment = assignment;
        this.decisionLevel = decisionLevel;
    }


    public void addRootImpliedNode(List<Integer> impliedByRootNode) {
        // There is an assumption here that the implied by list is immutable after this assignment.
        this.rootImplicationNodes = impliedByRootNode;
    }

    public void replaceAssignment(Boolean assignment) {
        this.assignment = assignment;
    }

    public void replaceDecisionLevel(Integer decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public List<Integer> getImpliedByRootNodeList() {
        return this.rootImplicationNodes;
    }

    public Boolean getAssignment() {
        return this.assignment;
    }

    public Integer getVarId() {
        return this.varId;
    }

    public Integer getDecisionLevel() {
        return this.decisionLevel;
    }
}

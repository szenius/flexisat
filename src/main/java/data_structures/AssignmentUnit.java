package data_structures;

import java.util.ArrayList;
import java.util.List;

public class AssignmentUnit {

    private Integer varId;
    private Boolean assignment;
    private Integer decisionLevel;
    private List<AssignmentUnit> impliedBy;

    public AssignmentUnit(int varId, Boolean assignment, int decisionLevel) {
        this.varId = varId;
        this.assignment = assignment;
        this.decisionLevel = decisionLevel;
    }


    public void addImpliedBy(List<AssignmentUnit> impliedBy) {
        // There is an assumption here that the implied by list is immutable after this assignment.
        this.impliedBy = impliedBy;
    }

    public void replaceAssignment(Boolean assignment) {
        this.assignment = assignment;
    }

    public void replaceDecisionLevel(Integer decisionLevel) {
        this.decisionLevel = decisionLevel;
    }


    public List<AssignmentUnit> getImpliedByList() {
        return this.impliedBy;
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

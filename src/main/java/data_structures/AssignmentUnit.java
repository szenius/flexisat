package data_structures;

import java.util.ArrayList;
import java.util.List;

public class AssignmentUnit {

    Integer varId;
    Boolean assignment;
    Integer decisionLevel;
    List<AssignmentUnit> impliedAssignments;
    List<AssignmentUnit> impliedBy;

    public AssignmentUnit(int varId, Boolean assignment, int decisionLevel) {
        this.varId = varId;
        this.assignment = assignment;
        this.decisionLevel = decisionLevel;
        this.impliedAssignments = new ArrayList<AssignmentUnit>();
        this.impliedBy = new ArrayList<AssignmentUnit>();
    }

    public void addImpliedAssignment(AssignmentUnit impliedAssignment) {
        this.impliedAssignments.add(impliedAssignment);
    }

    public void addImpliedBy(AssignmentUnit impliedBy) {
        this.impliedBy.add(impliedBy);
    }

    public void replaceAssignment(Boolean assignment) {
        this.assignment = assignment;
    }

    public List<AssignmentUnit> getImpliedAssignmentsList() {
        return this.impliedAssignments;
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

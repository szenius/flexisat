package branch_pickers;

import data_structures.Assignments;
import data_structures.Variable;

import java.util.Set;

public class SequentialBranchPicker implements BranchPicker {
    private Set<Variable> variables;

    public SequentialBranchPicker(Set<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public Variable pick(Assignments assignments) {
        for (Variable variable : variables) {
            if (!assignments.getImplicationGraphNodes().containsKey(variable)) {
                return variable;
            }
        }
        return null;
    }

}

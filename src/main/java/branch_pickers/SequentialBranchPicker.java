package branch_pickers;

import data_structures.Assignments;
import data_structures.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SequentialBranchPicker extends BranchPicker {
    public SequentialBranchPicker(Set<Variable> variables) {
        super(variables);
    }

    @Override
    public Variable pick(Assignments assignments) {
        List<Variable> unassignedVariables = getUnassignedVariables(assignments);
        Collections.sort(unassignedVariables);
        return unassignedVariables.get(0);
    }

}

package branch_pickers;

import data_structures.Assignments;
import data_structures.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RandomBranchPicker extends BranchPicker {

    public RandomBranchPicker(Set<Variable> variables) {
        super(variables);
    }

    @Override
    public Variable pick(Assignments assignments) {
        List<Variable> unassignedVariables = getUnassignedVariables(assignments);
        Collections.shuffle(unassignedVariables);
        return unassignedVariables.get(0);
    }
}

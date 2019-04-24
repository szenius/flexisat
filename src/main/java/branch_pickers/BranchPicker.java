package branch_pickers;

import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BranchPicker {
    private Set<Variable> variables;

    public abstract Variable pick(Assignments assignments);
    public abstract void updateWeights(Clause clause);
    public abstract void decayWeights();

    public BranchPicker(Set<Variable> variables) {
        this.variables = variables;
    }

    public List<Variable> getUnassignedVariables(Assignments assignments) {
        List<Variable> unassignedVariables = new ArrayList<>();
        for (Variable variable : variables) {
            if (!assignments.hasAssignedVariable(variable)) {
                unassignedVariables.add(variable);
            }
        }
        return unassignedVariables;
    }

    public Set<Variable> getVariables() {
        return variables;
    }
}

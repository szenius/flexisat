package branch_pickers.vsids;

import data_structures.Variable;

import java.util.Set;

public class ChaffVSIDSBranchPicker extends VSIDSBranchPicker {

    public ChaffVSIDSBranchPicker(Set<Variable> variables) {
        super(variables, 0.5, 1, 256);
    }
}

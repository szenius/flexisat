package branch_pickers.vsids;

import data_structures.Variable;

import java.util.Set;

public class MiniSATVSIDSBranchPicker extends VSIDSBranchPicker {

    public MiniSATVSIDSBranchPicker(Set<Variable> variables) {
        super(variables, 0.9, 1, 1);
    }

    @Override
    public boolean updateOnResolvedClause() {
        return true;
    }
}

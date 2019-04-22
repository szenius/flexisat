package branch_pickers;

import data_structures.Assignments;
import data_structures.Variable;

public interface BranchPicker {
    Variable pick(Assignments assignments);
}

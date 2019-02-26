package data_structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Assignment {
    private Set<Integer> varIds;
    private Map<Integer, Boolean> assignments;
    private Map<Integer, Integer> decisionLevels;

    public Assignment(Set<Integer> varIds) {
        this.varIds = varIds;
        this.assignments = new HashMap<>();
        this.decisionLevels = new HashMap<>();
    }

    /**
     * Tries to add an assignment to a variable by its integer ID.
     *
     * @param varId integer ID corresponding to a variable
     * @param assignment true/false
     * @return false if an alternative assignments already exists, otherwise true.
     */
    public boolean addAssignment(int varId, boolean assignment, int decisionLevel) {
        if (assignments.containsKey(varId)) {
            return assignments.get(varId) == assignment;
        }
        assignments.put(varId, assignment);
        decisionLevels.put(varId, decisionLevel);
        return true;
    }

    /**
     * @return All varIDs without an assignment
     */
    public Set<Integer> getUnassignedVarIds() {
        Set<Integer> result = new HashSet<>();
        for (Integer id : varIds) {
            if (!assignments.containsKey(id)) {
                result.add(id);
            }
        }
        return result;
    }

}

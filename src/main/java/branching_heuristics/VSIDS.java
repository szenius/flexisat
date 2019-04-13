package branching_heuristics;

import data_structures.Assignments;
import data_structures.Clauses;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of Chaff's VSIDS algorithm.
 * Activities are bumped for all variables in a newly learnt clause.
 * Decay occurs after every conflicts.
 */
public class VSIDS extends PickBranchingVariableHeuristic {

    Map<Integer, Integer> activityScore;
    double decayFactor;

    public VSIDS() {
        this.activityScore = new HashMap<>();
        this.decayFactor = Math.random();
    }

    @Override
    public int pickBranchingVariable(Clauses clauses, Assignments assignments) {
        Set<Integer> unassignedVariables = assignments.getUnassignedVarIds();
        int variableId = -1;
        double largestActivityScore = Double.MIN_VALUE;
        // Return the unassigned variable with the largest activity score
        for (Map.Entry<Integer, Integer> entry: this.activityScore.entrySet()) {
            if (entry.getValue() > largestActivityScore &&
                    unassignedVariables.contains(entry.getKey())) {
                largestActivityScore = entry.getValue();
                variableId = entry.getKey();
            }
        }
        return variableId;
    }

    /**
     * Increments the activity score of given variable by 1.
     */
    public void incrementActivityScore(int variableId) {
        this.activityScore.put(variableId, this.activityScore.get(variableId) + 1);
    }

    public void decayVariables() {
        for (Map.Entry<Integer, Integer> entry : this.activityScore.entrySet()) {
            int decayedScore = (int)this.decayFactor * entry.getValue();
            this.activityScore.replace(entry.getKey(), decayedScore);
        }
    }
}


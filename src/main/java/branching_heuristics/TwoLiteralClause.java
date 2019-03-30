package branching_heuristics;

import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Clauses;
import data_structures.Literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoLiteralClause implements PickBranchingVariableHeuristic{
    @Override
    public int pickBranchingVariable(Clauses clauses, Assignments assignments) {
        int maxOccurredVariable = countMaxVariableInTwoLiteralClauses(clauses, assignments);
        if (maxOccurredVariable == -1 ) {
            return (int) assignments.getUnassignedVarIds().toArray()[0];
        } else {
            return maxOccurredVariable;
        }
    }


    public void updateTwoClauseStatus(Assignments assignments, Clause clause) {
        int currentUnassignedVars = 0;
        List<Integer> unassignedVariablesInClause = new ArrayList<>();
        for (Literal literal : clause.getLiterals()) {
            if (assignments.getUnassignedVarIds().contains(literal.getVariable().getId())) {
                unassignedVariablesInClause.add(literal.getVariable().getId());
                currentUnassignedVars++;
            }
        }
        if (currentUnassignedVars == 2) {
            clause.setIsTwoClause();
            clause.setVariablesInTwoClause(unassignedVariablesInClause);
        }
    }


    /**
     * Finds the variable that occurred the most number of times in two-literal clauses.
     * @param clauses
     * @param assignments
     * @return varId of variable that occurred the most. Else return -1 if there are no two-literal clauses.
     */
    private int countMaxVariableInTwoLiteralClauses(Clauses clauses, Assignments assignments) {
        int maxOccurrence = 0;
        int mostOccurredVar = -1;
        Map<Integer, Integer> varIdToNumOccurrence = new HashMap<>();
        for (Clause clause : clauses.getClauses()) {
            if (clause.isTwoClause()) {
                for (int varId : clause.getVariablesInTwoClause()) {
                    int numOccurrence = varIdToNumOccurrence.getOrDefault(varId, 0);
                    numOccurrence++;
                    if (numOccurrence >= maxOccurrence) {
                        maxOccurrence = numOccurrence;
                        mostOccurredVar = varId;
                    }
                    varIdToNumOccurrence.put(varId, numOccurrence);
                }
            }
        }
        return mostOccurredVar;
    }
}

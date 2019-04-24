package branch_pickers;

import data_structures.*;

import java.util.*;

public class TwoClauseBranchPicker extends BranchPicker {
    private Clauses clauses;
    private BranchPicker defaultBranchPicker;

    public TwoClauseBranchPicker(Set<Variable> variables, Clauses clauses) {
        super(variables);
        this.clauses = clauses;
        this.defaultBranchPicker = new RandomBranchPicker(variables);
    }

    @Override
    public Variable pick(Assignments assignments) {
        List<List<Literal>> twoClauses = clauses.collectTwoClauses(assignments);

        if (twoClauses.isEmpty()) {
            // No 2 clauses found, default to random picking
            return defaultBranchPicker.pick(assignments);
        }

        // Count number of occurrences of each literal
        int mostOccurringVariableCount = 0;
        Map<Variable, Integer> variableCounts = new HashMap<>();
        for (List<Literal> clause : twoClauses) {
            for (Literal literal : clause) {
                Variable variable = literal.getVariable();
                int count = variableCounts.getOrDefault(variable, 0);
                count++;
                variableCounts.put(variable, count);
                mostOccurringVariableCount = Math.max(mostOccurringVariableCount, count);
            }
        }

        // Find the literal that occurs the most number of times and return
        // If there are more than one of such literals, pick a random one
        int numMostOccurringVariables = 0;
        Variable chosenMostOccurringVariable = null;
        for (Variable variable : variableCounts.keySet()) {
            if (variableCounts.get(variable) == mostOccurringVariableCount) {
                numMostOccurringVariables++;
                if (Math.random() <= (1.0 / numMostOccurringVariables)) {
                    // Given equal chance, this highest weighted variable should be chosen
                    chosenMostOccurringVariable  = variable;
                }
            }
        }
        return chosenMostOccurringVariable;
    }

    @Override
    public void updateWeights(Clause newClause) {
        // Do nothing for static branch pickers
    }

    @Override
    public void decayWeights() {
        // Do nothing for static branch pickers
    }
}

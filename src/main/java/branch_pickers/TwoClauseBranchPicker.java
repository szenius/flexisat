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
        int mostOccurringLiteralCount = 0;
        Map<Literal, Integer> literalCounts = new HashMap<>();
        for (List<Literal> clause : twoClauses) {
            for (Literal literal : clause) {
                int count = literalCounts.getOrDefault(literal, 0);
                count++;
                literalCounts.put(literal, count);
                mostOccurringLiteralCount = Math.max(mostOccurringLiteralCount, count);
            }
        }

        // Find the literal that occurs the most number of times and return
        // If there are more than one of such literals, pick a random one
        List<Literal> mostOccurringLiterals = new ArrayList<>();
        for (Literal literal : literalCounts.keySet()) {
            if (literalCounts.get(literal) == mostOccurringLiteralCount) {
                mostOccurringLiterals.add(literal);
            }
        }
        Collections.shuffle(mostOccurringLiterals);
        return mostOccurringLiterals.get(0).getVariable();
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

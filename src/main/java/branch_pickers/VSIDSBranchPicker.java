package branch_pickers;

import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Literal;
import data_structures.Variable;

import java.util.*;

public class VSIDSBranchPicker extends BranchPicker {
    private static final double DECAY_CHANCE = 0.2;

    Map<Variable, Integer> variableWeights;
    int maxWeight;

    public VSIDSBranchPicker(Set<Variable> variables) {
        super(variables);
        maxWeight = 0;
        variableWeights = new HashMap<>();
        initVariableWeights();
    }

    @Override
    public Variable pick(Assignments assignments) {
        List<Variable> highestWeightedVariables = new ArrayList<>();
        for (Variable variable : variableWeights.keySet()) {
            if (variableWeights.get(variable) == maxWeight) {
                highestWeightedVariables.add(variable);
            }
        }
        Collections.shuffle(highestWeightedVariables);
        return highestWeightedVariables.get(0);
    }

    @Override
    public void updateWeights(Clause newClause) {
        for (Literal literal : newClause.getLiterals()) {
            int newWeight = variableWeights.get(literal.getVariable()) + 1;
            variableWeights.put(literal.getVariable(), newWeight);
            maxWeight = Math.max(maxWeight, newWeight);
        }
    }

    @Override
    public void decayWeights() {
        if (Math.random() < DECAY_CHANCE) {
            for (Variable variable : variableWeights.keySet()) {
                variableWeights.put(variable, variableWeights.get(variable) / 2);
            }
            maxWeight = maxWeight / 2;
        }
    }

    private void initVariableWeights() {
        for (Variable variable : getVariables()) {
            variableWeights.put(variable, 0);
        }
    }
}

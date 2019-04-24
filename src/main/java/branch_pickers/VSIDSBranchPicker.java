package branch_pickers;

import data_structures.Assignments;
import data_structures.Clause;
import data_structures.Literal;
import data_structures.Variable;

import java.util.*;

public class VSIDSBranchPicker extends BranchPicker {
    private Map<Variable, Double> variableWeights;
    private double decayChance;

    public VSIDSBranchPicker(Set<Variable> variables, double decayChance) {
        super(variables);
        this.decayChance = decayChance;
        variableWeights = new HashMap<>();
        initVariableWeights();
    }

    @Override
    public Variable pick(Assignments assignments) {
        int numHighestWeightedVariables = 0;
        double highestWeight = Integer.MIN_VALUE;
        Variable chosenHighestWeightedVariable = null;
        for (Variable variable : variableWeights.keySet()) {
            if (assignments.hasAssignedVariable(variable)) {
                // Cannot pick variables that have already been assigned
                continue;
            }
            double weight = variableWeights.get(variable);
            if (weight == highestWeight) {
                numHighestWeightedVariables++;
                if (Math.random() <= 1.0 / numHighestWeightedVariables) {
                    // Given equal chance, this highest weighted variable should be chosen
                    chosenHighestWeightedVariable = variable;
                }
            } else if (weight > highestWeight) {
                // Reset number of highest weighted variables and highest weight
                numHighestWeightedVariables = 1;
                highestWeight = weight;
                chosenHighestWeightedVariable = variable;

            }
        }
        if (chosenHighestWeightedVariable == null) {
            assignments.printVariableAssignments();
            throw new NullPointerException("num variables: " + numHighestWeightedVariables);
        }
        return chosenHighestWeightedVariable;
    }

    @Override
    public void updateWeights(Clause newClause) {
        for (Literal literal : newClause.getLiterals()) {
            Variable variable = literal.getVariable();
            double newWeight = variableWeights.get(variable) + 1;
            variableWeights.put(variable, newWeight);
        }
    }

    @Override
    public void decayWeights() {
        if (Math.random() < decayChance) {
            for (Variable variable : variableWeights.keySet()) {
                variableWeights.put(variable, variableWeights.get(variable) / 2.0);
            }
        }
    }

    private void initVariableWeights() {
        for (Variable variable : getVariables()) {
            variableWeights.put(variable, 0.0);
        }
    }
}

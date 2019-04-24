package branch_pickers.vsids;

import branch_pickers.BranchPicker;
import data_structures.*;

import java.util.*;

public class VSIDSBranchPicker extends BranchPicker {
    private Map<Variable, Double> variableWeights;
    private int decayCounter;
    private double decayFactor;
    private int bump;
    private int decayInterval;

    public VSIDSBranchPicker(Set<Variable> variables, double decayFactor, int bump, int decayInterval) {
        super(variables);
        this.decayFactor = decayFactor;
        this.bump = bump;
        this.decayInterval = decayInterval;
        this.decayCounter = 0;
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
                // Found another candidate with the highest weight
                numHighestWeightedVariables++;
                if (Math.random() <= 1.0 / numHighestWeightedVariables) {
                    // Give equal chance for this candidate to be chosen
                    chosenHighestWeightedVariable = variable;
                }
            } else if (weight > highestWeight) {
                // Reset number of highest weighted variables and highest weight
                numHighestWeightedVariables = 1;
                highestWeight = weight;
                chosenHighestWeightedVariable = variable;

            }
        }
        return chosenHighestWeightedVariable;
    }

    @Override
    public void updateWeights(ConflictAnalyserResult result) {
        Set<Variable> variablesToUpdate = new HashSet<>();
        for (Literal literal : result.getLearntClause().getLiterals()) {
            variablesToUpdate.add(literal.getVariable());
        }
        if (updateOnResolvedClause()) {
            variablesToUpdate.addAll(result.getVariablesResolved());
            throw new IllegalArgumentException("YAS UPDATED");
        }

        for (Variable variable : variablesToUpdate) {
            double newWeight = variableWeights.get(variable) + bump;
            variableWeights.put(variable, newWeight);
        }
    }

    @Override
    public void decayWeights() {
        decayCounter++;
        if (decayCounter == decayInterval) {
            if (Math.random() < decayCounter) {
                for (Variable variable : variableWeights.keySet()) {
                    variableWeights.put(variable, variableWeights.get(variable) * decayFactor);
                }
            }
            decayCounter = 0;
        }
    }

    @Override
    public boolean updateOnResolvedClause() {
        return false;
    }

    private void initVariableWeights() {
        for (Variable variable : getVariables()) {
            variableWeights.put(variable, 0.0);
        }
    }
}

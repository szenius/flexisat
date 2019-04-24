package data_structures;

import java.util.Set;

public class ConflictAnalyserResult {
    private int assertionLevel;
    private Clause learntClause;
    private Set<Variable> variablesResolved;

    public ConflictAnalyserResult(int assertionLevel, Clause learntClause, Set<Variable> variablesResolved) {
        this.assertionLevel = assertionLevel;
        this.learntClause = learntClause;
        this.variablesResolved = variablesResolved;
    }

    public int getAssertionLevel() {
        return assertionLevel;
    }

    public Clause getLearntClause() {
        return learntClause;
    }

    public Set<Variable> getVariablesResolved() {
        return variablesResolved;
    }
}

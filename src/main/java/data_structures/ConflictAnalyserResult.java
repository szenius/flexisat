package data_structures;

public class ConflictAnalyserResult {
    private int assertionLevel;
    private Clause learntClause;

    public ConflictAnalyserResult(int assertionLevel, Clause learntClause) {
        this.assertionLevel = assertionLevel;
        this.learntClause = learntClause;
    }

    public int getAssertionLevel() {
        return assertionLevel;
    }

    public Clause getLearntClause() {
        return learntClause;
    }
}

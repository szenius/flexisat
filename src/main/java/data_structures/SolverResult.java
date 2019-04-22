package data_structures;

public class SolverResult {
    boolean sat;
    private int numPickBranching;
    long timeTaken;

    public SolverResult(boolean sat, int numPickBranching, long timeTaken) {
        this.sat = sat;
        this.numPickBranching = numPickBranching;
        this.timeTaken = timeTaken;
    }

    public boolean isSat() {
        return sat;
    }

    public int getNumPickBranching() {
        return numPickBranching;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}

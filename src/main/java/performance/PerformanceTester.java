package performance;

public class PerformanceTester {
    int numPickBranchingVariablesCalled;
    long startTime;
    long endTime;

    public PerformanceTester() {
        this.numPickBranchingVariablesCalled = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void pickBranchingVariablesCalled() {
        this.numPickBranchingVariablesCalled++;
    }

    public int getNumPickBranchingVariablesCalled() {
        return this.numPickBranchingVariablesCalled;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getExecutionTimeInMilliSeconds() {
        return this.endTime - this.startTime / 1000000;
    }

    public void printExecutionTime() {
        long timeElapsed = (this.endTime - this.startTime) / 1000000;
        System.out.println("Execution time: " + timeElapsed + " ms");
    }

    public void printNumPickBranchingVariablesCalled() {
        System.out.println("Number of pick branching variables called: " + this.numPickBranchingVariablesCalled);
    }
}

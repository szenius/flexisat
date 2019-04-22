package solvers;

import performance.PerformanceTester;

public interface Solver {
    boolean solveWithTimer(PerformanceTester perfTester);

    boolean solve(PerformanceTester perfTester);

}
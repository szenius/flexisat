package solvers;

import data_structures.Assignments;
import data_structures.Clauses;
import data_structures.Variable;
import performance.PerformanceTester;

import java.util.Set;

public interface Solver {
    boolean solveWithTimer(PerformanceTester perfTester);

    boolean solve(PerformanceTester perfTester);

}
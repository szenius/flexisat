package solvers;

import data_structures.Assignments;
import data_structures.Clauses;
import data_structures.Variable;
import performance.PerformanceTester;

import java.util.Set;

public interface Solver {
    boolean solveWithTimer(Clauses clauses, Set<Variable> variables, Assignments assignments, int decisionLevel,
                           PerformanceTester perfTester);

    boolean solve(Clauses clauses, Set<Variable> variables, Assignments assignments, int decisionLevel,
                  PerformanceTester perfTester);

}
import data_structures.*;
import parser.Parser;
import performance.PerformanceTester;
import solvers.CDCLSolver;
import solvers.Solver;

import java.util.Set;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // TODO: if this gets complicated, we can define an object class for input args
        Parser parser = new Parser();
        parser.parse(filename);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Assignments assignments = new Assignments(parser.getVarIds());
        CDCLSolver solver = new CDCLSolver(CDCLSolver.PickBranchingVariableHeuristics.TWO_LITERALS_CLAUSE);
        PerformanceTester perfTester = new PerformanceTester();

        boolean isSat = solver.solve(clauses, variables, assignments, 0, perfTester);
        if (isSat) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}
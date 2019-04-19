import data_structures.*;
import parser.Parser;
import performance.PerformanceTester;
import solvers.CDCLSolver;

import java.util.Set;

class Main {
    public static void main(String[] args) {
        String filename = args[0];

        // Parse input file
        Parser parser = new Parser();
        parser.parse(filename);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();

        // Run solver
        PerformanceTester perfTester = new PerformanceTester();
        CDCLSolver solver = new CDCLSolver(clauses, variables);
        boolean isValid = solver.solve(perfTester);
        if (isValid) {
            System.out.println("VALID");
        } else {
            System.out.println("UNSAT");
        }
    }
}

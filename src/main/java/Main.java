import data_structures.*;
import parser.Parser;
import performance.PerformanceTester;
import solvers.CDCLSolver;
import solvers.CDCLSolver2;

import java.util.Set;

class Main {
    public static void main(String[] args) {
        String filename = args[0];

        // Parse input file
        Parser parser = new Parser();
        parser.parse(filename);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Assignments assignments = new Assignments(parser.getVarIds());

        // Run solver v1
//        PerformanceTester perfTester = new PerformanceTester();
//        CDCLSolver solver = new CDCLSolver();
//        boolean isSat = solver.solve(clauses, variables, assignments, 0, perfTester);

        // Run solver 2
        CDCLSolver2 solver = new CDCLSolver2(clauses, variables, new Assignments2());
        boolean isValid = solver.solve();

        if (isValid) {
            System.out.println("VALID");
        } else {
            System.out.println("UNSAT");
        }
    }
}

import data_structures.*;
import parser.Parser;
import solvers.CDCLSolver;
import solvers.Solver;

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
        Solver solver = new CDCLSolver(clauses, variables);
        SolverResult result = solver.solve();
        if (result.isSat()) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}

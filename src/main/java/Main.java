import data_structures.*;
import parser.Parser;
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
        CDCLSolver solver = new CDCLSolver(clauses, variables);
        boolean sat = solver.solve();
        if (sat) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}

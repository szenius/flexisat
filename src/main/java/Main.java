import data_structures.*;
import parser.Parser;
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
//        CDCLSolver solver = new CDCLSolver();
//        if (solver.solve(clauses, variables, assignments, 0)) {
//            System.out.println("VALID");
//        } else {
//            // Print test to make sure that UNSAT assignments get added into our Clauses as part of the CDCL algorithm.
//            for (Clause clause : clauses.getClauses()) {
//                for (Literal literal : clause.getLiterals()) {
//                    System.out.println(literal.getVariable().getId());
//                    System.out.println(literal.isNegated());
//                }
//                System.out.println("~~~~~~~~~~~~~");
//            }
//            System.out.println("UNSAT");
//        }

        // Run solver 2
        CDCLSolver2 solver = new CDCLSolver2(clauses, variables, new Assignments2());
        if (solver.solve()) {
            System.out.println("VALID");
        } else {
            System.out.println("UNSAT");
        }
    }
}

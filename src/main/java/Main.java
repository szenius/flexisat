import data_structures.*;
import parser.Parser;
import solvers.CDCLSolver;

import java.util.Set;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // TODO: if this gets complicated, we can define an object class for input args
        Parser parser = new Parser();
        parser.parse(filename);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Assignments assignments = new Assignments(parser.getVarIds());
        CDCLSolver solver = new CDCLSolver();
        if (solver.solve(clauses, variables, assignments, 0)) {
            System.out.println("VALID");
        } else {
            // Print test to make sure that UNSAT assignments get added into our Clauses as part of the CDCL algorithm.
            for (Clause clause : clauses.getClauses()) {
                for (Literal literal : clause.getLiterals()) {
                    System.out.println(literal.getVariable().getId());
                    System.out.println(literal.isNegated());
                }
                System.out.println("~~~~~~~~~~~~~");
            }
            System.out.println("UNSAT");
        }
    }
}

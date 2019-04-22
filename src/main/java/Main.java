import data_structures.*;
import parser.Parser;
import solvers.CDCLSolver;
import solvers.Solver;

class Main {
    public static void main(String[] args) {
        Parser parser = new Parser(args);
        Solver solver = new CDCLSolver(parser);
        SolverResult result = solver.solve();
        if (result.isSat()) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}

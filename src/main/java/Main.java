import data_structures.*;
import parser.Parser;
import solvers.CDCLSolver;
import solvers.Solver;

class Main {
    public static SolverResult run(String[] args) {
        Parser parser = new Parser(args);
        Solver solver = new CDCLSolver(parser);
        return solver.solve();
    }

    public static void main(String[] args) {
        SolverResult result = run(args);
        if (result.isSat()) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}

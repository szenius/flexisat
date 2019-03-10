import data_structures.Assignment;
import data_structures.Clauses;
import parser.Parser;
import solvers.CDCLSolver;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // TODO: if this gets complicated, we can define an object class for input args
        Parser parser = new Parser();
        parser.parse(filename);
        Clauses clauses = parser.getClauses();
        Assignment assignment = new Assignment(parser.getVarIds());
        CDCLSolver solver = new CDCLSolver();
        if (solver.solve(clauses, assignment, 0)) {
            System.out.println("SAT");
        } else {
            System.out.println("UNSAT");
        }
    }
}

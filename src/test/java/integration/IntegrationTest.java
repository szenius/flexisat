package integration;

import data_structures.Assignments;
import data_structures.Clauses;
import data_structures.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import solvers.CDCLSolver;
import solvers.Solver;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    @Test
    @DisplayName("Runs a few tests on the SAT Solver to make sure that formulas that " +
            "are supposed to return SAT returns SAT.")
    void satCDCLSolverTest() {
        Solver solver = new CDCLSolver();
        String[] satTestInputs = {"input/input1.cnf"};
        for (String testInput : satTestInputs) {
            boolean sat = runSatSolverTest(testInput, solver);
            assertTrue(sat);
        }
    }

    @Test
    @DisplayName("Runs a few tests on the SAT Solver to make sure that formulas that " +
            "are supposed to return UNSAT returns UNSAT.")
    void unsatCDCLSolverTest() {
        Solver solver = new CDCLSolver();
        String[] unsatTestInputs = {"input/input2.cnf"};
        for (String testInput : unsatTestInputs) {
            boolean sat = runSatSolverTest(testInput, solver);
            assertFalse(sat);
        }
    }

    boolean runSatSolverTest(String testInput, Solver solver) {
        Parser parser = new Parser();
        parser.parse(testInput);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Assignments assignments = new Assignments(parser.getVarIds());
        return solver.solve(clauses, variables, assignments, 0);
    }


}
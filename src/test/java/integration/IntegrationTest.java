package integration;

import data_structures.Assignments;
import data_structures.Clauses;
import data_structures.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import performance.PerformanceTester;
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
        String[] satTestInputs = {
                "input/valid_input1.cnf",
                "input/valid_input2.cnf",
                "input/valid_input3.cnf"};
        for (String testInput : satTestInputs) {
            boolean sat = runSatSolverTest(testInput);
            assertTrue(sat);
        }
    }
    

    @Test
    @DisplayName("Runs a few tests on the SAT Solver to make sure that formulas that " +
            "are supposed to return UNSAT returns UNSAT.")
    void unsatCDCLSolverTest() {
        String[] unsatTestInputs = {
                "input/unsat_input1.cnf"};
        for (String testInput : unsatTestInputs) {
            boolean sat = runSatSolverTest(testInput);
            assertFalse(sat);
        }
    }

    /*
                    "input/unsat_input2.cnf",
                "input/unsat_input3.cnf"
     */


    boolean runSatSolverTest(String testInput) {
        Parser parser = new Parser();
        parser.parse(testInput);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Solver solver = new CDCLSolver(clauses, variables);
        PerformanceTester perfTester = new PerformanceTester();

        return solver.solveWithTimer(perfTester);
    }
}

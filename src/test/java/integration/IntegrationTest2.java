package integration;

import data_structures.Clauses;
import data_structures.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import solvers.CDCLSolver2;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest2 {


    @Test
    @DisplayName("VALID examples test")
    void testValidCNF() {
        String[] validTestFilenames = {
                "input/valid_input1.cnf",
                "input/valid_input2.cnf",
                "input/valid_input3.cnf"};
        for (String filename : validTestFilenames) {
            assertTrue(runSatSolverTest(filename), "Returned UNSAT for VALID test case " + filename);
        }
    }

    @Test
    @DisplayName("UNSAT examples test")
    void testUnsatCNF() {
        String[] unsatTestFilenames = {
                "input/unsat_input1.cnf",
                "input/unsat_input2.cnf",
                "input/unsat_input3.cnf"};
        for (String filename : unsatTestFilenames) {
            assertFalse(runSatSolverTest(filename), "Returned VALID for UNSAT test case " + filename);
        }
    }

    //@Test
    void test() {
        runSatSolverTest("input/default.cnf");
    }


    boolean runSatSolverTest(String testInput) {
        Parser parser = new Parser();
        parser.parse(testInput);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        CDCLSolver2 solver = new CDCLSolver2(clauses, variables);
        return solver.solve();
    }

}

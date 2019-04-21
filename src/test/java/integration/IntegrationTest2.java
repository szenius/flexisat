package integration;

import data_structures.Clauses;
import data_structures.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import solvers.CDCLSolver2;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest2 {

    private static final String UNSAT_DIRECTORY_PATH = "input/unsat/";
    private static final String SAT_DIRECTORY_PATH = "input/sat/";

    @Test
    @DisplayName("VALID examples test")
    void testValidCNF() {
        File satDir = new File(SAT_DIRECTORY_PATH);
        File[] satFiles = satDir.listFiles();
        for (File file : satFiles) {
            assertTrue(runSatSolverTest(file.getAbsolutePath()), "Returned UNSAT for VALID test case " + file.getName());
            System.out.println("=======================");
        }
    }

    @Test
    @DisplayName("UNSAT examples test")
    void testUnsatCNF() {
        File unsatDir = new File(UNSAT_DIRECTORY_PATH);
        File[] unsatFiles = unsatDir.listFiles();
        for (File file : unsatFiles) {
            assertFalse(runSatSolverTest(file.getAbsolutePath()), "Returned VALID for UNSAT test case " + file.getName());
            System.out.println("=======================");
        }
    }

    @Test
    @DisplayName("Single example test")
    void testSingleExample() {
        assertTrue(runSatSolverTest("input/sat/uf20-0117.cnf"), "Returned UNSAT for SAT test case " + "uf20-0117.cnf");
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

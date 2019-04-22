package integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import solvers.CDCLSolver;

import java.io.File;
import java.util.*;

import static branch_pickers.BranchPickerType.SEQ;
import static conflict_analysers.ConflictAnalyserType.UIP;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {
    // NOTE: if you want to test against full suite of test files, change the following to false.
    private static final boolean QUICK_TESTS_MODE = true;
    private static final int QUICK_TESTS_SIZE = 10;

    private static final String UNSAT_DIRECTORY_PATH = "input/unsat/";
    private static final String SAT_DIRECTORY_PATH = "input/sat/";

    @Test
    @DisplayName("SAT tests")
    public void testSatCNF() {
        File satDir = new File(SAT_DIRECTORY_PATH);
        List<File> satFiles = getTestFiles(satDir);
        for (File file : satFiles) {
            assertTrue(runSatSolverTest(file.getAbsolutePath()), "Returned UNSAT for SAT test case " + file.getName());
            System.out.println("=======================");
        }
    }

    @Test
    @DisplayName("UNSAT tests")
    public void testUnsatCNF() {
        File unsatDir = new File(UNSAT_DIRECTORY_PATH);
        List<File> unsatFiles = getTestFiles(unsatDir);
        for (File file : unsatFiles) {
            assertFalse(runSatSolverTest(file.getAbsolutePath()), "Returned VALID for UNSAT test case " + file.getName());
            System.out.println("=======================");
        }
    }

    private List<File> getTestFiles(File directory) {
        List<File> testFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));
        if (QUICK_TESTS_MODE) {
            Collections.shuffle(testFiles);
            testFiles = testFiles.subList(0, QUICK_TESTS_SIZE);
        }
        return testFiles;
    }

    boolean runSatSolverTest(String testInput) {
        Parser parser = new Parser(new String[]{testInput, SEQ.getType(), UIP.getType()});
        CDCLSolver solver = new CDCLSolver(parser);
        return solver.solve().isSat();
    }

}

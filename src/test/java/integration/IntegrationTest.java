package integration;

import branch_pickers.BranchPickerType;
import conflict_analysers.ConflictAnalyserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.Parser;
import solvers.CDCLSolver;

import java.io.File;
import java.util.*;

import static branch_pickers.BranchPickerType.SEQ;
import static conflict_analysers.ConflictAnalyserType.SINGLE_UIP;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {
    // NOTE: if you want to test against full suite of test files, change the following to false.
    private static final boolean QUICK_TESTS_MODE = true;
    private static final int QUICK_TESTS_SIZE = 1;

    private static final String UNSAT_DIRECTORY_PATH = "input/unsat/";
    private static final String SAT_DIRECTORY_PATH = "input/sat/";

    @Test
    @DisplayName("SAT tests")
    void testSatCNF() {
        File satDir = new File(SAT_DIRECTORY_PATH);
        List<File> satFiles = getTestFiles(satDir);
        for (File file : satFiles) {
            runSatSolverTests(file.getAbsolutePath(), true);
            System.out.println("=======================");
        }
    }

    @Test
    @DisplayName("UNSAT tests")
    void testUnsatCNF() {
        File unsatDir = new File(UNSAT_DIRECTORY_PATH);
        List<File> unsatFiles = getTestFiles(unsatDir);
        for (File file : unsatFiles) {
            runSatSolverTests(file.getAbsolutePath(), false);
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

    private void runSatSolverTests(String testInput, boolean expectedOutput) {
        // Try all types of branch picking
        for (BranchPickerType branchPickerType : BranchPickerType.values()) {
            assertEquals(expectedOutput, runSatSolverTest(testInput, branchPickerType.getType(), SINGLE_UIP.getType()), "Wrong output for " + testInput + " using " + branchPickerType.getType() + " as pick branching");
        }

        // Try all types of conflict analysers
        for (ConflictAnalyserType conflictAnalyserType : ConflictAnalyserType.values()) {
            assertEquals(expectedOutput, runSatSolverTest(testInput, SEQ.getType(), conflictAnalyserType.getType()), "Wrong output for " + testInput + " using " + conflictAnalyserType.getType() + " as conflict analysis");
        }
    }

    private boolean runSatSolverTest(String testInput, String branchPickerType, String conflictAnalyserType) {
        Parser parser = new Parser(new String[]{testInput, branchPickerType, conflictAnalyserType});
        CDCLSolver solver = new CDCLSolver(parser);
        return solver.solve().isSat();
    }

}

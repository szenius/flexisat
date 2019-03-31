package integration;

import branching_heuristics.PickBranchingVariableHeuristic;
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
        Solver solver = new CDCLSolver(CDCLSolver.PickBranchingVariableHeuristics.RANDOM);
        String[] satTestInputs = {
                "input/sat_input1.cnf",
                "input/sat_input2.cnf",
                "input/sat_input3.cnf"};
        for (String testInput : satTestInputs) {
            boolean sat = runSatSolverTest(testInput, solver);
            assertTrue(sat);
        }
    }

    @Test
    @DisplayName("Runs a few tests on the SAT Solver to make sure that formulas that " +
            "are supposed to return UNSAT returns UNSAT.")
    void unsatCDCLSolverTest() {
        Solver solver = new CDCLSolver(CDCLSolver.PickBranchingVariableHeuristics.RANDOM);
        String[] unsatTestInputs = {
                "input/unsat_input1.cnf",
                "input/unsat_input2.cnf",
                "input/unsat_input3.cnf"};
        for (String testInput : unsatTestInputs) {
            boolean sat = runSatSolverTest(testInput, solver);
            assertFalse(sat);
        }
    }

    @Test
    @DisplayName("Runs a few tests on the SAT Solver for performance testing of the two literals clause heuristic.")
    void satCDCLSolverTwoLiteralClauseHeuristicsTest() {
        Solver solver = new CDCLSolver(CDCLSolver.PickBranchingVariableHeuristics.TWO_LITERALS_CLAUSE);
        String[] satTestInputs = {
                "input/sat_input1.cnf",
                "input/unsat_input1.cnf"};
        for (String testInput : satTestInputs) {
            boolean sat = runSatSolverTest(testInput, solver);
        }
    }

    boolean runSatSolverTest(String testInput, Solver solver) {
        Parser parser = new Parser();
        parser.parse(testInput);
        Clauses clauses = parser.getClauses();
        Set<Variable> variables = parser.getVariables();
        Assignments assignments = new Assignments(parser.getVarIds());
        PerformanceTester perfTester = new PerformanceTester();

        return solver.solveWithTimer(clauses, variables, assignments, 0, perfTester);
    }
}

package solvers;

import data_structures.Assignment;
import data_structures.Clauses;

interface Solver {
    boolean solve(Clauses clauses, Assignment assignment, int decisionLevel);
}
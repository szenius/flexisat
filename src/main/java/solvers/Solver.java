package solvers;

import data_structures.Assignment;
import data_structures.Clauses;
import data_structures.Variable;

import java.util.Set;

interface Solver {
    boolean solve(Clauses clauses, Set<Variable> variables, Assignment assignment, int decisionLevel);
}
package solvers;

import data_structures.Formula;
import data_structures.Variable;

import java.util.List;

interface Solver {
    boolean solve(Formula form, List<Variable> vars);
}
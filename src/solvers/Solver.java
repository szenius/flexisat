package solvers;

interface Solver {
    public boolean solve(Formula form, List<Variable> vars);
}
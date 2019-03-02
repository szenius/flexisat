package data_structures;

public class Literal {
    private Variable variable;
    private boolean isNegated;

    public Literal(Variable variable, boolean isNegated) {
        this.variable = variable;
        this.isNegated = isNegated;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public boolean isNegated() {
        return this.isNegated;
    }

    /**
     * Does a xor on assignment and literal negation to evaluate
     * a literal's value.
     * @param assignment
     * @return evaluation of this literal with its assignment
     */
    public boolean isTrue(boolean assignment) {
        return this.isNegated ^ assignment;
    }
}
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
     *
     * @param assignment
     * @return evaluation of this literal with its assignment
     */
    public boolean getValue(boolean assignment) {
        return this.isNegated ^ assignment;
    }

    @Override
    public String toString(){
        if (this.isNegated()) {
            return "-" + Integer.toString(this.getVariable().getId());
        } else {
            return Integer.toString(this.getVariable().getId());
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Literal literal = (Literal) obj;
        return this.variable == literal.getVariable() && this.isNegated == literal.isNegated();
    }
}
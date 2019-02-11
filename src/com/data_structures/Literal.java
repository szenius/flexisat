package com.data_structures;

class Literal {
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
}
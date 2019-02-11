package com.data_structures;

class Variable {
    private int id;
    private int value;
    private int decisionLevel;
    private Variable ancestor;

    public Variable(int id) {
        this.id = id;
        this.value = -1;
        this.decisionLevel = -1;
        this.ancestor = null;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public void setAncestor(Variable ancestor) {
        this.ancestor = ancestor;
    }

    public int getId() {
        return this.id;
    }
    
    public int getValue() {
        return this.value;
    }

    public int getDecisionLevel() {
        return this.decisionLevel;
    }

    public Variable getAncestor() {
        return this.ancestor;
    }
}
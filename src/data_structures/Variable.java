package data_structures;

public class Variable {
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


    @Override
    public int hashCode(){
        // note: Not sure if this will cause any errors in the future.
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Variable other = (Variable) obj;
        if (other.getId() == this.getId()) {
            return true;
        }
        return false;
    }
}
package data_structures;

public class Variable {
    private int id;
    // For bayesian network only
    private String bayesianId;
    // TODO: probably have a better name for this
    private boolean happens;

    public Variable(int id) {
        this.id = id;
    }


    public Variable(String id, boolean happens) {
        this.bayesianId = id;
        this.happens = happens;
    }

    public int getId() {
        return this.id;
    }

    public String getBayesianId() {
        return this.bayesianId;
    }

    public boolean isHappen() {
        return this.happens;
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
        if (other.getBayesianId() == null && this.getBayesianId() != null ||
            other.getBayesianId() != null && this.getBayesianId() == null) {
            return false;
        }
        if (other.getBayesianId() == this.getBayesianId() && other.isHappen() == this.isHappen()) {
            return true;
        }
        return false;
    }
}
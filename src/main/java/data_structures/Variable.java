package data_structures;

public class Variable {
    private int id;

    public Variable(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
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
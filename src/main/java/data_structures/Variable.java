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
        return String.valueOf(getId()).hashCode();
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
        Variable other = (Variable) obj;
        return this.getId() == other.getId();
    }
}
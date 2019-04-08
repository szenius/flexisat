package data_structures;

import java.util.List;

public class BayesianClique {

    List<Integer> variables;
    float[][] functionTable;

    public BayesianClique(List<Integer> variables) {
        this.variables = variables;
        double totalEntries = Math.pow(2, variables.size());
        this.functionTable = new float[2][(int)totalEntries];
    }

    public void setFunctionTable(float[][] functionTable) {
        this.functionTable = functionTable;
    }

}

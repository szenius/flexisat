package bayesian_encoder;

import data_structures.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BayesianEncoder {

    List<BayesianVariable> variables;
    Clauses formula;
    enum VariableType{
        INDICATOR, PARAMETER
    }

    // Variables coding scheme:
    // Example:
    // 3 variables
    // a, b, c
    // Ia1 = 1, Ia2 = 2
    // ... Ian = 2n-1, 2n
    // Ordering of parameters will depend on the way it is specified in the Cliques.
    // Pa1 = 2n+1, Pa2 = 2n+2, Pb1 = 2n+3, Pb2 = 2n+4
    // Pa1b1c1 = 2n+5, Pa1b1c2 = 2n+6 ... ,Pa2b2c2 = 2n+12
    public void encodeBayesianQueryIntoCNF(int numVariables,
                                           List<BayesianClique> cliques, Map<Integer,Integer> evidence) {
        createVariables(numVariables);
        String fileName = new String("test.cnf");
        // Create CNF file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            createCNFHeaders(writer, numVariables, cliques);
            createTypeOneConstraints(writer, numVariables);
            createTypeTwoConstraints(writer, numVariables, cliques);

            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates the CNF that fulfils Type 1 constraints. Only 1 indicator variable allowed.
     */
    private void createTypeOneConstraints(BufferedWriter writer, int numVariables) throws IOException{
        if (numVariables == 0) {
            return;
        }
        for (int i = 0 ; i < numVariables; i++) {
            // Ia1 -> Ia2
            String rightImplication = "-" + 2*i + " " + 2*i+1 + " " + 0 + "\n";
            writer.write(rightImplication);
            // Ia2 -> Ia1
            String leftImplication = "-" + 2*i+1 + " " + 2*i + " " + 0 + "\n";
            writer.write(leftImplication);
        }
    }

    private void createTypeTwoConstraints(BufferedWriter writer,
                                          int numVariables, List<BayesianClique> cliques) throws IOException {
        int parameterVariableID = 2 * numVariables + 1;
        for (BayesianClique clique : cliques) {
            List<Integer> variables = clique.getVariables();
            for (int variable : variables) {
                //
                String rightImplication = "-" +  parameterVariableID + " " + 2*variable + " " + 0 + "\n";
                writer.write(rightImplication);
            }
        }
    }

    private void createCNFHeaders(BufferedWriter writer, int numVariables, List<BayesianClique> cliques) {

    }


    public Clauses getFormula() {
        return this.formula;
    }



    /**
     * We will create 2 data_structure Variables for each variable input.
     * 1 will be happen and 1 will be not happen, similar to a1 and a2 in lecture notes.
     * @param numVariables
     */
    private void createVariables(int numVariables) {
        this.variables = new ArrayList<>();
        for (int i = 0 ; i < numVariables; i++) {
            BayesianVariable variableOne = new BayesianVariable(i, 1, VariableType.INDICATOR);
            BayesianVariable variableTwo = new BayesianVariable(i, 2, VariableType.INDICATOR);
            this.variables.add(variableOne);
            this.variables.add(variableTwo);
        }
    }



    /**
     * TODO: Very inefficient! Better to store it in a Map with var id to variable for faster access.
     * @param variableId
     * @param isHappen
     * @return
     */
    private Variable findVariable(int variableId, boolean isHappen) {
        for (Variable variable : this.variables) {
            if (variable.getId() == variableId &&
                    variable.isHappen() == isHappen) {
                return variable;
            }
        }
        return null;
    }

    /**
     * This is variable which can either be an Indicative Variable or Pr
     */
    public class BayesianVariable {
        int id;
        // According to the convention taught in class, 1 = true and 2 = false.
        int type;
        VariableType variableType;

        private BayesianVariable(int id, int type, VariableType variableType) {
            this.id = id;
            this.type = type;
            this.variableType = variableType;
        }

        public VariableType getVariableType() {
            return this.variableType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            BayesianVariable other = (BayesianVariable) obj;
            if (other.id == this.id && other.type == this.type && other.variableType == this.variableType)
                return true;
            return false;
        }

        @Override
        public String toString() {
            String firstLetter;
            firstLetter = this.variableType == VariableType.INDICATOR ? "I" : "P";
            return firstLetter + this.id + type;
        }
    }

    /**
     * TODO: MIGHT NOT NEED THIS.
     */
    public class Literal {
        Variable variable;
        boolean isNegated;

        public Literal(Variable variable, boolean isNegated) {
            this.variable = variable;
            this.isNegated = isNegated;
        }

        public boolean isNegated() {
            return this.isNegated;
        }

    }


}

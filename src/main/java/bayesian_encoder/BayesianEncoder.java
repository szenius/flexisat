package bayesian_encoder;

import data_structures.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BayesianEncoder {
    
    String CNF_ENDER = " 0\n";

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
                                           List<BayesianClique> cliques, Map<Integer,Boolean> evidence) {
        String fileName = new String("test.cnf");
        // Create CNF file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            createCNFHeaders(writer, numVariables, cliques, evidence);
            createTypeOneConstraints(writer, numVariables);
            createTypeTwoConstraints(writer, numVariables, cliques);
            createHints(writer, evidence);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createCNFHeaders(BufferedWriter writer, int numVariables, List<BayesianClique> cliques,
                                  Map<Integer, Boolean> evidence) throws IOException{
        int totalNumVariables = 0;
        int totalNumClauses = 0;
        // Indicator variables
        totalNumVariables += 2 * numVariables;
        totalNumClauses += 2 * numVariables;
        // Parameter variables
        for (BayesianClique clique : cliques) {
            totalNumVariables += Math.pow(2,clique.getVariables().size());
            totalNumClauses += 2 * clique.getVariables().size() * Math.pow(2, clique.getVariables().size());
        }
        // Evidence
        totalNumClauses += evidence.size();
        writer.write("c SAT CNF BAYESIAN ENCODING \n");
        writer.write("p cnf " + totalNumVariables + " " +  totalNumClauses + "\n");
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
            String rightImplication = "-" + 2*i + " " + 2*i+1 + CNF_ENDER;
            writer.write(rightImplication);
            // Ia2 -> Ia1
            String leftImplication = "-" + 2*i+1 + " " + 2*i + CNF_ENDER;
            writer.write(leftImplication);
        }
    }

    private void createTypeTwoConstraints(BufferedWriter writer,
                                          int numVariables, List<BayesianClique> cliques) throws IOException {
        int parameterVariableID = 2 * numVariables + 1;
        for (BayesianClique clique : cliques) {
            List<Integer> variables = clique.getVariables();
            double totalNumCombinations = Math.pow(2, variables.size());

            for (int i = 0 ; i < totalNumCombinations; i++) {
                // Get the bits of i to retrieve the value of indicator variable
                // If bit is true, we will use 2n*variable_value
                // If bit is false, we will use 2n*variable_value + 1
                boolean[] integerBits = getBitsOfInteger(variables.size(), i);
                for (int j = 0 ; j < integerBits.length; j++) {
                    // Right implication
                    // Px1x2x3.. -> Ix1
                    int indicatorVariable;
                    if (integerBits[j])
                        indicatorVariable = 2*j;
                    else {
                        indicatorVariable = 2*j+1;
                    }
                    String rightImplication = "-" + parameterVariableID + " " + indicatorVariable + CNF_ENDER;
                    writer.write(rightImplication);
                    String leftImplication = "-" + indicatorVariable + " " + parameterVariableID + CNF_ENDER;
                    writer.write(leftImplication);
                }
                parameterVariableID++;
            }
        }
    }

    private void createHints(BufferedWriter writer, Map<Integer,Boolean> evidence) throws IOException {
        for (Map.Entry<Integer, Boolean> entry : evidence.entrySet()) {
            int indicatorVariable;
            if (entry.getValue()) {
                indicatorVariable = 2 * entry.getKey();
            } else {
                indicatorVariable = 2 * entry.getKey() + 1;
            }
            writer.write(Integer.toString(indicatorVariable));
        }
    }

    // Returns a Small Endian array
    private boolean[] getBitsOfInteger (int numBits,int value) {
        boolean[] bitArray = new boolean[numBits];
        for (int i = numBits - 1; i >= 0; i --) {
            bitArray[i] = (value & (1 << i)) != 0;
        }
        return bitArray;
    }

}

package bayesian_encoder;

import data_structures.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClassBayesianEncoder extends BayesianEncoder{

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
    @Override
    public void encodeBayesianQueryIntoCNF(int numVariables,
                                           List<BayesianClique> cliques, Map<Integer,Boolean> evidence) {
        String fileNameCNFEncoding = "test_encoder.cnf";
        String fileNameWeights = "test_weights.txt";
        // Create CNF file
        try {
            BufferedWriter encodingWriter = new BufferedWriter(new FileWriter(fileNameCNFEncoding));
            BufferedWriter weightsWriter = new BufferedWriter(new FileWriter(fileNameWeights));

            createCNFHeaders(encodingWriter, weightsWriter, numVariables, cliques, evidence);
            createTypeOneConstraints(encodingWriter, weightsWriter, numVariables);
            createTypeTwoConstraints(encodingWriter, weightsWriter, numVariables, cliques);
            createHints(encodingWriter, evidence);

            encodingWriter.close();
            weightsWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createCNFHeaders(BufferedWriter encoderWriter, BufferedWriter weightsWriter,int numVariables, List<BayesianClique> cliques,
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
        encoderWriter.write("c SAT CNF BAYESIAN ENCODING \n");
        encoderWriter.write("p cnf " + totalNumVariables + " " +  totalNumClauses + "\n");
        weightsWriter.write("p " + totalNumVariables + "\n");
    }


    /**
     * Creates the CNF that fulfils Type 1 constraints. Only 1 indicator variable allowed.
     */
    private void createTypeOneConstraints(BufferedWriter encoderWriter, BufferedWriter weightsWriter, int numVariables) throws IOException{
        if (numVariables == 0) {
            return;
        }
        for (int i = 0 ; i < numVariables; i++) {
            // Ia1 -> Ia2
            String rightImplication = "-" + (2*i) + " " + (2*i+1) + CNF_ENDER;
            encoderWriter.write(rightImplication);
            // Ia2 -> Ia1
            String leftImplication = "-" + (2*i+1) + " " + (2*i) + CNF_ENDER;
            encoderWriter.write(leftImplication);

            // All indicator variables will have weights of 1
            weightsWriter.write("w " + (2*i) + " 1 0\n");
            weightsWriter.write("w -" + (2*i+1) + " 1 0\n");
        }
    }

    private void createTypeTwoConstraints(BufferedWriter encoderWriter, BufferedWriter weightsWriter,
                                          int numVariables, List<BayesianClique> cliques) throws IOException {
        int parameterVariableID = 2 * numVariables + 1;
        for (BayesianClique clique : cliques) {
            // The last variable is the Bayesian Node being implied.
            List<Integer> variables = clique.getVariables();
            double totalNumCombinations = Math.pow(2, variables.size());

            for (int i = 0 ; i < totalNumCombinations; i++) {
                // Get the bits of i to retrieve the value of indicator variable
                // If bit is true, we will use 2n*variable_value
                // If bit is false, we will use 2n*variable_value +
                if (variables.size() == 0) {
                    System.out.println("WEIRD");
                    System.exit(1);
                }
                // Bit Array is Big Endian : integerBits[0] is the Bayesian Node being implied.
                boolean[] integerBits = Helper.getBitsOfInteger(variables.size(), i);
                for (int j = 0 ; j < integerBits.length; j++) {
                    // Right implication
                    // Px1x2x3.. -> Ix1
                    int indicatorVariable;
                    // Mapping Integer bits to its respective Indicator Variable.
                    if (integerBits[j])
                        indicatorVariable = 2 * variables.get(integerBits.length - 1 - j);
                    else {
                        indicatorVariable = 2 * variables.get(integerBits.length - 1 - j + 1);
                    }
                    String rightImplication = "-" + parameterVariableID + " " + indicatorVariable + CNF_ENDER;
                    encoderWriter.write(rightImplication);
                    String leftImplication = "-" + indicatorVariable + " " + parameterVariableID + CNF_ENDER;
                    encoderWriter.write(leftImplication);
                }
                float val = findWeightValue(integerBits, clique.getFunctionTable());
                String literal = parameterVariableID + val + " 0\n";
                weightsWriter.write(literal);
                String negatedLiteral = "-" + parameterVariableID + " 1 0\n";
                weightsWriter.write(negatedLiteral);
                parameterVariableID++;
            }
        }
    }

    /**
     * findWeightValue maps a particular Parameter variable to its weight value
     * @param integerBits
     * @param functionTable
     * @return
     */
    private float findWeightValue(boolean[] integerBits, float[][] functionTable) {
        int secondIndex;
        // Bit Array is Big Endian : integerBits[0] is the Bayesian Node being implied.
        if (integerBits[0]) {
            secondIndex = 1;
        } else {
            secondIndex = 0;
        }
        int firstIndex = Helper.bitsToInteger(Arrays.copyOfRange(integerBits, 0, integerBits.length - 1));
        return functionTable[firstIndex][secondIndex];
    }

    private void createHints(BufferedWriter writer, Map<Integer, Boolean> evidence) throws IOException {
        for (Map.Entry<Integer, Boolean> entry : evidence.entrySet()) {
            int indicatorVariable;
            if (entry.getValue()) {
                indicatorVariable = 2 * entry.getKey();
            } else {
                indicatorVariable = 2 * entry.getKey() + 1;
            }
            writer.write(Integer.toString(indicatorVariable) + " 0\n");
        }
    }
}

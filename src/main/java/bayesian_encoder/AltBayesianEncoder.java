package bayesian_encoder;

import data_structures.BayesianClique;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AltBayesianEncoder {

    Map<Integer, Integer> bayesianVarIdToLiteralId;
    Map<Integer, Integer> bayesianVarIdToChanceId;

    public void encodeBayesianQueryIntoCNF(int numVariables,
                                           List<BayesianClique> cliques, Map<Integer,Boolean> evidence){
        String fileNameCNFEncoding = "test_encoder.cnf";
        String fileNameWeights = "test_weights.txt";
        // Create CNF file
        try {
            BufferedWriter encodingWriter = new BufferedWriter(new FileWriter(fileNameCNFEncoding));
            BufferedWriter weightsWriter = new BufferedWriter(new FileWriter(fileNameWeights));

            createCNFHeaders(encodingWriter, weightsWriter, numVariables, cliques, evidence);
            createConstraints(encodingWriter, weightsWriter, numVariables, cliques);
            createHints(encodingWriter, evidence);

            encodingWriter.close();
            weightsWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private void createConstraints(BufferedWriter encoderWriter, BufferedWriter weightsWriter,
                                   int numVariables, List<BayesianClique> cliques) throws IOException {
        // Identify all the source nodes. For Source Nodes, their literal represents the Chance Variable.
        // TODO: Might not need this at all.
        Set<Integer> sourceNodes = new HashSet<>();
        for (BayesianClique clique : cliques) {
            if (clique.getVariables().size() == 1){
                sourceNodes.add(clique.getVariables().get(0));
            }
        }

        // The starting offset for the all the subsequent chance nodes.
        int literalId = numVariables + 1;

        for (BayesianClique clique : cliques) {
            List<Integer> variables = clique.getVariables();
            // Source node. Can just write in the weights for its Chance Nodes.
            if (variables.size() == 1) {
                String positiveLiteralWeight = "w " + variables.get(0) + " " + clique.getFunctionTable()[0][1] + " 0\n";
                String negativeLiteralWeight = "w -" + variables.get(0) + " " + clique.getFunctionTable()[0][0] + " 0\n";
                weightsWriter.write(positiveLiteralWeight);
                weightsWriter.write(negativeLiteralWeight);
            } else {
                // Creating weights for State Nodes of variables. They should all be of weight 1.
                for (int var : variables) {
                    if (!sourceNodes.contains(var)) {
                        String positiveLiteralWeight = "w " + var + " 1 0\n";
                        String negativeLiteralWeight = "w -" + var + " 1 0\n";
                        weightsWriter.write(positiveLiteralWeight);
                        weightsWriter.write(negativeLiteralWeight);
                    }
                }

                // Create CLAUSES according to the clique.
                // Assumption: The last variable in the list is the Bayesian Node.
                // TODO: Can further optimise for those with values 1.
                int nodeId = variables.get(variables.size() - 1 );
                double numChanceNodes = Math.pow(2, variables.size() - 1);
                for (int chanceNodeId = 0 ; chanceNodeId < numChanceNodes; chanceNodeId++ ) {
                    // All the other nodes
                    boolean[] bits = Helper.getBitsOfInteger(variables.size() - 1, chanceNodeId);
                    // Left side of implication. All the State Nodes.
                    String leftSideImplication = "";
                    for (int j = 0 ; j < bits.length; j++) {
                        if (bits[j]) {
                            leftSideImplication += "-" + variables.get(j) + " ";
                        } else {
                            leftSideImplication += variables.get(j) + " ";
                        }
                    }
                    int literalIdOfChanceNode = literalId + chanceNodeId;
                    // Right side of implication. Chance node with State Node of current Bayesian Node.
                    // Will need 2 of this.
                    String rightSideImplicationOne = "-" + nodeId + " " + literalIdOfChanceNode + " 0\n";
                    String rightSideImplicationTwo = "-" + literalIdOfChanceNode + " " + nodeId + " 0\n";
                    encoderWriter.write(leftSideImplication + rightSideImplicationOne);
                    encoderWriter.write(leftSideImplication + rightSideImplicationTwo);
                }
                literalId += numChanceNodes;
            }
        }
    }



    private int getChanceVariableOffset(List<BayesianClique> cliques) {
        int offset = 0;
        for (BayesianClique clique : cliques) {
            if (clique.getVariables().size() != 1) {
                offset++;
            }
        }
        return offset;
    }


    private void createCNFHeaders(BufferedWriter encoderWriter, BufferedWriter weightsWriter, int numVariables, List<BayesianClique> cliques,
                                  Map<Integer, Boolean> evidence) throws IOException {

    }

    private void createHints(BufferedWriter writer, Map<Integer,Boolean> evidence) throws IOException {
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

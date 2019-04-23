package bayesian_encoder;

import data_structures.BayesianClique;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AltBayesianEncoder extends BayesianEncoder{

    private Set<Integer> sourceNodes;

    @Override
    public void encodeBayesianQueryIntoCNF(int numVariables,
                                           List<BayesianClique> cliques, Map<Integer,Boolean> evidence){
        String fileNameCNFEncoding = "test_encoder.cnf";
        String fileNameWeights = "test_weights.txt";
        // Create CNF file
        try {
            BufferedWriter encodingWriter = new BufferedWriter(new FileWriter(fileNameCNFEncoding));
            BufferedWriter weightsWriter = new BufferedWriter(new FileWriter(fileNameWeights));

            identifySourceNodes(cliques);

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

        // The starting offset for the all the subsequent chance nodes.
        int literalId = numVariables;

        // DEBUG
        System.out.println("Literal ID @ start" + literalId);

        for (BayesianClique clique : cliques) {
            List<Integer> variables = clique.getVariables();
            // Source node. Can just write in the weights for its Chance Nodes.
            if (variables.size() == 1) {
                String positiveLiteralWeight = "w " + variables.get(0) + " " + clique.getFunctionTable()[1][0] + " 0\n";
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
                    System.out.println("Literal ID being added: " + literalId);
                    // Right side of implication. Chance node with State Node of current Bayesian Node.
                    // Will need 2 of this.
                    String rightSideImplicationOne = "-" + nodeId + " " + literalIdOfChanceNode + " 0\n";
                    String rightSideImplicationTwo = "-" + literalIdOfChanceNode + " " + nodeId + " 0\n";
                    encoderWriter.write(leftSideImplication + rightSideImplicationOne);
                    encoderWriter.write(leftSideImplication + rightSideImplicationTwo);

                    // Create Weights for Chance Nodes according to clique.
                    String positiveLiteralWeight = "w " + literalIdOfChanceNode + " " +
                            clique.getFunctionTable()[1][chanceNodeId];
                    String negativeLiteralWeight = "w -" + literalIdOfChanceNode + " " +
                            clique.getFunctionTable()[0][chanceNodeId];
                    weightsWriter.write(positiveLiteralWeight);
                    weightsWriter.write(negativeLiteralWeight);
                }
                literalId += numChanceNodes;
            }
        }
    }


    private void createCNFHeaders(BufferedWriter encoderWriter, BufferedWriter weightsWriter, int numVariables, List<BayesianClique> cliques,
                                  Map<Integer, Boolean> evidence) throws IOException {
        // Initialised to number of State Variables
        int totalNumVariables = numVariables;
        int totalNumClauses = 0;

        // Total number of chance variables
        for (BayesianClique clique : cliques){
            if (clique.getVariables().size() > 1 ) {
                totalNumVariables += Math.pow(2, clique.getVariables().size() - 1);
                totalNumClauses += 2 * Math.pow(2, clique.getVariables().size() - 1);
            }
        }
        // Add clauses created by evidence
        totalNumClauses += evidence.size();

        encoderWriter.write("c SAT CNF BAYESIAN ENCODING \n");
        encoderWriter.write("p cnf " + totalNumVariables + " " +  totalNumClauses + "\n");
        weightsWriter.write("p " + totalNumVariables + "\n");
    }


    private void identifySourceNodes(List<BayesianClique> cliques) {
        this.sourceNodes = new HashSet<>();
        for (BayesianClique clique : cliques) {
            if (clique.getVariables().size() == 1){
                sourceNodes.add(clique.getVariables().get(0));
            }
        }
    }

    private void createHints(BufferedWriter encoderWriter, Map<Integer,Boolean> evidence) throws IOException {
        for (Map.Entry<Integer, Boolean> entry : evidence.entrySet()) {
            String indicatorVariable;
            // Adding constraints to the State Nodes
            if (entry.getValue()) {
                indicatorVariable = "-" + entry.getKey();
            } else {
                indicatorVariable = entry.getKey().toString();
            }
            encoderWriter.write(indicatorVariable + " 0\n");
        }
    }

}

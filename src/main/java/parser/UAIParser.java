package parser;

import data_structures.BayesianClique;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UAIParser {

    List<BayesianClique> cliques;

    public void parse(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            // First line: Should be BAYESIAN
            line = br.readLine();
            if (!line.equals("BAYES")) {
                System.out.println("Wrong file format.");
                System.exit(1);
            }
            // Second line: Num variables
            line = br.readLine();
            int numVariables = Integer.parseInt(line);

            // Third line: Cardinality of each variable
            // Since we are only doing Bayesian, the cardinality of each variable
            // should be 2.
            line = br.readLine();
            String[] cardinalities = line.split(" ");
            for (String cardinality: cardinalities) {
                if (Integer.parseInt(cardinality) != 2) {
                    System.out.println("Wrong file format for cardinality.");
                    System.exit(1);
                }
            }

            // Fourth line: Number of cliques in the problem
            line = br.readLine();
            int numCliques = Integer.parseInt(line);
            // We will add in the cliques in order. When parsing the function tables later on we will make use
            // of this assumption.
            List<BayesianClique> cliques = new ArrayList<>();
            for (int i = 0; i < numCliques; i++) {
                line = br.readLine();
                String[] scopeOfClique = line.split(" ");
                int numVariablesInClique = Integer.parseInt(scopeOfClique[0]);
                List<Integer> variablesInClique = new ArrayList<>();
                // TODO: Is there a more idiomatic way to do this in Java?
                for (int j = 0 ; j < numVariablesInClique; j++) {
                    int variable = Integer.parseInt(scopeOfClique[j+1]);
                }
                BayesianClique cliqueToAdd = new BayesianClique(variablesInClique);
                cliques.add(cliqueToAdd);
            }

            // Empty line
            line = br.readLine();

            // Parsing function tables
            // TODO: Currently assuming that the format is a 2 lines format where the first line is 0 for MSV
            //  (most significant variable) and second line is 1 for MSV.
            for (int i = 0 ; i < numCliques; i++) {
                // Number of entries given
                line = br.readLine();
                int numEntries = Integer.parseInt(line);
                float [][] variableFunctionValues = new float[2][numEntries/2];
                for (int j = 0 ; j < 2; j++) {
                    line = br.readLine();
                    String[] functions = line.split(" ");
                    if (numEntries / 2 != functions.length) {
                        System.out.println("Error in function table length.");
                        System.exit(1);
                    }
                    float[] functionValues = new float[functions.length];
                    // TODO: Check if the first space affects this.
                    // Converting each entry from string into float values.
                    for (int k = 0 ; k < functions.length ; k ++ ){
                        functionValues[k] = Float.parseFloat(functions[k]);
                    }
                    variableFunctionValues[j] = functionValues;
                }
                cliques.get(i).setFunctionTable(variableFunctionValues);
            }

            this.cliques = cliques;
            br.close();
            System.out.println("Parsing done!");


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

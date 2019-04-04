import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CNFGenerator {

    public static void main(String[] args) {
        String numVariables = args[0];
        String numClauses = args[1];
        if (numVariables.equals("") || numClauses.equals("")) {
            System.out.println("Error. We require 2 arguments for numVariables and numClauses respectively.");
            System.exit(1);
        }
        createCNFFile(numVariables, numClauses);
    }

    private static void createCNFFile(String numVariables, String numClauses) {
        // Lazy way of creating file name
        String fileName = numVariables + "var-" + numClauses + ".cnf";
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                String firstLine = "c Generated CNF file\n";
                String secondLine = "p cnf " + numVariables + " " + numClauses + "\n";
                writer.write(firstLine);
                writer.write(secondLine);
                for (int i = 0; i < Integer.parseInt(numClauses); i++) {
                    String line = generate3CNFClause(Integer.parseInt(numVariables));
                    writer.write(line);
                }
                writer.close();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error in creating file.");
            System.exit(1);
        }
    }

    /**
     * This function generates a string line of clause randomly with equal probabilities across the
     * total number of variables and a 0.5% chance that the literal will be negated.
     * @param numVariables
     * @return
     */
    private static String generate3CNFClause(int numVariables) {
        StringBuilder clause = new StringBuilder();
        for (int i = 0 ; i < 3 ; i++) {
            int variableToChoose = (int) (Math.random() * numVariables + 1);
            int isFalse = (int) (Math.random() * 2);
            if (isFalse == 0) {
                clause.append(Integer.toString(variableToChoose));
            } else {
                clause.append("-");
                clause.append(Integer.toString(variableToChoose));
            }
            if (i == 2) {
                clause.append(" 0\n");
            } else {
                clause.append(" ");
            }
        }
        return clause.toString();
    }
}

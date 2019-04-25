package cnf_generator;

public class RunCNFGenerator {
    public static void main(String[] args) {
        int[] testNumClauses = new int[]{250, 300, 350, 400, 450, 500};
        int[] testNumVariables = new int[]{100, 200, 300, 400, 500};

        for (int numVariables : testNumVariables) {
            for (int numClauses : testNumClauses) {
                CNFGenerator.main(new String[]{String.valueOf(numVariables), String.valueOf(numClauses)});
            }
        }
    }
}

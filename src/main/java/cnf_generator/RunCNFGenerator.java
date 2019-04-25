package cnf_generator;

public class RunCNFGenerator {
    public static void main(String[] args) {
        int[] testNumClauses = new int[]{200, 250, 300, 350, 400};
        int[] testNumVariables = new int[]{20, 50, 100, 300, 500};

        for (int numVariables : testNumVariables) {
            for (int numClauses : testNumClauses) {
                CNFGenerator.main(new String[]{String.valueOf(numVariables), String.valueOf(numClauses)});
            }
        }
    }
}

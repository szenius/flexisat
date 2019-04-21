import bayesian_encoder.BayesianEncoder;
import data_structures.BayesianClique;
import data_structures.Clauses;
import parser.UAIParser;

import java.util.List;
import java.util.Map;

public class BayesianMain {

    public static void main(String[] args) {
        String fileName = args[0];
        String evidenceFileName = args[1];

        UAIParser parser = new UAIParser();
        parser.parse(fileName);

        List<BayesianClique> cliques = parser.getCliques();
        parser.parseEvidence(evidenceFileName);
        Map<Integer, Integer> queryVariables = parser.getQueryValues();
        BayesianEncoder networkEncoder = new BayesianEncoder();

        networkEncoder.encodeBayesianQueryIntoCNF(parser.getNumVariables(),
                cliques, parser.getQueryValues());

        calculateWeights(parser.getCliques());
        createDimacsFile();



    }

    private static void createDimacsFile() {

    }

    private static void calculateWeights(List<BayesianClique> cliques) {

    }

}

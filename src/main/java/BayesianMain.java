import bayesian_encoder.AltBayesianEncoder;
import bayesian_encoder.BayesianEncoder;
import bayesian_encoder.ClassBayesianEncoder;
import data_structures.BayesianClique;
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
        Map<Integer, Boolean> queryVariables = parser.getQueryValues();
        BayesianEncoder networkEncoder = new ClassBayesianEncoder();

        networkEncoder.encodeBayesianQueryIntoCNF(parser.getNumVariables(),
                cliques, parser.getQueryValues());
    }

}

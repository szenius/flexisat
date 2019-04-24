import bayesian_encoder.AltBayesianEncoder;
import bayesian_encoder.BayesianEncoder;
import bayesian_encoder.ClassBayesianEncoder;
import data_structures.BayesianClique;
import parser.UAIParser;

import java.util.List;
import java.util.Map;

public class BayesianMain {

    public enum ENCODER {
        CLASS_ENCODER, ALT_ENCODER
    }

    public static void main(String[] args) {
        String fileName = args[0];
        String evidenceFileName = args[1];
        ENCODER encoderUsed = ENCODER.valueOf(args[2]);

        UAIParser parser = new UAIParser();
        parser.parse(fileName);

        List<BayesianClique> cliques = parser.getCliques();
        parser.parseEvidence(evidenceFileName);
        Map<Integer, Boolean> queryVariables = parser.getQueryValues();
        // Instantiate either the ClassBayesianEncoder or the AltBayesianEncoder
        BayesianEncoder encoder;
        switch (encoderUsed) {
            case CLASS_ENCODER:
                System.out.println("Using the encoding scheme taught in class...");
                encoder = new ClassBayesianEncoder();
                break;
            case ALT_ENCODER:
                System.out.println("Using an alternative encoding scheme...");
                encoder = new AltBayesianEncoder();
                break;
            default:
                System.out.println("Please input a legal encoder type.");
                System.exit(1);
        }

        BayesianEncoder networkEncoder = new ClassBayesianEncoder();

        networkEncoder.encodeBayesianQueryIntoCNF(parser.getNumVariables(),
                cliques, parser.getQueryValues());
        System.out.println("Finished encoding network in CNF.");
    }

}

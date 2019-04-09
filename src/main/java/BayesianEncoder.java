import data_structures.BayesianClique;
import parser.UAIParser;

import java.util.List;

public class BayesianEncoder {

    public static void main(String[] args) {
        String fileName = args[0];

        UAIParser parser = new UAIParser();
        parser.parse(fileName);

        List<BayesianClique> cliques = parser.getCliques();

    }

}

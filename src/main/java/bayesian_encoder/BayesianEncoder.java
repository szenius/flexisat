package bayesian_encoder;

import data_structures.BayesianClique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BayesianEncoder {

    public void encodeBayesianQueryIntoCNF(int numVariables,
                                           List<BayesianClique> cliques, Map<Integer,Boolean> evidence) {
    }

}
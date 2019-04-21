package bayesian_encoder;

import data_structures.BayesianClique;
import data_structures.Literal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightsCalculator {

    public void calculateWeights(List<BayesianClique> cliques) {
        String fileName = "test_weights.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

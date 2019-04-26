import branch_pickers.BranchPickerType;
import conflict_analysers.ConflictAnalyserType;
import data_structures.SolverResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Experiment {
    private static final Logger LOGGER = LoggerFactory.getLogger(Experiment.class);

    private static final String TEST_DIRECTORY = "input/test/";
    private static final BranchPickerType DEFAULT_BRANCH_PICKER_TYPE = BranchPickerType.CHAFF;
    private static final ConflictAnalyserType DEFAULT_CONFLICT_ANALYSER_TYPE = ConflictAnalyserType.SINGLE_UIP;

    private static final int NUM_REPEATS = 5;

    private static void run() throws IOException {
        File directory = new File(TEST_DIRECTORY);
        File[] testFiles = directory.listFiles();

        List<String> results = new ArrayList<>();
        for (File testFile : testFiles) {
            // Test different branching heuristics
            for (BranchPickerType branchPickerType : BranchPickerType.values()) {
                for (int i = 0; i < NUM_REPEATS; i++) {
                    SolverResult result = Main.run(new String[]{testFile.getAbsolutePath(), branchPickerType.getType(), DEFAULT_CONFLICT_ANALYSER_TYPE.getType()});
                    String resultString = buildResultString(testFile.getName(), result, branchPickerType, DEFAULT_CONFLICT_ANALYSER_TYPE);
                    LOGGER.info("COMPLETED: {}", resultString);
                    results.add(resultString);
                }
            }

            // Test different conflict analyser
            for (ConflictAnalyserType conflictAnalyserType : ConflictAnalyserType.values()) {
                for (int i = 0; i < NUM_REPEATS; i++) {
                    SolverResult result = Main.run(new String[]{testFile.getAbsolutePath(), DEFAULT_BRANCH_PICKER_TYPE.getType(), conflictAnalyserType.getType()});
                    String resultString = buildResultString(testFile.getName(), result, DEFAULT_BRANCH_PICKER_TYPE, conflictAnalyserType);
                    LOGGER.info("COMPLETED: {}", resultString);
                    results.add(resultString);
                }
            }
        }

        String resultString = results.stream().collect(Collectors.joining("\n"));
        System.out.println(resultString);

        BufferedWriter writer = new BufferedWriter(new FileWriter("results.csv"));
        writer.write(resultString);
        writer.close();
    }

    private static String buildResultString(String filename, SolverResult result, BranchPickerType branchPickerType, ConflictAnalyserType conflictAnalyserType) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(filename)
                .add(branchPickerType.getType())
                .add(conflictAnalyserType.getType())
                .add(String.valueOf(result.isSat()))
                .add(String.valueOf(result.getTimeTaken()))
                .add(String.valueOf(result.getNumPickBranching()));
        return joiner.toString();
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}

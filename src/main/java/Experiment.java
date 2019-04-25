import branch_pickers.BranchPickerType;
import conflict_analysers.ConflictAnalyserType;
import data_structures.SolverResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Experiment {
    private static final String TEST_DIRECTORY = "input/test/";
    private static final BranchPickerType DEFAULT_BRANCH_PICKER_TYPE = BranchPickerType.CHAFF;
    private static final ConflictAnalyserType DEFAULT_CONFLICT_ANALYSER_TYPE = ConflictAnalyserType.SINGLE_UIP;

    private static void run() throws IOException {
        File directory = new File(TEST_DIRECTORY);
        File[] testFiles = directory.listFiles();

        List<String> results = new ArrayList<>();
        for (File testFile : testFiles) {
            // Test different branching heuristics
            for (BranchPickerType branchPickerType : BranchPickerType.values()) {
                SolverResult result = Main.run(new String[]{testFile.getAbsolutePath(), branchPickerType.getType(), DEFAULT_CONFLICT_ANALYSER_TYPE.getType()});
                results.add(buildResultString(testFile.getName(), result, branchPickerType, DEFAULT_CONFLICT_ANALYSER_TYPE));
            }

            // Test different conflict analyser
            for (ConflictAnalyserType conflictAnalyserType : ConflictAnalyserType.values()) {
                SolverResult result = Main.run(new String[]{testFile.getAbsolutePath(), DEFAULT_BRANCH_PICKER_TYPE.getType(), conflictAnalyserType.getType()});
                results.add(buildResultString(testFile.getName(), result, DEFAULT_BRANCH_PICKER_TYPE, conflictAnalyserType));
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

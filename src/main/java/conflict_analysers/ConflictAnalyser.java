package conflict_analysers;

import data_structures.*;

public interface ConflictAnalyser {
    ConflictAnalyserResult learnClause(UnitResolutionResult conflict, Assignments assignments);
}

package parser;

import data_structures.Clause;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static branch_pickers.BranchPickerType.SEQ;
import static conflict_analysers.ConflictAnalyserType.SINGLE_UIP;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    @DisplayName("Parses a sat cnf.")
    void validParseTest() {
        String filePath = "input/sat/sat_input1.cnf";
        Parser testParser = new Parser(new String[]{filePath, SEQ.getType(), SINGLE_UIP.getType()});
        Set<Clause> listOfClause = testParser.getClauses().getClauses();

        assertEquals(2, listOfClause.size());
        for (Clause clause : listOfClause) {
            assertEquals(3, clause.getLiterals().size());
        }
    }
}

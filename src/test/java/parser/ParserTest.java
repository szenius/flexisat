package parser;

import data_structures.Clause;
import data_structures.Literal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    @DisplayName("Parses a valid cnf.")
    void validParseTest() {
        String filePath = "input/valid_input1.cnf";
        Parser testParser = new Parser();
        List<Clause> listOfClause = new ArrayList<>(testParser.parse(filePath).getClauses());

        assertEquals(2, listOfClause.size());
        for (Clause clause : listOfClause) {
            assertEquals(3, clause.getLiterals().size());
        }
    }
}

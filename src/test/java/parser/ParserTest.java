package parser;

import data_structures.Clause;
import data_structures.Literal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    @DisplayName("Correctly parses a String of clause into a Clause object.")
    void createClauseTest() {
        String line = "1 -3 2 0";
        Parser testParser = new Parser();
        try {
            Clause resultClause = testParser.createClause(line);
            List<Literal> literals = resultClause.getLiterals();

            assertEquals(3, literals.size());
            // Test 1
            assertEquals(1, literals.get(0).getVariable().getId());
            assertEquals(false, literals.get(0).isNegated());
            // Test -3
            assertEquals(3, literals.get(1).getVariable().getId());
            assertEquals(true, literals.get(1).isNegated());
            // Test 2
            assertEquals(2, literals.get(2).getVariable().getId());
            assertEquals(false, literals.get(2).isNegated());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

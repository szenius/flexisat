package parser;

import data_structures.Clause;
import data_structures.Literal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    @DisplayName("Parses a valid cnf.")
    void validParseTest() {
        String filePath = "input/sat_input1.cnf";
        Parser testParser = new Parser();
        List<Clause> listOfClause = new ArrayList<>(testParser.parse(filePath).getClauses());

        assertEquals(2, listOfClause.size());
        // Test first clause
        List<Literal> clauseOneLiterals = listOfClause.get(0).getLiterals();
        assertEquals(3, clauseOneLiterals.size());
        assertEquals(1, clauseOneLiterals.get(0).getVariable().getId());
        assertEquals(false, clauseOneLiterals.get(0).isNegated());
        assertEquals(3, clauseOneLiterals.get(1).getVariable().getId());
        assertEquals(true, clauseOneLiterals.get(1).isNegated());
        assertEquals(2, clauseOneLiterals.get(2).getVariable().getId());
        assertEquals(false, clauseOneLiterals.get(2).isNegated());

        // Test second clause
        List<Literal> clauseTwoLiterals = listOfClause.get(1).getLiterals();
        assertEquals(3, clauseTwoLiterals.size());
        assertEquals(2, clauseTwoLiterals.get(0).getVariable().getId());
        assertEquals(false, clauseTwoLiterals.get(0).isNegated());
        assertEquals(3, clauseTwoLiterals.get(1).getVariable().getId());
        assertEquals(false, clauseTwoLiterals.get(1).isNegated());
        assertEquals(1, clauseTwoLiterals.get(2).getVariable().getId());
        assertEquals(true, clauseTwoLiterals.get(2).isNegated());
    }
}

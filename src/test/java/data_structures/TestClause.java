package data_structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestClause {

    @Test
    @DisplayName("equals test")
    public void testEquals() {
        Clause clause1 = new Clause(new ArrayList<>(Arrays.asList(new Literal(new Variable(1), false), new Literal(new Variable(2), true))));
        Clause clause2 = new Clause(new ArrayList<>(Arrays.asList(new Literal(new Variable(1), true), new Literal(new Variable(2), true))));
        Clause clause3 = new Clause(new ArrayList<>(Arrays.asList(new Literal(new Variable(3), false), new Literal(new Variable(2), true))));

        assertEquals(clause1, clause1);
        assertEquals(clause2, clause2);
        assertEquals(clause3, clause3);
        assertNotEquals(clause1, clause2);
        assertNotEquals(clause1, clause3);
    }
}

package data_structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestLiteral {

    @Test
    @DisplayName("equals test")
    public void testEquals() {
        Literal literal1 = new Literal(new Variable(1), true);
        Literal literal1Neg = new Literal(new Variable(1), false);
        Literal literal2 = new Literal(new Variable(2), true);

        assertEquals(literal1, literal1);
        assertEquals(literal1Neg, literal1Neg);
        assertEquals(literal2, literal2);
        assertNotEquals(literal1, literal1Neg);
        assertNotEquals(literal1, literal2);
    }

}

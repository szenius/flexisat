package data_structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestVariable {

    @Test
    @DisplayName("equals test")
    public void testEquals() {
        Variable variable1 = new Variable(1);
        Variable variable2 = new Variable(2);

        assertEquals(variable1, variable1);
        assertEquals(variable2, variable2);
        assertNotEquals(variable1, variable2);
    }

}

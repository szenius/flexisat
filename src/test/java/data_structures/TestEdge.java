package data_structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestEdge {
    @Test
    @DisplayName("equals test")
    public void testEquals() {
        Edge edge1 = new Edge(new Node(new Variable(1), 1), new Node(new Variable(2), 1), new Clause(new ArrayList<>()));
        Edge edge2 = new Edge(new Node(new Variable(2), 1), new Node(new Variable(3), 1), new Clause(new ArrayList<>()));

        assertEquals(edge1, edge1);
        assertEquals(edge2, edge2);
        assertNotEquals(edge1, edge2);
    }

}

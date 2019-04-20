package data_structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestNode {

    @Test
    @DisplayName("equals test")
    public void testEquals() {
        Node node1 = new Node(new Variable(1), 0);
        Node node2 = new Node(new Variable(1), 1);
        Node node3 = new Node(new Variable(2), 0);

        assertEquals(node1, node1);
        assertEquals(node2, node2);
        assertEquals(node3, node3);
        assertNotEquals(node1, node2);
        assertNotEquals(node1, node3);
    }

}

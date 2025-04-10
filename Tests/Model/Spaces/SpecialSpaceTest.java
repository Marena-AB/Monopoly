package Model.Spaces;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the SpecialSpace class
 */
public class SpecialSpaceTest {

    private SpecialSpace specialSpace;

    @Before
    public void setUp() {
        specialSpace = new SpecialSpace("Community Chest", 2, "Community Chest");
    }

    @Test
    public void testConstructor() {
        assertEquals("Community Chest", specialSpace.getName());
        assertEquals(2, specialSpace.getPosition());
        assertEquals("Community Chest", specialSpace.getType());
    }

    @Test
    public void testGetType() {
        assertEquals("Community Chest", specialSpace.getType());

        // Create another special space with different type
        SpecialSpace taxSpace = new SpecialSpace("Income Tax", 4, "Tax");
        assertEquals("Tax", taxSpace.getType());
    }

    @Test
    public void testInheritance() {
        // Verify SpecialSpace is a subclass of Space
        assertTrue(specialSpace instanceof Space);
    }

    @Test
    public void testPlayerOnSpecialSpace() {
        // Capture System.out to verify output
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            specialSpace.playerOnSpecialSpace();
            assertTrue(outContent.toString().contains("Player landed on Community Chest"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
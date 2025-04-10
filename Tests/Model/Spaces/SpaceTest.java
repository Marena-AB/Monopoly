package Model.Spaces;

import Model.Board.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the abstract Space class
 * Tests the concrete implementations of Space methods
 */
public class SpaceTest {

    // Concrete subclass of Space for testing
    private static class TestSpace extends Space {
        public TestSpace(String name, int position, String type) {
            super(name, position, type);
        }
    }

    private TestSpace space;
    private Player player;

    @Before
    public void setUp() {
        space = new TestSpace("Test Space", 5, "Test Type");
        player = new Player("Test Player");
    }

    @Test
    public void testConstructor() {
        assertEquals("Test Space", space.getName());
        assertEquals(5, space.getPosition());
        assertEquals("Test Type", space.getType());
        assertNull(space.getOwner());
        assertNull(space.getColorGroup());
    }

    @Test
    public void testGetType() {
        assertEquals("Test Type", space.getType());
    }

    @Test
    public void testGetAndSetOwner() {
        assertNull(space.getOwner());

        space.setOwner(player);
        assertEquals(player, space.getOwner());
    }

    @Test
    public void testGetName() {
        assertEquals("Test Space", space.getName());
    }

    @Test
    public void testGetAndSetColorGroup() {
        assertNull(space.getColorGroup());

        space.setColorGroup("Red");
        assertEquals("Red", space.getColorGroup());
    }

    @Test
    public void testPlayerInteractionMethods() {
        // Test the default methods that are meant to be overridden
        // These should just print to console, so we're just ensuring they don't throw exceptions

        // Capture System.out to verify output
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            space.playerOnSpecialSpace();
            assertTrue(outContent.toString().contains("Player landed on Test Space"));
            outContent.reset();

            space.playerOnProperty();
            assertTrue(outContent.toString().contains("Player landed on property Test Space"));
            outContent.reset();

            space.playerOnRailroad();
            assertTrue(outContent.toString().contains("Player landed on railroad Test Space"));
            outContent.reset();

            space.playerOnCardSpace();
            assertTrue(outContent.toString().contains("Player landed on card space Test Space"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testToString() {
        assertEquals("5: Test Space (Test Type)", space.toString());
    }

    @Test
    public void testGetPosition() {
        assertEquals(5, space.getPosition());
    }

    @Test
    public void testSetPosition() {
        space.setPosition(10);
        assertEquals(10, space.getPosition());
    }
}
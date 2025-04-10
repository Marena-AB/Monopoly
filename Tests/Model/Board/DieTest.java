package Model.Board;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the Die class
 */
public class DieTest {

    private Die die;

    @Before
    public void setUp() {
        // Create a new 6-sided die before each test
        die = new Die(6);
    }

    @Test
    public void testInitialState() {
        // Verify that the die has been created with the correct number of sides
        // We can't easily test the initial value since it's not initialized in constructor
        // Just make sure getDieValue doesn't throw an exception
        die.getDieValue();
    }

    @Test
    public void testRoll() {
        // Test that roll returns a value within the proper range
        int rollValue = die.roll();
        assertTrue("Roll value should be between 1 and 6", rollValue >= 1 && rollValue <= 6);

        // Test that getDieValue returns the same value after roll
        assertEquals(rollValue, die.getDieValue());

        // Test multiple rolls to ensure we get valid values
        for (int i = 0; i < 100; i++) {
            int value = die.roll();
            assertTrue("Roll value should be between 1 and 6", value >= 1 && value <= 6);
        }
    }

    @Test
    public void testDifferentSidedDie() {
        // Test that we can create dice with different numbers of sides
        Die tenSidedDie = new Die(10);

        // Test that roll returns a value within the proper range
        int rollValue = tenSidedDie.roll();
        assertTrue("Roll value should be between 1 and 10", rollValue >= 1 && rollValue <= 10);

        // Test that getDieValue returns the same value after roll
        assertEquals(rollValue, tenSidedDie.getDieValue());
    }

    @Test
    public void testGetDieValue() {
        // Roll the die to set its value
        int rollValue = die.roll();

        // Test that getDieValue returns the last roll value
        assertEquals(rollValue, die.getDieValue());

        // Roll again and confirm the value updates
        int newRollValue = die.roll();
        assertEquals(newRollValue, die.getDieValue());
    }
}
package Model.Board;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Test class for the HumanPlayer class
 */
public class HumanPlayerTest {

    private HumanPlayer humanPlayer;
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor;

    @Before
    public void setUp() {
        humanPlayer = new HumanPlayer("Test Human Player");

        // Set up output capture to verify console messages
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @After
    public void tearDown() {
        // Restore the original System.in and System.out
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    public void testInitialization() {
        // Test that the human player inherits correctly from Player
        assertEquals("Test Human Player", humanPlayer.getName());
        assertEquals(1500, humanPlayer.getMoney()); // Starting money should be 1500
        assertEquals(0, humanPlayer.getPosition()); // Starting position should be 0 (GO)
        assertNull(humanPlayer.getToken()); // Token should be null initially
        assertEquals(0, humanPlayer.getProperties().size()); // Should have no properties initially
    }

    @Test
    public void testPromptBuyPropertyYes() {
        // Set up a simulated "yes" input from the user
        String input = "yes\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns true for "yes" input
        boolean result = humanPlayer.promptBuyProperty("Test Property", 200);

        // Verify result
        assertTrue(result);

        // Verify console output
        String expectedPrompt = "Test Human Player, would you like to buy Test Property for $200? (y/n)";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedPrompt));
    }

    @Test
    public void testPromptBuyPropertyNo() {
        // Set up a simulated "no" input from the user
        String input = "no\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns false for "no" input
        boolean result = humanPlayer.promptBuyProperty("Test Property", 200);

        // Verify result
        assertFalse(result);

        // Verify console output
        String expectedPrompt = "Test Human Player, would you like to buy Test Property for $200? (y/n)";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedPrompt));
    }

    @Test
    public void testPromptBuyPropertyYInput() {
        // Set up a simulated "y" input from the user (should also work)
        String input = "y\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns true for "y" input
        boolean result = humanPlayer.promptBuyProperty("Test Property", 200);

        // Verify result
        assertTrue(result);
    }

    @Test
    public void testPromptUseGetOutOfJailCardYes() {
        // Set up a simulated "yes" input from the user
        String input = "yes\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns true for "yes" input
        boolean result = humanPlayer.promptUseGetOutOfJailCard();

        // Verify result
        assertTrue(result);

        // Verify console output
        String expectedPrompt = "Test Human Player, would you like to use your Get Out of Jail Free card? (y/n)";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedPrompt));
    }

    @Test
    public void testPromptUseGetOutOfJailCardNo() {
        // Set up a simulated "no" input from the user
        String input = "no\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns false for "no" input
        boolean result = humanPlayer.promptUseGetOutOfJailCard();

        // Verify result
        assertFalse(result);
    }

    @Test
    public void testPromptJailOptionsPay() {
        // Set up a simulated "1" input from the user (pay $50)
        String input = "1\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns 1 for "1" input
        int result = humanPlayer.promptJailOptions();

        // Verify result
        assertEquals(1, result);

        // Verify console output contains all options
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("Test Human Player, choose your jail option:"));
        assertTrue(output.contains("1. Pay $50 to get out"));
        assertTrue(output.contains("2. Use Get Out of Jail Free card"));
        assertTrue(output.contains("3. Try to roll doubles"));
    }

    @Test
    public void testPromptJailOptionsUseCard() {
        // Set up a simulated "2" input from the user (use card)
        String input = "2\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns 2 for "2" input
        int result = humanPlayer.promptJailOptions();

        // Verify result
        assertEquals(2, result);
    }

    @Test
    public void testPromptJailOptionsRoll() {
        // Set up a simulated "3" input from the user (roll)
        String input = "3\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method returns 3 for "3" input
        int result = humanPlayer.promptJailOptions();

        // Verify result
        assertEquals(3, result);
    }

    @Test
    public void testPromptJailOptionsInvalid() {
        // Set up a simulated invalid input from the user
        String input = "invalid\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method defaults to 3 for invalid input
        int result = humanPlayer.promptJailOptions();

        // Verify result (default should be option 3: roll for doubles)
        assertEquals(3, result);

        // Verify error message
        assertTrue(outputStreamCaptor.toString().trim().contains("Invalid choice. Defaulting to option 3"));
    }

    @Test
    public void testPromptJailOptionsOutOfRange() {
        // Set up a simulated out of range input from the user
        String input = "5\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        // Test that the method defaults to 3 for out of range input
        int result = humanPlayer.promptJailOptions();

        // Verify result (default should be option 3: roll for doubles)
        assertEquals(3, result);
    }

    @Test
    public void testInheritanceFromPlayer() {
        // Test that HumanPlayer inherits functionality from Player
        humanPlayer.addMoney(500);
        assertEquals(2000, humanPlayer.getMoney());

        humanPlayer.subtractMoney(300);
        assertEquals(1700, humanPlayer.getMoney());

        humanPlayer.setPosition(10);
        assertEquals(10, humanPlayer.getPosition());
    }
}
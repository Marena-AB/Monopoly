package Model.Spaces;

import Model.Board.Gameboard;
import Model.Board.Player;
import Model.GameState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for the JailSpace class.
 */
public class JailSpaceTest {
    private JailSpace jailSpace;
    private Player player;
    private GameState gameState;
    private Gameboard gameboard;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture console output
        System.setOut(new PrintStream(outContent));

        // Create game components
        gameboard = new Gameboard();
        List<Player> players = new ArrayList<>();
        player = new Player("Test Player");
        players.add(player);

        gameState = new GameState(players, gameboard);

        // Create Jail space
        jailSpace = new JailSpace();
    }

    @Test
    public void testJailSpaceInitialization() {
        // Verify initial properties of Jail space
        assertEquals("Jail", jailSpace.getName());
        assertEquals(10, jailSpace.getPosition());
        assertEquals("Jail", jailSpace.getType());
    }

    @Test
    public void testOnLandNotInJail() {
        // Reset output stream
        outContent.reset();

        // Ensure player is not in jail
        assertFalse(gameState.isPlayerInJail(player));

        // Land on jail space
        jailSpace.onLand(player, gameState);

        // Verify console output for just visiting
        assertTrue(outContent.toString().contains("is just visiting Jail"));
    }

    @Test
    public void testGoToJail() {
        // Reset output stream
        outContent.reset();

        // Use static method to send player to jail
        JailSpace.goToJail(player, gameState);

        // Verify player is in jail
        assertTrue(gameState.isPlayerInJail(player));

        // Verify player's position
        assertEquals(10, player.getPosition());

        // Verify console output
        assertTrue(outContent.toString().contains("has been sent to Jail"));
    }

    @Test
    public void testPayToGetOut() {
        // First, send player to jail
        gameState.sendToJail(player);
        assertTrue(gameState.isPlayerInJail(player));

        // Reset output stream
        outContent.reset();

        // Record initial money
        int initialMoney = player.getMoney();

        // Attempt to pay to get out
        boolean result = jailSpace.payToGetOut(player, gameState);

        // Verify successful payment
        assertTrue(result);
        assertFalse(gameState.isPlayerInJail(player));
        assertEquals(initialMoney - 50, player.getMoney());

        // Verify console output
        assertTrue(outContent.toString().contains("paid $50 to get out of Jail"));
    }

    @Test
    public void testPayToGetOutInsufficientFunds() {
        // Send player to jail
        gameState.sendToJail(player);

        // Reduce player's money to less than jail fee
        player.subtractMoney(1500); // This will set money to 0

        // Reset output stream
        outContent.reset();

        // Attempt to pay to get out
        boolean result = jailSpace.payToGetOut(player, gameState);

        // Verify payment fails
        assertFalse("Payment should fail with insufficient funds", result);
        assertTrue("Player should remain in jail", gameState.isPlayerInJail(player));

        // Verify console output
        String output = outContent.toString();
        assertTrue("Should show insufficient funds message",
                output.contains("doesn't have enough money to pay the Jail fee"));
    }

    @Test
    public void testTryRollForFreedom() {
        // Send player to jail
        gameState.sendToJail(player);
        assertTrue(gameState.isPlayerInJail(player));

        // Reset output stream
        outContent.reset();

        // Mock dice to return doubles
        player.setTurnsInJail(1);
        gameState.setDice(new MockDoublesRollDice());

        // Try to roll for freedom
        boolean result = jailSpace.tryRollForFreedom(player, gameState);

        // Verify player is released
        assertTrue(result);
        assertFalse(gameState.isPlayerInJail(player));
        assertTrue(outContent.toString().contains("rolled doubles and is out of Jail"));
    }

    @Test
    public void testTryRollForFreedomFailure() {
        // Send player to jail
        gameState.sendToJail(player);
        assertTrue(gameState.isPlayerInJail(player));

        // Reset output stream
        outContent.reset();

        // Mock dice to not return doubles
        player.setTurnsInJail(1);
        gameState.setDice(new MockNonDoublesRollDice());

        // Try to roll for freedom
        boolean result = jailSpace.tryRollForFreedom(player, gameState);

        // Verify player remains in jail
        assertFalse(result);
        assertTrue(gameState.isPlayerInJail(player));
        assertTrue(outContent.toString().contains("failed to roll doubles and remains in Jail"));
    }

    @Test
    public void testUseGetOutOfJailFreeCard() {
        // Send player to jail
        gameState.sendToJail(player);

        // Give player a Get Out of Jail Free card
        player.setHasGetOutOfJailFreeCard(true);

        // Reset output stream
        outContent.reset();

        // Use the card
        boolean result = jailSpace.useGetOutOfJailFreeCard(player, gameState);

        // Verify card usage
        assertTrue(result);
        assertFalse(gameState.isPlayerInJail(player));
        assertFalse(player.hasGetOutOfJailFreeCard());
        assertTrue(outContent.toString().contains("used a Get Out of Jail Free card"));
    }

    @Test
    public void testUseGetOutOfJailFreeCardFailure() {
        // Send player to jail
        gameState.sendToJail(player);

        // Ensure player does not have a card
        player.setHasGetOutOfJailFreeCard(false);

        // Reset output stream
        outContent.reset();

        // Try to use card
        boolean result = jailSpace.useGetOutOfJailFreeCard(player, gameState);

        // Verify card usage fails
        assertFalse(result);
        assertTrue(gameState.isPlayerInJail(player));
    }

    @Test
    public void testPlayerOnSpecialSpace() {
        // Reset output stream
        outContent.reset();

        // Call the method
        jailSpace.playerOnSpecialSpace();

        // Verify console output
        assertTrue(outContent.toString().contains("Player is at Jail (Just Visiting)"));
    }

    // Mock Dice classes for controlled testing
    private static class MockDoublesRollDice extends Model.Board.Dice {
        @Override
        public int rollDice() {
            return 4; // Some valid value
        }

        @Override
        public int getDie1Value() {
            return 2;
        }

        @Override
        public int getDie2Value() {
            return 2;
        }
    }

    private static class MockNonDoublesRollDice extends Model.Board.Dice {
        @Override
        public int rollDice() {
            return 5; // Some valid value
        }

        @Override
        public int getDie1Value() {
            return 2;
        }

        @Override
        public int getDie2Value() {
            return 3;
        }
    }

    @After
    public void restoreStreams() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}
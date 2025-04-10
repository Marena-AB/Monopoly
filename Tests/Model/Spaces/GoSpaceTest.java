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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test suite for the GoSpace class.
 */
public class GoSpaceTest {
    private GoSpace goSpace;
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

        // Create Go space
        goSpace = new GoSpace();
    }

    @Test
    public void testGoSpaceInitialization() {
        // Verify initial properties of Go space
        assertEquals("Go", goSpace.getName());
        assertEquals(0, goSpace.getPosition());
        assertEquals("Start", goSpace.getType());
    }

    @Test
    public void testOnLand() {
        // Test landing directly on Go space
        int initialMoney = player.getMoney();

        goSpace.onLand(player, gameState);

        // Verify money is increased by GO_AMOUNT
        assertEquals(initialMoney + 200, player.getMoney());

        // Verify console output
        assertTrue(outContent.toString().contains("landed on Go and collects $200"));
    }

    @Test
    public void testOnPass() {
        // Test passing Go space
        int initialMoney = player.getMoney();

        GoSpace.onPass(player, gameState);

        // Verify money is increased by GO_AMOUNT
        assertEquals(initialMoney + 200, player.getMoney());

        // Verify console output
        assertTrue(outContent.toString().contains("passed Go and collects $200"));
    }

    @Test
    public void testMoveToGo() {
        // Set player to a different position first
        player.setPosition(20);
        int initialMoney = player.getMoney();

        // Move to Go
        GoSpace.moveToGo(player, gameState);

        // Verify player is at Go (position 0)
        assertEquals(0, player.getPosition());

        // Verify money is increased by GO_AMOUNT
        assertEquals(initialMoney + 200, player.getMoney());

        // Verify console output
        assertTrue(outContent.toString().contains("moved to Go and collects $200"));
    }

    @Test
    public void testPlayerOnSpecialSpace() {
        // Capture console output
        outContent.reset();

        // Call the method
        goSpace.playerOnSpecialSpace();

        // Verify console output
        assertTrue(outContent.toString().contains("Player landed on Go and collects $200"));
    }

    @Test
    public void testMultiplePassesAndLandings() {
        // Test multiple interactions with Go space
        int initialMoney = player.getMoney();

        // Pass Go multiple times
        GoSpace.onPass(player, gameState);
        GoSpace.onPass(player, gameState);

        // Land on Go
        goSpace.onLand(player, gameState);

        // Verify total money increase
        assertEquals(initialMoney + (200 * 3), player.getMoney());
    }

    @Test
    public void testGoSpaceWithDifferentPlayerStates() {
        // Create multiple players with different initial states
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        player2.addMoney(-500); // Reduce initial money

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        GameState testGameState = new GameState(players, gameboard);

        // Initial money states
        int initialMoney1 = player1.getMoney();
        int initialMoney2 = player2.getMoney();

        // Test Go space interactions
        GoSpace.onPass(player1, testGameState);
        GoSpace.onPass(player2, testGameState);

        // Verify money increase for both players
        assertEquals(initialMoney1 + 200, player1.getMoney());
        assertEquals(initialMoney2 + 200, player2.getMoney());
    }

    @After
    public void restoreStreams() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}
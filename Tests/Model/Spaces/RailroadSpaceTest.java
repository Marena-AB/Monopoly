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
 * Comprehensive test suite for the RailroadSpace class.
 */
public class RailroadSpaceTest {
    private RailroadSpace railroad;
    private Player player;
    private Player player2;
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
        player = new Player("Test Player 1");
        player2 = new Player("Test Player 2");
        players.add(player);
        players.add(player2);

        gameState = new GameState(players, gameboard);

        // Create Railroad space
        railroad = new RailroadSpace("Reading Railroad", 5);
    }

    @Test
    public void testRailroadSpaceInitialization() {
        // Verify initial properties of Railroad space
        assertEquals("Reading Railroad", railroad.getName());
        assertEquals(5, railroad.getPosition());
        assertEquals("Railroad", railroad.getType());
        assertEquals(200, railroad.getPrice());
        assertNull(railroad.getOwner());
    }

    @Test
    public void testSetAndGetOwner() {
        // Set owner
        railroad.setOwner(player);

        // Verify owner is set correctly
        assertEquals(player, railroad.getOwner());
        assertTrue(railroad.isOwned());
    }

    @Test
    public void testCalculateRentNoOwnership() {
        // Calculate rent for unowned railroad
        int rent = railroad.calculateRent(gameState);

        // Verify no rent is charged
        assertEquals(0, rent);
    }

    @Test
    public void testCalculateRentSingleRailroad() {
        // Set owner and add railroad to game board
        railroad.setOwner(player);

        // Calculate rent
        int rent = railroad.calculateRent(gameState);

        // Verify base rent (25)
        assertEquals(25, rent);
    }

    @Test
    public void testCalculateRentMultipleRailroads() {
        // Create multiple railroads for the same owner
        List<Space> spaces = gameboard.getSpaces();
        List<RailroadSpace> railroads = new ArrayList<>();

        // Find and set multiple railroads to the same owner
        for (Space space : spaces) {
            if (space instanceof RailroadSpace) {
                RailroadSpace rr = (RailroadSpace) space;
                rr.setOwner(player);
                railroads.add(rr);

                // Stop after finding 2 railroads
                if (railroads.size() == 2) break;
            }
        }

        // Calculate rent for the first railroad
        int rent = railroads.get(0).calculateRent(gameState);

        // Verify rent increases with multiple railroad ownership
        // 25 * 2^(number of railroads - 1)
        assertEquals(50, rent);
    }

    @Test
    public void testOnLandUnownedRailroad() {
        // Reset output stream
        outContent.reset();

        // Land on unowned railroad
        railroad.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("landed on Reading Railroad"));
        assertTrue(output.contains("is not owned. It costs $200"));
    }

    @Test
    public void testOnLandOwnedRailroad() {
        // Set owner
        railroad.setOwner(player2);

        // Reset output stream
        outContent.reset();

        // Land on owned railroad
        railroad.onLand(player, gameState);

        // Verify console output and rent payment
        String output = outContent.toString();
        assertTrue(output.contains("landed on Reading Railroad"));
        assertTrue(output.contains("must pay $25 to " + player2.getName()));
    }

    @Test
    public void testOnLandSelfOwnedRailroad() {
        // Set owner to current player
        railroad.setOwner(player);

        // Reset output stream
        outContent.reset();

        // Land on own railroad
        railroad.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("landed on Reading Railroad"));
        assertTrue(output.contains("You own this railroad"));
    }

    @Test
    public void testToString() {
        // Set owner
        railroad.setOwner(player);

        // Get string representation
        String railroadString = railroad.toString();

        // Verify string contains key information
        assertTrue(railroadString.contains("Reading Railroad"));
        assertTrue(railroadString.contains("Price: $200"));
        assertTrue(railroadString.contains("Owner: Test Player 1"));
    }

    @Test
    public void testPlayerOnRailroad() {
        // Reset output stream
        outContent.reset();

        // Call method
        railroad.playerOnRailroad();

        // Verify console output
        assertTrue(outContent.toString().contains("Player landed on railroad Reading Railroad"));
    }

    @After
    public void restoreStreams() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}
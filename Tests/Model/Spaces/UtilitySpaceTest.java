package Model.Spaces;

import Model.Board.Dice;
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
 * Comprehensive test suite for the UtilitySpace class.
 */
public class UtilitySpaceTest {
    private UtilitySpace utilitySpace;
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

        // Create Utility space
        utilitySpace = new UtilitySpace("Electric Company", 12);
    }

    @Test
    public void testUtilitySpaceInitialization() {
        // Verify initial properties of Utility space
        assertEquals("Electric Company", utilitySpace.getName());
        assertEquals(12, utilitySpace.getPosition());
        assertEquals("Utility", utilitySpace.getType());
        assertEquals(150, utilitySpace.getPrice());
        assertNull(utilitySpace.getOwner());
    }

    @Test
    public void testSetAndGetOwner() {
        // Set owner
        utilitySpace.setOwner(player);

        // Verify owner is set correctly
        assertEquals(player, utilitySpace.getOwner());
        assertTrue(utilitySpace.isOwned());
    }

    @Test
    public void testCalculateRentNoOwnership() {
        // Calculate rent for unowned utility
        int diceRoll = 6;
        int rent = utilitySpace.calculateRent(diceRoll, gameState);

        // Verify no rent is charged
        assertEquals(0, rent);
    }

    @Test
    public void testCalculateRentSingleUtility() {
        // Create multiple players and multiple utilities
        List<Player> testPlayers = new ArrayList<>();
        Player testOwner = new Player("Test Owner");
        testPlayers.add(testOwner);
        testPlayers.add(player);

        // Create a new game state with these players
        GameState testGameState = new GameState(testPlayers, gameboard);

        // Find all utility spaces
        List<UtilitySpace> allUtilities = new ArrayList<>();
        for (Space space : gameboard.getSpaces()) {
            if (space instanceof UtilitySpace) {
                UtilitySpace utility = (UtilitySpace) space;
                allUtilities.add(utility);
            }
        }

        // Ensure we have at least one utility
        assertFalse("Should have utilities on the board", allUtilities.isEmpty());

        // Set owner of the first utility
        UtilitySpace firstUtility = allUtilities.get(0);
        firstUtility.setOwner(testOwner);

        // Verify owner is set
        assertEquals(testOwner, firstUtility.getOwner());

        // Use a mock dice roll
        int diceRoll = 6;

        // Calculate rent
        int rent = firstUtility.calculateRent(diceRoll, testGameState);

        // Verify rent is 4 times dice roll
        assertEquals("Rent should be 4 times dice roll for single utility",
                diceRoll * 4, rent);
    }

    @Test
    public void testCalculateRentMultipleUtilities() {
        // Find and add multiple utilities to the same owner
        List<Space> spaces = gameboard.getSpaces();
        List<UtilitySpace> utilities = new ArrayList<>();

        for (Space space : spaces) {
            if (space instanceof UtilitySpace) {
                UtilitySpace utility = (UtilitySpace) space;
                utility.setOwner(player);
                utilities.add(utility);

                // Stop after finding both utilities
                if (utilities.size() == 2) break;
            }
        }

        // Use a mock dice roll
        int diceRoll = 6;

        // Calculate rent for the first utility
        int rent = utilities.get(0).calculateRent(diceRoll, gameState);

        // Verify rent is 10 times dice roll when both utilities are owned
        assertEquals(diceRoll * 10, rent);
    }

    @Test
    public void testOnLandUnownedUtility() {
        // Reset output stream
        outContent.reset();

        // Land on unowned utility
        utilitySpace.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("landed on Electric Company"));
        assertTrue(output.contains("is not owned. It costs $150"));
    }

    @Test
    public void testOnLandOwnedUtility() {
        // Find an actual utility space from the game board
        UtilitySpace actualUtility = null;
        for (Space space : gameboard.getSpaces()) {
            if (space instanceof UtilitySpace) {
                actualUtility = (UtilitySpace) space;
                break;
            }
        }

        // Ensure we found a utility
        assertNotNull("Should find a utility space on the board", actualUtility);

        // Set the owner of the utility
        actualUtility.setOwner(player2);

        // Reset output stream
        outContent.reset();

        // Create a mock dice that always rolls 6
        Dice mockDice = new Dice() {
            @Override
            public int rollDice() {
                return 6;
            }

            @Override
            public int getDie1Value() {
                return 3;
            }

            @Override
            public int getDie2Value() {
                return 3;
            }
        };

        // Set the mock dice in game state
        gameState.setDice(mockDice);

        // Perform the land action
        actualUtility.onLand(player, gameState);

        // Get the output
        String output = outContent.toString();
        System.out.println("Console Output: " + output);

        // Assertions with more detailed error messages
        assertTrue("Should mention landing on utility",
                output.contains("landed on " + actualUtility.getName()));

        // Calculate expected rent
        int expectedRent = 6 * 4;
        String expectedRentMessage = "must pay $" + expectedRent + " to " + player2.getName();

        assertTrue("Should show rent payment message: " + expectedRentMessage,
                output.contains(expectedRentMessage));
    }

    @Test
    public void testOnLandSelfOwnedUtility() {
        // Set owner to current player
        utilitySpace.setOwner(player);

        // Reset output stream
        outContent.reset();

        // Land on own utility
        utilitySpace.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("landed on Electric Company"));
        assertTrue(output.contains("owns this utility"));
    }

    @Test
    public void testToString() {
        // Set owner
        utilitySpace.setOwner(player);

        // Get string representation
        String utilityString = utilitySpace.toString();

        // Verify string contains key information
        assertTrue(utilityString.contains("Electric Company"));
        assertTrue(utilityString.contains("Price: $150"));
        assertTrue(utilityString.contains("Owner: Test Player 1"));
    }

    @Test
    public void testPlayerOnRailroad() {
        // Reset output stream
        outContent.reset();

        // Call method (this is a inherited method from Space class)
        utilitySpace.playerOnRailroad();

        // Verify console output
        assertTrue(outContent.toString().contains("Player landed on railroad Electric Company"));
    }

    // Mock Dice class to control dice roll
    private static class MockDice extends Dice {
        private final int rollValue;

        public MockDice(int rollValue) {
            this.rollValue = rollValue;
        }

        @Override
        public int rollDice() {
            return rollValue;
        }

        @Override
        public int getDie1Value() {
            return rollValue / 2;
        }

        @Override
        public int getDie2Value() {
            return rollValue - (rollValue / 2);
        }
    }

    @After
    public void restoreStreams() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}
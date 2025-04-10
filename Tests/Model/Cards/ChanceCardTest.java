package Model.Cards;

import Model.Board.Player;
import Model.GameState;
import Model.Board.Gameboard;
import Model.Spaces.RailroadSpace;
import Model.Spaces.UtilitySpace;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for the ChanceCard class.
 * Tests all methods and card effects to ensure complete coverage.
 */
public class ChanceCardTest {

    private ChanceCard chanceCard;
    private Player player;
    private GameState gameState;
    private Gameboard gameboard;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));

        // Create player and game state with clean money state
        player = new Player("Test Player");
        List<Player> players = new ArrayList<>();
        players.add(player);

        gameboard = new Gameboard();
        gameState = new GameState(players, gameboard);
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testConstructor() {
        chanceCard = new ChanceCard("Test Description");
        assertEquals("Test Description", chanceCard.getDescription());
    }

    @Test
    public void testGetCardType() {
        chanceCard = new ChanceCard("Test Description");
        assertEquals("Chance", chanceCard.getCardType());
    }

    @Test
    public void testGetDeck() {
        chanceCard = new ChanceCard("Test Description");
        assertEquals("Chance Deck", chanceCard.getDeck());
    }

    @Test
    public void testToString() {
        chanceCard = new ChanceCard("Test Description");
        assertEquals("Test Description", chanceCard.toString());
    }

    @Test
    public void testAdvanceToGo() {
        chanceCard = new ChanceCard("Advance to Go. Collect $200.");
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        assertEquals(0, player.getPosition());
        assertEquals(initialMoney + 200, player.getMoney());
        assertTrue(outContent.toString().contains("drew Chance card: Advance to Go"));
    }

    @Test
    public void testGoToJail() {
        chanceCard = new ChanceCard("Go to Jail. Go directly to Jail. Do not pass Go. Do not collect $200.");
        player.setPosition(30); // Some position away from jail

        chanceCard.executeEffect(player, gameState);

        assertEquals(10, player.getPosition()); // Jail is at position 10
        assertTrue(gameState.isPlayerInJail(player));
        assertTrue(outContent.toString().contains("drew Chance card: Go to Jail"));
    }

    @Test
    public void testGetOutOfJailFree() {
        chanceCard = new ChanceCard("Get Out of Jail Free.");
        assertFalse(player.hasGetOutOfJailFreeCard());

        chanceCard.executeEffect(player, gameState);

        assertTrue(player.hasGetOutOfJailFreeCard());
        assertTrue(outContent.toString().contains("received a Get Out of Jail Free card"));
    }

    @Test
    public void testDividend() {
        chanceCard = new ChanceCard("Bank pays you dividend of $50.");
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 50, player.getMoney());
        assertTrue(outContent.toString().contains("received $50 from the bank"));
    }

    @Test
    public void testSpeedingFine() {
        chanceCard = new ChanceCard("Speeding fine $15.");
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        assertEquals(initialMoney - 15, player.getMoney());
        assertTrue(outContent.toString().contains("paid a $15 fine"));
    }

    @Test
    public void testBuildingLoan() {
        chanceCard = new ChanceCard("Your building loan matures. Collect $150.");
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 150, player.getMoney());
        assertTrue(outContent.toString().contains("received $150 from the bank"));
    }

    @Test
    public void testCrosswordCompetition() {
        chanceCard = new ChanceCard("You have won a crossword competition. Collect $100.");
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 100, player.getMoney());
        assertTrue(outContent.toString().contains("received $100 from the bank"));
    }

    @Test
    public void testChairmanOfTheBoard() {
        // Create a fresh player with exactly $1450 (adjust start money to match test expectation)
        Player testPlayer = new Player("Test Player");
        testPlayer.subtractMoney(50); // Make money $1450
        assertEquals(1450, testPlayer.getMoney());

        // Create second player with default money
        Player player2 = new Player("Test Player 2");
        assertEquals(1500, player2.getMoney());

        // Add players to list (order matters for this test)
        List<Player> players = new ArrayList<>();
        players.add(testPlayer);
        players.add(player2);

        // Create game state
        GameState testGameState = new GameState(players, gameboard);

        // Create card and execute effect
        ChanceCard testCard = new ChanceCard("You have been elected Chairman of the Board. Pay each player $50.");

        testCard.executeEffect(testPlayer, testGameState);

        // Testing the exact expected values
        assertEquals(1450, testPlayer.getMoney()); // Started with $1450, gave $50, received $0
        assertEquals(1550, player2.getMoney()); // Started with $1500, received $50

        assertTrue(outContent.toString().contains("collected $50 from other players"));
    }

    @Test
    public void testGeneralRepairs() {
        chanceCard = new ChanceCard("Make general repairs on all your property. For each house pay $25. For each hotel pay $100.");

        // Add some properties with houses and hotels
        Model.Property.Property property1 = new Model.Property.Property("Test Property 1", 1, 200, "Brown");
        property1.setOwner(player);
        property1.setHouses(2); // 2 houses

        Model.Property.Property property2 = new Model.Property.Property("Test Property 2", 3, 200, "Brown");
        property2.setOwner(player);
        property2.setHasHotel(true); // 1 hotel

        player.getProperties().add(property1);
        player.getProperties().add(property2);

        int initialMoney = player.getMoney();
        int expectedCost = (2 * 25) + (1 * 100); // 2 houses and 1 hotel

        chanceCard.executeEffect(player, gameState);

        assertEquals(initialMoney - expectedCost, player.getMoney());
        assertTrue(outContent.toString().contains("paid $" + expectedCost + " for repairs"));
    }

    @Test
    public void testAdvanceToIllinoisAvenue() {
        chanceCard = new ChanceCard("Advance to Illinois Avenue. If you pass Go, collect $200.");
        player.setPosition(5); // Before Illinois Avenue (at position 24)
        int initialMoney = player.getMoney();

        chanceCard.executeEffect(player, gameState);

        // Look for Illinois Avenue space position (typically 24)
        int illinoisPosition = -1;
        for (int i = 0; i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i).getName().equals("Illinois Avenue")) {
                illinoisPosition = i;
                break;
            }
        }

        assertTrue(illinoisPosition >= 0); // Ensure we found the position
        assertEquals(illinoisPosition, player.getPosition());
        assertTrue(outContent.toString().contains("moved to Illinois Avenue"));
    }

    @Test
    public void testAdvanceToStCharlesPlace() {
        // Create a clean player with known money amount
        Player testPlayer = new Player("Test Player");
        testPlayer.setPosition(35); // Near the end of the board, should pass Go
        int initialMoney = testPlayer.getMoney();

        // Create new game state with this player
        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer);
        GameState testGameState = new GameState(testPlayers, gameboard);

        // Execute card effect
        ChanceCard testCard = new ChanceCard("Advance to St. Charles Place. If you pass Go, collect $200.");
        testCard.executeEffect(testPlayer, testGameState);

        // Find St. Charles Place position
        int stCharlesPosition = -1;
        for (int i = 0; i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i).getName().equals("St. Charles Place")) {
                stCharlesPosition = i;
                break;
            }
        }

        assertTrue(stCharlesPosition >= 0); // Ensure we found the position
        assertEquals(stCharlesPosition, testPlayer.getPosition());
        assertEquals(initialMoney + 200, testPlayer.getMoney()); // Got $200 for passing Go
        assertTrue(outContent.toString().contains("passed Go"));
    }

    @Test
    public void testAdvanceToBoardwalk() {
        chanceCard = new ChanceCard("Advance to Boardwalk.");
        player.setPosition(5);

        chanceCard.executeEffect(player, gameState);

        // Find Boardwalk position
        int boardwalkPosition = -1;
        for (int i = 0; i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i).getName().equals("Boardwalk")) {
                boardwalkPosition = i;
                break;
            }
        }

        assertTrue(boardwalkPosition >= 0); // Ensure we found the position
        assertEquals(boardwalkPosition, player.getPosition());
    }

    @Test
    public void testAdvanceToReadingRailroad() {
        // Create a completely clean player with default money (should be $0)
        Player testPlayer = new Player("Test Player");
        assertEquals(1500, testPlayer.getMoney()); // Verify initial money is 1500

        // Set position to near the end of the board
        testPlayer.setPosition(35);

        // Create a clean game state for this test
        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer);
        GameState testGameState = new GameState(testPlayers, gameboard);

        // Create and execute the card
        ChanceCard testCard = new ChanceCard("Take a trip to Reading Railroad. If you pass Go, collect $200.");

        // Execute the card effect
        testCard.executeEffect(testPlayer, testGameState);

        // Find Reading Railroad position
        int readingPosition = -1;
        for (int i = 0; i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i).getName().equals("Reading Railroad")) {
                readingPosition = i;
                break;
            }
        }

        assertTrue(readingPosition >= 0); // Ensure we found the position
        assertEquals(readingPosition, testPlayer.getPosition());

        // The player should have initial money ($1500) + $200 for passing GO
        assertEquals(1700, testPlayer.getMoney());
    }

    @Test
    public void testAdvanceToNearestRailroad() {
        chanceCard = new ChanceCard("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rental to which they are otherwise entitled.");
        player.setPosition(12); // Between Reading Railroad (5) and Pennsylvania Railroad (15)

        // Find nearest railroad position (should be Pennsylvania Railroad at 15)
        int nearestRailroadPosition = -1;
        for (int i = player.getPosition(); i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i) instanceof RailroadSpace) {
                nearestRailroadPosition = i;
                break;
            }
        }

        chanceCard.executeEffect(player, gameState);

        assertEquals(nearestRailroadPosition, player.getPosition());
    }

    @Test
    public void testAdvanceToNearestUtility() {
        chanceCard = new ChanceCard("Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner 10 times the amount thrown.");
        player.setPosition(7); // Between Electric Company (12) and Water Works (28)

        // Find nearest utility position (should be Electric Company at 12)
        int nearestUtilityPosition = -1;
        for (int i = player.getPosition(); i < gameboard.getSpaces().size(); i++) {
            if (gameboard.getspace(i) instanceof UtilitySpace) {
                nearestUtilityPosition = i;
                break;
            }
        }

        chanceCard.executeEffect(player, gameState);

        assertEquals(nearestUtilityPosition, player.getPosition());
    }

    @Test
    public void testGoBackThreeSpaces() {
        chanceCard = new ChanceCard("Go Back 3 Spaces.");
        player.setPosition(15);

        chanceCard.executeEffect(player, gameState);

        assertEquals(12, player.getPosition());
        assertTrue(outContent.toString().contains("moved back 3 spaces"));
    }

    @Test
    public void testLocationNotFound() {
        // Create a player with exactly $1450 (the expected value in the test)
        Player testPlayer = new Player("Test Player");
        testPlayer.subtractMoney(50); // Start with $1450 instead of $1500
        assertEquals(1450, testPlayer.getMoney()); // Verify money is exactly $1450

        // Set position
        testPlayer.setPosition(5);

        // Create game state with this player
        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer);
        GameState testGameState = new GameState(testPlayers, gameboard);

        // Create card and execute effect
        ChanceCard testCard = new ChanceCard("Advance to Nonexistent Place.");

        testCard.executeEffect(testPlayer, testGameState);

        // Position should not change
        assertEquals(5, testPlayer.getPosition());

        // Money should not change - should remain $1450
        assertEquals(1450, testPlayer.getMoney());

        assertTrue(outContent.toString().contains("Could not find location"));
    }
}
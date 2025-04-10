
package Model.Cards;

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
 * Test class for the CommunityChestCard class.
 * Tests all methods and card effects to ensure complete coverage.
 */
public class CommunityChestCardTest {

    private CommunityChestCard communityChestCard;
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
        communityChestCard = new CommunityChestCard("Test Description");
        assertEquals("Test Description", communityChestCard.getDescription());
    }

    @Test
    public void testGetCardType() {
        communityChestCard = new CommunityChestCard("Test Description");
        assertEquals("Community Chest", communityChestCard.getCardType());
    }

    @Test
    public void testGetDeck() {
        communityChestCard = new CommunityChestCard("Test Description");
        assertEquals("Community Chest Deck", communityChestCard.getDeck());
    }

    @Test
    public void testToString() {
        communityChestCard = new CommunityChestCard("Test Description");
        assertEquals("Test Description", communityChestCard.toString());
    }

    @Test
    public void testAdvanceToGo() {
        communityChestCard = new CommunityChestCard("Advance to Go. Collect $200.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(0, player.getPosition());
        assertEquals(initialMoney + 200, player.getMoney());
        assertTrue(outContent.toString().contains("drew Community Chest card: Advance to Go"));
    }

    @Test
    public void testGoToJail() {
        communityChestCard = new CommunityChestCard("Go to Jail. Go directly to Jail. Do not pass Go. Do not collect $200.");
        player.setPosition(30); // Some position away from jail

        communityChestCard.executeEffect(player, gameState);

        assertEquals(10, player.getPosition()); // Jail is at position 10
        assertTrue(gameState.isPlayerInJail(player));
        assertTrue(outContent.toString().contains("drew Community Chest card: Go to Jail"));
    }

    @Test
    public void testGetOutOfJailFree() {
        communityChestCard = new CommunityChestCard("Get Out of Jail Free.");
        assertFalse(player.hasGetOutOfJailFreeCard());

        communityChestCard.executeEffect(player, gameState);

        assertTrue(player.hasGetOutOfJailFreeCard());
        assertTrue(outContent.toString().contains("received a Get Out of Jail Free card"));
    }

    @Test
    public void testBankError() {
        communityChestCard = new CommunityChestCard("Bank error in your favor. Collect $200.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 200, player.getMoney());
        assertTrue(outContent.toString().contains("received $200 from the bank"));
    }

    @Test
    public void testDoctorsFee() {
        communityChestCard = new CommunityChestCard("Doctor's fee. Pay $50.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney - 50, player.getMoney());
        assertTrue(outContent.toString().contains("paid $50 doctor's fee"));
    }

    @Test
    public void testStockSale() {
        communityChestCard = new CommunityChestCard("From sale of stock you get $50.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 50, player.getMoney());
        assertTrue(outContent.toString().contains("received $50 from stock sale"));
    }

    @Test
    public void testHolidayFund() {
        communityChestCard = new CommunityChestCard("Holiday fund matures. Receive $100.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 100, player.getMoney());
        assertTrue(outContent.toString().contains("received $100 from holiday fund"));
    }

    @Test
    public void testIncomeTaxRefund() {
        communityChestCard = new CommunityChestCard("Income tax refund. Collect $20.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 20, player.getMoney());
        assertTrue(outContent.toString().contains("received $20 tax refund"));
    }

    @Test
    public void testBirthday() {
        // Create additional player for test
        Player player2 = new Player("Test Player 2");
        int initialMoney1 = player.getMoney();
        int initialMoney2 = player2.getMoney();

        // Add players to list
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player2);

        // Create game state
        GameState testGameState = new GameState(players, gameboard);

        // Create card and execute effect
        communityChestCard = new CommunityChestCard("It is your birthday. Collect $10 from each player.");
        communityChestCard.executeEffect(player, testGameState);

        // Verify: player should collect $10 from player2
        assertEquals(initialMoney1 + 10, player.getMoney());
        assertEquals(initialMoney2 - 10, player2.getMoney());

        assertTrue(outContent.toString().contains("collected $10 for their birthday"));
    }

    @Test
    public void testLifeInsurance() {
        communityChestCard = new CommunityChestCard("Life insurance matures. Collect $100.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 100, player.getMoney());
        assertTrue(outContent.toString().contains("received $100 from life insurance"));
    }

    @Test
    public void testHospital() {
        communityChestCard = new CommunityChestCard("Pay hospital fees of $100.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney - 100, player.getMoney());
        assertTrue(outContent.toString().contains("paid $100 hospital fees"));
    }

    @Test
    public void testSchoolFees() {
        communityChestCard = new CommunityChestCard("Pay school fees of $50.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney - 50, player.getMoney());
        assertTrue(outContent.toString().contains("paid $50 school fees"));
    }

    @Test
    public void testConsultancyFee() {
        communityChestCard = new CommunityChestCard("Receive $25 consultancy fee.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 25, player.getMoney());
        assertTrue(outContent.toString().contains("received $25 consultancy fee"));
    }

    @Test
    public void testStreetRepairs() {
        communityChestCard = new CommunityChestCard("You are assessed for street repairs. $40 per house. $115 per hotel.");

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
        int expectedCost = (2 * 40) + (1 * 115); // 2 houses and 1 hotel

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney - expectedCost, player.getMoney());
        assertTrue(outContent.toString().contains("paid $" + expectedCost + " for street repairs"));
    }

    @Test
    public void testBeautyContest() {
        communityChestCard = new CommunityChestCard("You have won second prize in a beauty contest. Collect $10.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 10, player.getMoney());
        assertTrue(outContent.toString().contains("received $10 from beauty contest"));
    }

    @Test
    public void testInheritance() {
        communityChestCard = new CommunityChestCard("You inherit $100.");
        int initialMoney = player.getMoney();

        communityChestCard.executeEffect(player, gameState);

        assertEquals(initialMoney + 100, player.getMoney());
        assertTrue(outContent.toString().contains("inherited $100"));
    }

    @Test
    public void testInheritanceEdgeCase() {
        // Test with a player who has exactly $0
        Player testPlayer = new Player("Test Player");
        testPlayer.subtractMoney(1500); // Set money to 0
        assertEquals(0, testPlayer.getMoney());

        // Create game state with this player
        List<Player> testPlayers = new ArrayList<>();
        testPlayers.add(testPlayer);
        GameState testGameState = new GameState(testPlayers, gameboard);

        // Create card and execute effect
        CommunityChestCard testCard = new CommunityChestCard("You inherit $100.");
        testCard.executeEffect(testPlayer, testGameState);

        // Verify money increased by exactly $100
        assertEquals(100, testPlayer.getMoney());
        assertTrue(outContent.toString().contains("inherited $100"));
    }

    @Test
    public void testGetOutOfJailFreeEdgeCase() {
        // Test with a player who already has a Get Out of Jail Free card
        player.setHasGetOutOfJailFreeCard(true);
        assertTrue(player.hasGetOutOfJailFreeCard());

        // Execute card effect
        communityChestCard = new CommunityChestCard("Get Out of Jail Free.");
        communityChestCard.executeEffect(player, gameState);

        // Verify still has card (implementation should still work)
        assertTrue(player.hasGetOutOfJailFreeCard());
        assertTrue(outContent.toString().contains("received a Get Out of Jail Free card"));
    }

    @Test
    public void testCardWithNoMatchingEffect() {
        // Test a card that doesn't match any known effect
        communityChestCard = new CommunityChestCard("This is an unknown card effect.");
        int initialMoney = player.getMoney();
        int initialPosition = player.getPosition();

        communityChestCard.executeEffect(player, gameState);

        // Verify no change in money or position
        assertEquals(initialMoney, player.getMoney());
        assertEquals(initialPosition, player.getPosition());
        assertTrue(outContent.toString().contains("drew Community Chest card: This is an unknown card effect"));
    }
}
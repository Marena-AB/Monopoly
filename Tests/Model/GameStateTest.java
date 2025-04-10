package Model;

import Model.Board.Bank;
import Model.Board.Dice;
import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Property.Property;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test class for the GameState class
 */
public class GameStateTest {

    private GameState gameState;
    private Gameboard board;
    private Player player1;
    private Player player2;
    private List<Player> players;
    private Bank bank;

    @Before
    public void setUp() {
        // Create game board
        board = new Gameboard();

        // Create players
        player1 = new Player("Test Player 1");
        player2 = new Player("Test Player 2");
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Create bank
        bank = new Bank();

        // Create game state
        gameState = new GameState(players, board);
        gameState.setBank(bank);
    }

    @Test
    public void testInitialization() {
        // Test initial game state
        assertEquals(players, gameState.getPlayers());
        assertEquals(board, gameState.getBoard());
        assertEquals(0, gameState.getCurrentPlayerIndex());
        assertEquals(player1, gameState.getCurrentPlayer());
        assertTrue(gameState.isGameActive());
        assertFalse(gameState.isPlayerInJail(player1));
        assertFalse(gameState.isPlayerInJail(player2));
    }

    @Test
    public void testInitializeGame() {
        // Test initialize game
        gameState.initializeGame();

        // Check starting money was given
        assertEquals(1500, player1.getMoney());
        assertEquals(1500, player2.getMoney());

        // Check game state was reset
        assertTrue(gameState.isGameActive());
        assertEquals(0, gameState.getCurrentPlayerIndex());
        assertFalse(gameState.isPlayerInJail(player1));
        assertFalse(gameState.isPlayerInJail(player2));
    }

    @Test
    public void testNextTurn() {
        // Check initial player is player1
        assertEquals(player1, gameState.getCurrentPlayer());

        // Move to next turn
        gameState.nextTurn();

        // Check current player is now player2
        assertEquals(player2, gameState.getCurrentPlayer());

        // Move to next turn again
        gameState.nextTurn();

        // Check it cycles back to player1
        assertEquals(player1, gameState.getCurrentPlayer());
    }

    @Test
    public void testRollDice() {
        // Roll the dice
        int roll = gameState.rollDice();

        // Check roll is within valid range (2-12)
        assertTrue(roll >= 2 && roll <= 12);

        // Check dice values match the roll
        int[] diceValues = gameState.getDiceValues();
        assertEquals(2, diceValues.length);
        assertEquals(roll, diceValues[0] + diceValues[1]);
    }

    @Test
    public void testJailFunctionality() {
        // Test player not in jail initially
        assertFalse(gameState.isPlayerInJail(player1));

        // Send player to jail
        gameState.sendToJail(player1);

        // Check player is in jail
        assertTrue(gameState.isPlayerInJail(player1));
        assertEquals(10, player1.getPosition()); // Jail position

        // Release player from jail
        gameState.releaseFromJail(player1);

        // Check player is no longer in jail
        assertFalse(gameState.isPlayerInJail(player1));
        assertEquals(0, player1.getTurnsInJail());
    }

    @Test
    public void testDrawChanceCard() {
        // Draw a chance card
        String cardDescription = gameState.drawChanceCard();

        // Check card description is not empty
        assertNotNull(cardDescription);
        assertFalse(cardDescription.isEmpty());
    }

    @Test
    public void testDrawCommunityChestCard() {
        // Draw a community chest card
        String cardDescription = gameState.drawCommunityChestCard();

        // Check card description is not empty
        assertNotNull(cardDescription);
        assertFalse(cardDescription.isEmpty());
    }

    @Test
    public void testReturnGetOutOfJailFreeCard() {
        // Get initial deck sizes
        int initialChanceSize = gameState.getChanceCardDeck().size();
        int initialCommunityChestSize = gameState.getCommunityChestCardDeck().size();

        // Return cards to each deck
        gameState.returnGetOutOfJailFreeCard("Chance");
        gameState.returnGetOutOfJailFreeCard("Community Chest");

        // Check decks have one more card each
        assertEquals(initialChanceSize + 1, gameState.getChanceCardDeck().size());
        assertEquals(initialCommunityChestSize + 1, gameState.getCommunityChestCardDeck().size());
    }

    @Test
    public void testTransferMoney() {
        // Set up initial money
        player1.addMoney(1000);
        player2.addMoney(500);

        // Transfer money
        boolean result = gameState.transferMoney(player1, player2, 300);

        // Check result and money was transferred
        assertTrue(result);
        assertEquals(1200, player2.getMoney());
        assertEquals(700, player1.getMoney());

        // Try to transfer more money than player has
        result = gameState.transferMoney(player1, player2, 800);

        // Check result and money was not transferred
        assertFalse(result);
        assertEquals(1200, player2.getMoney());
        assertEquals(700, player1.getMoney());
    }

    @Test
    public void testCollectFromBank() {
        int initialMoney = player1.getMoney();

        // Collect money from bank
        gameState.collectFromBank(player1, 200);

        // Check money was added
        assertEquals(initialMoney + 200, player1.getMoney());
    }

    @Test
    public void testPayToBank() {
        // Set up initial money
        player1.addMoney(500);

        // Pay to bank
        boolean result = gameState.payToBank(player1, 200);

        // Check result and money was paid
        assertTrue(result);
        assertEquals(1800, player1.getMoney());

        // Try to pay more money than player has
        result = gameState.payToBank(player1, 2000);

        // Check result and money was not paid
        assertFalse(result);
        assertEquals(1800, player1.getMoney());
    }

    @Test
    public void testHandlePlayerBankruptcy() {
        // Give player1 a property
        Property property = new Property("Test Property", 1, 200, "Brown");
        property.setOwner(player1);
        player1.getProperties().add(property);

        // Add property to bank's available properties
        List<Property> availableProperties = new ArrayList<>();
        bank.setAvailableProperties(availableProperties);

        // Make player bankrupt
        player1.subtractMoney(1500); // Remove all money

        // Handle bankruptcy
        gameState.handlePlayerBankruptcy(player1);

        // Check player was removed
        assertFalse(players.contains(player1));
        assertEquals(1, players.size());

        // Check property was returned to bank
        assertNull(property.getOwner());
        assertTrue(bank.getAvailableProperties().contains(property));

        // Check if game is over with only one player
        assertFalse(gameState.isGameActive());
    }

    @Test
    public void testGettersAndSetters() {
        // Test dice getter/setter
        Dice newDice = new Dice();
        gameState.setDice(newDice);
        assertEquals(newDice, gameState.getDice());

        // Test players getter/setter
        List<Player> newPlayers = new ArrayList<>();
        Player player3 = new Player("Test Player 3");
        newPlayers.add(player3);
        gameState.setPlayers(newPlayers);
        assertEquals(newPlayers, gameState.getPlayers());

        // Test board getter/setter
        Gameboard newBoard = new Gameboard();
        gameState.setBoard(newBoard);
        assertEquals(newBoard, gameState.getBoard());

        // Test current player index getter/setter
        gameState.setCurrentPlayerIndex(0);
        assertEquals(0, gameState.getCurrentPlayerIndex());

        // Test invalid index
        gameState.setCurrentPlayerIndex(999); // Should not change as it's invalid
        assertEquals(0, gameState.getCurrentPlayerIndex());

        // Test game active getter/setter
        gameState.setGameActive(false);
        assertFalse(gameState.isGameActive());
    }

    @Test
    public void testGetChanceAndCommunityChestDecks() {
        // Check decks are not null
        assertNotNull(gameState.getChanceCardDeck());
        assertNotNull(gameState.getCommunityChestCardDeck());

        // Check decks have cards
        assertFalse(gameState.getChanceCardDeck().isEmpty());
        assertFalse(gameState.getCommunityChestCardDeck().isEmpty());
    }

    @Test
    public void testGetPropertyOwnership() {
        // Get property ownership map
        Map<Integer, String> propertyOwnership = gameState.getPropertyOwnership();

        // Check map is not null
        assertNotNull(propertyOwnership);
    }

    @Test
    public void testGetBank() {
        // Check bank is correct
        assertEquals(bank, gameState.getBank());
    }
}
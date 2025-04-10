package Model.Cards;

import Model.Board.Player;
import Model.GameState;
import Model.Board.Gameboard;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for the abstract Card class.
 */
public class CardTest {

    // Concrete implementation of abstract Card for testing
    private static class TestCard extends Card {
        public TestCard(String description) {
            super(description);
        }

        @Override
        public String getCardType() {
            return "Test Card Type";
        }

        @Override
        public String getDeck() {
            return "Test Deck";
        }

        @Override
        public void executeEffect(Player player, GameState gameState) {
            // Placeholder implementation
            player.addMoney(50);
        }
    }

    private TestCard testCard;
    private Player testPlayer;
    private GameState gameState;

    @Before
    public void setUp() {
        // Create test game components
        Gameboard gameboard = new Gameboard();
        List<Player> players = new ArrayList<>();
        testPlayer = new Player("Test Player");
        players.add(testPlayer);

        gameState = new GameState(players, gameboard);

        // Create a test card
        testCard = new TestCard("Test Card Description");
    }

    @Test
    public void testCardConstructor() {
        // Verify that the card is created with the correct description
        assertEquals("Card description should match constructor input",
                "Test Card Description", testCard.getDescription());
    }

    @Test
    public void testGetDescription() {
        // Test the getDescription method
        String description = testCard.getDescription();
        assertNotNull("Description should not be null", description);
        assertEquals("Description should match", "Test Card Description", description);
    }

    @Test
    public void testGetCardType() {
        // Test the getCardType method
        assertEquals("Card type should be 'Test Card Type'", "Test Card Type", testCard.getCardType());
    }

    @Test
    public void testGetDeck() {
        // Test the getDeck method
        assertEquals("Deck should be 'Test Deck'", "Test Deck", testCard.getDeck());
    }

    @Test
    public void testToString() {
        // Test the toString method
        assertEquals("toString should return the description",
                "Test Card Description", testCard.toString());
    }

    @Test
    public void testExecuteEffect() {
        // Record initial player money
        int initialMoney = testPlayer.getMoney();

        // Execute the effect (which adds 50 to player's money in our test implementation)
        testCard.executeEffect(testPlayer, gameState);

        // Verify the effect was executed
        assertEquals("Player should have received money",
                initialMoney + 50, testPlayer.getMoney());
    }

    @Test
    public void testCardWithDifferentDescriptions() {
        // Create multiple cards with different descriptions
        TestCard card1 = new TestCard("First Description");
        TestCard card2 = new TestCard("Second Description");

        // Verify descriptions are different
        assertNotEquals("Card descriptions should be unique",
                card1.getDescription(), card2.getDescription());
    }

    @Test
    public void testCardWithEmptyDescription() {
        // Create a card with an empty description
        TestCard emptyDescCard = new TestCard("");

        // Verify the empty description is allowed
        assertEquals("Empty description should be allowed", "", emptyDescCard.getDescription());
    }

    @Test
    public void testCardWithNullDescription() {
        // Create a card with null description
        TestCard nullDescCard = new TestCard(null);

        // Verify null description is allowed
        assertNull("Null description should be allowed", nullDescCard.getDescription());
    }
}
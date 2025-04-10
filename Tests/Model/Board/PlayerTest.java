package Model.Board;

import Model.Property.Property;
import Model.Spaces.RailroadSpace;
import Model.Spaces.UtilitySpace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Player class
 */
public class PlayerTest {

    private Player player;

    @Before
    public void setUp() {
        // Create a new player before each test
        player = new Player("Test Player");
    }

    @Test
    public void testInitialization() {
        assertEquals("Test Player", player.getName());
        assertEquals(1500, player.getMoney()); // Starting money should be 1500
        assertEquals(0, player.getPosition()); // Starting position should be 0 (GO)
        assertNull(player.getToken()); // Token should be null initially
        assertEquals(0, player.getProperties().size()); // Should have no properties initially
        assertFalse(player.hasGetOutOfJailFreeCard()); // Should not have a Get Out of Jail Free card initially
        assertEquals(0, player.getTurnsInJail()); // Should not be in jail initially
    }

    @Test
    public void testAddMoney() {
        // Test adding money to the player
        int initialMoney = player.getMoney();
        player.addMoney(500);
        assertEquals(initialMoney + 500, player.getMoney());
    }

    @Test
    public void testSubtractMoney() {
        // Test subtracting money when player has enough
        int initialMoney = player.getMoney();
        boolean result = player.subtractMoney(500);
        assertTrue("Player should have enough money", result);
        assertEquals(initialMoney - 500, player.getMoney());

        // Test subtracting money when player doesn't have enough
        result = player.subtractMoney(2000);
        assertFalse("Player should not have enough money", result);
        assertEquals(initialMoney - 500, player.getMoney()); // Money should not have changed
    }

    @Test
    public void testGetSet() {
        // Test various getters and setters

        // Position
        player.setPosition(10);
        assertEquals(10, player.getPosition());

        // Token
        player.setToken("Hat");
        assertEquals("Hat", player.getToken());

        // Jail Card
        player.setHasGetOutOfJailFreeCard(true);
        assertTrue(player.hasGetOutOfJailFreeCard());

        // Turns in Jail
        player.setTurnsInJail(2);
        assertEquals(2, player.getTurnsInJail());
    }

    @Test
    public void testMove() {
        // Test moving the player
        Gameboard board = new Gameboard();
        player.setPosition(0); // Start at GO
        player.move(7, board); // Move 7 spaces
        assertEquals(7, player.getPosition());

        // Test moving past GO (should wrap around)
        player.setPosition(39); // Last space
        player.move(3, board); // Move 3 spaces, wrapping to position 2
        assertEquals(2, player.getPosition());
    }

    @Test
    public void testBuyProperty() {
        // Create a property
        Property property = new Property("Test Property", 3, 200, "Brown");

        // Test buying the property
        int initialMoney = player.getMoney();
        boolean result = player.buyProperty(property);

        assertTrue("Player should be able to buy the property", result);
        assertEquals(initialMoney - 200, player.getMoney());
        assertEquals(player, property.getOwner());
        assertTrue(player.getProperties().contains(property));

        // Test buying a property that's too expensive
        // Use "Dark Blue" instead of "Blue" as it appears to be a valid color group
        Property expensiveProperty = new Property("Expensive Property", 5, 2000, "Dark Blue");

        // Create a player with little money for this test
        Player poorPlayer = new Player("Poor Player");
        // Subtract most of the money to make them poor
        poorPlayer.subtractMoney(1400);

        result = poorPlayer.buyProperty(expensiveProperty);
        assertFalse("Player should not be able to buy the expensive property", result);
        // Money should not change from the unsuccessful purchase
        assertEquals(100, poorPlayer.getMoney());
        assertNull(expensiveProperty.getOwner()); // Property should not have an owner
        assertFalse(poorPlayer.getProperties().contains(expensiveProperty));
    }

    @Test
    public void testBuyRailroad() {
        // Create a railroad
        RailroadSpace railroad = new RailroadSpace("Test Railroad", 5);

        // Test buying the railroad
        int initialMoney = player.getMoney();
        boolean result = player.buyRailroad(railroad);

        assertTrue("Player should be able to buy the railroad", result);
        assertEquals(initialMoney - railroad.getPrice(), player.getMoney());
        assertEquals(player, railroad.getOwner());
    }

    @Test
    public void testBuyUtility() {
        // Create a utility
        UtilitySpace utility = new UtilitySpace("Test Utility", 12);

        // Test buying the utility
        int initialMoney = player.getMoney();
        boolean result = player.buyUtility(utility);

        assertTrue("Player should be able to buy the utility", result);
        assertEquals(initialMoney - utility.getPrice(), player.getMoney());
        assertEquals(player, utility.getOwner());
    }

    @Test
    public void testPayRent() {
        // Create another player to receive rent
        Player owner = new Player("Owner");

        // Test paying rent
        int initialMoney = player.getMoney();
        int initialOwnerMoney = owner.getMoney();
        int rentAmount = 50;

        boolean result = player.payRent(owner, rentAmount);

        assertTrue("Player should be able to pay rent", result);
        assertEquals(initialMoney - rentAmount, player.getMoney());
        assertEquals(initialOwnerMoney + rentAmount, owner.getMoney());
    }

    @Test
    public void testReceiveRent() {
        // Test receiving rent
        int initialMoney = player.getMoney();
        int rentAmount = 50;

        player.receiveRent(rentAmount);

        assertEquals(initialMoney + rentAmount, player.getMoney());
    }

    @Test
    public void testIsBankrupt() {
        // Test bankruptcy status
        assertFalse("Player should not be bankrupt initially", player.isBankrupt());

        // Create a player and make them bankrupt through subtraction
        Player bankruptPlayer = new Player("Bankrupt Player");
        bankruptPlayer.subtractMoney(1500); // Subtract all money
        assertTrue("Player should be bankrupt with 0 money", bankruptPlayer.isBankrupt());
    }

    @Test
    public void testShouldGoToJail() {
        // Test jail conditions - this test needs to set up dice rolls
        // to test it properly, we'd need to do more complex mocking
        // This basic test just verifies the initial state
        assertFalse(player.shouldGoToJail());
    }

    @Test
    public void testToString() {
        // Test string representation contains expected values
        player.setToken("Hat");
        String playerString = player.toString();

        assertTrue(playerString.contains(player.getName()));
        assertTrue(playerString.contains(String.valueOf(player.getMoney())));
        assertTrue(playerString.contains(String.valueOf(player.getPosition())));
        assertTrue(playerString.contains(player.getToken()));
    }
}
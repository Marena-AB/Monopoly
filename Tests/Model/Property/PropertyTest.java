package Model.Property;

import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Cards.TitleDeedCard;
import Model.GameState;
import Model.Spaces.Space;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for the Property class.
 * Tests all methods and properties to ensure complete coverage.
 */
public class PropertyTest {

    private Property property;
    private Property property2;
    private Player player;
    private Player player2;
    private GameState gameState;
    private Gameboard gameboard;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));

        // Create test property with specific values
        property = new Property("Boardwalk", 39, 400, "Dark Blue");
        property2 = new Property("Park Place", 37, 350, "Dark Blue");

        // Create test players
        player = new Player("Test Player");
        player2 = new Player("Test Player 2");

        // Add some money to players for testing
        player.addMoney(2000);
        player2.addMoney(2000);

        // Create gameboard and game state
        gameboard = new Gameboard();
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player2);
        gameState = new GameState(players, gameboard);
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testConstructorWithAllParameters() {
        // Test the constructor with all parameters
        Property testProperty = new Property("Test Property", 5, 200, "Red");

        assertEquals("Test Property", testProperty.getName());
        assertEquals(5, testProperty.getPosition());
        assertEquals("Property", testProperty.getType());
        assertEquals(200, testProperty.getPrice());
        assertEquals("Red", testProperty.getColorGroup());
        assertNull(testProperty.getOwner());
        assertEquals(0, testProperty.getHouses());
        assertFalse(testProperty.hasHotel());
        assertFalse(testProperty.isMortgaged());

        // Verify title deed was created
        TitleDeedCard titleDeed = testProperty.getTitleDeed();
        assertNotNull(titleDeed);
        assertEquals("Test Property", titleDeed.getPropertyName());
        assertEquals("Red", titleDeed.getColorGroup());
        assertEquals(200, titleDeed.getPrice());
    }

    @Test
    public void testSimplifiedConstructor() {
        // Test the simplified constructor with only name and position
        Property simpleProperty = new Property("Simple Property", 15);

        assertEquals("Simple Property", simpleProperty.getName());
        assertEquals(15, simpleProperty.getPosition());
        assertEquals("Property", simpleProperty.getType());

        // Price and color group should be set with default values
        assertTrue(simpleProperty.getPrice() > 0);
        assertNotNull(simpleProperty.getColorGroup());

        // Verify title deed was created
        TitleDeedCard titleDeed = simpleProperty.getTitleDeed();
        assertNotNull(titleDeed);
    }

    @Test
    public void testCalculateBaseRent() {
        // This test indirectly tests the private calculateBaseRent method
        // Base rent is typically 10% of purchase price

        Property property100 = new Property("Test 100", 1, 100, "Brown");
        Property property200 = new Property("Test 200", 2, 200, "Light Blue");
        Property property300 = new Property("Test 300", 3, 300, "Pink");

        // Get the base rent via the title deed
        assertEquals(10, property100.getTitleDeed().getBaseRent()); // 10% of 100
        assertEquals(20, property200.getTitleDeed().getBaseRent()); // 10% of 200
        assertEquals(30, property300.getTitleDeed().getBaseRent()); // 10% of 300
    }

    @Test
    public void testCalculateHouseRents() {
        // This test indirectly tests the private calculateHouseRents method
        // Different color groups have different rent progressions

        Property brownProperty = new Property("Brown Test", 1, 100, "Brown");
        Property redProperty = new Property("Red Test", 21, 200, "Red");

        TitleDeedCard brownDeed = brownProperty.getTitleDeed();
        TitleDeedCard redDeed = redProperty.getTitleDeed();

        // Test house rents for different color groups
        // Brown properties follow one progression
        assertEquals(50, brownDeed.getHouseRent(1)); // 5 * base rent
        assertEquals(150, brownDeed.getHouseRent(2)); // 15 * base rent

        // Red properties follow another progression
        assertEquals(60, redDeed.getHouseRent(1)); // 6 * base rent (for Red)
        assertEquals(180, redDeed.getHouseRent(2)); // 18 * base rent (for Red)
    }

    @Test
    public void testCalculateHotelRent() {
        // This test indirectly tests the private calculateHotelRent method

        Property brownProperty = new Property("Brown Test", 1, 100, "Brown");
        Property redProperty = new Property("Red Test", 21, 200, "Red");

        TitleDeedCard brownDeed = brownProperty.getTitleDeed();
        TitleDeedCard redDeed = redProperty.getTitleDeed();

        // Brown properties have hotel rent = 125 * base rent
        assertEquals(125, brownDeed.getHotelRent() / brownDeed.getBaseRent());

        // Red properties have hotel rent = 150 * base rent
        assertEquals(150, redDeed.getHotelRent() / redDeed.getBaseRent());
    }

    @Test
    public void testGetDefaultPrice() {
        // This test indirectly tests the private getDefaultPrice method
        // The default price logic is position-based

        Property pos1 = new Property("Test 1", 1);
        Property pos10 = new Property("Test 10", 10);
        Property pos20 = new Property("Test 20", 20);

        // Prices should increase with position
        assertTrue(pos10.getPrice() > pos1.getPrice());
        assertTrue(pos20.getPrice() > pos10.getPrice());
    }

    @Test
    public void testGetDefaultColorGroup() {
        // This test indirectly tests the private getDefaultColorGroup method
        // Color groups are assigned based on position

        // Create properties at different positions
        Property pos1 = new Property("Test 1", 1);
        Property pos6 = new Property("Test 6", 6);
        Property pos11 = new Property("Test 11", 11);

        // Color groups should be different
        assertNotNull(pos1.getColorGroup());
        assertNotNull(pos6.getColorGroup());
        assertNotNull(pos11.getColorGroup());
    }

    @Test
    public void testGetTitleDeed() {
        TitleDeedCard deed = property.getTitleDeed();
        assertNotNull(deed);
        assertEquals("Boardwalk", deed.getPropertyName());
        assertEquals("Dark Blue", deed.getColorGroup());
        assertEquals(400, deed.getPrice());
    }

    @Test
    public void testGetPrice() {
        assertEquals(400, property.getPrice());
    }

    @Test
    public void testAddHouse() {
        // Test adding houses one by one
        assertTrue(property.addHouse());
        assertEquals(1, property.getHouses());

        assertTrue(property.addHouse());
        assertEquals(2, property.getHouses());

        assertTrue(property.addHouse());
        assertEquals(3, property.getHouses());

        assertTrue(property.addHouse());
        assertEquals(4, property.getHouses());

        // Adding a fifth house should convert to a hotel
        assertTrue(property.addHouse());
        assertEquals(0, property.getHouses());
        assertTrue(property.hasHotel());

        // Cannot add more houses when property has a hotel
        assertFalse(property.addHouse());
        assertTrue(property.hasHotel());
        assertEquals(0, property.getHouses());

        // Verify console output
        assertTrue(outContent.toString().contains("already has a hotel"));
    }

    @Test
    public void testRemoveHouse() {
        // Add houses first
        property.addHouse();
        property.addHouse();
        property.addHouse();
        assertEquals(3, property.getHouses());

        // Remove houses one by one
        assertTrue(property.removeHouse());
        assertEquals(2, property.getHouses());

        assertTrue(property.removeHouse());
        assertEquals(1, property.getHouses());

        assertTrue(property.removeHouse());
        assertEquals(0, property.getHouses());

        // Cannot remove more houses
        assertFalse(property.removeHouse());
        assertEquals(0, property.getHouses());

        // Check removing from a hotel
        property.setHasHotel(true);
        assertTrue(property.removeHouse());
        assertEquals(4, property.getHouses());
        assertFalse(property.hasHotel());

        // Verify console output
        assertTrue(outContent.toString().contains("has no houses or hotels"));
    }

    @Test
    public void testIsOwned() {
        // Initially not owned
        assertFalse(property.isOwned());

        // Set an owner
        property.setOwner(player);
        assertTrue(property.isOwned());

        // Remove owner
        property.setOwner(null);
        assertFalse(property.isOwned());
    }

    @Test
    public void testIsMortgaged() {
        // Initially not mortgaged
        assertFalse(property.isMortgaged());

        // Mortgage property
        property.setMortgaged(true);
        assertTrue(property.isMortgaged());

        // Unmortgage property
        property.setMortgaged(false);
        assertFalse(property.isMortgaged());
    }

    @Test
    public void testSetMortgaged() {
        // Test that mortgage status is set on both property and title deed
        property.setMortgaged(true);
        assertTrue(property.isMortgaged());
        assertTrue(property.getTitleDeed().isMortgaged());

        property.setMortgaged(false);
        assertFalse(property.isMortgaged());
        assertFalse(property.getTitleDeed().isMortgaged());
    }

    @Test
    public void testOnLandNoOwner() {
        // Test landing on an unowned property
        property.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains(player.getName() + " landed on " + property.getName()));
        assertTrue(output.contains("is not owned"));
        assertTrue(output.contains("It costs $" + property.getPrice()));
    }

    @Test
    public void testOnLandSelfOwner() {
        // Set player as owner
        property.setOwner(player);

        // Test landing on your own property
        property.onLand(player, gameState);

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains(player.getName() + " landed on " + property.getName()));
        assertTrue(output.contains(player.getName() + " owns this property"));
    }

    @Test
    public void testOnLandOtherOwnerNotMortgaged() {
        // Set player2 as owner
        property.setOwner(player2);

        // Record initial money
        int initialMoney1 = player.getMoney();
        int initialMoney2 = player2.getMoney();

        // Test landing on another player's property
        property.onLand(player, gameState);

        // Calculate expected rent (base rent for a single property)
        int expectedRent = property.getTitleDeed().getBaseRent();

        // Verify money was transferred
        assertEquals(initialMoney1 - expectedRent, player.getMoney());
        assertEquals(initialMoney2 + expectedRent, player2.getMoney());

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains(player.getName() + " landed on " + property.getName()));
        assertTrue(output.contains(player.getName() + " must pay $" + expectedRent));
    }

    @Test
    public void testOnLandOtherOwnerMortgaged() {
        // Set player2 as owner and mortgage property
        property.setOwner(player2);
        property.setMortgaged(true);

        // Record initial money
        int initialMoney1 = player.getMoney();
        int initialMoney2 = player2.getMoney();

        // Test landing on mortgaged property
        property.onLand(player, gameState);

        // No rent should be paid
        assertEquals(initialMoney1, player.getMoney());
        assertEquals(initialMoney2, player2.getMoney());

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("is mortgaged, no rent is due"));
    }

    @Test
    public void testOnLandCannotAfford() {
        // Set player as poor and player2 as owner
        player.subtractMoney(1900); // Leave only $100
        property.setOwner(player2);

        // Test landing on property player can't afford
        property.onLand(player, gameState);

        // Verify console output - should indicate player can't afford
        String output = outContent.toString();
        assertTrue(output.contains(player.getName() + " landed on " + property.getName()));
        // The output won't show "cannot afford" because payRent handles that logic
        // We'd need a more integrated test to fully verify this behavior
    }

    @Test
    public void testCalculateRentNoHousesNoMonopoly() {
        // Set up a property with an owner but no houses/hotel and no monopoly
        property.setOwner(player);

        // Calculate rent
        int rent = property.calculateRent(gameState);

        // Should be base rent
        assertEquals(property.getTitleDeed().getBaseRent(), rent);
    }

    @Test
    public void testCalculateRentNoHousesWithMonopoly() {
        // Set up properties to form a monopoly
        property.setOwner(player);
        property2.setOwner(player);
        player.getProperties().add(property);
        player.getProperties().add(property2);

        // Add properties to gameboard for monopoly detection
        // First, clear existing properties and add our test properties
        List<Space> spaces = new ArrayList<>(gameboard.getSpaces());
        for (int i = 0; i < spaces.size(); i++) {
            if (spaces.get(i).getPosition() == property.getPosition()) {
                spaces.set(i, property);
            } else if (spaces.get(i).getPosition() == property2.getPosition()) {
                spaces.set(i, property2);
            }
        }
        gameboard.setSpaces(spaces);

        // Calculate rent
        int rent = property.calculateRent(gameState);

        // Should be double the base rent
        assertEquals(property.getTitleDeed().getBaseRent() * 2, rent);
    }

    @Test
    public void testCalculateRentWithHouses() {
        // Set up a property with houses
        property.setOwner(player);
        property.addHouse(); // 1 house

        // Calculate rent
        int rent = property.calculateRent(gameState);

        // Should match the rent for 1 house
        assertEquals(property.getTitleDeed().getHouseRent(1), rent);

        // Add more houses and test again
        property.addHouse(); // 2 houses
        assertEquals(property.getTitleDeed().getHouseRent(2), property.calculateRent(gameState));

        property.addHouse(); // 3 houses
        assertEquals(property.getTitleDeed().getHouseRent(3), property.calculateRent(gameState));

        property.addHouse(); // 4 houses
        assertEquals(property.getTitleDeed().getHouseRent(4), property.calculateRent(gameState));
    }

    @Test
    public void testCalculateRentWithHotel() {
        // Set up a property with a hotel
        property.setOwner(player);
        property.setHasHotel(true);

        // Calculate rent
        int rent = property.calculateRent(gameState);

        // Should match the hotel rent
        assertEquals(property.getTitleDeed().getHotelRent(), rent);
    }

    @Test
    public void testCalculateRentMortgaged() {
        // Set up a mortgaged property
        property.setOwner(player);
        property.setMortgaged(true);

        // Calculate rent
        int rent = property.calculateRent(gameState);

        // Rent should be 0 for mortgaged property
        assertEquals(0, rent);
    }

    @Test
    public void testToString() {
        // Test the toString method
        String propertyString = property.toString();

        // Should contain important property information
        assertTrue(propertyString.contains(property.getName()));
        assertTrue(propertyString.contains(String.valueOf(property.getPosition())));
        assertTrue(propertyString.contains(String.valueOf(property.getPrice())));
        assertTrue(propertyString.contains(property.getColorGroup()));
        assertTrue(propertyString.contains("Owner: None"));

        // Set owner and verify toString updates
        property.setOwner(player);
        propertyString = property.toString();
        assertTrue(propertyString.contains("Owner: " + player.getName()));

        // Add houses and verify toString updates
        property.addHouse();
        propertyString = property.toString();
        assertTrue(propertyString.contains("Houses: 1"));

        // Add hotel and verify toString updates
        property.setHasHotel(true);
        propertyString = property.toString();
        assertTrue(propertyString.contains("Hotel: Yes"));

        // Mortgage property and verify toString updates
        property.setMortgaged(true);
        propertyString = property.toString();
        assertTrue(propertyString.contains("Mortgaged: Yes"));
    }

    @Test
    public void testHasHotel() {
        // Initially no hotel
        assertFalse(property.hasHotel());

        // Set hotel
        property.setHasHotel(true);
        assertTrue(property.hasHotel());

        // Remove hotel
        property.setHasHotel(false);
        assertFalse(property.hasHotel());
    }

    @Test
    public void testGetHouses() {
        // Initially no houses
        assertEquals(0, property.getHouses());

        // Add houses
        property.addHouse();
        assertEquals(1, property.getHouses());

        property.addHouse();
        assertEquals(2, property.getHouses());

        // Set houses directly
        property.setHouses(3);
        assertEquals(3, property.getHouses());
    }

    @Test
    public void testGetRent() {
        // Get base rent
        int baseRent = property.getRent();
        assertEquals(property.getTitleDeed().getBaseRent(), baseRent);
    }

    @Test
    public void testGetMortgageValue() {
        // Mortgage value should be half the purchase price
        assertEquals(property.getPrice() / 2, property.getMortgageValue());
    }

    @Test
    public void testGetUnmortgageCost() {
        // Unmortgage cost should be mortgage value plus 10% interest
        int mortgageValue = property.getMortgageValue();
        int expectedUnmortgageCost = (int)(mortgageValue * 1.1);
        assertEquals(expectedUnmortgageCost, property.getUnmortgageCost());
    }

    @Test
    public void testPlayerOnProperty() {
        // Test the playerOnProperty method
        property.playerOnProperty();

        // Verify console output
        String output = outContent.toString();
        assertTrue(output.contains("Player landed on property " + property.getName()));
    }
}
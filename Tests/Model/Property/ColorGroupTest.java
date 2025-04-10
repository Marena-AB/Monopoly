package Model.Property;

import Model.Board.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for the ColorGroup class.
 * Tests all methods and properties to ensure complete coverage.
 */
public class ColorGroupTest {

    private ColorGroup brownGroup;
    private ColorGroup redGroup;
    private Property mediterranean;
    private Property baltic;
    private Property kentucky;
    private Property indiana;
    private Property illinois;
    private Player player1;
    private Player player2;

    @Before
    public void setUp() {
        // Create color groups
        brownGroup = new ColorGroup(PropertyColor.BROWN);
        redGroup = new ColorGroup(PropertyColor.RED);

        // Create properties
        mediterranean = new Property("Mediterranean Avenue", 1, 60, "Brown");
        baltic = new Property("Baltic Avenue", 3, 60, "Brown");

        kentucky = new Property("Kentucky Avenue", 21, 220, "Red");
        indiana = new Property("Indiana Avenue", 23, 220, "Red");
        illinois = new Property("Illinois Avenue", 24, 240, "Red");

        // Create players
        player1 = new Player("Test Player 1");
        player2 = new Player("Test Player 2");
    }

    @Test
    public void testConstructor() {
        // Verify color group is created correctly
        assertEquals(PropertyColor.BROWN, brownGroup.getColor());
        assertEquals("Brown", brownGroup.getDisplayName());
        assertTrue(brownGroup.getProperties().isEmpty());

        assertEquals(PropertyColor.RED, redGroup.getColor());
        assertEquals("Red", redGroup.getDisplayName());
        assertTrue(redGroup.getProperties().isEmpty());
    }

    @Test
    public void testGetColor() {
        assertEquals(PropertyColor.BROWN, brownGroup.getColor());
        assertEquals(PropertyColor.RED, redGroup.getColor());
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("Brown", brownGroup.getDisplayName());
        assertEquals("Red", redGroup.getDisplayName());
    }

    @Test
    public void testGetProperties() {
        // Initially empty
        assertTrue(brownGroup.getProperties().isEmpty());

        // Add properties
        brownGroup.addProperty(mediterranean);
        brownGroup.addProperty(baltic);

        // Verify properties were added
        List<Property> properties = brownGroup.getProperties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains(mediterranean));
        assertTrue(properties.contains(baltic));

        // Verify the returned list is a copy (can't modify original)
        properties.clear();
        assertEquals(2, brownGroup.getProperties().size()); // Original still has 2 properties
    }

    @Test
    public void testAddProperty() {
        // Add properties to brown group
        brownGroup.addProperty(mediterranean);
        brownGroup.addProperty(baltic);

        // Verify properties were added
        assertEquals(2, brownGroup.getProperties().size());
        assertTrue(brownGroup.getProperties().contains(mediterranean));
        assertTrue(brownGroup.getProperties().contains(baltic));

        // Verify property color group was set
        assertEquals("Brown", mediterranean.getColorGroup());
        assertEquals("Brown", baltic.getColorGroup());

        // Test adding null property (should be ignored)
        int sizeBefore = brownGroup.getProperties().size();
        brownGroup.addProperty(null);
        assertEquals(sizeBefore, brownGroup.getProperties().size());

        // Test adding duplicate property (should be ignored)
        brownGroup.addProperty(mediterranean);
        assertEquals(2, brownGroup.getProperties().size()); // Still 2 properties
    }

    @Test
    public void testIsMonopoly() {
        // Set up properties
        brownGroup.addProperty(mediterranean);
        brownGroup.addProperty(baltic);

        // Initially no player owns all properties
        assertFalse(brownGroup.isMonopoly(player1));

        // Player 1 owns one property
        mediterranean.setOwner(player1);
        assertFalse(brownGroup.isMonopoly(player1));

        // Player 1 owns both properties
        baltic.setOwner(player1);
        assertTrue(brownGroup.isMonopoly(player1));

        // Player 2 owns one property
        baltic.setOwner(player2);
        assertFalse(brownGroup.isMonopoly(player1));
        assertFalse(brownGroup.isMonopoly(player2));
    }

    @Test
    public void testIsMonopolyEdgeCases() {
        // Test with null player
        assertFalse(brownGroup.isMonopoly(null));

        // Test with empty group
        assertFalse(brownGroup.isMonopoly(player1));

        // Test with null owner
        brownGroup.addProperty(mediterranean);
        brownGroup.addProperty(baltic);
        mediterranean.setOwner(null);
        baltic.setOwner(null);
        assertFalse(brownGroup.isMonopoly(player1));
    }

    @Test
    public void testGetHousePrice() {
        // House prices should match PropertyColor values
        assertEquals(50, brownGroup.getHousePrice());
        assertEquals(150, redGroup.getHousePrice());
    }

    @Test
    public void testGetHotelPrice() {
        // Hotel prices should be the same as house prices
        assertEquals(brownGroup.getHousePrice(), brownGroup.getHotelPrice());
        assertEquals(redGroup.getHousePrice(), redGroup.getHotelPrice());
    }

    @Test
    public void testGetExpectedSize() {
        // Brown should have 2 properties
        assertEquals(2, brownGroup.getExpectedSize());

        // Red should have 3 properties
        assertEquals(3, redGroup.getExpectedSize());
    }

    @Test
    public void testIsEvenlyDeveloped() {
        // Set up brown group with both properties
        brownGroup.addProperty(mediterranean);
        brownGroup.addProperty(baltic);

        // Set up red group with all three properties
        redGroup.addProperty(kentucky);
        redGroup.addProperty(indiana);
        redGroup.addProperty(illinois);

        // All properties have 0 houses - should be evenly developed
        assertTrue(brownGroup.isEvenlyDeveloped());
        assertTrue(redGroup.isEvenlyDeveloped());

        // All have 1 house - should be evenly developed
        mediterranean.setHouses(1);
        baltic.setHouses(1);
        assertTrue(brownGroup.isEvenlyDeveloped());

        // Houses differ by 1 - should still be evenly developed
        mediterranean.setHouses(2);
        assertTrue(brownGroup.isEvenlyDeveloped());

        // Houses differ by more than 1 - should not be evenly developed
        mediterranean.setHouses(3);
        assertFalse(brownGroup.isEvenlyDeveloped());

        // Reset houses
        mediterranean.setHouses(0);
        baltic.setHouses(0);

        // Test with hotels
        mediterranean.setHasHotel(true);
        baltic.setHasHotel(false);
        assertFalse(brownGroup.isEvenlyDeveloped());

        // All have hotels - should be evenly developed
        baltic.setHasHotel(true);
        assertTrue(brownGroup.isEvenlyDeveloped());
    }

    @Test
    public void testIsEvenlyDevelopedEdgeCases() {
        // Empty group should be considered evenly developed
        assertTrue(brownGroup.isEvenlyDeveloped());

        // Single property group should be evenly developed
        brownGroup.addProperty(mediterranean);
        assertTrue(brownGroup.isEvenlyDeveloped());
    }
}
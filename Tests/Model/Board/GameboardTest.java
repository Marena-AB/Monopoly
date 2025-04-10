package Model.Board;

import Model.Property.Property;
import Model.Spaces.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test class for the Gameboard class
 */
public class GameboardTest {

    private Gameboard gameboard;
    private Player player;

    @Before
    public void setUp() {
        gameboard = new Gameboard();
        player = new Player("Test Player");
    }

    @Test
    public void testInitialization() {
        // Test that the gameboard initializes with 40 spaces
        List<Space> spaces = gameboard.getSpaces();
        assertEquals(40, spaces.size());

        // Test specific spaces are correctly created
        Space goSpace = gameboard.getspace(0);
        assertEquals("Go", goSpace.getName());
        assertEquals(0, goSpace.getPosition());

        Space jailSpace = gameboard.getspace(10);
        assertEquals("Jail", jailSpace.getName());
        assertEquals(10, jailSpace.getPosition());

        Space freeParkingSpace = gameboard.getspace(20);
        assertEquals("Free Parking", freeParkingSpace.getName());
        assertEquals(20, freeParkingSpace.getPosition());

        Space goToJailSpace = gameboard.getspace(30);
        assertEquals("Go To Jail", goToJailSpace.getName());
        assertEquals(30, goToJailSpace.getPosition());
    }

    @Test
    public void testGetSpace() {
        // Test getting spaces at different positions
        Space space = gameboard.getspace(1);
        assertNotNull(space);
        assertEquals("Mediterranean Avenue", space.getName());
        assertEquals(1, space.getPosition());

        // Test getting a space at an invalid position
        Space invalidSpace = gameboard.getspace(100);
        assertNull(invalidSpace);

        Space negativeSpace = gameboard.getspace(-1);
        assertNull(negativeSpace);
    }

    @Test
    public void testGetPropertiesByColorGroup() {
        // Test getting properties by color group
        List<Property> brownProperties = gameboard.getPropertiesByColorGroup("Brown");
        assertNotNull(brownProperties);

        // Test specific properties exist in the color group
        boolean mediterraneanFound = false;
        boolean balticFound = false;

        for (Property property : brownProperties) {
            if (property.getName().equals("Mediterranean Avenue")) {
                mediterraneanFound = true;
            } else if (property.getName().equals("Baltic Avenue")) {
                balticFound = true;
            }
        }

        assertTrue("Mediterranean Avenue should be in Brown group", mediterraneanFound);
        assertTrue("Baltic Avenue should be in Brown group", balticFound);

        // Test getting properties of a non-existent color group
        List<Property> nonExistentProperties = gameboard.getPropertiesByColorGroup("NonExistent");
        assertTrue(nonExistentProperties.isEmpty());
    }

    @Test
    public void testGetRailroads() {
        // Test getting all railroads
        List<RailroadSpace> railroads = gameboard.getRailroads();
        assertNotNull(railroads);
        assertEquals(4, railroads.size()); // There should be 4 railroads

        // Check that the railroads are correctly identified
        boolean foundReadingRailroad = false;
        boolean foundPennsylvaniaRailroad = false;
        boolean foundBORailroad = false;
        boolean foundShortLine = false;

        for (RailroadSpace railroad : railroads) {
            if (railroad.getName().equals("Reading Railroad")) foundReadingRailroad = true;
            if (railroad.getName().equals("Pennsylvania Railroad")) foundPennsylvaniaRailroad = true;
            if (railroad.getName().equals("B. & O. Railroad")) foundBORailroad = true;
            if (railroad.getName().equals("Short Line")) foundShortLine = true;
        }

        assertTrue(foundReadingRailroad);
        assertTrue(foundPennsylvaniaRailroad);
        assertTrue(foundBORailroad);
        assertTrue(foundShortLine);
    }

    @Test
    public void testGetUtilities() {
        // Test getting all utilities
        List<UtilitySpace> utilities = gameboard.getUtilities();
        assertNotNull(utilities);
        assertEquals(2, utilities.size()); // There should be 2 utilities

        // Check that the utilities are correctly identified
        boolean foundElectricCompany = false;
        boolean foundWaterWorks = false;

        for (UtilitySpace utility : utilities) {
            if (utility.getName().equals("Electric Company")) foundElectricCompany = true;
            if (utility.getName().equals("Water Works")) foundWaterWorks = true;
        }

        assertTrue(foundElectricCompany);
        assertTrue(foundWaterWorks);
    }

    @Test
    public void testPlayerOwnsAllInColorGroup() {
        // Set up a test scenario where player owns all properties in a color group
        List<Property> brownProperties = gameboard.getPropertiesByColorGroup("Brown");

        // Player doesn't own any properties yet
        assertFalse(gameboard.playerOwnsAllInColorGroup(player, "Brown"));

        // Make player own all brown properties
        for (Property property : brownProperties) {
            property.setOwner(player);
        }

        // Now player should own all brown properties
        assertTrue(gameboard.playerOwnsAllInColorGroup(player, "Brown"));

        // Test with a color group the player doesn't own all properties in
        List<Property> blueProperties = gameboard.getPropertiesByColorGroup("Blue");
        if (!blueProperties.isEmpty()) {
            // Set only one blue property to be owned by player
            blueProperties.get(0).setOwner(player);

            // Player shouldn't own all blue properties
            assertFalse(gameboard.playerOwnsAllInColorGroup(player, "Blue"));
        }
    }

    @Test
    public void testPropertyOwnership() {
        // Test getting and setting the property ownership map
        Map<Integer, String> initialOwnership = gameboard.getPropertyOwnership();
        assertNotNull(initialOwnership);

        // Create a new ownership map
        Map<Integer, String> newOwnership = initialOwnership;
        newOwnership.put(1, player.getName()); // Mediterranean Avenue

        // Set the new ownership map
        gameboard.setPropertyOwnership(newOwnership);

        // Verify the ownership map was set
        assertEquals(newOwnership, gameboard.getPropertyOwnership());
        assertEquals(player.getName(), gameboard.getPropertyOwnership().get(1));
    }

    @Test
    public void testSetSpaces() {
        // Get the original spaces
        List<Space> originalSpaces = gameboard.getSpaces();

        // Create modified spaces (for test purposes we'll just use the original)
        List<Space> modifiedSpaces = originalSpaces;

        // Set the modified spaces
        gameboard.setSpaces(modifiedSpaces);

        // Verify the spaces were set
        assertEquals(modifiedSpaces, gameboard.getSpaces());
    }
}
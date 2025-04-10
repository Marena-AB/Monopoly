package Model.Property;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the PropertyColor enum.
 * Tests all enum values, methods, and properties to ensure complete coverage.
 */
public class PropertyColorTest {

    @Test
    public void testEnumValues() {
        // Verify all enum values exist
        PropertyColor[] colors = PropertyColor.values();
        assertEquals(8, colors.length);

        // Check for specific values
        assertEquals(PropertyColor.BROWN, PropertyColor.valueOf("BROWN"));
        assertEquals(PropertyColor.LIGHT_BLUE, PropertyColor.valueOf("LIGHT_BLUE"));
        assertEquals(PropertyColor.PINK, PropertyColor.valueOf("PINK"));
        assertEquals(PropertyColor.ORANGE, PropertyColor.valueOf("ORANGE"));
        assertEquals(PropertyColor.RED, PropertyColor.valueOf("RED"));
        assertEquals(PropertyColor.YELLOW, PropertyColor.valueOf("YELLOW"));
        assertEquals(PropertyColor.GREEN, PropertyColor.valueOf("GREEN"));
        assertEquals(PropertyColor.DARK_BLUE, PropertyColor.valueOf("DARK_BLUE"));
    }

    @Test
    public void testGetDisplayName() {
        // Test each color's display name
        assertEquals("Brown", PropertyColor.BROWN.getDisplayName());
        assertEquals("Light Blue", PropertyColor.LIGHT_BLUE.getDisplayName());
        assertEquals("Pink", PropertyColor.PINK.getDisplayName());
        assertEquals("Orange", PropertyColor.ORANGE.getDisplayName());
        assertEquals("Red", PropertyColor.RED.getDisplayName());
        assertEquals("Yellow", PropertyColor.YELLOW.getDisplayName());
        assertEquals("Green", PropertyColor.GREEN.getDisplayName());
        assertEquals("Dark Blue", PropertyColor.DARK_BLUE.getDisplayName());
    }

    @Test
    public void testGetGroupSize() {
        // Brown and Dark Blue have 2 properties
        assertEquals(2, PropertyColor.BROWN.getGroupSize());
        assertEquals(2, PropertyColor.DARK_BLUE.getGroupSize());

        // All other colors have 3 properties
        assertEquals(3, PropertyColor.LIGHT_BLUE.getGroupSize());
        assertEquals(3, PropertyColor.PINK.getGroupSize());
        assertEquals(3, PropertyColor.ORANGE.getGroupSize());
        assertEquals(3, PropertyColor.RED.getGroupSize());
        assertEquals(3, PropertyColor.YELLOW.getGroupSize());
        assertEquals(3, PropertyColor.GREEN.getGroupSize());
    }

    @Test
    public void testGetHousePrice() {
        // Test house prices for each color group
        assertEquals(50, PropertyColor.BROWN.getHousePrice());
        assertEquals(50, PropertyColor.LIGHT_BLUE.getHousePrice());

        assertEquals(100, PropertyColor.PINK.getHousePrice());
        assertEquals(100, PropertyColor.ORANGE.getHousePrice());

        assertEquals(150, PropertyColor.RED.getHousePrice());
        assertEquals(150, PropertyColor.YELLOW.getHousePrice());

        assertEquals(200, PropertyColor.GREEN.getHousePrice());
        assertEquals(200, PropertyColor.DARK_BLUE.getHousePrice());
    }

    @Test
    public void testFromDisplayName() {
        // Test successful lookups
        assertEquals(PropertyColor.BROWN, PropertyColor.fromDisplayName("Brown"));
        assertEquals(PropertyColor.LIGHT_BLUE, PropertyColor.fromDisplayName("Light Blue"));
        assertEquals(PropertyColor.PINK, PropertyColor.fromDisplayName("Pink"));
        assertEquals(PropertyColor.ORANGE, PropertyColor.fromDisplayName("Orange"));
        assertEquals(PropertyColor.RED, PropertyColor.fromDisplayName("Red"));
        assertEquals(PropertyColor.YELLOW, PropertyColor.fromDisplayName("Yellow"));
        assertEquals(PropertyColor.GREEN, PropertyColor.fromDisplayName("Green"));
        assertEquals(PropertyColor.DARK_BLUE, PropertyColor.fromDisplayName("Dark Blue"));

        // Test case insensitivity
        assertEquals(PropertyColor.BROWN, PropertyColor.fromDisplayName("brown"));
        assertEquals(PropertyColor.LIGHT_BLUE, PropertyColor.fromDisplayName("light blue"));
        assertEquals(PropertyColor.DARK_BLUE, PropertyColor.fromDisplayName("DARK BLUE"));

        // Test invalid input
        assertNull(PropertyColor.fromDisplayName("Purple")); // Non-existent color
        assertNull(PropertyColor.fromDisplayName("")); // Empty string
        assertNull(PropertyColor.fromDisplayName(null)); // Null input
    }

    @Test
    public void testEnumConstruction() {
        // Verify construction of enum values
        assertEquals("Brown", PropertyColor.BROWN.getDisplayName());
        assertEquals(2, PropertyColor.BROWN.getGroupSize());

        assertEquals("Light Blue", PropertyColor.LIGHT_BLUE.getDisplayName());
        assertEquals(3, PropertyColor.LIGHT_BLUE.getGroupSize());
    }

    @Test
    public void testHousePriceDefault() {
        // Create a local mock enum to test the default branch of getHousePrice
        // This is just for code coverage, as all real enum values have specific prices

        // The default return value in the switch statement is 0
        // Cannot directly test in a JUnit test since we can't add to the enum

        // Just verify all existing values return non-zero prices
        PropertyColor[] allColors = PropertyColor.values();
        for (PropertyColor color : allColors) {
            assertTrue("House price should be greater than 0 for " + color,
                    color.getHousePrice() > 0);
        }
    }
}
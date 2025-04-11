package Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Houses class.
 * Tests all methods to ensure complete coverage.
 */
public class HousesTest {

    private Houses houses;

    @Before
    public void setUp() {
        // Create a fresh Houses object before each test
        houses = new Houses();
    }

    @Test
    public void testInitialization() {
        // Test that Houses initializes with the correct number of houses
        assertEquals(32, houses.getAvailableHouses());
        assertFalse(houses.isShortage());
    }

    @Test
    public void testUseHouses_Success() {
        // Test using houses when enough are available
        boolean result = houses.useHouses(5);
        assertTrue(result);
        assertEquals(27, houses.getAvailableHouses());
    }

    @Test
    public void testUseHouses_NotEnough() {
        // First use most houses
        houses.useHouses(30);
        assertEquals(2, houses.getAvailableHouses());

        // Now try to use more than available
        boolean result = houses.useHouses(3);
        assertFalse(result);
        assertEquals(2, houses.getAvailableHouses()); // Count should remain the same
    }

    @Test
    public void testUseAllHouses() {
        // Use all houses
        boolean result = houses.useHouses(32);
        assertTrue(result);
        assertEquals(0, houses.getAvailableHouses());

        // Verify shortage is detected
        assertTrue(houses.isShortage());
    }

    @Test
    public void testReturnHouses() {
        // First use some houses
        houses.useHouses(10);
        assertEquals(22, houses.getAvailableHouses());

        // Return houses
        houses.returnHouses(5);
        assertEquals(27, houses.getAvailableHouses());
    }

    @Test
    public void testReturnHouses_ExceedMax() {
        // First use some houses
        houses.useHouses(10);
        assertEquals(22, houses.getAvailableHouses());

        // Return more houses than used (should cap at max)
        houses.returnHouses(20);
        assertEquals(32, houses.getAvailableHouses());

        // Try to return more (should remain at max)
        houses.returnHouses(5);
        assertEquals(32, houses.getAvailableHouses());
    }

    @Test
    public void testReset() {
        // Use some houses
        houses.useHouses(15);
        assertEquals(17, houses.getAvailableHouses());

        // Reset and verify count is back to 32
        houses.reset();
        assertEquals(32, houses.getAvailableHouses());
    }

    @Test
    public void testGetHousePrice_BrownLightBlue() {
        assertEquals(50, Houses.getHousePrice("Brown"));
        assertEquals(50, Houses.getHousePrice("Light Blue"));

        // Test case insensitivity
        assertEquals(50, Houses.getHousePrice("brown"));
        assertEquals(50, Houses.getHousePrice("light blue"));
    }

    @Test
    public void testGetHousePrice_PinkOrange() {
        assertEquals(100, Houses.getHousePrice("Pink"));
        assertEquals(100, Houses.getHousePrice("Orange"));

        // Test case insensitivity
        assertEquals(100, Houses.getHousePrice("pink"));
        assertEquals(100, Houses.getHousePrice("orange"));
    }

    @Test
    public void testGetHousePrice_RedYellow() {
        assertEquals(150, Houses.getHousePrice("Red"));
        assertEquals(150, Houses.getHousePrice("Yellow"));

        // Test case insensitivity
        assertEquals(150, Houses.getHousePrice("red"));
        assertEquals(150, Houses.getHousePrice("yellow"));
    }

    @Test
    public void testGetHousePrice_GreenDarkBlue() {
        assertEquals(200, Houses.getHousePrice("Green"));
        assertEquals(200, Houses.getHousePrice("Dark Blue"));

        // Test case insensitivity
        assertEquals(200, Houses.getHousePrice("green"));
        assertEquals(200, Houses.getHousePrice("dark blue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHousePrice_InvalidColorGroup() {
        Houses.getHousePrice("Invalid Color");
    }

    @Test
    public void testIsShortage_NoShortage() {
        // Initially no shortage
        assertFalse(houses.isShortage());

        // Use some houses but not enough to cause shortage
        houses.useHouses(28);
        assertFalse(houses.isShortage());
    }

    @Test
    public void testIsShortage_HasShortage() {
        // Use enough houses to cause shortage (less than 4 left)
        houses.useHouses(29);
        assertEquals(3, houses.getAvailableHouses());
        assertTrue(houses.isShortage());
    }

    @Test
    public void testToString() {
        String expected = "Houses available: 32 out of 32";
        assertEquals(expected, houses.toString());

        houses.useHouses(10);
        expected = "Houses available: 22 out of 32";
        assertEquals(expected, houses.toString());
    }
}
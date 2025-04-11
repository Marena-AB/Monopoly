package Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Hotels class.
 * Tests all methods to ensure complete coverage.
 */
public class HotelsTest {

    private Hotels hotels;

    @Before
    public void setUp() {
        // Create a fresh Hotels object before each test
        hotels = new Hotels();
    }

    @Test
    public void testInitialization() {
        // Test that Hotels initializes with the correct number of hotels
        assertEquals(12, hotels.getAvailableHotels());
        assertTrue(hotels.areHotelsAvailable());
    }

    @Test
    public void testUseHotel_Success() {
        // Test using a hotel when available
        boolean result = hotels.useHotel();
        assertTrue(result);
        assertEquals(11, hotels.getAvailableHotels());
        assertTrue(hotels.areHotelsAvailable());
    }

    @Test
    public void testUseHotel_UseAll() {
        // Use all hotels
        for (int i = 0; i < 12; i++) {
            boolean result = hotels.useHotel();
            assertTrue(result);
        }

        // Verify none left
        assertEquals(0, hotels.getAvailableHotels());
        assertFalse(hotels.areHotelsAvailable());
    }

    @Test
    public void testUseHotel_NotEnough() {
        // First use all hotels
        for (int i = 0; i < 12; i++) {
            hotels.useHotel();
        }

        // Now try to use another
        boolean result = hotels.useHotel();
        assertFalse(result);
        assertEquals(0, hotels.getAvailableHotels());
    }

    @Test
    public void testReturnHotel() {
        // First use some hotels
        hotels.useHotel();
        hotels.useHotel();
        assertEquals(10, hotels.getAvailableHotels());

        // Return a hotel
        hotels.returnHotel();
        assertEquals(11, hotels.getAvailableHotels());
    }

    @Test
    public void testReturnHotel_ExceedMax() {
        // Use a hotel
        hotels.useHotel();
        assertEquals(11, hotels.getAvailableHotels());

        // Return more than used (should cap at maximum)
        hotels.returnHotel();
        hotels.returnHotel();
        assertEquals(12, hotels.getAvailableHotels());

        // Try to return another (should remain at max)
        hotels.returnHotel();
        assertEquals(12, hotels.getAvailableHotels());
    }

    @Test
    public void testReset() {
        // Use some hotels
        hotels.useHotel();
        hotels.useHotel();
        assertEquals(10, hotels.getAvailableHotels());

        // Reset
        hotels.reset();
        assertEquals(12, hotels.getAvailableHotels());
    }

    @Test
    public void testGetHotelPrice() {
        // Test that hotel prices match house prices for each color group
        assertEquals(50, Hotels.getHotelPrice("Brown"));
        assertEquals(50, Hotels.getHotelPrice("Light Blue"));
        assertEquals(100, Hotels.getHotelPrice("Pink"));
        assertEquals(100, Hotels.getHotelPrice("Orange"));
        assertEquals(150, Hotels.getHotelPrice("Red"));
        assertEquals(150, Hotels.getHotelPrice("Yellow"));
        assertEquals(200, Hotels.getHotelPrice("Green"));
        assertEquals(200, Hotels.getHotelPrice("Dark Blue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHotelPrice_InvalidColorGroup() {
        Hotels.getHotelPrice("Invalid Color");
    }

    @Test
    public void testGetHousesReturnedForHotel() {
        // Test the number of houses returned when building a hotel
        assertEquals(4, Hotels.getHousesReturnedForHotel());
    }

    @Test
    public void testAreHotelsAvailable_Available() {
        // Initially hotels are available
        assertTrue(hotels.areHotelsAvailable());

        // Use some but not all
        hotels.useHotel();
        hotels.useHotel();
        assertTrue(hotels.areHotelsAvailable());
    }

    @Test
    public void testAreHotelsAvailable_NotAvailable() {
        // Use all hotels
        for (int i = 0; i < 12; i++) {
            hotels.useHotel();
        }

        // Verify none available
        assertFalse(hotels.areHotelsAvailable());
    }

    @Test
    public void testToString() {
        String expected = "Hotels available: 12 out of 12";
        assertEquals(expected, hotels.toString());

        hotels.useHotel();
        expected = "Hotels available: 11 out of 12";
        assertEquals(expected, hotels.toString());
    }
}
package Model.Cards;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the TitleDeedCard class.
 * Tests all methods and properties to ensure complete coverage.
 */
public class TitleDeedCardTest {

    private TitleDeedCard titleDeedCard;
    private String propertyName;
    private String colorGroup;
    private int price;
    private int baseRent;
    private int[] houseRents;
    private int hotelRent;
    private int houseCost;

    @Before
    public void setUp() {
        // Initialize test data
        propertyName = "Boardwalk";
        colorGroup = "Dark Blue";
        price = 400;
        baseRent = 50;
        houseRents = new int[]{200, 600, 1400, 1700}; // Rent for 1-4 houses
        hotelRent = 2000;
        houseCost = 200;

        // Create the title deed card
        titleDeedCard = new TitleDeedCard(propertyName, colorGroup, price, baseRent, houseRents, hotelRent, houseCost);
    }

    @Test
    public void testConstructor() {
        // Verify all properties were set correctly
        assertEquals(propertyName, titleDeedCard.getPropertyName());
        assertEquals(colorGroup, titleDeedCard.getColorGroup());
        assertEquals(price, titleDeedCard.getPrice());
        assertEquals(baseRent, titleDeedCard.getBaseRent());
        assertEquals(hotelRent, titleDeedCard.getHotelRent());
        assertEquals(houseCost, titleDeedCard.getHouseCost());

        // Verify mortgage value is calculated correctly (half of price)
        assertEquals(price / 2, titleDeedCard.getMortgageValue());

        // Verify title deed is not mortgaged by default
        assertFalse(titleDeedCard.isMortgaged());
    }

    @Test
    public void testGetPropertyName() {
        assertEquals(propertyName, titleDeedCard.getPropertyName());

        // Test with a different property name
        TitleDeedCard otherCard = new TitleDeedCard("Park Place", colorGroup, price, baseRent, houseRents, hotelRent, houseCost);
        assertEquals("Park Place", otherCard.getPropertyName());
    }

    @Test
    public void testGetColorGroup() {
        assertEquals(colorGroup, titleDeedCard.getColorGroup());

        // Test with a different color group
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, "Red", price, baseRent, houseRents, hotelRent, houseCost);
        assertEquals("Red", otherCard.getColorGroup());
    }

    @Test
    public void testGetPrice() {
        assertEquals(price, titleDeedCard.getPrice());

        // Test with a different price
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, 350, baseRent, houseRents, hotelRent, houseCost);
        assertEquals(350, otherCard.getPrice());
    }

    @Test
    public void testGetBaseRent() {
        assertEquals(baseRent, titleDeedCard.getBaseRent());

        // Test with a different base rent
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, price, 60, houseRents, hotelRent, houseCost);
        assertEquals(60, otherCard.getBaseRent());
    }

    @Test
    public void testGetHouseRent() {
        // Test all house rent values
        assertEquals(houseRents[0], titleDeedCard.getHouseRent(1));
        assertEquals(houseRents[1], titleDeedCard.getHouseRent(2));
        assertEquals(houseRents[2], titleDeedCard.getHouseRent(3));
        assertEquals(houseRents[3], titleDeedCard.getHouseRent(4));

        // Test with invalid house count (less than 1)
        assertEquals(baseRent, titleDeedCard.getHouseRent(0));

        // Test with invalid house count (more than 4)
        assertEquals(baseRent, titleDeedCard.getHouseRent(5));
    }

    @Test
    public void testGetHotelRent() {
        assertEquals(hotelRent, titleDeedCard.getHotelRent());

        // Test with a different hotel rent
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, price, baseRent, houseRents, 2200, houseCost);
        assertEquals(2200, otherCard.getHotelRent());
    }

    @Test
    public void testGetMortgageValue() {
        // Mortgage value should be half the property price
        assertEquals(price / 2, titleDeedCard.getMortgageValue());

        // Test with a different price
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, 300, baseRent, houseRents, hotelRent, houseCost);
        assertEquals(150, otherCard.getMortgageValue()); // 300 / 2 = 150
    }

    @Test
    public void testGetHouseCost() {
        assertEquals(houseCost, titleDeedCard.getHouseCost());

        // Test with a different house cost
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, price, baseRent, houseRents, hotelRent, 150);
        assertEquals(150, otherCard.getHouseCost());
    }

    @Test
    public void testGetHotelCost() {
        // Hotel cost should be the same as house cost
        assertEquals(houseCost, titleDeedCard.getHotelCost());

        // Test with a different house cost
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, price, baseRent, houseRents, hotelRent, 150);
        assertEquals(150, otherCard.getHotelCost());
    }

    @Test
    public void testIsMortgaged() {
        // Should not be mortgaged by default
        assertFalse(titleDeedCard.isMortgaged());
    }

    @Test
    public void testSetMortgaged() {
        // Mortgage the property
        titleDeedCard.setMortgaged(true);
        assertTrue(titleDeedCard.isMortgaged());

        // Unmortgage the property
        titleDeedCard.setMortgaged(false);
        assertFalse(titleDeedCard.isMortgaged());
    }

    @Test
    public void testGetUnmortgageCost() {
        // Unmortgage cost should be mortgage value + 10% interest
        int mortgageValue = titleDeedCard.getMortgageValue();
        int expectedUnmortgageCost = (int)(mortgageValue * 1.1);
        assertEquals(expectedUnmortgageCost, titleDeedCard.getUnmortgageCost());

        // Test with a different price
        TitleDeedCard otherCard = new TitleDeedCard(propertyName, colorGroup, 300, baseRent, houseRents, hotelRent, houseCost);
        int otherMortgageValue = otherCard.getMortgageValue();
        int otherExpectedUnmortgageCost = (int)(otherMortgageValue * 1.1);
        assertEquals(otherExpectedUnmortgageCost, otherCard.getUnmortgageCost());
    }

    @Test
    public void testToString() {
        // Test that toString returns a non-empty string
        String cardString = titleDeedCard.toString();
        assertNotNull(cardString);
        assertFalse(cardString.isEmpty());

        // Verify the string contains important information
        assertTrue(cardString.contains(propertyName));
        assertTrue(cardString.contains(colorGroup));
        assertTrue(cardString.contains(String.valueOf(price)));
        assertTrue(cardString.contains(String.valueOf(baseRent)));

        // Test with a mortgaged property
        titleDeedCard.setMortgaged(true);
        String mortgagedCardString = titleDeedCard.toString();
        assertTrue(mortgagedCardString.contains("MORTGAGED"));
    }

    @Test
    public void testUnmortgageCostCalculation() {
        // Test that unmortgage cost is correctly calculated
        int mortgageValue = titleDeedCard.getMortgageValue();
        int expectedUnmortgageCost = (int)(mortgageValue * 1.1);

        assertEquals(expectedUnmortgageCost, titleDeedCard.getUnmortgageCost());

        // Test with different prices to verify calculation
        TitleDeedCard card1 = new TitleDeedCard("Test1", "Red", 100, 10, houseRents, hotelRent, houseCost);
        assertEquals(55, card1.getUnmortgageCost()); // (100/2) * 1.1 = 55

        TitleDeedCard card2 = new TitleDeedCard("Test2", "Red", 200, 20, houseRents, hotelRent, houseCost);
        assertEquals(110, card2.getUnmortgageCost()); // (200/2) * 1.1 = 110
    }
}
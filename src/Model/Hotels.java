package Model;

/**
 * Represents the hotels that can be built on properties in Monopoly.
 * Manages hotel availability and provides utility methods.
 */
public class Hotels {
    private static final int TOTAL_HOTELS = 12; // Standard Monopoly game has 12 hotels
    private int availableHotels;

    /**
     * Author: Marena
     * Constructs a new Model.Hotels object with the default number of hotels.
     */
    public Hotels() {
        this.availableHotels = TOTAL_HOTELS;
    }

    /**
     * Author: Marena
     * Gets the number of hotels currently available.
     *
     * @return The number of available hotels
     */
    public int getAvailableHotels() {
        return availableHotels;
    }

    /**
     * Author: Marena
     * Attempts to use a hotel for building.
     *
     * @return true if a hotel was successfully allocated, false if there are none left
     */
    public boolean useHotel() {
        if (availableHotels > 0) {
            availableHotels--;
            return true;
        }
        return false;
    }

    /**
     * Author: Marena
     * Returns a hotel to the bank when it is removed from a property.
     */
    public void returnHotel() {
        if (availableHotels < TOTAL_HOTELS) {
            availableHotels++;
        }
    }

    /**
     * Author: Marena
     * Checks if hotels are available.
     *
     * @return true if at least one hotel is available, false otherwise
     */
    public boolean areHotelsAvailable() {
        return availableHotels > 0;
    }

    /**
     * Author: Marena
     * Resets the hotels to the initial count (for starting a new game).
     */
    public void reset() {
        availableHotels = TOTAL_HOTELS;
    }

    /**
     * Author: Marena
     * Gets the price for building a hotel on a property.
     * This is the same as a house price, since a hotel costs the same as a house
     * and replaces 4 houses.
     *
     * @param colorGroup The color group of the property
     * @return The price for a hotel in that color group
     */
    public static int getHotelPrice(String colorGroup) {
        return Houses.getHousePrice(colorGroup);
    }

    /**
     * Author: Marena
     * Gets the number of houses that are returned to the bank when upgrading to a hotel.
     * In standard Monopoly, you exchange 4 houses for 1 hotel.
     *
     * @return The number of houses returned when building a hotel
     */
    public static int getHousesReturnedForHotel() {
        return 4;
    }

    /**
     * Author: Marena
     * Returns a string representation of the hotels.
     *
     * @return A string with information about available hotels
     */
    @Override
    public String toString() {
        return "Model.Hotels available: " + availableHotels + " out of " + TOTAL_HOTELS;
    }
}
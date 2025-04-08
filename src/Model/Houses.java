package Model;

/**
 * Represents the houses that can be built on properties in Monopoly.
 * Manages house availability, costs, and provides utility methods.
 */
public class Houses {
    private static final int TOTAL_HOUSES = 32; // Standard Monopoly game has 32 houses
    private int availableHouses;

    // House prices vary based on property color group
    private static final int BROWN_LIGHT_BLUE_PRICE = 50;
    private static final int PINK_ORANGE_PRICE = 100;
    private static final int RED_YELLOW_PRICE = 150;
    private static final int GREEN_DARK_BLUE_PRICE = 200;

    /**
     * Author: Marena
     * Constructs a new Model.Houses object with the default number of houses.
     */
    public Houses() {
        this.availableHouses = TOTAL_HOUSES;
    }

    /**
     * Author: Marena
     * Gets the number of houses currently available.
     *
     * @return The number of available houses
     */
    public int getAvailableHouses() {
        return availableHouses;
    }

    /**
     * Author: Marena
     * Attempts to use a number of houses for building.
     *
     * @param count The number of houses needed
     * @return true if the houses were successfully allocated, false if there aren't enough
     */
    public boolean useHouses(int count) {
        if (availableHouses >= count) {
            availableHouses -= count;
            return true;
        }
        return false;
    }

    /**
     * Author: Marena
     * Returns houses to the bank when they are removed from properties.
     *
     * @param count The number of houses to return
     */
    public void returnHouses(int count) {
        availableHouses += count;
        if (availableHouses > TOTAL_HOUSES) {
            availableHouses = TOTAL_HOUSES;
        }
    }

    /**
     * Author: Marena
     * Resets the houses to the initial count (for starting a new game).
     */
    public void reset() {
        availableHouses = TOTAL_HOUSES;
    }

    /**
     * Author: Marena
     * Gets the price for building a house on a property based on its color group.
     *
     * @param colorGroup The color group of the property
     * @return The price for a house in that color group
     */
    public static int getHousePrice(String colorGroup) {
        switch (colorGroup.toLowerCase()) {
            case "brown":
            case "light blue":
                return BROWN_LIGHT_BLUE_PRICE;
            case "pink":
            case "orange":
                return PINK_ORANGE_PRICE;
            case "red":
            case "yellow":
                return RED_YELLOW_PRICE;
            case "green":
            case "dark blue":
                return GREEN_DARK_BLUE_PRICE;
            default:
                throw new IllegalArgumentException("Invalid color group: " + colorGroup);
        }
    }

    /**
     * Author: Marena
     * Checks if there's a house shortage (less than 4 houses available).
     *
     * @return true if there's a house shortage, false otherwise
     */
    public boolean isShortage() {
        return availableHouses < 4;
    }

    /**
     * Author: Marena
     * Returns a string representation of the houses.
     *
     * @return A string with information about available houses
     */
    @Override
    public String toString() {
        return "Model.Houses available: " + availableHouses + " out of " + TOTAL_HOUSES;
    }
}

package Model.Cards;

/**
 * Author: Marena
 * Represents a Model.Property.Property Title Deed Card in the Monopoly game.
 * Contains all information about a property including rent values and mortgage details.
 */
public class TitleDeedCard {
    private String propertyName;
    private String colorGroup;
    private int price;
    private int baseRent;
    private int[] houseRents; // Rent with 1-4 houses
    private int hotelRent;
    private int mortgageValue;
    private int houseCost;
    private boolean isMortgaged;

    /**
     * Author: Marena
     * Constructs a new Title Deed Card for a property.
     *
     * @param propertyName The name of the property
     * @param colorGroup The color group of the property
     * @param price The purchase price of the property
     * @param baseRent The base rent of the property (with no houses)
     * @param houseRents Array of rent values with 1-4 houses
     * @param hotelRent The rent with a hotel
     * @param houseCost The cost to build a house on this property
     */
    public TitleDeedCard(String propertyName, String colorGroup, int price, int baseRent,
                         int[] houseRents, int hotelRent, int houseCost) {
        this.propertyName = propertyName;
        this.colorGroup = colorGroup;
        this.price = price;
        this.baseRent = baseRent;
        this.houseRents = houseRents;
        this.hotelRent = hotelRent;
        this.houseCost = houseCost;
        this.mortgageValue = price / 2; // Standard mortgage value is half the property price
        this.isMortgaged = false;
    }

    /**
     * Author: Marena
     * Gets the property name.
     *
     * @return The property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Author: Aiden Clare
     * Gets the color group.
     *
     * @return The color group
     */
    public String getColorGroup() {
        return colorGroup;
    }

    /**
     * Author: Marena
     * Gets the property price.
     *
     * @return The property price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Author: Marena
     * Gets the base rent (with no houses).
     *
     * @return The base rent
     */
    public int getBaseRent() {
        return baseRent;
    }

    /**
     * Author: Marena
     * Gets the rent with a specific number of houses.
     *
     * @param houses The number of houses (1-4)
     * @return The rent for that number of houses
     */
    public int getHouseRent(int houses) {
        if (houses >= 1 && houses <= 4) {
            return houseRents[houses - 1];
        }
        return baseRent;
    }

    /**
     * Author: Marena
     * Gets the rent with a hotel.
     *
     * @return The hotel rent
     */
    public int getHotelRent() {
        return hotelRent;
    }

    /**
     * Author: Marena
     * Gets the mortgage value.
     *
     * @return The mortgage value
     */
    public int getMortgageValue() {
        return mortgageValue;
    }

    /**
     * Author: Marena
     * Gets the cost to build a house.
     *
     * @return The house cost
     */
    public int getHouseCost() {
        return houseCost;
    }

    /**
     * Author: Marena
     * Gets the cost to build a hotel.
     *
     * @return The hotel cost (same as house cost)
     */
    public int getHotelCost() {
        return houseCost;
    }

    /**
     * Author: Marena
     * Checks if the property is mortgaged.
     *
     * @return true if mortgaged, false otherwise
     */
    public boolean isMortgaged() {
        return isMortgaged;
    }

    /**
     * Author: Marena
     * Sets the mortgage status of the property.
     *
     * @param mortgaged The new mortgage status
     */
    public void setMortgaged(boolean mortgaged) {
        this.isMortgaged = mortgaged;
    }

    /**
     * Author: Marena
     * Gets the cost to unmortgage the property.
     *
     * @return The unmortgage cost (mortgage value plus 10% interest)
     */
    public int getUnmortgageCost() {
        return (int)(mortgageValue * 1.1);
    }

    /**
     * Author: Marena
     * Returns a string representation of the title deed card.
     *
     * @return A formatted string with property details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TITLE DEED ===\n");
        sb.append(propertyName).append(" (").append(colorGroup).append(")\n");
        sb.append("Price: $").append(price).append("\n");
        sb.append("Rent: $").append(baseRent).append("\n");
        sb.append("With 1 House: $").append(houseRents[0]).append("\n");
        sb.append("With 2 Model.Houses: $").append(houseRents[1]).append("\n");
        sb.append("With 3 Model.Houses: $").append(houseRents[2]).append("\n");
        sb.append("With 4 Model.Houses: $").append(houseRents[3]).append("\n");
        sb.append("With Hotel: $").append(hotelRent).append("\n");
        sb.append("Mortgage Value: $").append(mortgageValue).append("\n");
        sb.append("Model.Houses cost $").append(houseCost).append(" each\n");
        if (isMortgaged) {
            sb.append("MORTGAGED\n");
            sb.append("Unmortgage Cost: $").append(getUnmortgageCost()).append("\n");
        }

        return sb.toString();
    }
}

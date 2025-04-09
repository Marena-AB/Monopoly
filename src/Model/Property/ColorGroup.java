package Model.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of properties of the same color in Monopoly.
 * Color groups are used to determine when a player has a monopoly and
 * can build houses or charge double rent.
 */
public class ColorGroup {
    private PropertyColor color;
    private List<Property> properties;

    /**
     * Constructs a new color group with the specified color.
     *
     * @param color The color of this property group
     */
    public ColorGroup(PropertyColor color) {
        this.color = color;
        this.properties = new ArrayList<>();
    }

    /**
     * Gets the color of this property group.
     *
     * @return The property color
     */
    public PropertyColor getColor() {
        return color;
    }

    /**
     * Gets the display name of the color group.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return color.getDisplayName();
    }

    /**
     * Gets the list of properties in this color group.
     *
     * @return The list of properties
     */
    public List<Property> getProperties() {
        return new ArrayList<>(properties);
    }

    /**
     * Adds a property to this color group.
     *
     * @param property The property to add
     */
    public void addProperty(Property property) {
        if (property != null && !properties.contains(property)) {
            properties.add(property);
            property.setColorGroup(color.getDisplayName());
        }
    }

    /**
     * Checks if a player owns all properties in this color group (has a monopoly).
     *
     * @param player The player to check
     * @return true if the player owns all properties in this group, false otherwise
     */
    public boolean isMonopoly(Model.Board.Player player) {
        if (player == null || properties.isEmpty()) {
            return false;
        }

        for (Property property : properties) {
            if (property.getOwner() != player) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the house price for properties in this color group.
     *
     * @return The house price
     */
    public int getHousePrice() {
        return color.getHousePrice();
    }

    /**
     * Gets the hotel price for properties in this color group (same as house price).
     *
     * @return The hotel price
     */
    public int getHotelPrice() {
        return getHousePrice();
    }

    /**
     * Gets the expected size of this color group.
     *
     * @return The expected number of properties in this group
     */
    public int getExpectedSize() {
        return color.getGroupSize();
    }

    /**
     * Checks if all properties in this group have the same number of houses.
     *
     * @return true if development is even, false otherwise
     */
    public boolean isEvenlyDeveloped() {
        if (properties.isEmpty()) {
            return true;
        }

        int baseHouses = properties.get(0).getHouses();
        boolean baseHotel = properties.get(0).hasHotel();

        for (int i = 1; i < properties.size(); i++) {
            Property property = properties.get(i);
            int diff = Math.abs(property.getHouses() - baseHouses);

            // If one has a hotel and the other doesn't, they're not evenly developed
            if (property.hasHotel() != baseHotel) {
                return false;
            }

            // If not a hotel, check house count difference is not more than 1
            if (!baseHotel && diff > 1) {
                return false;
            }
        }

        return true;
    }
}

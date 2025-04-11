package Model.Property;

/**
 * Enumeration of property colors in Monopoly.
 * Each color corresponds to a group of properties on the board.
 */
public enum PropertyColor {
    BROWN("Brown", 2),
    LIGHT_BLUE("Light Blue", 3),
    PINK("Pink", 3),
    ORANGE("Orange", 3),
    RED("Red", 3),
    YELLOW("Yellow", 3),
    GREEN("Green", 3),
    DARK_BLUE("Dark Blue", 2);

    private final String displayName;
    private final int groupSize;

    /**
     * Author: Marena
     * Constructs a property color with display name and group size.
     *
     * @param displayName The display name of the color
     * @param groupSize The number of properties in this color group
     */
    PropertyColor(String displayName, int groupSize) {
        this.displayName = displayName;
        this.groupSize = groupSize;
    }

    /**
     * Author: Marena
     * Gets the display name of the color.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Author: Marena
     * Gets the number of properties in this color group.
     *
     * @return The group size
     */
    public int getGroupSize() {
        return groupSize;
    }

    /**
     * Author: Marena
     * Gets the house price for this color group.
     *
     * @return The house price
     */
    public int getHousePrice() {
        switch (this) {
            case BROWN:
            case LIGHT_BLUE:
                return 50;
            case PINK:
            case ORANGE:
                return 100;
            case RED:
            case YELLOW:
                return 150;
            case GREEN:
            case DARK_BLUE:
                return 200;
            default:
                return 0;
        }
    }

    /**
     * Author: Marena
     * Finds a PropertyColor by its display name (case-insensitive).
     *
     * @param name The display name to search for
     * @return The matching PropertyColor or null if not found
     */
    public static PropertyColor fromDisplayName(String name) {
        if (name == null) return null;

        for (PropertyColor color : values()) {
            if (color.displayName.equalsIgnoreCase(name)) {
                return color;
            }
        }
        return null;
    }
}
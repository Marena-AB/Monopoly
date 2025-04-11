package Model.Property;
/**
 * Model.Property.PropertyManager.java
 * This class manages a collection of properties in a game.
 * It allows adding, removing, and retrieving properties.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aiden Clare
 * This class represents a property in the game.
 * It contains the position and other details of the property.
 * It can be extended to include more attributes like price, owner, etc.
 */
public class PropertyManager {
    private List<Property> properties;

    /**
     * Author: Aiden Clare
     * This is the constructor for the Model.Property.PropertyManager class.
     * It initializes the properties list.
     */

    public PropertyManager() {
        this.properties = new ArrayList<>();
    }

    /**
     * Author: Aiden Clare
     * This method is used to add a property to the list.
     * @param property
     */

    public void addProperty(Property property) {
        properties.add(property);
    }

    /**
     * Author: Aiden Clare
     * This method is used to remove a property from the list.
     * @param position
     */

    public void removeProperty(int position) {
        properties.removeIf(property -> property.getPosition() == position);
    }

    /**
     * Author: Aiden Clare
     * This method is used to get a property by its position.
     * @param position
     * @return
     */


    public Property getProperty(int position) {
        return properties.stream()
                .filter(property -> property.getPosition() == position)
                .reduce((first, second) -> second) // Take the last property
                .orElse(null);
    }

    /**
     * Author: Aiden Clare
     * This method is used to get all properties.
     * @return
     */

    public void displayProperties() {
        properties.forEach(System.out::println);
    }
}

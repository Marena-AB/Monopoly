package Model.Spaces;

/**
 * Model.Spaces.SpecialSpace.java
 * This class represents a special space on the Monopoly board.
 */
public class SpecialSpace extends Space {
    /**
     * Author: Tati Curtis
     * This is the constructor for the Model.Spaces.SpecialSpace class.
     * @param name
     * @param position
     * @param type
     */
    public SpecialSpace(String name, int position, String type){
        super(name, position, type);
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the type of space.
     * @return
     */

    public String getType() {
        return type;
    }
}

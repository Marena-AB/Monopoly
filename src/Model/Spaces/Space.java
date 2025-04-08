package Model.Spaces;

import Model.Board.Player;

/**
 * Space class represents a space on the Monopoly board.
 * It is an abstract class that serves as a base for different types of spaces,
 * including properties, railroads, and special spaces.
 */
public abstract class Space {
    protected String name;
    protected int position;
    protected String type;
    protected Player owner;
    protected String colorGroup;

    /**
     * Constructor for the Space class.
     * @param name The name of the space
     * @param position The position on the board
     * @param type The type of space
     */
    public Space(String name, int position, String type) {
        this.name = name;
        this.position = position;
        this.type = type;
    }

    /**
     * Gets the type of space.
     * @return The type of space
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the owner of the space.
     * @return The owner of the space
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the space.
     * @param owner The player who owns the space
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Gets the name of the space.
     * @return The name of the space
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color group of the space.
     * @return The color group of the space
     */
    public String getColorGroup() {
        return colorGroup;
    }

    /**
     * Sets the color group of the space.
     * @param colorGroup The color group to set
     */
    public void setColorGroup(String colorGroup) {
        this.colorGroup = colorGroup;
    }

    /**
     * Method for handling a player landing on a special space.
     */
    public void playerOnSpecialSpace(){
        System.out.println("Player landed on "+ name );
    }

    /**
     * Method for handling a player landing on a property.
     */
    public void playerOnProperty(){
        System.out.println("Player landed on property " + name);
    }

    /**
     * Method for handling a player landing on a railroad.
     */
    public void playerOnRailroad(){
        System.out.println("Player landed on railroad " + name);
    }

    /**
     * Method for handling a player landing on a card space.
     */
    public void playerOnCardSpace(){
        System.out.println("Player landed on card space " + name);
    }

    /**
     * Gets the string representation of the space.
     * @return The position, name, and type of the space
     */
    @Override
    public String toString() {
        return position + ": " + name + " (" + type + ")";
    }

    /**
     * Gets the position of the space.
     * @return The position of the space
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position of the space.
     * @param position The position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }


}

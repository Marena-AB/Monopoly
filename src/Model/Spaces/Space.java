package Model.Spaces;

import Model.Board.Player;

/**
 * Model.Spaces.Space class represents a space on the Monopoly board.
 * It is an abstract class that serves as a base for different types of spaces,
 * including properties, railroads, and special spaces.
 */
public abstract class Space {
    String name;
    int position;
    String type;
    private Player owner;
    private String colorGroup;

    /**
     * Author: Ronnie
     * Constructor for the Model.Spaces.Space class.
     * @param name
     * @param position
     * @param property
     */
    public Space(String name, int position, String property) {
        this.name = name;
        this.position = position;
        this.type = property;
    }

    /**
     * Author: Aiden Clare
     * This method is used to get the type of space.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the owner of the space.
     * @return
     */

    public Player getOwner() {
        return owner;
    }

    /**
     * Author: Tati Curtis
     * This method is used to set the owner of the space.
     * @param owner
     */

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the name of the space.
     * @return
     */

    public String getName() {
        return name;
    }

    /**
     * Author: Aiden Clare
     * @return
     */
   public String getColorGroup() {
        return colorGroup;
    }


    // method for landing on a special space (go, jail, etc.)

    /**
     * Author: Ronnie
     * This method is used to handle the player landing on a special space.
     */
    public void playerOnSpecialSpace(){
        System.out.println("Model.Board.Player landed on "+ name );
    }

    // method for landing on a property

    /**
     * Author: Ronnie
     * This method is used to handle the player landing on a property.
     * It prints the name of the property.
     */
    public void playerOnProperty(){
        System.out.println("Model.Board.Player landed on property " + name);
    }
    // method for landing on a railroad

    /**
     * Author: Ronnie
     * This method is used to handle the player landing on a railroad.
     * It prints the name of the railroad.
     */
    public void playerOnRailroad(){
        System.out.println("Model.Board.Player landed on railroad " + name);
    }
    // method for landing on a card space

    /**
     * Author: Ronnie
     * This method is used to handle the player landing on a card space.
     * It prints the name of the card space.
     */
    public void playerOnCardSpace(){
        System.out.println("Model.Board.Player landed on card space " + name);
    }

    /**
     * Author: Ronnie
     * This method is used to get the string representation of the space.
     * It returns the position, name, and type of the space.
     * @return
     */
    @Override
    public String toString() {
        return position + ": " + name + " (" + type + ")";
    }

    /**
     * Author: Aiden Clare
     * This method is used to get the position of the space.
     * @return
     */

    public int getPosition() {
        return position;
    }

    /**
     * Author: Aiden Clare
     * This method is used to set the position of the space.
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }
}

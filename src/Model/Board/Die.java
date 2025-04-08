package Model.Board; /** Purpose: The die class represents a 6 Sided Model.Board.Die. Returns a random number between 1 and 6 inclusive.
* Author: Tati Curtis
* */
import java.util.Random;

/**
 * Author : Tati Curtis
 * This class represents a die with a specified number of sides.
 * It can roll the die and return the value of the last roll.
 */
public class Die {
    private int numberOfSides;
    private Random random;
    private int lastRollValue;

    /**
     * Author : Tati Curtis
     * This is the constructor for the Model.Board.Die class.
     * It initializes the number of sides and the random number generator.
     * @param numberOfSides
     */
    public Die(int numberOfSides) {
        this.numberOfSides = numberOfSides;
        this.random = new Random();
    }

    /**
     * Author : Tati Curtis
     * This method is used to roll the die.
     * It generates a random number between 1 and the number of sides.
     * @return
     */
    public int roll(){
        lastRollValue = random.nextInt(numberOfSides) + 1;
        return lastRollValue;
    }
    /**
     * Author : Tati Curtis
     * This method is used to get the value of the last roll.
     * @return
     */
    public int getDieValue() {
        return lastRollValue;
    }
}

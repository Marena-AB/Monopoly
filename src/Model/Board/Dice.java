package Model.Board;

/** Purpose: Represents a pair of dice used for rolling in the game. Tracks doubles and determines if a player rolls
 * three consecutive doubles and should go to jail.
 * Author: Tati Curtis
 **/
public class Dice {
    private Die die1;
    private Die die2;
    private int consecutiveDoubles;

    /**
     * Author: Tati Curtis
     * This is the constructor for the Model.Board.Dice class.
     * It initializes the two dice and sets the consecutiveDoubles count to zero.
     */
    public Dice(){
        die1 = new Die(6);
        die2 = new Die(6);
        consecutiveDoubles = 0;
    }

    /**
     * Author: Tati Curtis
     * This is the constructor for the Model.Board.Dice class.
     * It initializes the two dice and sets the consecutiveDoubles count to zero.
     * @param die1
     * @param die2
     */
    public Dice(Die die1, Die die2) {
        this.die1 = die1;
        this.die2 = die2;
        consecutiveDoubles = 0;
    }

    /**
     * Author: Tati Curtis
     * This method is used to roll the dice.
     * It rolls both dice and checks if they are doubles.
     * @return
     */
    public int rollDice(){
        int roll1 = die1.roll();
        int roll2 = die2.roll();

        if (roll1 == roll2) {
            consecutiveDoubles++;
        } else {
            consecutiveDoubles = 0;
        }
        return roll1 + roll2;
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the total value of the two dice.
     * It returns the sum of the two dice.
     * @return
     */
    public int getTotal(){
        return die1.getDieValue() + die2.getDieValue();
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the value of the first die.
     * It returns the value of the first die.
     * @return
     */
    public int getConsecutiveDoubles() {
        return consecutiveDoubles;
    }


    /**
     * Author: Tati Curtis
     * This method is used to reset the consecutiveDoubles count.
     * It sets the consecutiveDoubles count to zero.
     */
    public void resetConsecutiveDoubles() {
        consecutiveDoubles = 0;
    }

    /**
     * Author: Tati Curtis
     * This method is used to roll the dice.
     * It rolls both dice and checks if they are doubles.
     * @return
     */
    public int roll() {
        return rollDice();
    }

    /**
     * Author: Aiden Clare
     * This method is used to get the value of the first die.
     * It returns the value of the first die.
     * @return
     */
    public int getDie1Value() {
        return die1.getDieValue();
    }

    /**
     * Author: Aiden Clare
     * This method is used to get the value of the second die.
     * It returns the value of the second die.
     * @return
     */
    public int getDie2Value() {
        return die2.getDieValue();
    }

    /**
     * Author: Marena Abboud
     * This method is used to check if the player should go to jail.
     * It checks if the player has rolled three consecutive doubles.
     * @return
     */
    public boolean shouldGoToJail() {
        return consecutiveDoubles == 3;
    }
}

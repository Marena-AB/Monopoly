package Model.Spaces;

import Model.Board.Player;
import Model.GameState;

/**
 * Represents a utility space on the Monopoly board.
 * Utilities include the Electric Company and Water Works.
 * The rent depends on the dice roll and how many utilities the owner has.
 */
public class UtilitySpace extends Space {
    private int price;
    private Model.Board.Player owner;
    private static final int PRICE = 150; // Standard price for utilities in Monopoly

    /**
     * Author: Marena
     * Constructs a utility space on the Monopoly board.
     *
     * @param name The name of the utility (Electric Company or Water Works)
     * @param position The position on the board
     */
    public UtilitySpace(String name, int position) {
        super(name, position, "Utility");
        this.price = PRICE;
        this.owner = null;
    }

    /**
     * Author: Marena
     * Gets the price of the utility.
     *
     * @return The price of the utility
     */
    public int getPrice() {
        return price;
    }

    /**
     * Author: Marena
     * Sets the owner of the utility.
     *
     * @param owner The player who owns the utility
     */
    public void setOwner(Model.Board.Player owner) {
        this.owner = owner;
    }

    /**
     * Author: Marena
     * Gets the owner of the utility.
     *
     * @return The owner of the utility
     */
    public Model.Board.Player getOwner() {
        return owner;
    }

    /**
     * Author: Marena
     * Checks if the utility is owned.
     *
     * @return True if the utility is owned, false otherwise
     */
    public boolean isOwned() {
        return owner != null;
    }

    /**
     * Author: Marena
     * Calculates the rent based on the dice roll and how many utilities the owner has.
     * If the owner has 1 utility, the rent is 4 times the dice roll.
     * If the owner has 2 utilities, the rent is 10 times the dice roll.
     *
     * @param diceRoll The value of the dice roll
     * @param gameState The current game state
     * @return The rent amount to be paid
     */
    public int calculateRent(int diceRoll, Model.GameState gameState) {
        if (owner == null) {
            return 0;
        }

        // Count how many utilities the owner has
        int utilityCount = 0;
        for (Space space : gameState.getBoard().getSpaces()) {
            if (space instanceof UtilitySpace) {
                UtilitySpace utility = (UtilitySpace) space;
                if (utility.getOwner() == owner) {
                    utilityCount++;
                }
            }
        }

        // Calculate rent based on utility count
        if (utilityCount == 1) {
            return 4 * diceRoll;
        } else if (utilityCount == 2) {
            return 10 * diceRoll;
        }

        return 0; // Should never reach here
    }

    /**
     * Author: Marena
     * Handles what happens when a player lands on this utility.
     *
     * @param player The player who landed on the utility
     * @param gameState The current game state
     */
    public void onLand(Model.Board.Player player, Model.GameState gameState) {
        System.out.println(player.getName() + " landed on " + name);

        if (!isOwned()) {
            System.out.println(name + " is not owned. It costs $" + price);
            // Logic for player to decide to buy would be handled elsewhere
            if (player.getMoney() >= price) {
                boolean wantToBuy = true; // In a real game, this would be a player decision
                if (wantToBuy) {
                    player.buyUtility(this);
                }
            } else {
                System.out.println(player.getName() + " cannot afford to buy " + name);
            }
        } else if (owner != player) {
            // Roll dice to determine rent
            int diceRoll = gameState.rollDice();
            int rent = calculateRent(diceRoll, gameState);

            System.out.println(player.getName() + " rolled " + diceRoll + " and must pay $" + rent +
                    " to " + owner.getName());
            player.payRent(owner, rent);
        } else {
            System.out.println(player.getName() + " owns this utility.");
        }
    }

    /**
     * Author: Marena
     * Returns a string representation of the utility space.
     * @return
     */
    @Override
    public String toString() {
        return super.toString() + " - Price: $" + price + ", Owner: " +
                (owner != null ? owner.getName() : "None");
    }
}
package Model.Spaces;
/**
 * this class represents the Free Parking space in Monopoly.
 * It extends the Model.Spaces.SpecialSpace class and implements the onLand method.
 * Author: Marena Abboud
 */
import Model.Board.Player;
import Model.GameState;


public class FreeParkingSpace extends SpecialSpace {
    private int moneyPool; // Optional: for house rule where money from fines goes to Free Parking

    /**
     * @author: Marena Abboud
     * Constructs a Free Parking space.
     * In standard Monopoly, landing on Free Parking has no effect.
     */
    public FreeParkingSpace() {
        super("Free Parking", 20, "Free Parking");
        this.moneyPool = 0;
    }

    /**
     * Author: Marena Abboud
     * Handles a player landing on the Free Parking space.
     * By default, nothing happens in standard rules.
     * But a common house rule is that the player collects any money in the pot.
     *
     * @param player The player who landed on Free Parking
     * @param gameState The current game state
     * @param useHouseRules Whether to use the house rule where player collects money
     */
    public void onLand(Model.Board.Player player, Model.GameState gameState, boolean useHouseRules) {
        System.out.println(player.getName() + " landed on Free Parking.");

        if (useHouseRules && moneyPool > 0) {
            System.out.println(player.getName() + " collects $" + moneyPool + " from Free Parking!");
            player.addMoney(moneyPool);
            moneyPool = 0; // Reset the money pool
        }
    }

    /**
     * Author: Marena Abboud
     * Adds money to the Free Parking pool.
     * This would be used with house rules that put fines and taxes into the pot.
     *
     * @param amount The amount of money to add to the pool
     */
    public void addMoneyToPool(int amount) {
        moneyPool += amount;
    }

    /**
     * Author: Marena Abboud
     * Gets the current amount of money in the Free Parking pool.
     *
     * @return The amount of money in the pool
     */
    public int getMoneyPool() {
        return moneyPool;
    }

    /**
     * Author: Marena Abboud
     * Default version of onLand that uses standard rules (no money collection).
     *
     * @param player The player who landed on Free Parking
     * @param gameState The current game state
     */
    public void onLand(Model.Board.Player player, Model.GameState gameState) {
        onLand(player, gameState, false);
    }

    /**
     * Author: Marena Abboud
     * This method is called when a player lands on a special space.
     */
    @Override
    public void playerOnSpecialSpace() {
        System.out.println("Player landed on Free Parking");
    }
}
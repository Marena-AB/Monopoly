package Model.Spaces;

import Model.Board.Player;
import Model.GameState;

/**
 * Model.Spaces.JailSpace.java
 * this class represents the Jail space in the game of Monopoly.
 * It extends the Model.Spaces.SpecialSpace class.
 */
public class JailSpace extends SpecialSpace {
    private static final int JAIL_FEE = 50;

    /**
     * Author: Marena
     * Constructs a Jail space on the Monopoly board.
     */
    public JailSpace() {
        super("Jail", 10, "Jail");
    }

    /**
     * Author: Marena
     * Handles a player landing on the Jail space when they're "Just Visiting".
     *
     * @param player The player who landed on Jail
     * @param gameState The current game state
     */
    public void onLand(Player player, GameState gameState) {
        // If the player is not sent to jail, they're just visiting
        if (!gameState.isPlayerInJail(player)) {
            System.out.println(player.getName() + " is just visiting Jail.");
        }
    }

    /**
     * Author: Marena
     * Sends a player to Jail.
     * This method updates the player's position and the game state to reflect that they're in jail.
     *
     * @param player The player to send to jail
     * @param gameState The current game state
     */
    public static void goToJail(Player player, GameState gameState) {
        player.setPosition(10); // Jail is at position 10 on the board
        gameState.sendToJail(player);
        System.out.println(player.getName() + " has been sent to Jail!");
    }

    /**
     * Author: Marena
     * Allows a player to get out of jail by paying the fine.
     *
     * @param player The player who wants to pay to get out of jail
     * @param gameState The current game state
     * @return True if the player successfully paid and got out, false otherwise
     */
    public boolean payToGetOut(Player player, GameState gameState) {
        if (gameState.isPlayerInJail(player) && player.getMoney() >= JAIL_FEE) {
            player.subtractMoney(JAIL_FEE);
            gameState.releaseFromJail(player);
            System.out.println(player.getName() + " paid $" + JAIL_FEE + " to get out of Jail.");
            return true;
        } else if (gameState.isPlayerInJail(player)) {
            System.out.println(player.getName() + " doesn't have enough money to pay the Jail fee.");
            return false;
        }
        return false;
    }

    /**
     * Author: Marena
     * Tries to get the player out of jail by rolling doubles.
     *
     * @param player The player trying to get out of jail
     * @param gameState The current game state
     * @return True if the player rolled doubles and got out, false otherwise
     */
    public boolean tryRollForFreedom(Player player, GameState gameState) {
        if (gameState.isPlayerInJail(player)) {
            int roll = gameState.rollDice();
            int[] diceValues = gameState.getDiceValues();

            System.out.println(player.getName() + " rolled " + diceValues[0] + " and " + diceValues[1]);

            if (diceValues[0] == diceValues[1]) {
                gameState.releaseFromJail(player);
                System.out.println(player.getName() + " rolled doubles and is out of Jail!");
                // Move the player based on the roll
                int newPosition = (player.getPosition() + roll) % gameState.getBoard().getSpaces().size();
                player.setPosition(newPosition);
                return true;
            } else {
                System.out.println(player.getName() + " failed to roll doubles and remains in Jail.");
                return false;
            }
        }
        return false;
    }

    /**
     * Author: Marena
     * Uses a "Get Out of Jail Free" card if the player has one.
     * Implementation would depend on how cards are tracked in your game.
     *
     * @param player The player using the card
     * @param gameState The current game state
     * @return True if card was used successfully, false otherwise
     */
    public boolean useGetOutOfJailFreeCard(Player player, GameState gameState) {
        // This would require tracking if players have these cards
        // For now, just a placeholder implementation
        boolean hasCard = false; // Implement logic to check if player has the card

        if (gameState.isPlayerInJail(player) && hasCard) {
            gameState.releaseFromJail(player);
            // Remove the card from player's possession
            System.out.println(player.getName() + " used a Get Out of Jail Free card!");
            return true;
        }
        return false;
    }

    /**
     * Author: Marena
     * Handles a player landing on the Jail space.
     */

    @Override
    public void playerOnSpecialSpace() {
        System.out.println("Model.Board.Player is at Jail (Just Visiting)");
    }
}

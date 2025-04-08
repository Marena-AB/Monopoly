package Model.Spaces;

import Model.GameState;

/**
 * Author: Marena
 * Represents the Go space on the Monopoly board.
 * Players collect $200 when landing on or passing this space.
 */
public class GoSpace extends SpecialSpace {
    private static final int GO_AMOUNT = 200;

    /**
     * Author: Marena
     * Constructs a Go space, which is the starting space on the Monopoly board.
     */
    public GoSpace() {
        super("Go", 0, "Start");
    }

    /**
     * Author: Marena
     * Handles a player landing directly on the Go space.
     * Players collect $200 when landing on Go.
     *
     * @param player The player who landed on Go
     * @param gameState The current game state
     */
    public void onLand(Player player, GameState gameState) {
        System.out.println(player.getName() + " landed on Go and collects $" + GO_AMOUNT);
        player.addMoney(GO_AMOUNT);
    }

    /**
     * Author: Marena
     * Handles a player passing the Go space (not landing on it).
     * This method is called when a player's move causes them to pass Go.
     *
     * @param player The player who passed Go
     * @param gameState The current game state
     */
    public static void onPass(Player player, GameState gameState) {
        System.out.println(player.getName() + " passed Go and collects $" + GO_AMOUNT);
        player.addMoney(GO_AMOUNT);
    }

    /**
     * Author: Marena
     * Handles moving a player to the Go space, typically due to a card or special rule.
     * The player collects $200 in this case.
     *
     * @param player The player to move to Go
     * @param gameState The current game state
     */
    public static void moveToGo(Player player, GameState gameState) {
        player.setPosition(0);
        System.out.println(player.getName() + " moved to Go and collects $" + GO_AMOUNT);
        player.addMoney(GO_AMOUNT);
    }

    /**
     * Author: Marena
     * Handles a player landing on the Go space.
     */
    @Override
    public void playerOnSpecialSpace() {
        System.out.println("Model.Board.Player landed on Go and collects $" + GO_AMOUNT);
    }
}
package Model.Cards;

import Model.Board.Player;
import Model.GameState;

/**
 * Author: Marena
 * Abstract base class for cards in the Monopoly game.
 * Provides common functionality for all card types.
 */
public abstract class Card {
    protected String description;

    /**
     * Author: Marena
     * Constructs a card with the given description.
     *
     * @param description The text describing the card's effect
     */
    public Card(String description) {
        this.description = description;
    }

    /**
     * Author: Marena
     * Gets the description of the card.
     *
     * @return The card description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Author: Marena
     * Gets the type of card ("Chance" or "Community Chest").
     *
     * @return The card type
     */
    public abstract String getCardType();

    /**
     * Author: Marena
     * Gets the deck this card belongs to.
     *
     * @return The card deck
     */
    public abstract String getDeck();

    /**
     * Author: Aiden Clare
     * Executes the effect of the card on a player.
     *
     * @param player The player who drew the card
     * @param gameState The current game state
     */
    public abstract void executeEffect(Player player, GameState gameState);

    /**
     * Author: Aiden Clare
     * Returns a string representation of the card.
     *
     * @return The card description
     */
    @Override
    public String toString() {
        return description;
    }
}
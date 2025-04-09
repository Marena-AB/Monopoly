package Model.Cards;

import Model.Board.Player;
import Model.GameState;

/**
 * Abstract base class for cards in the Monopoly game.
 * Provides common functionality for all card types.
 */
public abstract class Card {
    protected String description;

    /**
     * Constructs a card with the given description.
     *
     * @param description The text describing the card's effect
     */
    public Card(String description) {
        this.description = description;
    }

    /**
     * Gets the description of the card.
     *
     * @return The card description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the type of card ("Chance" or "Community Chest").
     *
     * @return The card type
     */
    public abstract String getCardType();

    /**
     * Gets the deck this card belongs to.
     *
     * @return The card deck
     */
    public abstract String getDeck();

    /**
     * Executes the effect of the card on a player.
     *
     * @param player The player who drew the card
     * @param gameState The current game state
     */
    public abstract void executeEffect(Player player, GameState gameState);

    /**
     * Returns a string representation of the card.
     *
     * @return The card description
     */
    @Override
    public String toString() {
        return description;
    }
}
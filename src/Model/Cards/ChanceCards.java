package Model.Cards;

import Model.Board.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Team 4
 * This class represents the Chance Cards in the game of Monopoly.
 * It implements the Model.ChestAndCardSpot interface.
 * The class has a Map of Chance Cards. And is responsible for shuffling the cards.
 */
public class ChanceCards {
    /**
     * Author: Marena Abboud
     * This is a map of the Chance Cards.
     * The key is the card name and the value is the card description.
     */
    private static Map<String, String> chanceCards = new HashMap<>();

    /**
     *  Author: Marena Abboud
     * This is the constructor for the Model.Cards.ChanceCards class.
     * It initializes the chanceCards map.
     */
    public ChanceCards() {
        // Ensure cards are initialized when the class is instantiated
        if (chanceCards.isEmpty()) {
            cards();
        }
    }

    /**
     * Author: Marena Abboud
     * This method is used to shuffle the cards.
     */
    public Map<String, String> getChanceCards() {
        return chanceCards;
    }

    /**
     * Author: Tati Curtis
     * This method is used to set the chanceCards map.
     * @param chanceCards The chanceCards map to set.
     */
    public void setChanceCards(Map<String, String> chanceCards) {
        ChanceCards.chanceCards = chanceCards;
    }

    /**
     * Author: Marena Abboud
     * This method is used to create the chance cards.
     * It initializes the chanceCards map with the card name and description.
     * @see ChanceCards
     */
    public void cards() {
        chanceCards.put("Card1", "Take a ride on the Reading Railroad. If you pass Go, collect $200.");
        chanceCards.put("Card2", "Advance Token to Nearest Railroad and Pay Owner Twice the Rental to Which He is Otherwise Entitled. If Railroad is Unowned, You May Buy it from the Bank.");
        chanceCards.put("Card3", "Go Back 3 Spaces.");
        chanceCards.put("Card4", "Advance to Go (Collect $200).");
        chanceCards.put("Card5", "Bank pays you dividend of $50.");
        chanceCards.put("Card6", "Advance to Illinois Ave.");
        chanceCards.put("Card7", "Make General Repairs on All Your Property. For each house pay $25. For each hotel $100.");
        chanceCards.put("Card8", "This Card May Be Kept Until Needed or Sold. Get Out of Jail Free.");
        chanceCards.put("Card9", "Take A Walk on the Boardwalk. Advance token to Boardwalk.");
        chanceCards.put("Card10", "Pay Poor Tax of $15.");
        chanceCards.put("Card11", "Advance Token to the Nearest Railroad and Pay Owner Twice the Rental to Which He is Otherwise Entitled. If Railroad is Unowned, You May Buy it from the Bank.");
        chanceCards.put("Card12", "Go Directly to Jail. Do Not Pass Go, Do Not Collect $200.");
        chanceCards.put("Card13", "You Have Been Elected Chairman of the Board. Pay Each Player $50.");
        chanceCards.put("Card14", "Advance To St. Charles Place. If you pass Go, collect $200.");
        chanceCards.put("Card15", "Your Building Loan Matures. Collect $150.");
        chanceCards.put("Card16", "Advance Token to Nearest Railroad and Pay Owner Twice the Rental to Which He is Otherwise Entitled. If Railroad is Unowned, You May Buy it from the Bank.");
    }

    /**
     * Author: Marena Abboud
     * Edited by: Aiden Clare
     * This method is used to shuffle the cards.
     * It returns a random card from the chanceCards map.
     * @return A random card from the chanceCards map.
     */
    public static String shuffleCards() {
        Random rand = new Random();
        List<String> cards = new ArrayList<>(chanceCards.keySet());
        String randomCard = cards.get(rand.nextInt(cards.size()));

        return chanceCards.get(randomCard);
    }

    /**
     * Author: Aiden Clare
     * This method is used to draw a card from the chanceCards map.
     */
    public static void drawCard(Model.Board.Player player1) {
        System.out.println("Player " + player1.getName() + " drew a Chance card: " + shuffleCards());
    }
}
package Model.Board; /**
 * Model.Board.Tokens.java
 * This class represents the tokens for the Monopoly game.
 * It has a list of available tokens and ensures that players can only choose from those that remain.
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Marena Abboud
 *
 * This class represents the tokens for the Monopoly game.
 */

public class Tokens {

    public static final String[] TOKENS = {
            "Top Hat", "Thimble", "Iron", "Boot", "Battleship",
            "Cannon", "Race Car", "Scottie Dog", "Wheelbarrow"
    };


    private static ArrayList<String> availableTokens = new ArrayList<>();
    private String owner;
    private int boardPosition;

    /**
     * Author: Aiden Clare
     * This method is used to initialize the available tokens.
     */

    // Initialize the available tokens
    public static void initializeTokens() {
        availableTokens.clear(); // Reset the list
        availableTokens.addAll(Arrays.asList(TOKENS));
    }

    /**
     * Author: Aiden Clare
     * This method is used to check if a token is available.
     * @param token
     * @return
     */
    public static boolean isTokenAvailable(String token) {
        return availableTokens.contains(token);
    }

    /**
     * Author: Marena Abboud
     * This method is used to assign a token to a player.
     * @param token
     * @return
     */

    // Assigns a token to a player if it's available
    public static boolean assignToken(String token) {
        if (availableTokens.contains(token)) {
            availableTokens.remove(token);
            return true;
        }
        return false;
    }

    /**
     * Author: Aiden Clare
     * This method is used to remove a token from the available tokens.
     */

    // Displays the list of remaining available tokens
    public static void displayAvailableTokens() {
        System.out.println("Available tokens: " + availableTokens);
    }

    /**
     * Author: Marena Abboud
     * This method is used to get the list of available tokens.
     */

    // Constructor for setting owner and board position
    public Tokens() {
        this.owner = null;
        this.boardPosition = 0;
    }

    /**
     * Author: Marena Abboud
     * This method is used to assign a token to a player.
     * @return
     */

    public static char[] assignToken() {
        if (availableTokens.size() > 0) {
            return availableTokens.remove(0).toCharArray();
        }
        return null;
    }

    /**
     * Author: Marena Abboud
     * This method is used to choose a token for a player.
     * @param player1
     * @param raceCar
     * @return
     */

    public static boolean chooseToken(Player player1, String raceCar) {
        return false;
    }

    /**
     * Author: Marena Abboud
     * This method is used to move a token to a new position.
     * @param player
     * @param position
     */

    public static void moveToken(Player player, int position) {
    }

    /**
     * Author: Marena Abboud
     * This method is used to get the list of available tokens.
     * @return
     */

    public static String getavailabletokens() {
        if (availableTokens.size() > 0) {
            return availableTokens.toString();
        }
        else
        return "No tokens available";
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the owner of the token.
     * @return
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the board position of the token.
     * @return
     */

    // Getter for board position
    public int getBoardPosition() {
        return boardPosition;
    }

    /**
     * Author: Tati Curtis
     * This method is used to set the owner of the token.
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Author: Tati Curtis
     * This method is used to set the board position of the token.
     * @param boardPosition
     */

    public void setBoardPosition(int boardPosition) {
        this.boardPosition = boardPosition;
    }

    /**
     * Author: Tati Curtis
     * This method is used to get the string representation of the token.
     * @return
     */

    @Override
    public String toString() {
        return owner + " - " + boardPosition;
    }
}

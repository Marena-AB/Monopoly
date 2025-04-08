package Model; /**
 * Model.GameState.java
 * This class represents the state of the game, including players, board, and game logic.
 * It manages player turns, properties, money transactions, and game status.
 */

import Model.Board.Bank;
import Model.Board.Dice;
import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Cards.ChanceCards;
import Model.Cards.CommunityChestCards;
import Model.Property.Property;

import java.util.*;

/**
 * Author: Aiden Clare
 * This class represents the state of the game, including players, board, and game logic.
 * It manages player turns, properties, money transactions, and game status.
 */
public class GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private Gameboard board;
    private Dice dice;
    private CommunityChestCards communityChestCards;
    private ChanceCards chanceCards;
    private Map<Player, Boolean> isInJail;
    private boolean gameActive;
    private Bank bank; // Added Model.Board.Bank reference

    /**
     * Author: Aiden Clare
     * Constructor for Model.GameState.
     * Initializes the game state with players and a gameboard.
     * @param players
     * @param board
     */
    public GameState(List<Player> players, Gameboard board) {
        this.players = players; // Initialize players
        this.board = board;  // Use the provided board
        this.dice = new Dice();
        this.communityChestCards = new CommunityChestCards();
        this.chanceCards = new ChanceCards();
        this.isInJail = new HashMap<>();
        for (Player player : this.players) {
            isInJail.put(player, false);
        }
        this.gameActive = true;
        this.currentPlayerIndex = 0;
        communityChestCards.cards();
        chanceCards.cards();
        // Model.Board.Bank will be set separately
    }

    /**
     * Author: Aiden Clare
     * Gets the bank for this game.
     *
     * @return The bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Author: Aiden Clare
     * Sets the bank for this game.
     *
     * @param bank The bank to use
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Author: Aiden Clare
     * Starts the game loop where players take turns in the correct order.
     * The game continues until all but one player is bankrupt.
     */
    public void startGame(List<Player> players, Gameboard gameboard) {
        boolean gameActive = true;
        int currentPlayerIndex = 0;

        while (gameActive) {
            Player currentPlayer = players.get(currentPlayerIndex);

            // Skip bankrupt players
            if (currentPlayer.getMoney() <= 0) {
                players.remove(currentPlayer);
                if (players.size() == 1) {
                    System.out.println(players.get(0).getName() + " wins the game!");
                    break;
                }
                currentPlayerIndex = currentPlayerIndex % players.size();
                continue;
            }
            System.out.println("It's " + currentPlayer.getName() + "'s turn.");
            currentPlayer.takeTurn(gameboard, this);

            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    /**
     * Author: Aiden Clare
     * Gets the list of players in the game.
     * @return
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Author: Aiden Clare
     * Gets the current player.
     * @return
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Author: Aiden Clare
     * Moves to the next player's turn.
     * This method updates the current player index to the next player in the list.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Author: Aiden Clare
     * Gets the gameboard for this game.
     * @return
     */

    public Gameboard getBoard() {
        return board;
    }

    /**
     * Author: Aiden Clare
     * Gets the property ownership map.
     * This map contains the property IDs and their corresponding owner names.
     * @return
     */

    public Map<Integer, String> getPropertyOwnership() {
        return board.getPropertyOwnership();
    }

    /**
     * Author: Aiden Clare
     * Rolls the dice and returns the result.
     * This method uses the Model.Board.Dice class to roll two dice and returns the sum of their values.
     * @return
     */
    public int rollDice() {
        return dice.rollDice();
    }

    /**
     * Author: Aiden Clare
     * Gets the values of the dice.
     * This method returns an array containing the values of the two dice.
     * @return
     */
    public int[] getDiceValues() {
        return new int[]{dice.getDie1Value(), dice.getDie2Value()};
    }

    /**
     * Author: Aiden Clare
     * Draws a chance card for the current player.
     * This method uses the Model.Cards.ChanceCards class to shuffle and draw a card.
     * @return
     */
    public String drawChanceCard() {
        return chanceCards.shuffleCards();
    }

    /**
     * Author: Aiden Clare
     * Draws a community chest card for the current player.
     * This method uses the Model.Cards.CommunityChestCards class to shuffle and draw a card.
     * @return
     */
    public String drawCommunityChestCard() {
        return communityChestCards.shuffleCards();
    }

    /**
     * Author: Aiden Clare
     * Transfers money between two players.
     * This method updates the money of both players involved in the transaction.
     * @param from
     * @param to
     * @param amount
     */
    public void transferMoney(Player from, Player to, int amount) {
        from.deductMoney(amount);
        to.addMoney(amount);
    }

    /**
     * Author: Aiden Clare
     * Collects money from the bank for a player.
     * @param player
     * @param amount
     */
    public void collectFromBank(Player player, int amount) {
        player.addMoney(amount);
    }

    /**
     * Author: Aiden Clare
     * Pays money to the bank for a player.
     * This method updates the player's money and the bank's balance.
     * @param player
     * @param amount
     */

    public void payToBank(Player player, int amount) {
        player.deductMoney(amount);
    }

    /**
     * Author: Aiden Clare
     * Checks if the game is still active.
     * @return
     */
    public boolean isGameActive() {
        return gameActive;
    }

    /**
     * Author: Aiden Clare
     * Checks if a player is in jail.
     * @param player
     * @return
     */
    public boolean isPlayerInJail(Player player) {
        return isInJail.getOrDefault(player, false);
    }

    /**
     * Author: Aiden Clare
     * Sends a player to jail.
     * @param player
     */
    public void sendToJail(Player player) {
        isInJail.put(player, true);
    }

    /**
     * Author: Aiden Clare
     * Releases a player from jail.
     * @param player
     */

    public void releaseFromJail(Player player) {
        isInJail.put(player, false);
    }

    /**
     * Author: Aiden Clare
     * Displays the current game state.
     */
    public void displayGameState() {
        System.out.println("Game State: ");
        for (Player player : players) {
            displayPlayerStatus(player);
        }
    }

    /**
     * Author: Aiden Clare
     * Displays the status of a player.
     * @param player
     */
    public void displayPlayerStatus(Player player) {
        System.out.println(player.getName() + ": $" + player.getMoney() +
                (isPlayerInJail(player) ? " (In Jail)" : ""));
    }

    /**
     * Author: Marena
     * Handles a player going bankrupt.
     * This includes returning all properties to the bank and removing the player from the game.
     *
     * @param player The bankrupt player
     */
    public void handlePlayerBankruptcy(Player player) {
        System.out.println(player.getName() + " is bankrupt and out of the game!");

        // Return all properties to the bank
        for (Property property : player.getProperties()) {
            property.setOwner(null);
            property.setHouses(0);
            property.setHasHotel(false);
            property.setMortgaged(false);
        }


        // Remove player from the game
        players.remove(player);
        isInJail.remove(player);

        // Check if game is over
        if (players.size() == 1) {
            System.out.println(players.get(0).getName() + " wins the game!");
            gameActive = false;
        }
    }

    /**
     * Author: Aiden Clare
     * Edited by Marena
     * Handles a player skipping bankruptcy.
     * @param player
     */

    public void skipBankrupt(Player player) {
        players.remove(player);
    }

    /**
     * Author: Aiden Clare
     * Gets the current player index.
     * @return
     */

    public Object getDice() {
        return dice;
    }

    /**
     * Author: Aiden Clare
     * Sets the dice for this game.
     * @param dice
     */

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     * Author: Aiden Clare
     * Gets the community chest cards for this game.
     * @return
     */

    public CommunityChestCards getCommunityChestCards() {
        return communityChestCards;
    }

    /**
     * Author: Aiden Clare
     * Sets the community chest cards for this game.
     * @return
     */

    public ChanceCards getChanceCards() {
        return chanceCards;
    }

    /**
     * Author: Aiden Clare
     * Sets the chance cards for this game.
     * @param gameActive
     */

    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    /**
     * Author: Aiden Clare
     * Sets the players for this game.
     * @param players
     */

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Author: Aiden Clare
     * Sets the current player index for this game.
     * @param board
     */

    public void setBoard(Gameboard board) {
        this.board = board;
    }

    /**
     * Author: Aiden Clare
     * Sets the property ownership map for this game.
     * @return
     */

    public Collection<Object> getIsInJail() {
        return Collections.singleton(isInJail.values());
    }

    /**
     * Author: Aiden Clare
     * Gets the game active status.
     * @return
     */

    public boolean getGameActive() {
        return gameActive;
    }

    /**
     * Author: Aiden Clare
     * Gets the current player index.
     * @return
     */

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}
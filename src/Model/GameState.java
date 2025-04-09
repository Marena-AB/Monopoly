package Model;

import Model.Board.Bank;
import Model.Board.Dice;
import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Cards.ChanceCard;
import Model.Cards.ChanceCards;
import Model.Cards.CommunityChestCard;
import Model.Cards.CommunityChestCards;
import Model.Property.Property;
import Model.Spaces.Space;

import java.util.*;

/**
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
    private Bank bank;

    /**
     * Constructor for GameState.
     * Initializes the game state with players and a gameboard.
     *
     * @param players The list of players in the game
     * @param board The game board
     */
    public GameState(List<Player> players, Gameboard board) {
        this.players = players;
        this.board = board;
        this.dice = new Dice();
        this.communityChestCards = new CommunityChestCards();
        this.chanceCards = new ChanceCards();
        this.isInJail = new HashMap<>();
        for (Player player : this.players) {
            isInJail.put(player, false);
        }
        this.gameActive = true;
        this.currentPlayerIndex = 0;
    }

    /**
     * Gets the bank for this game.
     *
     * @return The bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank for this game.
     *
     * @param bank The bank to use
     */
    public void setBank(Bank bank) {
        this.bank = bank;

        // Initialize the available properties in the bank
        List<Property> properties = new ArrayList<>();
        for (Space space : board.getSpaces()) {
            if (space instanceof Property) {
                properties.add((Property) space);
            }
        }
        bank.setAvailableProperties(properties);
    }

    /**
     * Gets the list of players in the game.
     *
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current player.
     *
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Moves to the next player's turn.
     * This method updates the current player index to the next player in the list.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Gets the gameboard for this game.
     *
     * @return The gameboard
     */
    public Gameboard getBoard() {
        return board;
    }

    /**
     * Gets the property ownership map.
     * This map contains the property IDs and their corresponding owner names.
     *
     * @return The property ownership map
     */
    public Map<Integer, String> getPropertyOwnership() {
        return board.getPropertyOwnership();
    }

    /**
     * Rolls the dice and returns the result.
     * This method uses the Dice class to roll two dice and returns the sum of their values.
     *
     * @return The sum of the dice values
     */
    public int rollDice() {
        return dice.rollDice();
    }

    /**
     * Gets the values of the dice.
     * This method returns an array containing the values of the two dice.
     *
     * @return An array with the dice values [die1, die2]
     */
    public int[] getDiceValues() {
        return new int[]{dice.getDie1Value(), dice.getDie2Value()};
    }

    /**
     * Draws a chance card for the current player.
     * This method uses the ChanceCards class to shuffle and draw a card.
     *
     * @return The drawn chance card text
     */
    public String drawChanceCard() {
        ChanceCard card = chanceCards.drawCard();
        card.executeEffect(getCurrentPlayer(), this);
        return card.getDescription();
    }

    /**
     * Draws a community chest card for the current player.
     * This method uses the CommunityChestCards class to shuffle and draw a card.
     *
     * @return The drawn community chest card text
     */
    public String drawCommunityChestCard() {
        CommunityChestCard card = communityChestCards.drawCard();
        card.executeEffect(getCurrentPlayer(), this);
        return card.getDescription();
    }

    /**
     * Transfers money between two players.
     * This method updates the money of both players involved in the transaction.
     *
     * @param from The player paying money
     * @param to The player receiving money
     * @param amount The amount of money to transfer
     * @return true if the transfer was successful, false if the payer doesn't have enough money
     */
    public boolean transferMoney(Player from, Player to, int amount) {
        if (from.getMoney() >= amount) {
            from.subtractMoney(amount);
            to.addMoney(amount);
            return true;
        }
        return false;
    }

    /**
     * Collects money from the bank for a player.
     *
     * @param player The player receiving money
     * @param amount The amount to collect
     */
    public void collectFromBank(Player player, int amount) {
        player.addMoney(amount);
    }

    /**
     * Pays money to the bank from a player.
     *
     * @param player The player paying money
     * @param amount The amount to pay
     * @return true if the payment was successful, false if the player doesn't have enough money
     */
    public boolean payToBank(Player player, int amount) {
        return player.subtractMoney(amount);
    }

    /**
     * Checks if the game is still active.
     *
     * @return true if the game is active, false otherwise
     */
    public boolean isGameActive() {
        return gameActive;
    }

    /**
     * Checks if a player is in jail.
     *
     * @param player The player to check
     * @return true if the player is in jail, false otherwise
     */
    public boolean isPlayerInJail(Player player) {
        return isInJail.getOrDefault(player, false);
    }

    /**
     * Sends a player to jail.
     *
     * @param player The player to send to jail
     */
    public void sendToJail(Player player) {
        isInJail.put(player, true);
        player.setPosition(10); // Move to jail space
        System.out.println(player.getName() + " has been sent to Jail!");
    }

    /**
     * Releases a player from jail.
     *
     * @param player The player to release from jail
     */
    public void releaseFromJail(Player player) {
        isInJail.put(player, false);
        player.setTurnsInJail(0);
        System.out.println(player.getName() + " has been released from Jail!");
    }

    /**
     * Displays the current game state.
     * This is a view functionality and should eventually be moved to the View layer.
     */
    public void displayGameState() {
        System.out.println("Game State: ");
        for (Player player : players) {
            displayPlayerStatus(player);
        }
    }

    /**
     * Displays the status of a player.
     * This is a view functionality and should eventually be moved to the View layer.
     *
     * @param player The player whose status to display
     */
    public void displayPlayerStatus(Player player) {
        System.out.println(player.getName() + ": $" + player.getMoney() +
                (isPlayerInJail(player) ? " (In Jail)" : ""));
    }

    /**
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
            bank.getAvailableProperties().add(property);
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
     * Gets the dice for this game.
     *
     * @return The dice
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Sets the dice for this game.
     *
     * @param dice The new dice
     */
    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     * Gets the community chest cards for this game.
     *
     * @return The community chest cards
     */
    public CommunityChestCards getCommunityChestCards() {
        return communityChestCards;
    }

    /**
     * Gets the chance cards for this game.
     *
     * @return The chance cards
     */
    public ChanceCards getChanceCards() {
        return chanceCards;
    }

    /**
     * Sets the game active status.
     *
     * @param gameActive The new game active status
     */
    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    /**
     * Sets the players for this game.
     *
     * @param players The new list of players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets the gameboard for this game.
     *
     * @param board The new gameboard
     */
    public void setBoard(Gameboard board) {
        this.board = board;
    }

    /**
     * Gets the current player index.
     *
     * @return The current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Sets the current player index.
     *
     * @param index The new current player index
     */
    public void setCurrentPlayerIndex(int index) {
        if (index >= 0 && index < players.size()) {
            this.currentPlayerIndex = index;
        }
    }

    /**
     * Starts a new game by giving money to players and initializing the game state.
     */
    public void initializeGame() {
        // Give each player starting money
        for (Player player : players) {
            bank.giveStartingMoney(player);
        }

        // Reset game state
        gameActive = true;
        currentPlayerIndex = 0;

        // Reset jail status
        isInJail.clear();
        for (Player player : players) {
            isInJail.put(player, false);
        }
    }

    /**
     * Returns a "Get Out of Jail Free" card to the appropriate deck.
     *
     * @param cardType The type of card ("Chance" or "Community Chest")
     */
    public void returnGetOutOfJailFreeCard(String cardType) {
        if (cardType.equals("Chance")) {
            chanceCards.returnGetOutOfJailFreeCard();
        } else if (cardType.equals("Community Chest")) {
            communityChestCards.returnGetOutOfJailFreeCard();
        }
    }
}
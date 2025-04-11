package Model;

import Model.Board.Bank;
import Model.Board.Dice;
import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Cards.Card;
import Model.Cards.ChanceCard;
import Model.Cards.CommunityChestCard;
import Model.Property.Property;
import Model.Spaces.Space;

import java.util.*;

/**
 * Author: Aiden Clare
 * Edited by: Marena
 * This class represents the state of the game, including players, board, and game logic.
 * It manages player turns, properties, money transactions, and game status.
 */
public class GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private Gameboard board;
    private Dice dice;
    private List<ChanceCard> chanceCardDeck;
    private List<CommunityChestCard> communityChestCardDeck;
    private int currentChanceCardIndex;
    private int currentCommunityChestCardIndex;
    private Map<Player, Boolean> isInJail;
    private boolean gameActive;
    private Bank bank;

    /**
     * Author: Aiden Clare
     * Edited by: Marena
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
        this.isInJail = new HashMap<>();

        // Initialize card decks
        this.chanceCardDeck = new ArrayList<>();
        this.communityChestCardDeck = new ArrayList<>();
        this.currentChanceCardIndex = 0;
        this.currentCommunityChestCardIndex = 0;

        // Initialize player jail status
        for (Player player : this.players) {
            isInJail.put(player, false);
        }

        this.gameActive = true;
        this.currentPlayerIndex = 0;

        // Initialize cards
        initializeChanceCards();
        initializeCommunityChestCards();
    }

    /**
     * Author: Marena
     * Initializes the game, including giving money to players and setting up the game state.
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

        // Initialize card decks
        initializeChanceCards();
        initializeCommunityChestCards();
    }

    /**
     * Author: Marena
     * Initializes the Chance card deck with standard Monopoly Chance cards.
     */
    private void initializeChanceCards() {
        chanceCardDeck.clear();
        currentChanceCardIndex = 0;

        // Standard Monopoly Chance cards
        chanceCardDeck.add(new ChanceCard("Advance to Go. Collect $200."));
        chanceCardDeck.add(new ChanceCard("Advance to Illinois Avenue. If you pass Go, collect $200."));
        chanceCardDeck.add(new ChanceCard("Advance to St. Charles Place. If you pass Go, collect $200."));
        chanceCardDeck.add(new ChanceCard("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rental to which they are otherwise entitled."));
        chanceCardDeck.add(new ChanceCard("Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner 10 times the amount thrown."));
        chanceCardDeck.add(new ChanceCard("Bank pays you dividend of $50."));
        chanceCardDeck.add(new ChanceCard("Get Out of Jail Free."));
        chanceCardDeck.add(new ChanceCard("Go Back 3 Spaces."));
        chanceCardDeck.add(new ChanceCard("Go to Jail. Go directly to Jail. Do not pass Go. Do not collect $200."));
        chanceCardDeck.add(new ChanceCard("Make general repairs on all your property. For each house pay $25. For each hotel pay $100."));
        chanceCardDeck.add(new ChanceCard("Speeding fine $15."));
        chanceCardDeck.add(new ChanceCard("Take a trip to Reading Railroad. If you pass Go, collect $200."));
        chanceCardDeck.add(new ChanceCard("You have been elected Chairman of the Board. Pay each player $50."));
        chanceCardDeck.add(new ChanceCard("Your building loan matures. Collect $150."));
        chanceCardDeck.add(new ChanceCard("You have won a crossword competition. Collect $100."));
        chanceCardDeck.add(new ChanceCard("Advance to Boardwalk."));

        // Shuffle the deck
        Collections.shuffle(chanceCardDeck);
    }

    /**
     * Author: Marena
     * Initializes the Community Chest card deck with standard Monopoly Community Chest cards.
     */
    private void initializeCommunityChestCards() {
        communityChestCardDeck.clear();
        currentCommunityChestCardIndex = 0;

        // Standard Monopoly Community Chest cards
        communityChestCardDeck.add(new CommunityChestCard("Advance to Go. Collect $200."));
        communityChestCardDeck.add(new CommunityChestCard("Bank error in your favor. Collect $200."));
        communityChestCardDeck.add(new CommunityChestCard("Doctor's fee. Pay $50."));
        communityChestCardDeck.add(new CommunityChestCard("From sale of stock you get $50."));
        communityChestCardDeck.add(new CommunityChestCard("Get Out of Jail Free."));
        communityChestCardDeck.add(new CommunityChestCard("Go to Jail. Go directly to Jail. Do not pass Go. Do not collect $200."));
        communityChestCardDeck.add(new CommunityChestCard("Holiday fund matures. Receive $100."));
        communityChestCardDeck.add(new CommunityChestCard("Income tax refund. Collect $20."));
        communityChestCardDeck.add(new CommunityChestCard("It is your birthday. Collect $10 from each player."));
        communityChestCardDeck.add(new CommunityChestCard("Life insurance matures. Collect $100."));
        communityChestCardDeck.add(new CommunityChestCard("Pay hospital fees of $100."));
        communityChestCardDeck.add(new CommunityChestCard("Pay school fees of $50."));
        communityChestCardDeck.add(new CommunityChestCard("Receive $25 consultancy fee."));
        communityChestCardDeck.add(new CommunityChestCard("You are assessed for street repairs. $40 per house. $115 per hotel."));
        communityChestCardDeck.add(new CommunityChestCard("You have won second prize in a beauty contest. Collect $10."));
        communityChestCardDeck.add(new CommunityChestCard("You inherit $100."));

        // Shuffle the deck
        Collections.shuffle(communityChestCardDeck);
    }

    /**
     * Author: Marena
     * Gets the bank for this game.
     *
     * @return The bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Author: Marena
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
     * Author: Marena
     * Gets the list of players in the game.
     *
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Author: Aiden Clare
     * Gets the current player.
     *
     * @return The current player
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
     *
     * @return The gameboard
     */
    public Gameboard getBoard() {
        return board;
    }

    /**
     * Author: Aiden Clare
     * Gets the property ownership map.
     * This map contains the property IDs and their corresponding owner names.
     *
     * @return The property ownership map
     */
    public Map<Integer, String> getPropertyOwnership() {
        return board.getPropertyOwnership();
    }

    /**
     * Author: Aiden Clare
     * Rolls the dice and returns the result.
     * This method uses the Dice class to roll two dice and returns the sum of their values.
     *
     * @return The sum of the dice values
     */
    public int rollDice() {
        return dice.rollDice();
    }

    /**
     * Author: Aiden Clare
     * Gets the values of the dice.
     * This method returns an array containing the values of the two dice.
     *
     * @return An array with the dice values [die1, die2]
     */
    public int[] getDiceValues() {
        return new int[]{dice.getDie1Value(), dice.getDie2Value()};
    }

    /**
     * Author: Aiden Clare
     * Draws a Chance card from the deck.
     * If the deck is exhausted, it is reshuffled.
     *
     * @return The drawn Chance card description
     */
    public String drawChanceCard() {
        if (chanceCardDeck.isEmpty()) {
            initializeChanceCards();
        }

        if (currentChanceCardIndex >= chanceCardDeck.size()) {
            Collections.shuffle(chanceCardDeck);
            currentChanceCardIndex = 0;
        }

        ChanceCard drawnCard = chanceCardDeck.get(currentChanceCardIndex);
        currentChanceCardIndex++;

        // If it's a "Get Out of Jail Free" card, remove it from the deck until it's returned
        if (drawnCard.getDescription().contains("Get Out of Jail Free")) {
            chanceCardDeck.remove(currentChanceCardIndex - 1);
            currentChanceCardIndex--;
        }

        // Execute the card's effect
        drawnCard.executeEffect(getCurrentPlayer(), this);

        return drawnCard.getDescription();
    }

    /**
     * Author: Marena
     * Draws a Community Chest card from the deck.
     * If the deck is exhausted, it is reshuffled.
     *
     * @return The drawn Community Chest card description
     */
    public String drawCommunityChestCard() {
        if (communityChestCardDeck.isEmpty()) {
            initializeCommunityChestCards();
        }

        if (currentCommunityChestCardIndex >= communityChestCardDeck.size()) {
            Collections.shuffle(communityChestCardDeck);
            currentCommunityChestCardIndex = 0;
        }

        CommunityChestCard drawnCard = communityChestCardDeck.get(currentCommunityChestCardIndex);
        currentCommunityChestCardIndex++;

        // If it's a "Get Out of Jail Free" card, remove it from the deck until it's returned
        if (drawnCard.getDescription().contains("Get Out of Jail Free")) {
            communityChestCardDeck.remove(currentCommunityChestCardIndex - 1);
            currentCommunityChestCardIndex--;
        }

        // Execute the card's effect
        drawnCard.executeEffect(getCurrentPlayer(), this);

        return drawnCard.getDescription();
    }

    /**
     * Author: Marena
     * Returns a "Get Out of Jail Free" card to the appropriate deck.
     *
     * @param cardType The type of card ("Chance" or "Community Chest")
     */
    public void returnGetOutOfJailFreeCard(String cardType) {
        if (cardType.equals("Chance")) {
            chanceCardDeck.add(new ChanceCard("Get Out of Jail Free."));
            Collections.shuffle(chanceCardDeck);
        } else if (cardType.equals("Community Chest")) {
            communityChestCardDeck.add(new CommunityChestCard("Get Out of Jail Free."));
            Collections.shuffle(communityChestCardDeck);
        }
    }

    /**
     * Author: Marena
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
     * Author: Marena
     * Collects money from the bank for a player.
     *
     * @param player The player receiving money
     * @param amount The amount to collect
     */
    public void collectFromBank(Player player, int amount) {
        player.addMoney(amount);
    }

    /**
     * Author: Marena
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
     * Author: Aiden Clare
     * Checks if the game is still active.
     *
     * @return true if the game is active, false otherwise
     */
    public boolean isGameActive() {
        return gameActive;
    }

    /**
     * Author: Marena
     * Checks if a player is in jail.
     *
     * @param player The player to check
     * @return true if the player is in jail, false otherwise
     */
    public boolean isPlayerInJail(Player player) {
        return isInJail.getOrDefault(player, false);
    }

    /**
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
     * Gets the dice for this game.
     *
     * @return The dice
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Author: Marena
     * Sets the dice for this game.
     *
     * @param dice The new dice
     */
    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     * Author: Aiden Clare
     * Gets the Chance card deck.
     *
     * @return The Chance card deck
     */
    public List<ChanceCard> getChanceCardDeck() {
        return chanceCardDeck;
    }

    /**
     * Author: Aiden Clare
     * Gets the Community Chest card deck.
     *
     * @return The Community Chest card deck
     */
    public List<CommunityChestCard> getCommunityChestCardDeck() {
        return communityChestCardDeck;
    }

    /**
     * Author: Aiden Clare
     * Sets the game active status.
     *
     * @param gameActive The new game active status
     */
    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    /**
     * Author: Aiden Clare
     * Sets the players for this game.
     *
     * @param players The new list of players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Author: Aiden Clare
     * Sets the gameboard for this game.
     *
     * @param board The new gameboard
     */
    public void setBoard(Gameboard board) {
        this.board = board;
    }

    /**
     * Author: Aiden Clare
     * Gets the current player index.
     *
     * @return The current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Author: Aiden Clare
     * Sets the current player index.
     *
     * @param index The new current player index
     */
    public void setCurrentPlayerIndex(int index) {
        if (index >= 0 && index < players.size()) {
            this.currentPlayerIndex = index;
        }
    }
}
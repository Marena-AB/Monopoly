package Model.Board;

/**
 * This class represents a player in the game of Monopoly.
 * It manages the player's money, position, properties, and game actions.
 */

import Model.*;
import Model.Property.Property;
import Model.Spaces.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Represents a player in the Monopoly game.
 * Manages the player's money, position, properties, and game actions.
 */
public class Player {
    private String name;
    private int money;
    private int position;
    private int turnCounter;
    private String token;
    private List<Property> properties;
    private List<Property> mortgagedProperties;
    private boolean hasGetOutOfJailFreeCard;
    private int turnsInJail;
    private Dice dice;

    /**
     * Constructs a new player with the given name.
     *
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.money = 1500; // Starting money for the player
        this.position = 0;  // Starting position on the gameboard (GO)
        this.turnCounter = 0; // Number of turns the player has taken
        this.token = null; // Token starts as null
        this.properties = new ArrayList<>();
        this.mortgagedProperties = new ArrayList<>();
        this.hasGetOutOfJailFreeCard = false;
        this.turnsInJail = 0;
        this.dice = new Dice();
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's token.
     *
     * @return The player's token
     */
    public String getToken() {
        return token;
    }

    /**
     * Allows the player to choose a token. Ensures they pick an available one.
     *
     * @param chosenToken The token the player wants to use
     * @return true if the token was successfully chosen, false otherwise
     */
    public boolean chooseToken(String chosenToken) {
        if (Tokens.isTokenAvailable(chosenToken)) {
            this.token = chosenToken;
            Tokens.assignToken(chosenToken);
            System.out.println(name + " has chosen the token: " + chosenToken);
            return true;
        } else {
            System.out.println("Token " + chosenToken + " is already taken! Choose another.");
            return false;
        }
    }

    /**
     * Gets the player's current money amount.
     *
     * @return The player's money
     */
    public int getMoney() {
        return money;
    }

    /**
     * Gets the player's current position on the board.
     *
     * @return The player's position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the player's position on the board.
     *
     * @param position The new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Adds money to the player's account.
     *
     * @param amount The amount to add
     */
    public void addMoney(int amount) {
        this.money += amount;
        System.out.println(name + " received $" + amount + ". New balance: $" + money);
    }

    /**
     * Subtracts money from the player's account if they have enough.
     *
     * @param amount The amount to subtract
     * @return true if the money was successfully subtracted, false if the player doesn't have enough
     */
    public boolean subtractMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            System.out.println(name + " paid $" + amount + ". New balance: $" + money);
            return true;
        }
        System.out.println(name + " doesn't have enough money to pay $" + amount + "!");
        return false;
    }

    /**
     * Gets the player's current balance.
     *
     * @return The player's money
     */
    public int getBalance() {
        return money;
    }

    /**
     * Gets a string representation of the player's tokens and position.
     *
     * @return A string with the player's information
     */
    public String getTokens() {
        return name + " has $" + money + " and is on space " + position;
    }
    /**
     * Handles buying property if the player has enough money.
     *
     * @param property The property to buy
     * @return true if the property was successfully bought, false otherwise
     */
    public boolean buyProperty(Property property) {
        if (money >= property.getPrice()) {
            money -= property.getPrice();
            property.setOwner(this);
            properties.add(property);
            System.out.println(name + " bought " + property.getName() + " for $" + property.getPrice() +
                    ". New balance: $" + money);
            return true;
        } else {
            System.out.println(name + " does not have enough money to buy " + property.getName());
            return false;
        }
    }

    /**
     * Handles buying property if the player has enough money.
     * This is an overloaded method that accepts a Space parameter.
     *
     * @param space The space to buy (must be a Property, RailroadSpace, or UtilitySpace)
     * @return true if the space was successfully bought, false otherwise
     */
    public boolean buyProperty(Space space) {
        if (space instanceof Property) {
            Property property = (Property) space;
            return buyProperty(property);
        } else if (space instanceof RailroadSpace) {
            RailroadSpace railroad = (RailroadSpace) space;
            return buyRailroad(railroad);
        } else if (space instanceof UtilitySpace) {
            UtilitySpace utility = (UtilitySpace) space;
            return buyUtility(utility);
        } else {
            System.out.println("Cannot buy this type of space: " + space.getType());
            return false;
        }
    }

    /**
     * Pays rent to another player.
     *
     * @param owner The property owner receiving the rent
     * @param amount The amount of rent to pay
     * @return true if the rent was paid, false if the player is bankrupt
     */
    public boolean payRent(Player owner, int amount) {
        if (money >= amount) {
            money -= amount;
            owner.receiveRent(amount);
            System.out.println(name + " paid $" + amount + " rent to " + owner.getName() +
                    ". New balance: $" + money);
            return true;
        } else {
            System.out.println(name + " is bankrupt and cannot pay $" + amount + " to " + owner.getName());
            return false; // Player is bankrupt
        }
    }

    /**
     * Receives rent from another player.
     *
     * @param amount The amount of rent received
     */
    public void receiveRent(int amount) {
        money += amount;
        System.out.println(name + " received $" + amount + " in rent. New balance: $" + money);
    }

    /**
     * Mortgages a property to get cash from the bank.
     * The property must be owned by this player and not already mortgaged.
     *
     * @param property The property to mortgage
     * @return true if successfully mortgaged, false otherwise
     */
    public boolean mortgageProperty(Property property) {
        // Check if player owns the property
        if (!properties.contains(property)) {
            System.out.println(name + " does not own " + property.getName());
            return false;
        }

        // Check if property is already mortgaged
        if (mortgagedProperties.contains(property)) {
            System.out.println(property.getName() + " is already mortgaged");
            return false;
        }

        // Check if property has houses or hotels
        if (property.getHouses() > 0 || property.hasHotel()) {
            System.out.println("You must sell all houses and hotels on this property before mortgaging");
            return false;
        }

        // Get mortgage value and add to player's money
        int mortgageValue = property.getMortgageValue();
        addMoney(mortgageValue);

        // Update property status
        property.setMortgaged(true);

        // Add to mortgaged properties list
        mortgagedProperties.add(property);

        System.out.println(name + " mortgaged " + property.getName() + " for $" + mortgageValue);
        return true;
    }

    /**
     * Unmortgages a property by paying the unmortgage cost to the bank.
     * The property must be owned by this player and currently mortgaged.
     *
     * @param property The property to unmortgage
     * @return true if successfully unmortgaged, false otherwise
     */
    public boolean unmortgageProperty(Property property) {
        // Check if player owns the property
        if (!properties.contains(property)) {
            System.out.println(name + " does not own " + property.getName());
            return false;
        }

        // Check if property is mortgaged
        if (!mortgagedProperties.contains(property)) {
            System.out.println(property.getName() + " is not mortgaged");
            return false;
        }

        // Get unmortgage cost (mortgage value plus 10% interest)
        int unmortgageCost = property.getUnmortgageCost();

        // Check if player has enough money
        if (money < unmortgageCost) {
            System.out.println(name + " does not have enough money to unmortgage " + property.getName());
            return false;
        }

        // Subtract cost from player's money
        subtractMoney(unmortgageCost);

        // Update property status
        property.setMortgaged(false);

        // Remove from mortgaged properties list
        mortgagedProperties.remove(property);

        System.out.println(name + " unmortgaged " + property.getName() + " for $" + unmortgageCost);
        return true;
    }

    /**
     * Gets all mortgaged properties owned by this player.
     *
     * @return The list of mortgaged properties
     */
    public List<Property> getMortgagedProperties() {
        return mortgagedProperties;
    }

    /**
     * Checks if a property is mortgaged.
     *
     * @param property The property to check
     * @return true if the property is mortgaged, false otherwise
     */
    public boolean isPropertyMortgaged(Property property) {
        return mortgagedProperties.contains(property);
    }

    /**
     * Gets all color groups that this player has a monopoly in.
     *
     * @param gameboard The game board
     * @return A list of color groups where the player has a monopoly
     */
    public List<String> getMonopolies(Gameboard gameboard) {
        List<String> monopolies = new ArrayList<>();

        // Get all unique color groups from the player's properties
        Set<String> colorGroups = new HashSet<>();
        for (Property property : properties) {
            ((HashSet<?>) colorGroups).add(property.getColorGroup());
        }

        // Check each color group to see if the player owns all properties in it
        for (String colorGroup : colorGroups) {
            if (gameboard.playerOwnsAllInColorGroup(this, colorGroup)) {
                monopolies.add(colorGroup);
            }
        }

        return monopolies;
    }

    /**
     * Buys a house for a property if possible.
     *
     * @param property The property to buy a house for
     * @param gameboard The game board
     * @param bank The bank
     * @return true if the house was bought, false otherwise
     */
    public boolean buyHouse(Property property, Gameboard gameboard, Bank bank) {
        // Check if the player owns the property
        if (!properties.contains(property)) {
            System.out.println("You don't own " + property.getName());
            return false;
        }

        // Check if the property is part of a monopoly
        if (!gameboard.playerOwnsAllInColorGroup(this, property.getColorGroup())) {
            System.out.println("You must own all properties in the " + property.getColorGroup() + " color group to buy houses");
            return false;
        }

        // Check if the property is mortgaged
        if (property.isMortgaged()) {
            System.out.println("You cannot buy houses for a mortgaged property");
            return false;
        }

        // Check if the property already has a hotel
        if (property.hasHotel()) {
            System.out.println(property.getName() + " already has a hotel");
            return false;
        }

        // Check if houses will be evenly distributed
        List<Property> propertiesInGroup = gameboard.getPropertiesByColorGroup(property.getColorGroup());
        if (!willHousesBeEvenlyDistributed(propertiesInGroup, property)) {
            System.out.println("Houses must be evenly distributed across properties in a color group");
            return false;
        }

        // Calculate house cost
        int houseCost = Houses.getHousePrice(property.getColorGroup());

        // Check if player has enough money
        if (money < houseCost) {
            System.out.println("You don't have enough money to buy a house for " + property.getName());
            return false;
        }

        // Check if bank has houses available
        if (bank.getHouses() <= 0) {
            System.out.println("The bank has no houses left");
            return false;
        }

        // Buy the house
        subtractMoney(houseCost);
        property.addHouse();

        System.out.println(name + " bought a house for " + property.getName() + " for $" + houseCost);
        return true;
    }

    /**
     * Buys a hotel for a property if possible.
     *
     * @param property The property to buy a hotel for
     * @param gameboard The game board
     * @param bank The bank
     * @return true if the hotel was bought, false otherwise
     */
    public boolean buyHotel(Property property, Gameboard gameboard, Bank bank) {
        // Check if the player owns the property
        if (!properties.contains(property)) {
            System.out.println("You don't own " + property.getName());
            return false;
        }

        // Check if the property is part of a monopoly
        if (!gameboard.playerOwnsAllInColorGroup(this, property.getColorGroup())) {
            System.out.println("You must own all properties in the " + property.getColorGroup() + " color group to buy a hotel");
            return false;
        }

        // Check if the property is mortgaged
        if (property.isMortgaged()) {
            System.out.println("You cannot buy a hotel for a mortgaged property");
            return false;
        }

        // Check if the property already has 4 houses
        if (property.getHouses() != 4) {
            System.out.println(property.getName() + " must have 4 houses before you can buy a hotel");
            return false;
        }

        // Calculate hotel cost
        int hotelCost = Houses.getHousePrice(property.getColorGroup());

        // Check if player has enough money
        if (money < hotelCost) {
            System.out.println("You don't have enough money to buy a hotel for " + property.getName());
            return false;
        }

        // Check if bank has hotels available
        if (bank.getHotels() <= 0) {
            System.out.println("The bank has no hotels left");
            return false;
        }

        // Buy the hotel
        subtractMoney(hotelCost);
        property.addHouse(); // This will convert 4 houses to a hotel in the Property class

        System.out.println(name + " bought a hotel for " + property.getName() + " for $" + hotelCost);
        return true;
    }

    /**
     * Checks if adding a house to a property will maintain even distribution across all properties in the color group.
     *
     * @param propertiesInGroup All properties in the color group
     * @param property The property getting a new house
     * @return true if houses will be evenly distributed, false otherwise
     */
    private boolean willHousesBeEvenlyDistributed(List<Property> propertiesInGroup, Property property) {
        int currentHouses = property.getHouses();
        int newHouses = currentHouses + 1;

        for (Property p : propertiesInGroup) {
            if (p != property) {
                int diff = newHouses - p.getHouses();
                if (diff > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Sells a house from a property back to the bank.
     *
     * @param property The property to sell a house from
     * @param bank The bank
     * @return true if the house was sold, false otherwise
     */
    public boolean sellHouse(Property property, Bank bank) {
        // Check if the player owns the property
        if (!properties.contains(property)) {
            System.out.println("You don't own " + property.getName());
            return false;
        }

        // Check if the property has houses
        if (property.getHouses() == 0 && !property.hasHotel()) {
            System.out.println(property.getName() + " has no houses to sell");
            return false;
        }

        // Calculate house value (half of purchase price)
        int housePrice = Houses.getHousePrice(property.getColorGroup());
        int sellValue = housePrice / 2;

        // Sell the house
        if (property.hasHotel()) {
            // If there's a hotel, convert it back to 4 houses first
            property.setHasHotel(false);
            property.setHouses(4);

            // Then sell one house
            property.removeHouse();
        } else {
            property.removeHouse();
        }

        // Add money to player
        addMoney(sellValue);

        System.out.println(name + " sold a house from " + property.getName() + " for $" + sellValue);
        return true;
    }

    /**
     * Determines if the player should go to jail based on dice rolls.
     *
     * @return true if the player should go to jail, false otherwise
     */
    public boolean shouldGoToJail() {
        return dice.getConsecutiveDoubles() == 3;
    }

    /**
     * Moves the player on the board, handling passing Go.
     *
     * @param rollDice The number of spaces to move
     * @param gameboard The game board
     */
    public void move(int rollDice, Gameboard gameboard) {
        int oldPosition = position;
        position = (position + rollDice) % gameboard.getSpaces().size();

        // Check if player passed Go
        if (position < oldPosition && oldPosition + rollDice >= gameboard.getSpaces().size()) {
            System.out.println(name + " passed Go and collects $200!");
            addMoney(200);
        }

        System.out.println(name + " moved from " + oldPosition + " to " + position +
                " (" + gameboard.getspace(position).getName() + ")");
    }

    /**
     * Takes a turn for the player.
     *
     * @param gameboard The game board
     * @param gameState The current game state
     */
    public void takeTurn(Gameboard gameboard, GameState gameState) {
        System.out.println("\n" + name + " is taking their turn.");

        // Increment turn counter for this player
        turnCounter++;

        // Check if player is in jail
        if (gameState.isPlayerInJail(this)) {
            handleJailTurn(gameState);
            return;
        }

        // Roll the dice and move
        int roll = dice.rollDice();
        System.out.println(name + " rolled " + dice.getDie1Value() + " + " + dice.getDie2Value() + " = " + roll);

        // Check for three doubles (go to jail)
        if (dice.getDie1Value() == dice.getDie2Value()) {
            System.out.println(name + " rolled doubles!");

            if (shouldGoToJail()) {
                System.out.println(name + " rolled three consecutive doubles and is going to jail!");
                JailSpace.goToJail(this, gameState);
                return;
            }
        } else {
            // Reset doubles counter if player didn't roll doubles
            dice.resetConsecutiveDoubles();
        }

        // Move the player
        move(roll, gameboard);

        // Perform actions based on the space landed on
        performTurnActions(gameState);

        // If player rolled doubles, they get another turn (unless they're in jail)
        if (dice.getDie1Value() == dice.getDie2Value() && !gameState.isPlayerInJail(this)) {
            System.out.println(name + " gets another turn for rolling doubles!");
            takeTurn(gameboard, gameState);
        }
    }

    /**
     * Handles a player's turn when they are in jail.
     *
     * @param gameState The current game state
     */
    private void handleJailTurn(GameState gameState) {
        System.out.println(name + " is in Jail (Turn " + (turnsInJail + 1) + " in jail)");
        turnsInJail++;

        // Option 1: Pay to get out
        if (money >= 50 && turnsInJail <= 3) {
            System.out.println(name + " pays $50 to get out of Jail.");
            subtractMoney(50);
            gameState.releaseFromJail(this);
            turnsInJail = 0;

            // Roll and move after getting out
            int roll = dice.rollDice();
            System.out.println(name + " rolled " + dice.getDie1Value() + " + " + dice.getDie2Value() + " = " + roll);
            move(roll, gameState.getBoard());

            // Handle the new space
            performTurnActions(gameState);
            return;
        }

        // Option 2: Use Get Out of Jail Free card
        if (hasGetOutOfJailFreeCard) {
            System.out.println(name + " uses a Get Out of Jail Free card.");
            hasGetOutOfJailFreeCard = false;
            gameState.releaseFromJail(this);
            turnsInJail = 0;

            // Roll and move after getting out
            int roll = dice.rollDice();
            System.out.println(name + " rolled " + dice.getDie1Value() + " + " + dice.getDie2Value() + " = " + roll);
            move(roll, gameState.getBoard());

            // Handle the new space
            performTurnActions(gameState);
            return;
        }

        // Option 3: Try to roll doubles
        int roll = dice.rollDice();
        System.out.println(name + " rolled " + dice.getDie1Value() + " + " + dice.getDie2Value() + " = " + roll);

        if (dice.getDie1Value() == dice.getDie2Value()) {
            System.out.println(name + " rolled doubles and gets out of Jail!");
            gameState.releaseFromJail(this);
            turnsInJail = 0;
            move(roll, gameState.getBoard());

            // Handle the new space
            performTurnActions(gameState);
        } else if (turnsInJail >= 3) {
            // After 3 turns, player must pay and get out
            System.out.println(name + " has been in Jail for 3 turns and must pay $50 to get out.");
            subtractMoney(50);
            gameState.releaseFromJail(this);
            turnsInJail = 0;
            move(roll, gameState.getBoard());

            // Handle the new space
            performTurnActions(gameState);
        } else {
            System.out.println(name + " stays in Jail.");
        }
    }

    /**
     * Performs dynamic turn steps based on where the player landed.
     * This method should be called after the player has moved to a new position.
     *
     * @param gameState The current game state
     */
    public void performTurnActions(GameState gameState) {
        Space currentSpace = gameState.getBoard().getspace(position);

        // Different actions based on space type
        if (currentSpace instanceof Property) {
            Property property = (Property) currentSpace;
            property.onLand(this, gameState);
        } else if (currentSpace instanceof RailroadSpace) {
            RailroadSpace railroad = (RailroadSpace) currentSpace;
            railroad.onLand(this, gameState);
        } else if (currentSpace instanceof UtilitySpace) {
            UtilitySpace utility = (UtilitySpace) currentSpace;
            utility.onLand(this, gameState);
        } else if (currentSpace instanceof GoSpace) {
            GoSpace goSpace = (GoSpace) currentSpace;
            goSpace.onLand(this, gameState);
        } else if (currentSpace instanceof JailSpace) {
            JailSpace jailSpace = (JailSpace) currentSpace;
            jailSpace.onLand(this, gameState);
        } else if (currentSpace instanceof FreeParkingSpace) {
            FreeParkingSpace freeParkingSpace = (FreeParkingSpace) currentSpace;
            freeParkingSpace.onLand(this, gameState);
        } else if (currentSpace instanceof SpecialSpace) {
            SpecialSpace specialSpace = (SpecialSpace) currentSpace;

            if (specialSpace.getType().equals("Chance")) {
                String chanceCard = gameState.drawChanceCard();
                System.out.println(name + " drew a Chance card: " + chanceCard);
                handleCardEffect(chanceCard, gameState);
            } else if (specialSpace.getType().equals("Community Chest")) {
                String communityCard = gameState.drawCommunityChestCard();
                System.out.println(name + " drew a Community Chest card: " + communityCard);
                handleCardEffect(communityCard, gameState);
            } else if (specialSpace.getType().equals("Go To Jail")) {
                System.out.println(name + " landed on Go To Jail!");
                JailSpace.goToJail(this, gameState);
            } else if (specialSpace.getType().equals("Tax")) {
                handleTaxSpace(specialSpace, gameState);
            }
        }
    }

    /**
     * Handles the effects of Chance and Community Chest cards.
     * This is a simplified implementation that handles a few common card effects.
     *
     * @param cardText The text of the card
     * @param gameState The current game state
     */
    private void handleCardEffect(String cardText, GameState gameState) {
        // Simple pattern matching for card effects
        if (cardText.contains("Advance to Go")) {
            position = 0;
            addMoney(200);
            System.out.println(name + " advances to Go and collects $200");
        } else if (cardText.contains("Go to Jail")) {
            JailSpace.goToJail(this, gameState);
        } else if (cardText.contains("Get out of Jail Free")) {
            hasGetOutOfJailFreeCard = true;
            System.out.println(name + " received a Get Out of Jail Free card");
        } else if (cardText.contains("collect") || cardText.contains("Collect") || cardText.contains("receive") || cardText.contains("Receive")) {
            // Extract amount and add to player
            String[] words = cardText.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].startsWith("$")) {
                    try {
                        int amount = Integer.parseInt(words[i].substring(1));
                        addMoney(amount);
                        break;
                    } catch (NumberFormatException e) {
                        // Not a valid number, continue
                    }
                }
            }
        } else if (cardText.contains("pay") || cardText.contains("Pay")) {
            // Extract amount and subtract from player
            String[] words = cardText.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].startsWith("$")) {
                    try {
                        int amount = Integer.parseInt(words[i].substring(1));
                        subtractMoney(amount);
                        break;
                    } catch (NumberFormatException e) {
                        // Not a valid number, continue
                    }
                }
            }
        } else if (cardText.contains("each player")) {
            // Handle payments to or from each player
            int amount = 0;
            String[] words = cardText.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].startsWith("$")) {
                    try {
                        amount = Integer.parseInt(words[i].substring(1));
                        break;
                    } catch (NumberFormatException e) {
                        // Not a valid number, continue
                    }
                }
            }

            if (amount > 0) {
                if (cardText.toLowerCase().contains("pay each")) {
                    // Pay each player
                    for (Player player : gameState.getPlayers()) {
                        if (player != this) {
                            subtractMoney(amount);
                            player.addMoney(amount);
                            System.out.println(name + " paid $" + amount + " to " + player.getName());
                        }
                    }
                } else if (cardText.toLowerCase().contains("collect from each")) {
                    // Collect from each player
                    for (Player player : gameState.getPlayers()) {
                        if (player != this) {
                            player.subtractMoney(amount);
                            addMoney(amount);
                            System.out.println(name + " collected $" + amount + " from " + player.getName());
                        }
                    }
                }
            }
        } else if (cardText.contains("Advance")) {
            // Handle "Advance to..." cards
            String destination = "";
            if (cardText.contains("Illinois")) {
                destination = "Illinois Avenue";
            } else if (cardText.contains("St. Charles")) {
                destination = "St. Charles Place";
            } else if (cardText.contains("Boardwalk")) {
                destination = "Boardwalk";
            } else if (cardText.contains("nearest Railroad")) {
                destination = "nearest Railroad";
            } else if (cardText.contains("nearest Utility")) {
                destination = "nearest Utility";
            }

            if (!destination.isEmpty()) {
                advanceToLocation(destination, gameState);
            }
        }
    }

    /**
     * Handles buying a railroad if the player has enough money.
     *
     * @param railroad The railroad to buy
     * @return true if the railroad was successfully bought, false otherwise
     */
    public boolean buyRailroad(RailroadSpace railroad) {
        if (money >= railroad.getPrice()) {
            money -= railroad.getPrice();
            railroad.setOwner(this);
            System.out.println(name + " bought " + railroad.getName() + " for $" + railroad.getPrice() +
                    ". New balance: $" + money);
            return true;
        } else {
            System.out.println(name + " does not have enough money to buy " + railroad.getName());
            return false;
        }
    }

    /**
     * Handles buying a utility if the player has enough money.
     *
     * @param utility The utility to buy
     * @return true if the utility was successfully bought, false otherwise
     */
    public boolean buyUtility(UtilitySpace utility) {
        if (money >= utility.getPrice()) {
            money -= utility.getPrice();
            utility.setOwner(this);
            System.out.println(name + " bought " + utility.getName() + " for $" + utility.getPrice() +
                    ". New balance: $" + money);
            return true;
        } else {
            System.out.println(name + " does not have enough money to buy " + utility.getName());
            return false;
        }
    }

    /**
     * Advances the player to a specific location on the board.
     *
     * @param destination The name of the destination or a special descriptor like "nearest Railroad"
     * @param gameState The current game state
     */
    private void advanceToLocation(String destination, GameState gameState) {
        Gameboard board = gameState.getBoard();
        List<Space> spaces = board.getSpaces();

        if (destination.equals("nearest Railroad")) {
            // Find the nearest railroad
            int closestRailroad = -1;
            int closestDistance = Integer.MAX_VALUE;

            for (int i = 0; i < spaces.size(); i++) {
                Space space = spaces.get(i);
                if (space instanceof RailroadSpace) {
                    int distance = (i - position + spaces.size()) % spaces.size();
                    if (distance > 0 && distance < closestDistance) {
                        closestDistance = distance;
                        closestRailroad = i;
                    }
                }
            }

            if (closestRailroad != -1) {
                System.out.println(name + " advances to " + spaces.get(closestRailroad).getName());
                boolean passedGo = closestRailroad < position;
                setPosition(closestRailroad);

                if (passedGo) {
                    System.out.println(name + " passed Go and collects $200");
                    addMoney(200);
                }
                // Check if a Railroad space has an owner and make the player pay double rent
                Space railroadSpace = spaces.get(closestRailroad);
                if (railroadSpace instanceof RailroadSpace) {
                    RailroadSpace railroad = (RailroadSpace) railroadSpace;
                    Player owner = railroad.getOwner();
                    if (owner != null && owner != this) {
                        // Pay double rent
                        int rent = railroad.calculateRent(gameState) * 2;
                        System.out.println(name + " must pay double rent ($" + rent + ") for landing on " + railroad.getName());
                        payRent(owner, rent);
                    } else if (owner == null) {
                        // Option to buy the railroad
                        if (money >= railroad.getPrice()) {
                            boolean wantToBuy = true; // Simplified - in a real game this would be a player choice
                            if (wantToBuy) {
                                buyRailroad(railroad);
                            }
                        }
                    }
                }
            }
        } else if (destination.equals("nearest Utility")) {
            // Find the nearest utility
            int closestUtility = -1;
            int closestDistance = Integer.MAX_VALUE;

            for (int i = 0; i < spaces.size(); i++) {
                Space space = spaces.get(i);
                if (space instanceof UtilitySpace) {
                    int distance = (i - position + spaces.size()) % spaces.size();
                    if (distance > 0 && distance < closestDistance) {
                        closestDistance = distance;
                        closestUtility = i;
                    }
                }
            }

            if (closestUtility != -1) {
                System.out.println(name + " advances to " + spaces.get(closestUtility).getName());
                boolean passedGo = closestUtility < position;
                setPosition(closestUtility);

                if (passedGo) {
                    System.out.println(name + " passed Go and collects $200");
                    addMoney(200);
                }

                // Check if a Utility space has an owner and make the player pay rent based on dice roll
                Space utilitySpace = spaces.get(closestUtility);
                if (utilitySpace instanceof UtilitySpace) {
                    UtilitySpace utility = (UtilitySpace) utilitySpace;
                    Player owner = utility.getOwner();
                    if (owner != null && owner != this) {
                        // Roll dice to determine rent
                        int diceRoll = dice.rollDice();
                        // Charge 10 times the dice roll for utilities landed on via Chance or Community Chest
                        int rent = diceRoll * 10;
                        System.out.println(name + " must pay special utility rent ($" + rent + ") for landing on " + utility.getName());
                        payRent(owner, rent);
                    } else if (owner == null) {
                        // Option to buy the utility
                        if (money >= utility.getPrice()) {
                            boolean wantToBuy = true; // Simplified - in a real game this would be a player choice
                            if (wantToBuy) {
                                buyUtility(utility);
                            }
                        }
                    }
                }
            }
        } else {
            // Find the named location
            int destinationPosition = -1;
            for (int i = 0; i < spaces.size(); i++) {
                if (spaces.get(i).getName().equals(destination)) {
                    destinationPosition = i;
                    break;
                }
            }

            if (destinationPosition != -1) {
                System.out.println(name + " advances to " + destination);
                boolean passedGo = destinationPosition < position && position != 0;
                setPosition(destinationPosition);

                if (passedGo) {
                    System.out.println(name + " passed Go and collects $200");
                    addMoney(200);
                }

                // Handle actions for the new space
                performTurnActions(gameState);
            } else {
                System.out.println("Could not find location: " + destination);
            }
        }
    }

    /**
     * Handles landing on a tax space.
     *
     * @param taxSpace The tax space landed on
     * @param gameState The current game state
     */
    private void handleTaxSpace(SpecialSpace taxSpace, GameState gameState) {
        int taxAmount = 0;
        if (taxSpace.getName().equals("Income Tax")) {
            taxAmount = 200;
        } else if (taxSpace.getName().equals("Luxury Tax")) {
            taxAmount = 100;
        }

        System.out.println(name + " must pay $" + taxAmount + " in taxes");
        subtractMoney(taxAmount);

        // Add the tax money to Free Parking if using house rules
        for (Space space : gameState.getBoard().getSpaces()) {
            if (space instanceof FreeParkingSpace) {
                FreeParkingSpace freeParkingSpace = (FreeParkingSpace) space;
                freeParkingSpace.addMoneyToPool(taxAmount);
                break;
            }
        }
    }

    /**
     * Deducts money from the player's account.
     * Alias for subtractMoney for compatibility.
     *
     * @param amount The amount to deduct
     */
    public void deductMoney(int amount) {
        subtractMoney(amount);
    }

    /**
     * Gets all properties owned by this player.
     *
     * @return The list of properties owned by the player
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Sets whether this player has a Get Out of Jail Free card.
     *
     * @param hasCard Whether the player has the card
     */
    public void setHasGetOutOfJailFreeCard(boolean hasCard) {
        this.hasGetOutOfJailFreeCard = hasCard;
    }

    /**
     * Checks if the player has a Get Out of Jail Free card.
     *
     * @return true if the player has the card, false otherwise
     */
    public boolean hasGetOutOfJailFreeCard() {
        return hasGetOutOfJailFreeCard;
    }

    /**
     * Gets the number of turns the player has been in jail.
     *
     * @return The number of turns in jail
     */
    public int getTurnsInJail() {
        return turnsInJail;
    }

    /**
     * Sets the number of turns the player has been in jail.
     *
     * @param turns The number of turns
     */
    public void setTurnsInJail(int turns) {
        this.turnsInJail = turns;
    }

    /**
     * Checks if the player is bankrupt (has no money).
     *
     * @return true if the player is bankrupt, false otherwise
     */
    public boolean isBankrupt() {
        return money <= 0;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string representation of the player
     */
    @Override
    public String toString() {
        return name + " ($" + money + ", Position: " + position +
                ", Token: " + token + ", Properties: " + properties.size() +
                ", Mortgaged: " + mortgagedProperties.size() + ")";
    }
}
}

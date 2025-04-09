package Model.Board;

import Model.GameState;
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
     * Handles a turn for the player.
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
                goToJail(gameState);
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
     * Sends the player to jail.
     *
     * @param gameState The current game state
     */
    public void goToJail(GameState gameState) {
        setPosition(10); // Jail space is at position 10
        gameState.sendToJail(this);
    }

    /**
     * Checks if the player should go to jail based on consecutive doubles.
     *
     * @return true if the player should go to jail, false otherwise
     */
    public boolean shouldGoToJail() {
        return dice.getConsecutiveDoubles() == 3;
    }

    /**
     * Moves the player on the board, handling passing Go.
     *
     * @param rollDice  The number of spaces to move
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
     * Performs actions based on the space landed on.
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
                goToJail(gameState);
            } else if (specialSpace.getType().equals("Tax")) {
                handleTaxSpace(specialSpace, gameState);
            }
        }
    }

    // Other existing methods remain the same...

    // Add methods like handleCardEffect(), handleTaxSpace(), etc. from the previous implementation

    // Getters and setters for all properties
    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean subtractMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Handles the effects of Chance and Community Chest cards.
     *
     * @param cardText  The text of the card
     * @param gameState The current game state
     */
    private void handleCardEffect(String cardText, GameState gameState) {
        // Simple pattern matching for card effects
        if (cardText.contains("Advance to Go")) {
            position = 0;
            addMoney(200);
            System.out.println(name + " advances to Go and collects $200");
        } else if (cardText.contains("Go to Jail")) {
            goToJail(gameState);
        } else if (cardText.contains("Get out of Jail Free")) {
            hasGetOutOfJailFreeCard = true;
            System.out.println(name + " received a Get Out of Jail Free card");
        } else if (cardText.contains("collect") || cardText.contains("Collect") ||
                cardText.contains("receive") || cardText.contains("Receive")) {
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
     * Handles landing on a tax space.
     *
     * @param taxSpace  The tax space landed on
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
     * Advances the player to a specific location on the board.
     *
     * @param destination The name of the destination or a special descriptor like "nearest Railroad"
     * @param gameState   The current game state
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
     * Pays rent to another player.
     *
     * @param owner  The property owner receiving the rent
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
     * Buys a railroad.
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
     * Buys a utility.
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
     * Buys a property.
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
     * Checks if the player is bankrupt.
     *
     * @return true if the player is bankrupt, false otherwise
     */
    public boolean isBankrupt() {
        return money <= 0;
    }

    /**
     * Gets a list of the player's properties.
     *
     * @return The list of properties owned by the player
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Sets whether the player has a Get Out of Jail Free card.
     *
     * @param hasCard true if the player has the card, false otherwise
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
     * @param turns The number of turns in jail
     */
    public void setTurnsInJail(int turns) {
        this.turnsInJail = turns;
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
     * Sets the player's token.
     *
     * @param token The token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Chooses a token for the player.
     *
     * @param chosenToken The token to choose
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
     * Gets the mortgaged properties of the player.
     *
     * @return The list of mortgaged properties
     */
    public List<Property> getMortgagedProperties() {
        return mortgagedProperties;
    }

    /**
     * Mortgages a property.
     *
     * @param property The property to mortgage
     * @return true if the property was successfully mortgaged, false otherwise
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
     * Unmortgages a property.
     *
     * @param property The property to unmortgage
     * @return true if the property was successfully unmortgaged, false otherwise
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
     * Checks if a property is mortgaged.
     *
     * @param property The property to check
     * @return true if the property is mortgaged, false otherwise
     */
    public boolean isPropertyMortgaged(Property property) {
        return mortgagedProperties.contains(property);
    }

    /**
     * Returns a string representation of the player.
     *
     * @return A string with player details
     */
    @Override
    public String toString() {
        return name + " ($" + money + ", Position: " + position +
                ", Token: " + token + ", Properties: " + properties.size() +
                ", Mortgaged: " + mortgagedProperties.size() + ")";
    }
}

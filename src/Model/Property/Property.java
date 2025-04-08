package Model.Property;

import Model.Board.Bank;
import Model.Board.Player;
import Model.GameState;
import Model.Houses;
import Model.Spaces.Space;
import Model.Cards.TitleDeedCard;

/**
 * Represents a property space on the Monopoly board.
 * Properties can be bought, sold, and developed with houses and hotels.
 */
public class Property extends Space {
    private int price;
    private String colorGroup;
    private Player owner;
    private int houses;
    private boolean hasHotel;
    private boolean isMortgaged;
    private TitleDeedCard titleDeed;

    /**
     * Author: Marena
     * Constructs a new property on the Monopoly board with a title deed card.
     *
     * @param name     The name of the property
     * @param position The position on the board
     */
    public Property(String name, int position) {
        super(name, position, "Model.Property.Property");
        this.price = price;
        this.colorGroup = colorGroup;
        this.owner = null;  // No owner initially
        this.houses = 0;
        this.hasHotel = false;
        this.isMortgaged = false;

        // Create corresponding title deed card with rent values
        // These rent values should be specific to each property in a real implementation
        int[] houseRents = new int[4];
        int  baseRent = 0;
        switch (colorGroup) {
            case "Brown":
            case "Light Blue":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 80;   // 4 houses
                break;
            case "Pink":
            case "Orange":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 80;   // 4 houses
                break;
            case "Red":
            case "Yellow":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 80;   // 4 houses
                break;
            case "Green":
            case "Dark Blue":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 80;   // 4 houses
                break;
            default:
                houseRents[0] = baseRent * 5;    // Default values
                houseRents[1] = baseRent * 15;
                houseRents[2] = baseRent * 45;
                houseRents[3] = baseRent * 80;
        }

        int houseCost = Houses.getHousePrice(colorGroup);
        int hotelRent = baseRent * 100;

        this.titleDeed = new TitleDeedCard(name, colorGroup, price, baseRent, houseRents, hotelRent, houseCost);
    }

    /**
     * Author: Marena
     * Sets the owner of the property.
     *
     * @param owner The player who owns the property
     */
    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Author: Marena
     * Gets the owner of the property.
     *
     * @return The owner of the property
     */
    @Override
    public Player getOwner() {
        return owner;
    }

    /**
     * Author: Marena
     * Gets the title deed card for this property.
     *
     * @return The title deed card
     */
    public TitleDeedCard getTitleDeed() {
        return titleDeed;
    }

    /**
     * Author: Marena
     * Gets the name of the property.
     *
     * @return The name of the property
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Author: Marena
     * Gets the price of the property.
     *
     * @return The price of the property
     */
    public int getPrice() {
        return price;
    }

    /**
     * Author: Marena
     * Gets the color group of the property.
     *
     * @return The color group of the property
     */
    public String getColorGroup() {
        return colorGroup;
    }

    /**
     * Author: Marena
     * Adds a house to the property if possible.
     *
     * @return True if a house was added, false otherwise
     */
    public boolean addHouse() {
        if (houses < 4 && !hasHotel) {
            houses++;
            return true;
        } else if (houses == 4 && !hasHotel) {
            houses = 0;
            hasHotel = true;
            return true;
        } else {
            System.out.println(name + " already has a hotel!");
            return false;
        }
    }

    /**
     * Author: Marena
     * Removes a house from the property if possible.
     *
     * @return True if a house was removed, false otherwise
     */
    public boolean removeHouse() {
        if (houses > 0) {
            houses--;
            return true;
        } else if (hasHotel) {
            hasHotel = false;
            houses = 4;
            return true;
        } else {
            System.out.println(name + " has no houses or hotels to remove!");
            return false;
        }
    }

    /**
     * Author: Marena
     * Checks if the property is owned.
     *
     * @return True if the property is owned, false otherwise
     */
    public boolean isOwned() {
        return owner != null;
    }

    /**
     * Author: Marena
     * Checks if the property is mortgaged.
     *
     * @return True if the property is mortgaged, false otherwise
     */
    public boolean isMortgaged() {
        return isMortgaged;
    }

    /**
     * Sets the mortgage status of the property.
     *
     * @param mortgaged The new mortgage status
     */
    public void setMortgaged(boolean mortgaged) {
        this.isMortgaged = mortgaged;
        this.titleDeed.setMortgaged(mortgaged);
    }

    /**
     * Author: Marena
     * Handles what happens when a player lands on this property.
     *
     * @param player The player who landed on the property
     * @param gameState The current game state
     */
    public void onLand(Player player, GameState gameState) {
        System.out.println(player.getName() + " landed on " + name);

        if (isOwned()) {
            if (owner != player) {
                if (isMortgaged) {
                    System.out.println(name + " is mortgaged, no rent is due.");
                } else {
                    int calculatedRent = calculateRent(gameState);
                    System.out.println(player.getName() + " must pay $" + calculatedRent + " rent to " + owner.getName());
                    player.payRent(owner, calculatedRent);
                }
            } else {
                System.out.println(player.getName() + " owns this property.");
            }
        } else {
            System.out.println(name + " is not owned. It costs $" + price);
            // Logic for player to decide to buy would be handled elsewhere
            if (player.getMoney() >= price) {
                // In a real game, this would be a player decision
                // For now we'll assume the player always wants to buy if they can afford it
                boolean wantToBuy = true; // Simplified for implementation
                if (wantToBuy) {
                    player.buyProperty(this);
                } else {
                    // Auction the property if player doesn't want to buy
                    Bank bank = gameState.getBank();
                    if (bank != null) {
                        bank.auctionProperty(this, gameState.getPlayers());
                    }
                }
            } else {
                System.out.println(player.getName() + " cannot afford to buy " + name);
            }
        }
    }

    /**
     * Author: Marena
     * Prompt the player to buy the property.
     * This is a placeholder for a real UI interaction.
     *
     * @param player The player to prompt
     * @return true if the player wants to buy, false otherwise
     */
    private boolean promptToBuy(Player player) {
        // In a real implementation, this would show a prompt to the player
        // For now, we'll just return true to simulate the player always buying
        return true;
    }

    /**
     * Author: Marena
     * Calculates the rent based on houses, hotels, and monopoly ownership.
     *
     * @param gameState The current game state
     * @return The calculated rent
     */
    public int calculateRent(GameState gameState) {
        if (isMortgaged) {
            return 0;
        }

        if (houses == 0 && !hasHotel) {
            // Check if all properties in the color group are owned by the same player
            if (gameState.getBoard().playerOwnsAllInColorGroup(owner, colorGroup)) {
                return titleDeed.getBaseRent() * 2; // Double rent for a monopoly
            }
            return titleDeed.getBaseRent();
        } else if (hasHotel) {
            return titleDeed.getHotelRent();
        } else {
            return titleDeed.getHouseRent(houses);
        }
    }

    /**
     * Author: Marena
     * Returns a string representation of the property.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" - Price: $").append(price);
        sb.append(", Color: ").append(colorGroup);
        sb.append(", Owner: ").append(owner != null ? owner.getName() : "None");
        sb.append(", Model.Houses: ").append(houses);
        sb.append(", Hotel: ").append(hasHotel ? "Yes" : "No");
        sb.append(", Mortgaged: ").append(isMortgaged ? "Yes" : "No");
        return sb.toString();
    }

    /**
     * Author: Marena
     * Checks if the property has a hotel.
     *
     * @return True if the property has a hotel, false otherwise
     */
    public boolean hasHotel() {
        return hasHotel;
    }

    /**
     * Author: Aiden Clare
     * Gets the number of houses on the property.
     *
     * @return The number of houses
     */
    public int getHouses() {
        return houses;
    }

    /**
     * Author: Marena
     * Sets the number of houses on the property.
     *
     * @param houses The new number of houses
     */
    public void setHouses(int houses) {
        this.houses = houses;
    }

    /**
     * Author: Marena
     * Gets the base rent of the property.
     *
     * @return The base rent
     */
    public int getRent() {
        return titleDeed.getBaseRent();
    }

    /**
     * Author: Marena
     * Sets whether the property has a hotel.
     *
     * @param hasHotel True if the property should have a hotel, false otherwise
     */
    public void setHasHotel(boolean hasHotel) {
        this.hasHotel = hasHotel;
    }

    /**
     * Author: Marena
     * Gets the mortgage value of the property.
     *
     * @return The mortgage value (half the purchase price)
     */
    public int getMortgageValue() {
        return titleDeed.getMortgageValue();
    }

    /**
     * Author: Marena
     * Gets the cost to unmortgage the property.
     *
     * @return The unmortgage cost (mortgage value plus 10% interest)
     */
    public int getUnmortgageCost() {
        return titleDeed.getUnmortgageCost();
    }

    /**
     * Author: Marena
     * Checks if the property is a utility.
     */

    @Override
    public void playerOnProperty() {
        System.out.println("Model.Board.Player landed on property " + name);
    }
}
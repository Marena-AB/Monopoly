package Model.Property;
/**
 * Represents a property space on the Monopoly board.
 * Properties can be bought, sold, and developed with houses and hotels.
 */
import Model.GameState;
import Model.Houses;
import Model.Spaces.Space;
import Model.Cards.TitleDeedCard;


public class Property extends Space {
    private int price;
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
     * @param price    The purchase price of the property
     * @param colorGroup The color group of the property
     */
    public Property(String name, int position, int price, String colorGroup) {
        super(name, position, "Property");
        this.price = price;
        this.colorGroup = colorGroup;
        this.owner = null;  // No owner initially
        this.houses = 0;
        this.hasHotel = false;
        this.isMortgaged = false;

        // Create corresponding title deed card with rent values
        // These rent values are based on standard Monopoly rules
        int baseRent = calculateBaseRent(price);
        int[] houseRents = calculateHouseRents(baseRent, colorGroup);
        int hotelRent = calculateHotelRent(baseRent, colorGroup);
        int houseCost = Houses.getHousePrice(colorGroup);

        this.titleDeed = new TitleDeedCard(name, colorGroup, price, baseRent, houseRents, hotelRent, houseCost);
    }

    /**
     * Author: Marena
     * Simplified constructor for when specific values are not provided.
     * This constructor can be used for backward compatibility.
     *
     * @param name     The name of the property
     * @param position The position on the board
     */
    public Property(String name, int position) {
        this(name, position, getDefaultPrice(position), getDefaultColorGroup(position));
    }

    /**
     * Author: Marena
     * Calculates the base rent for a property based on its price.
     * By Monopoly standards, base rent is typically 10% of the purchase price.
     *
     * @param price The purchase price of the property
     * @return The base rent
     */
    private int calculateBaseRent(int price) {
        return (int)(price * 0.1); // Standard: base rent is 10% of purchase price
    }

    /**
     * Author: Marena
     * Calculates the rent values for properties with houses.
     * Follows standard Monopoly progression of rent increases.
     *
     * @param baseRent The base rent without houses
     * @param colorGroup The color group of the property
     * @return An array of rent values for 1-4 houses
     */
    private int[] calculateHouseRents(int baseRent, String colorGroup) {
        int[] houseRents = new int[4];

        // Different color groups have different rent progressions
        switch (colorGroup.toLowerCase()) {
            case "brown":
            case "light blue":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 80;   // 4 houses
                break;
            case "pink":
            case "orange":
                houseRents[0] = baseRent * 5;    // 1 house
                houseRents[1] = baseRent * 15;   // 2 houses
                houseRents[2] = baseRent * 45;   // 3 houses
                houseRents[3] = baseRent * 90;   // 4 houses
                break;
            case "red":
            case "yellow":
                houseRents[0] = baseRent * 3;    // 1 house
                houseRents[1] = baseRent * 9;    // 2 houses
                houseRents[2] = baseRent * 27;   // 3 houses
                houseRents[3] = baseRent * 50;   // 4 houses
                break;
            case "green":
            case "dark blue":
                houseRents[0] = baseRent * 7;    // 1 house
                houseRents[1] = baseRent * 20;   // 2 houses
                houseRents[2] = baseRent * 55;   // 3 houses
                houseRents[3] = baseRent * 110;  // 4 houses
                break;
            default:
                houseRents[0] = baseRent * 5;    // Default values
                houseRents[1] = baseRent * 15;
                houseRents[2] = baseRent * 45;
                houseRents[3] = baseRent * 80;
        }

        return houseRents;
    }

    /**
     * Author: Marena
     * Calculates the hotel rent based on base rent and color group.
     *
     * @param baseRent The base rent without houses
     * @param colorGroup The color group of the property
     * @return The hotel rent
     */
    private int calculateHotelRent(int baseRent, String colorGroup) {
        // Hotel rent is typically higher than 4-house rent
        switch (colorGroup.toLowerCase()) {
            case "brown":
            case "light blue":
                return baseRent * 125;
            case "pink":
            case "orange":
                return baseRent * 140;
            case "red":
            case "yellow":
                return baseRent * 150;
            case "green":
            case "dark blue":
                return baseRent * 170;
            default:
                return baseRent * 125;
        }
    }

    /**
     * Author: Marena
     * Gets the default price for a property based on its position.
     * This is a simplified approach for backward compatibility.
     *
     * @param position The position on the board
     * @return The default price
     */
    private static int getDefaultPrice(int position) {
        // Simple rule: Properties closer to Go are cheaper
        // In a real implementation, this would be a lookup table with actual prices
        int basePrice = 60;
        return basePrice + ((position / 5) * 20);
    }

    /**
     * Author: Marena
     * Gets the default color group for a property based on its position.
     * This is a simplified approach for backward compatibility.
     *
     * @param position The position on the board
     * @return The default color group
     */
    private static String getDefaultColorGroup(int position) {
        // Simple mapping of positions to color groups
        // In a real implementation, this would be a lookup table with actual assignments
        String[] colorGroups = {"Brown", "Light Blue", "Pink", "Orange",
                "Red", "Yellow", "Green", "Dark Blue"};
        return colorGroups[(position / 5) % colorGroups.length];
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
     * Gets the price of the property.
     *
     * @return The price of the property
     */
    public int getPrice() {
        return price;
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
     * Edited by: Aiden Clare
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
     * Author: Marena
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
    public void onLand(Model.Board.Player player, Model.GameState gameState) {
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
                boolean wantToBuy = true;
                if (wantToBuy) {
                    player.buyProperty(this);
                } else {
                    // Auction the property if player doesn't want to buy
                    if (gameState.getBank() != null) {
                        gameState.getBank().auctionProperty(this, gameState.getPlayers());
                    }
                }
            } else {
                System.out.println(player.getName() + " cannot afford to buy " + name);
            }
        }
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
     * @return A formatted string with property details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" - Price: $").append(price);
        sb.append(", Color: ").append(colorGroup);
        sb.append(", Owner: ").append(owner != null ? owner.getName() : "None");
        sb.append(", Houses: ").append(houses);
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
     * Author: Marena
     * Gets the number of houses on the property.
     *
     * @return The number of houses
     */
    public int getHouses() {
        return houses;
    }

    /**
     * Author: Aiden Clare
     * Sets the number of houses on the property.
     *
     * @param houses The new number of houses
     */
    public void setHouses(int houses) {
        this.houses = houses;
    }

    /**
     * Author: Aiden Clare
     * Gets the base rent of the property.
     *
     * @return The base rent
     */
    public int getRent() {
        return titleDeed.getBaseRent();
    }

    /**
     * Author: Aiden Clare
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
     * Called when a player lands on the property.
     * This is an implementation of the method from the Space class.
     */
    @Override
    public void playerOnProperty() {
        System.out.println("Player landed on property " + name);
    }
}
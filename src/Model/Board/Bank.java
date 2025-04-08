package Model.Board; /**
 * This class represents the Model.Board.Bank in the Monopoly game.
 * The Model.Board.Bank manages money, property deeds, houses, and hotels.
 * It provides methods for buying and selling properties,
 * houses, and hotels, as well as conducting auctions.
 */

import Model.Houses;
import Model.Property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Marena Abboud
 * Represents the Model.Board.Bank in the Monopoly game.
 * The Model.Board.Bank manages money, property deeds, houses, and hotels.
 */
public class Bank {
    private int money;
    private List<Property> availableProperties;
    private int houses;
    private int hotels;
    private static final int MAX_HOUSES = 32;
    private static final int MAX_HOTELS = 12;
    private static final int STARTING_MONEY = 1500;

    /**
     * Author: Marena Abboud
     * Constructs a new Model.Board.Bank with all resources initialized.
     */
    public Bank() {
        this.money = Integer.MAX_VALUE; // Model.Board.Bank has unlimited money
        this.availableProperties = new ArrayList<>();
        this.houses = MAX_HOUSES;
        this.hotels = MAX_HOTELS;
    }

    /**
     * Author : Marena
     * Sets the initial properties available for purchase.
     *
     * @param properties The list of properties from the game board
     */
    public void setAvailableProperties(List<Property> properties) {
        this.availableProperties = new ArrayList<>(properties);
    }
    public List<Property> getAvailableProperties() {
        return availableProperties;
    }

    /**
     * Author: Marena Abboud
     * Gives starting money to a player.
     *
     * @param player The player to receive the money
     */
    public void giveStartingMoney(Player player) {
        player.addMoney(STARTING_MONEY);
        System.out.println(player.getName() + " receives $" + STARTING_MONEY + " from the bank.");
    }

    /**
     * Author: Marena Abboud
     * Handles a player passing Go by giving them $200.
     *
     * @param player The player who passed Go
     */
    public void playerPassedGo(Player player) {
        player.addMoney(200);
        System.out.println(player.getName() + " passed Go and collects $200 from the bank.");
    }

    /**
     * Author: Marena Abboud
     * Sells a property to a player if they have enough money.
     *
     * @param property The property to sell
     * @param player The player buying the property
     * @return true if the sale was successful, false otherwise
     */
    public boolean sellProperty(Property property, Player player) {
        if (!availableProperties.contains(property)) {
            System.out.println("This property is not available for purchase.");
            return false;
        }

        if (player.getMoney() < property.getPrice()) {
            System.out.println(player.getName() + " does not have enough money to buy " + property.getName());
            return false;
        }

        player.subtractMoney(property.getPrice());
        property.setOwner(player);
        availableProperties.remove(property);

        System.out.println(player.getName() + " bought " + property.getName() + " for $" + property.getPrice());
        return true;
    }

    /**
     * Author: Marena Abboud
     * Sells houses to a player for a specific property if the bank has enough houses.
     *
     * @param property The property to add houses to
     * @param player The player buying the houses
     * @param count The number of houses to buy
     * @return true if the houses were successfully sold, false otherwise
     */
    public boolean sellHouses(Property property, Player player, int count) {
        // Check if bank has enough houses
        if (houses < count) {
            System.out.println("The bank does not have enough houses. Only " + houses + " available.");
            return false;
        }

        // Check if player owns the property
        if (property.getOwner() != player) {
            System.out.println(player.getName() + " does not own " + property.getName());
            return false;
        }

        // Check if property is part of a monopoly
        Gameboard board = new Gameboard(); // You'll need to pass the actual board instance
        if (!board.playerOwnsAllInColorGroup(player, property.getColorGroup())) {
            System.out.println("You must own all properties in the " + property.getColorGroup() + " color group to buy houses.");
            return false;
        }

        // Check if houses will be evenly distributed
        List<Property> propertiesInGroup = board.getPropertiesByColorGroup(property.getColorGroup());
        if (!willHousesBeEvenlyDistributed(propertiesInGroup, property, count)) {
            System.out.println("Model.Houses must be evenly distributed across all properties in a color group.");
            return false;
        }

        // Calculate cost
        int housePrice = Houses.getHousePrice(property.getColorGroup());
        int totalCost = housePrice * count;

        // Check if player has enough money
        if (player.getMoney() < totalCost) {
            System.out.println(player.getName() + " does not have enough money to buy " + count + " houses.");
            return false;
        }

        // Update money, house count, and property
        player.subtractMoney(totalCost);
        houses -= count;

        // Add houses to the property
        for (int i = 0; i < count; i++) {
            property.addHouse();
        }

        System.out.println(player.getName() + " bought " + count + " houses for " + property.getName() + " at $" + totalCost);
        return true;
    }

    /**
     * Author: Marena Abboud
     * Sells a hotel to a player for a specific property if the bank has enough hotels.
     *
     * @param property The property to add a hotel to
     * @param player The player buying the hotel
     * @return true if the hotel was successfully sold, false otherwise
     */
    public boolean sellHotel(Property property, Player player) {
        // Check if bank has enough hotels
        if (hotels < 1) {
            System.out.println("The bank does not have any hotels available.");
            return false;
        }

        // Check if player owns the property
        if (property.getOwner() != player) {
            System.out.println(player.getName() + " does not own " + property.getName());
            return false;
        }

        // Check if property has 4 houses
        if (property.getHouses() != 4) {
            System.out.println("You need 4 houses on " + property.getName() + " before you can buy a hotel.");
            return false;
        }

        // Calculate cost
        int hotelPrice = Houses.getHousePrice(property.getColorGroup());

        // Check if player has enough money
        if (player.getMoney() < hotelPrice) {
            System.out.println(player.getName() + " does not have enough money to buy a hotel.");
            return false;
        }

        // Update money and property
        player.subtractMoney(hotelPrice);
        property.addHouse(); // This will convert 4 houses to a hotel
        hotels--;
        houses += 4; // Return the 4 houses to the bank

        System.out.println(player.getName() + " bought a hotel for " + property.getName() + " at $" + hotelPrice);
        return true;
    }

    /**
     * Author: Marena Abboud
     * Buys back houses from a player.
     *
     * @param property The property to remove houses from
     * @param player The player selling the houses
     * @param count The number of houses to sell
     * @return true if the houses were successfully bought back, false otherwise
     */
    public boolean buyBackHouses(Property property, Player player, int count) {
        // Check if player owns the property
        if (property.getOwner() != player) {
            System.out.println(player.getName() + " does not own " + property.getName());
            return false;
        }

        // Check if property has enough houses
        if (property.getHouses() < count) {
            System.out.println(property.getName() + " does not have " + count + " houses to sell.");
            return false;
        }

        // Calculate refund (half of purchase price)
        int housePrice = Houses.getHousePrice(property.getColorGroup());
        int refund = (housePrice * count) / 2;

        // Remove houses from property
        for (int i = 0; i < count; i++) {
            property.removeHouse();
        }

        // Update money and house count
        player.addMoney(refund);
        houses += count;

        System.out.println(player.getName() + " sold " + count + " houses from " + property.getName() + " for $" + refund);
        return true;
    }

    /**
     * Author: Marena Abboud
     * Buys back a hotel from a player.
     *
     * @param property The property to remove the hotel from
     * @param player The player selling the hotel
     * @return true if the hotel was successfully bought back, false otherwise
     */
    public boolean buyBackHotel(Property property, Player player) {
        // Check if player owns the property
        if (property.getOwner() != player) {
            System.out.println(player.getName() + " does not own " + property.getName());
            return false;
        }

        // Check if property has a hotel
        if (!property.hasHotel()) {
            System.out.println(property.getName() + " does not have a hotel to sell.");
            return false;
        }

        // Check if bank has enough houses to give back
        if (houses < 4) {
            System.out.println("The bank does not have enough houses to replace the hotel.");
            return false;
        }

        // Calculate refund (half of purchase price)
        int hotelPrice = Houses.getHousePrice(property.getColorGroup());
        int refund = hotelPrice / 2;

        // Remove hotel from property and replace with 4 houses
        property.setHasHotel(false);
        property.setHouses(4);

        // Update money, house count, and hotel count
        player.addMoney(refund);
        hotels++;
        houses -= 4;

        System.out.println(player.getName() + " sold a hotel from " + property.getName() + " for $" + refund);
        return true;
    }

    /**
     * Author: Marena Abboud
     * Conducts an auction for a property when a player doesn't want to buy it.
     *
     * @param property The property to auction
     * @param players The list of players who can bid
     */
    public void auctionProperty(Property property, List<Player> players) {
        System.out.println("Starting auction for " + property.getName());
        System.out.println("Minimum bid: $1");

        Map<Player, Integer> bids = new HashMap<>();
        for (Player player : players) {
            // In a real implementation, you would get bids from each player
            // For simplicity, we'll simulate random bids
            if (player.getMoney() > 0) {
                int maxBid = Math.min(player.getMoney(), property.getPrice());
                int bid = (int) (Math.random() * maxBid) + 1;
                bids.put(player, bid);
                System.out.println(player.getName() + " bids $" + bid);
            }
        }

        // Find the highest bidder
        Player highestBidder = null;
        int highestBid = 0;

        for (Map.Entry<Player, Integer> entry : bids.entrySet()) {
            if (entry.getValue() > highestBid) {
                highestBid = entry.getValue();
                highestBidder = entry.getKey();
            }
        }

        if (highestBidder != null) {
            // Sell property to highest bidder
            highestBidder.subtractMoney(highestBid);
            property.setOwner(highestBidder);
            availableProperties.remove(property);

            System.out.println(highestBidder.getName() + " won the auction for " + property.getName() + " with a bid of $" + highestBid);
        } else {
            System.out.println("No one bid on " + property.getName() + ". It remains with the bank.");
        }
    }

    /**
     * Author: Marena Abboud
     * Checks if adding houses to a property will maintain even distribution across all properties in the color group.
     *
     * @param propertiesInGroup All properties in the color group
     * @param property The property getting new houses
     * @param count The number of houses to add
     * @return true if houses will be evenly distributed, false otherwise
     */
    private boolean willHousesBeEvenlyDistributed(List<Property> propertiesInGroup, Property property, int count) {
        int currentHouses = property.getHouses();
        int newHouses = currentHouses + count;

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
     * Author: Marena Abboud
     * Returns the number of houses available in the bank.
     */
    public int getHouses() {
        return houses;
    }

    /**
     * Author: Marena Abboud
     * Returns the number of hotels available in the bank.
     */
    public int getHotels() {
        return hotels;
    }
}
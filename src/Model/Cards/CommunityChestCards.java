package Model.Cards;

import Model.Board.Player;
import Model.GameState;
import Model.Spaces.GoSpace;
import Model.Spaces.JailSpace;

/**
 * Represents a Community Chest card in the Monopoly game.
 * Each card has a specific effect when drawn.
 */
class CommunityChestCard extends Card {

    /**
     * Constructs a Community Chest card with the given description.
     *
     * @param description The text of the card
     */
    public CommunityChestCard(String description) {
        super(description);
    }

    /**
     * Gets the card type.
     *
     * @return "Community Chest"
     */
    @Override
    public String getCardType() {
        return "Community Chest";
    }

    /**
     * Gets the deck name.
     *
     * @return "Community Chest Deck"
     */
    @Override
    public String getDeck() {
        return "Community Chest Deck";
    }

    /**
     * Executes the effect of the Community Chest card based on its description.
     *
     * @param player The player who drew the card
     * @param gameState The current game state
     */
    @Override
    public void executeEffect(Player player, GameState gameState) {
        System.out.println(player.getName() + " drew Community Chest card: " + description);

        if (description.contains("Advance to Go")) {
            GoSpace.moveToGo(player, gameState);
        }
        else if (description.contains("Go to Jail")) {
            JailSpace.goToJail(player, gameState);
        }
        else if (description.contains("Get Out of Jail Free")) {
            player.setHasGetOutOfJailFreeCard(true);
            System.out.println(player.getName() + " received a Get Out of Jail Free card");
        }
        else if (description.contains("Bank error in your favor")) {
            player.addMoney(200);
            System.out.println(player.getName() + " received $200 from the bank");
        }
        else if (description.contains("Doctor's fee")) {
            player.subtractMoney(50);
            System.out.println(player.getName() + " paid $50 doctor's fee");
        }
        else if (description.contains("From sale of stock")) {
            player.addMoney(50);
            System.out.println(player.getName() + " received $50 from stock sale");
        }
        else if (description.contains("Holiday fund")) {
            player.addMoney(100);
            System.out.println(player.getName() + " received $100 from holiday fund");
        }
        else if (description.contains("Income tax refund")) {
            player.addMoney(20);
            System.out.println(player.getName() + " received $20 tax refund");
        }
        else if (description.contains("your birthday")) {
            int totalCollected = 0;
            for (Player otherPlayer : gameState.getPlayers()) {
                if (otherPlayer != player) {
                    otherPlayer.subtractMoney(10);
                    totalCollected += 10;
                }
            }
            player.addMoney(totalCollected);
            System.out.println(player.getName() + " collected $" + totalCollected + " for their birthday");
        }
        else if (description.contains("Life insurance")) {
            player.addMoney(100);
            System.out.println(player.getName() + " received $100 from life insurance");
        }
        else if (description.contains("Pay hospital")) {
            player.subtractMoney(100);
            System.out.println(player.getName() + " paid $100 hospital fees");
        }
        else if (description.contains("Pay school")) {
            player.subtractMoney(50);
            System.out.println(player.getName() + " paid $50 school fees");
        }
        else if (description.contains("consultancy fee")) {
            player.addMoney(25);
            System.out.println(player.getName() + " received $25 consultancy fee");
        }
        else if (description.contains("street repairs")) {
            int houses = 0;
            int hotels = 0;

            for (Model.Property.Property property : player.getProperties()) {
                if (property.hasHotel()) {
                    hotels++;
                } else {
                    houses += property.getHouses();
                }
            }

            int totalCost = (houses * 40) + (hotels * 115);
            player.subtractMoney(totalCost);
            System.out.println(player.getName() + " paid $" + totalCost + " for street repairs");
        }
        else if (description.contains("beauty contest")) {
            player.addMoney(10);
            System.out.println(player.getName() + " received $10 from beauty contest");
        }
        else if (description.contains("inherit")) {
            player.addMoney(100);
            System.out.println(player.getName() + " inherited $100");
        }
    }
}

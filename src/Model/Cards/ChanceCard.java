package Model.Cards;

import Model.Board.Player;
import Model.GameState;
import Model.Spaces.GoSpace;
import Model.Spaces.JailSpace;

/**
 * Represents a Chance card in the Monopoly game.
 * Each card has a specific effect when drawn.
 */
public class ChanceCard extends Card {

    /**
     * Constructs a Chance card with the given description.
     *
     * @param description The text of the card
     */
    public ChanceCard(String description) {
        super(description);
    }

    /**
     * Gets the card type.
     *
     * @return "Chance"
     */
    @Override
    public String getCardType() {
        return "Chance";
    }

    /**
     * Gets the deck name.
     *
     * @return "Chance Deck"
     */
    @Override
    public String getDeck() {
        return "Chance Deck";
    }

    /**
     * Executes the effect of the Chance card based on its description.
     *
     * @param player The player who drew the card
     * @param gameState The current game state
     */
    @Override
    public void executeEffect(Player player, GameState gameState) {
        System.out.println(player.getName() + " drew Chance card: " + description);

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
        else if (description.contains("dividend of $50")) {
            player.addMoney(50);
            System.out.println(player.getName() + " received $50 from the bank");
        }
        else if (description.contains("Speeding fine")) {
            player.subtractMoney(15);
            System.out.println(player.getName() + " paid a $15 fine");
        }
        else if (description.contains("building loan")) {
            player.addMoney(150);
            System.out.println(player.getName() + " received $150 from the bank");
        }
        else if (description.contains("crossword competition")) {
            player.addMoney(100);
            System.out.println(player.getName() + " received $100 from the bank");
        }
        else if (description.contains("Chairman of the Board")) {
            int totalPaid = 0;
            for (Player otherPlayer : gameState.getPlayers()) {
                if (otherPlayer != player) {
                    otherPlayer.subtractMoney(50);
                    player.addMoney(50);
                    totalPaid += 50;
                }
            }
            System.out.println(player.getName() + " collected $" + totalPaid + " from other players");
        }
        else if (description.contains("general repairs")) {
            int houses = 0;
            int hotels = 0;

            for (Model.Property.Property property : player.getProperties()) {
                if (property.hasHotel()) {
                    hotels++;
                } else {
                    houses += property.getHouses();
                }
            }

            int totalCost = (houses * 25) + (hotels * 100);
            player.subtractMoney(totalCost);
            System.out.println(player.getName() + " paid $" + totalCost + " for repairs");
        }
        else if (description.contains("Illinois Avenue")) {
            movePlayerToNamedLocation(player, "Illinois Avenue", gameState);
        }
        else if (description.contains("St. Charles Place")) {
            movePlayerToNamedLocation(player, "St. Charles Place", gameState);
        }
        else if (description.contains("Reading Railroad")) {
            movePlayerToNamedLocation(player, "Reading Railroad", gameState);
        }
        else if (description.contains("Boardwalk")) {
            movePlayerToNamedLocation(player, "Boardwalk", gameState);
        }
        else if (description.contains("nearest Railroad")) {
            movePlayerToNearestType(player, "Railroad", gameState);
        }
        else if (description.contains("nearest Utility")) {
            movePlayerToNearestType(player, "Utility", gameState);
        }
        else if (description.contains("Go Back 3 Spaces")) {
            int currentPosition = player.getPosition();
            int newPosition = (currentPosition - 3 + gameState.getBoard().getSpaces().size()) % gameState.getBoard().getSpaces().size();
            player.setPosition(newPosition);
            System.out.println(player.getName() + " moved back 3 spaces to " +
                    gameState.getBoard().getspace(newPosition).getName());
            player.performTurnActions(gameState);
        }
    }

    /**
     * Helper method to move a player to a named location on the board.
     *
     * @param player The player to move
     * @param locationName The name of the location to move to
     * @param gameState The current game state
     */
    private void movePlayerToNamedLocation(Player player, String locationName, GameState gameState) {
        int currentPosition = player.getPosition();
        int destinationPosition = -1;

        // Find the position of the named location
        for (int i = 0; i < gameState.getBoard().getSpaces().size(); i++) {
            if (gameState.getBoard().getspace(i).getName().equals(locationName)) {
                destinationPosition = i;
                break;
            }
        }

        if (destinationPosition != -1) {
            // Check if player passes Go
            boolean passedGo = destinationPosition < currentPosition && currentPosition != 0;

            // Move player
            player.setPosition(destinationPosition);
            System.out.println(player.getName() + " moved to " + locationName);

            // Handle passing Go
            if (passedGo) {
                System.out.println(player.getName() + " passed Go and collects $200");
                player.addMoney(200);
            }

            // Handle actions for the new space
            player.performTurnActions(gameState);
        } else {
            System.out.println("Could not find location: " + locationName);
        }
    }

    /**
     * Helper method to move a player to the nearest location of a specific type.
     *
     * @param player The player to move
     * @param locationType The type of location to move to ("Railroad" or "Utility")
     * @param gameState The current game state
     */
    private void movePlayerToNearestType(Player player, String locationType, GameState gameState) {
        int currentPosition = player.getPosition();
        int boardSize = gameState.getBoard().getSpaces().size();
        int closestPosition = -1;
        int closestDistance = boardSize; // Maximum possible distance

        // Find the nearest location of specified type
        for (int i = 0; i < boardSize; i++) {
            if (gameState.getBoard().getspace(i).getType().contains(locationType)) {
                // Calculate distance (going forward around the board)
                int distance = (i - currentPosition + boardSize) % boardSize;
                if (distance > 0 && distance < closestDistance) {
                    closestDistance = distance;
                    closestPosition = i;
                }
            }
        }

        if (closestPosition != -1) {
            // Check if player passes Go
            boolean passedGo = (currentPosition + closestDistance) > boardSize;

            // Move player
            player.setPosition(closestPosition);
            System.out.println(player.getName() + " moved to " + gameState.getBoard().getspace(closestPosition).getName());

            // Handle passing Go
            if (passedGo) {
                System.out.println(player.getName() + " passed Go and collects $200");
                player.addMoney(200);
            }

            // Handle actions for the new space
            player.performTurnActions(gameState);
        } else {
            System.out.println("Could not find nearest " + locationType);
        }
    }
}
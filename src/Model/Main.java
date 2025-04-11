package Model;
/**
 * Monopoly Game
 * This is a simple implementation of the Monopoly game.
 * It includes basic game mechanics such as rolling dice, moving players, buying properties, and handling jail.
 * and of course, demonstration of the game.
 */

import Model.Board.*;
import Model.Property.Property;
import Model.Spaces.JailSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Author: Team 4
 * Model.Main class to run the Monopoly game.
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Welcome to Monopoly!");

    // Initialize the game components
    Gameboard board = new Gameboard();
    Tokens.initializeTokens();
    Bank bank = new Bank();
    Houses houses = new Houses();
    Hotels hotels = new Hotels();

    // Create players (2-4 players)
    List<Player> players = createPlayers();

    // Let players choose tokens
    assignPlayerTokens(players);

    // Initialize game state and give starting money
    GameState gameState = new GameState(players, board);
    gameState.setBank(bank);

    // Give each player starting money
    for (Player player : players) {
      bank.giveStartingMoney(player);
    }

    // Model.Main game loop
    playGame(gameState);

    // End of game
    announceWinner(players);
  }

  /**
   * Author: Aiden Clare
   * Edited by Marena
   * Creates players for the game.
   *
   * @return A list of players
   */
  private static List<Player> createPlayers() {
    Scanner scanner = new Scanner(System.in);
    List<Player> players = new ArrayList<>();

    System.out.println("How many players? (2-4)");
    int numPlayers = 0;

    // Input validation - enforce 2-4 player limit
    while (numPlayers < 2 || numPlayers > 4) {
      try {
        numPlayers = Integer.parseInt(scanner.nextLine());
        if (numPlayers < 2 || numPlayers > 4) {
          System.out.println("Please enter a number between 2 and 4.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number.");
      }
    }

    // Create player objects
    for (int i = 1; i <= numPlayers; i++) {
      System.out.println("Enter name for Model.Board.Player " + i + ":");
      String name = scanner.nextLine();
      players.add(new Player(name));
    }

    return players;
  }

  /**
   * Author: Aiden Clare
   * Edited by Marena
   * Assigns tokens to players.
   *
   * @param players The list of players
   */
  private static void assignPlayerTokens(List<Player> players) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("\nChoose your tokens:");

    for (Player player : players) {
      boolean validTokenSelected = false;

      while (!validTokenSelected) {
        Tokens.displayAvailableTokens();
        System.out.println(player.getName() + ", choose a token:");
        String token = scanner.nextLine();

        validTokenSelected = player.chooseToken(token);
        if (!validTokenSelected) {
          System.out.println("Token not available. Please choose another.");
        }
      }
    }
  }

  /**
   * Author: Marena
   * Model.Main game loop.
   *
   * @param gameState The game state
   */
  private static void playGame(GameState gameState) {
    Scanner scanner = new Scanner(System.in);
    int turnCount = 0;
    int maxTurns = 20; // Limit for demonstration purposes

    System.out.println("\nStarting the game!");

    // Model.Main game loop
    while (gameState.isGameActive() && turnCount < maxTurns && gameState.getPlayers().size() > 1) {
      Player currentPlayer = gameState.getCurrentPlayer();
      System.out.println("\n--- Turn " + (turnCount + 1) + " ---");
      gameState.displayGameState();

      System.out.println("\nPress Enter for " + currentPlayer.getName() + " to take their turn...");
      scanner.nextLine();

      // Model.Board.Player takes their turn with the updated turn flow
      playerTurn(currentPlayer, gameState);

      // Check for bankrupt players
      if (currentPlayer.isBankrupt()) {
        gameState.handlePlayerBankruptcy(currentPlayer);
        if (gameState.getPlayers().size() == 1) {
          System.out.println("\nGame over! " + gameState.getPlayers().get(0).getName() + " wins!");
          break;
        }
      } else {
        // Move to next player
        gameState.nextTurn();
      }

      turnCount++;
    }

    if (turnCount >= maxTurns) {
      System.out.println("\nReached maximum number of turns. Game ends.");
    }
  }

  /**
   * Author: Marena
   * Handles a player's turn with the dynamic turn system.
   *
   * @param player The player taking a turn
   * @param gameState The current game state
   */
  private static void playerTurn(Player player, GameState gameState) {
    System.out.println(player.getName() + " is taking their turn.");

    // Check if player is in jail
    if (gameState.isPlayerInJail(player)) {
      handleJailTurn(player, gameState);
      return;
    }

    // Step 1: Roll the dice
    int roll = gameState.rollDice();
    int[] diceValues = gameState.getDiceValues();
    System.out.println(player.getName() + " rolled " + diceValues[0] + " + " + diceValues[1] + " = " + roll);

    // Check for three doubles (go to jail)
    if (diceValues[0] == diceValues[1]) {
      System.out.println(player.getName() + " rolled doubles!");

      Dice dice = (Dice) gameState.getDice();
      if (dice.getConsecutiveDoubles() == 3) {
        System.out.println(player.getName() + " rolled three consecutive doubles and is going to jail!");
        JailSpace.goToJail(player, gameState);
        return;
      }
    } else {
      // Reset doubles counter if player didn't roll doubles
      Dice dice = (Dice) gameState.getDice();
      dice.resetConsecutiveDoubles();
    }

    // Step 2: Move the player
    int oldPosition = player.getPosition();
    int newPosition = (oldPosition + roll) % gameState.getBoard().getSpaces().size();
    player.setPosition(newPosition);

    // Check if player passed Go
    if (newPosition < oldPosition) {
      System.out.println(player.getName() + " passed Go and collects $200!");
      gameState.getBank().playerPassedGo(player);
    }

    System.out.println(player.getName() + " moved from " + oldPosition + " to " + newPosition +
            " (" + gameState.getBoard().getspace(newPosition).getName() + ")");

    // Step 3: Perform actions based on the space landed on
    player.performTurnActions(gameState);

    // Step 4: If player rolled doubles, they get another turn (unless they're in jail)
    if (diceValues[0] == diceValues[1] && !gameState.isPlayerInJail(player)) {
      System.out.println(player.getName() + " rolled doubles and gets another turn!");
      playerTurn(player, gameState);
    }
  }

  /**
   * Author: Marena
   * Announces the winner of the game.
   *
   * @param players The list of remaining players
   */
  private static void announceWinner(List<Player> players) {
    if (players.size() == 1) {
      System.out.println("\n" + players.get(0).getName() + " is the winner!");
    } else if (players.size() > 1) {
      // Find player with most assets
      Player richestPlayer = players.get(0);
      int highestValue = calculatePlayerValue(richestPlayer);

      for (int i = 1; i < players.size(); i++) {
        Player player = players.get(i);
        int value = calculatePlayerValue(player);
        if (value > highestValue) {
          richestPlayer = player;
          highestValue = value;
        }
      }

      System.out.println("\nGame over! " + richestPlayer.getName() +
              " wins with total assets worth $" + highestValue + "!");
    } else {
      System.out.println("\nNo winner determined.");
    }
  }

  /**
   * Author: Aiden Clare
   * Edited by Marena
   * Calculates a player's total value (money + property values).
   *
   * @param player The player
   * @return The player's total value
   */
  private static int calculatePlayerValue(Player player) {
    int value = player.getMoney();

    // Add property values
    for (Property property : player.getProperties()) {
      value += property.getPrice();
      // Add house/hotel values
      if (property.hasHotel()) {
        value += 5 * Houses.getHousePrice(property.getColorGroup());
      } else {
        value += property.getHouses() * Houses.getHousePrice(property.getColorGroup());
      }
    }

    return value;
  }

  /**
   * Author: Marena
   * Handles a player's turn when they are in jail.
   *
   * @param player The player in jail
   * @param gameState The current game state
   */
  private static void handleJailTurn(Player player, GameState gameState) {
    System.out.println(player.getName() + " is in Jail (Turn " + (player.getTurnsInJail() + 1) + " in jail)");
    player.setTurnsInJail(player.getTurnsInJail() + 1);

    Scanner scanner = new Scanner(System.in);
    System.out.println("Options:");
    System.out.println("1. Pay $50 to get out");

    if (player.hasGetOutOfJailFreeCard()) {
      System.out.println("2. Use Get Out of Jail Free card");
    }

    System.out.println("3. Try to roll doubles");

    // Simple decision for demonstration
    int choice;
    if (player.getMoney() >= 50) {
      choice = 1; // Pay to get out
    } else if (player.hasGetOutOfJailFreeCard()) {
      choice = 2; // Use card
    } else {
      choice = 3; // Roll for doubles
    }

    System.out.println(player.getName() + " chooses option " + choice);

    switch (choice) {
      case 1: // Pay to get out
        if (player.getMoney() >= 50) {
          player.subtractMoney(50);
          gameState.releaseFromJail(player);
          player.setTurnsInJail(0);
          System.out.println(player.getName() + " paid $50 to get out of Jail.");

          // Roll and move
          int roll = gameState.rollDice();
          System.out.println(player.getName() + " rolled " + roll);
          player.move(roll, gameState.getBoard());
          player.performTurnActions(gameState);
        } else {
          System.out.println(player.getName() + " doesn't have enough money to pay the Jail fee.");
          handleJailOption3(player, gameState); // Fall back to option 3
        }
        break;

      case 2: // Use Get Out of Jail Free card
        if (player.hasGetOutOfJailFreeCard()) {
          player.setHasGetOutOfJailFreeCard(false);
          gameState.releaseFromJail(player);
          player.setTurnsInJail(0);
          System.out.println(player.getName() + " used a Get Out of Jail Free card.");

          // Roll and move
          int roll = gameState.rollDice();
          System.out.println(player.getName() + " rolled " + roll);
          player.move(roll, gameState.getBoard());
          player.performTurnActions(gameState);
        } else {
          handleJailOption3(player, gameState); // Fall back to option 3
        }
        break;

      case 3: // Try to roll doubles
      default:
        handleJailOption3(player, gameState);
        break;
    }
  }

  /**
   * Author: Marena
   * Helper method to handle the "roll for doubles" jail option.
   *
   * @param player The player in jail
   * @param gameState The current game state
   */
  private static void handleJailOption3(Player player, GameState gameState) {
    int roll = gameState.rollDice();
    int[] diceValues = gameState.getDiceValues();
    System.out.println(player.getName() + " rolled " + diceValues[0] + " + " + diceValues[1] + " = " + roll);

    if (diceValues[0] == diceValues[1]) {
      System.out.println(player.getName() + " rolled doubles and gets out of Jail!");
      gameState.releaseFromJail(player);
      player.setTurnsInJail(0);
      player.move(roll, gameState.getBoard());
      player.performTurnActions(gameState);
    } else if (player.getTurnsInJail() >= 3) {
      System.out.println(player.getName() + " has been in Jail for 3 turns and must pay $50 to get out.");
      player.subtractMoney(50);
      gameState.releaseFromJail(player);
      player.setTurnsInJail(0);
      player.move(roll, gameState.getBoard());
      player.performTurnActions(gameState);
    } else {
      System.out.println(player.getName() + " stays in Jail.");
    }
  }
}
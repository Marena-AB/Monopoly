package View;

import Model.Board.Bank;
import Model.Board.Gameboard;
import Model.Board.Player;
import Model.Board.Tokens;
import Model.GameState;
import Model.Property.Property;
import Model.Spaces.RailroadSpace;
import Model.Spaces.Space;
import Model.Spaces.UtilitySpace;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Monopoly Game GUI
 */
public class GUI extends JFrame {
    // Game components
    private GameState gameState;
    private Gameboard board;
    private List<Player> players;
    private Bank bank;

    // UI components
    private JPanel mainPanel;
    private BoardPanel boardPanel;
    private JPanel playerInfoPanel;
    private JPanel actionPanel;
    private JPanel dicePanel;
    private JPanel cardDisplayPanel;
    private JTextArea gameLog;
    private JScrollPane logScrollPane;

    // Action buttons - separated by turn phases
    private JButton rollDiceButton;
    private JButton buyPropertyButton;
    private JButton auctionPropertyButton;
    private JButton buildHouseButton;
    private JButton mortgageButton;
    private JButton unmortgageButton;
    private JButton endTurnButton;

    // Jail action buttons
    private JButton payJailFeeButton;
    private JButton useJailCardButton;
    private JButton rollForJailButton;

    // Current state tracking
    private int[] lastDiceRoll = {0, 0};
    private String lastDrawnCard = "";
    private String lastCardType = "";

    // Constants
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final int BOARD_SIZE = 700;
    private static final int SPACE_SIZE = 60;

    /**
     * Constructor that initializes the GUI
     */
    public GUI() {
        super("Monopoly");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        // Set up the game components
        setupGame();

        // Create all UI components
        createUIComponents();

        // Add components to the frame
        layoutUIComponents();

        // Update UI with initial game state
        updateUI();

        setVisible(true);

        // Initial log message
        logMessage("Welcome to Monopoly! Game started with " + players.size() + " players.");
        logMessage("Current player: " + gameState.getCurrentPlayer().getName());
    }

    /**
     * Sets up the game components
     */
    private void setupGame() {
        // Initialize tokens
        Tokens.initializeTokens();

        // Set up players
        players = setupPlayers();

        // Create game board
        board = new Gameboard();

        // Create bank
        bank = new Bank();

        // Create game state
        gameState = new GameState(players, board);
        gameState.setBank(bank);

        // Give starting money to players
        for (Player player : players) {
            bank.giveStartingMoney(player);
        }
    }

    /**
     * Sets up players for the game
     */
    private List<Player> setupPlayers() {
        List<Player> setupPlayers = new ArrayList<>();

        // Player setup dialog
        int numPlayers = promptNumberOfPlayers();

        // Create each player
        for (int i = 1; i <= numPlayers; i++) {
            String playerName = promptPlayerName(i);
            Player player = new Player(playerName);

            // Choose token
            String token = promptPlayerToken(player);
            player.chooseToken(token);

            setupPlayers.add(player);
        }

        return setupPlayers;
    }

    /**
     * Updates the entire UI
     */
    private void updateUI() {
        updatePlayerInfo();
        updateActionButtons();
        updateDiceDisplay();
        updateCardDisplay();
        boardPanel.repaint();
    }

    /**
     * Logs a message to the game log
     */
    private void logMessage(String message) {
        gameLog.append(message + "\n");
        // Scroll to the bottom of the log
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    /**
     * Handles rolling dice for the current player
     */
    private void handleRollDice() {
        // Roll two dice
        int die1 = (int) (Math.random() * 6) + 1;
        int die2 = (int) (Math.random() * 6) + 1;
        lastDiceRoll[0] = die1;
        lastDiceRoll[1] = die2;

        // Update dice display
        updateDiceDisplay();

        Player currentPlayer = gameState.getCurrentPlayer();

        // Move player
        int totalRoll = die1 + die2;
        currentPlayer.move(totalRoll, board);

        // Log the roll and movement
        logMessage(currentPlayer.getName() + " rolled a " + die1 + " and a " + die2 +
                " (Total: " + totalRoll + ")");
        logMessage(currentPlayer.getName() + " moved to " +
                board.getspace(currentPlayer.getPosition()).getName());

        // Perform actions for the space landed on
        currentPlayer.performTurnActions(gameState);

        // Update UI components
        updatePlayerInfo();
        updateActionButtons();
        boardPanel.repaint();
    }

    /**
     * Handles buying a property
     */
    private void handleBuyProperty() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Space currentSpace = board.getspace(currentPlayer.getPosition());

        try {
            if (currentSpace instanceof Property) {
                Property property = (Property) currentSpace;
                currentPlayer.buyProperty(property);
                logMessage(currentPlayer.getName() + " bought " + property.getName() + " for $" + property.getPrice());
            } else if (currentSpace instanceof RailroadSpace) {
                RailroadSpace railroad = (RailroadSpace) currentSpace;
                currentPlayer.buyRailroad(railroad);
                logMessage(currentPlayer.getName() + " bought " + railroad.getName() + " for $" + railroad.getPrice());
            } else if (currentSpace instanceof UtilitySpace) {
                UtilitySpace utility = (UtilitySpace) currentSpace;
                currentPlayer.buyUtility(utility);
                logMessage(currentPlayer.getName() + " bought " + utility.getName() + " for $" + utility.getPrice());
            }

            // Update UI
            updatePlayerInfo();
            updateActionButtons();
            boardPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Cannot Buy Property", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles starting an auction for a property
     */
    private void handleAuctionProperty() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Space currentSpace = board.getspace(currentPlayer.getPosition());

        // Open auction dialog
        AuctionDialog auctionDialog = new AuctionDialog(this, players, currentSpace);
        auctionDialog.setVisible(true);

        // Update UI after auction
        updatePlayerInfo();
        updateActionButtons();
        boardPanel.repaint();
    }

    /**
     * Handles building a house on a property
     */
    private void handleBuildHouse() {
        Player currentPlayer = gameState.getCurrentPlayer();

        // Get list of properties where player can build
        List<Property> buildableProperties = currentPlayer.getProperties().stream()
                .filter(p -> p.getColorGroup() != null &&
                        board.playerOwnsAllInColorGroup(currentPlayer, p.getColorGroup()) &&
                        p.getHouses() < 4)
                .collect(Collectors.toList());

        if (buildableProperties.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No properties available to build houses on.",
                    "Build House", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show property selection dialog
        Property selectedProperty = (Property) JOptionPane.showInputDialog(
                this,
                "Select a property to build a house on:",
                "Build House",
                JOptionPane.QUESTION_MESSAGE,
                null,
                buildableProperties.toArray(),
                buildableProperties.get(0)
        );

        if (selectedProperty != null) {
            try {
                bank.sellHouses(selectedProperty, currentPlayer, 1, board);
                logMessage(currentPlayer.getName() + " built a house on " + selectedProperty.getName());

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
                boardPanel.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Cannot Build House", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles mortgaging a property
     */
    private void handleMortgage() {
        Player currentPlayer = gameState.getCurrentPlayer();

        // Get list of properties that can be mortgaged
        List<Property> mortgageableProperties = currentPlayer.getProperties().stream()
                .filter(p -> !p.isMortgaged() && p.getHouses() == 0)
                .collect(Collectors.toList());

        if (mortgageableProperties.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No properties available to mortgage.",
                    "Mortgage Property", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show property selection dialog
        Property selectedProperty = (Property) JOptionPane.showInputDialog(
                this,
                "Select a property to mortgage:",
                "Mortgage Property",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mortgageableProperties.toArray(),
                mortgageableProperties.get(0)
        );

        if (selectedProperty != null) {
            try {
                currentPlayer.mortgageProperty(selectedProperty);
                logMessage(currentPlayer.getName() + " mortgaged " + selectedProperty.getName());

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
                boardPanel.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Cannot Mortgage", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles unmortgaging a property
     */
    private void handleUnmortgage() {
        Player currentPlayer = gameState.getCurrentPlayer();

        // Get list of mortgaged properties
        List<Property> mortgagedProperties = currentPlayer.getProperties().stream()
                .filter(Property::isMortgaged)
                .collect(Collectors.toList());

        if (mortgagedProperties.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No mortgaged properties to unmortgage.",
                    "Unmortgage Property", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show property selection dialog
        Property selectedProperty = (Property) JOptionPane.showInputDialog(
                this,
                "Select a property to unmortgage:",
                "Unmortgage Property",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mortgagedProperties.toArray(),
                mortgagedProperties.get(0)
        );

        if (selectedProperty != null) {
            try {
                currentPlayer.unmortgageProperty(selectedProperty);
                logMessage(currentPlayer.getName() + " unmortgaged " + selectedProperty.getName());

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
                boardPanel.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Cannot Unmortgage", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles ending the current player's turn
     */
    private void handleEndTurn() {
        // Reset dice roll
        lastDiceRoll[0] = 0;
        lastDiceRoll[1] = 0;

        // Move to next player
        gameState.nextTurn();

        // Update UI components
        updatePlayerInfo();
        updateDiceDisplay();
        updateActionButtons();
        updateCardDisplay();

        // Log turn change
        logMessage("Turn ended. Current player: " + gameState.getCurrentPlayer().getName());
    }

    /**
     * Handles paying jail fee
     */
    private void handlePayJailFee() {
        Player currentPlayer = gameState.getCurrentPlayer();

        try {
            // Only allow paying if player has enough money and is in jail
            if (gameState.isPlayerInJail(currentPlayer) && currentPlayer.getMoney() >= 50) {
                currentPlayer.subtractMoney(50);
                gameState.releaseFromJail(currentPlayer);
                logMessage(currentPlayer.getName() + " paid $50 to get out of jail");

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot pay jail fee. Either not in jail or not enough money.",
                        "Jail Fee Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Cannot Pay Jail Fee", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles using a Get Out of Jail Free card
     */
    private void handleUseJailCard() {
        Player currentPlayer = gameState.getCurrentPlayer();

        try {
            // Only allow using card if player is in jail and has a card
            if (gameState.isPlayerInJail(currentPlayer) && currentPlayer.hasGetOutOfJailFreeCard()) {
                gameState.releaseFromJail(currentPlayer);
                currentPlayer.setHasGetOutOfJailFreeCard(false);
                logMessage(currentPlayer.getName() + " used a Get Out of Jail Free card");

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot use jail card. Either not in jail or no card available.",
                        "Jail Card Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Cannot Use Jail Card", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles rolling for jail release
     */
    private void handleRollForJail() {
        Player currentPlayer = gameState.getCurrentPlayer();

        // Only allow rolling if player is in jail
        if (!gameState.isPlayerInJail(currentPlayer)) {
            JOptionPane.showMessageDialog(this,
                    "You are not in jail.",
                    "Roll for Jail Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int die1 = (int) (Math.random() * 6) + 1;
        int die2 = (int) (Math.random() * 6) + 1;

        lastDiceRoll[0] = die1;
        lastDiceRoll[1] = die2;

        // Update dice display
        updateDiceDisplay();

        try {
            if (die1 == die2) {
                // Doubles, get out of jail
                gameState.releaseFromJail(currentPlayer);
                logMessage(currentPlayer.getName() + " rolled doubles and got out of jail!");

                // Move player based on dice roll
                currentPlayer.move(die1 + die2, board);
                currentPlayer.performTurnActions(gameState);
            } else {
                // Increment turns in jail
                currentPlayer.setTurnsInJail(currentPlayer.getTurnsInJail() + 1);
                logMessage(currentPlayer.getName() + " did not roll doubles. Remaining in jail.");

                // Check if player has been in jail for 3 turns
                if (currentPlayer.getTurnsInJail() >= 3) {
                    // Must pay $50 to get out after 3 turns
                    if (currentPlayer.getMoney() >= 50) {
                        currentPlayer.subtractMoney(50);
                        gameState.releaseFromJail(currentPlayer);
                        currentPlayer.move(die1 + die2, board);
                        currentPlayer.performTurnActions(gameState);
                        logMessage(currentPlayer.getName() + " paid $50 and got out of jail after 3 turns.");
                    } else {
                        logMessage(currentPlayer.getName() + " cannot pay $50 to get out of jail.");
                    }
                }
            }

            // Update UI
            updatePlayerInfo();
            updateActionButtons();
            boardPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Jail Roll Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inner class for Auction Dialog
     */
    private class AuctionDialog extends JDialog {
        private List<Player> players;
        private Space currentSpace;
        private Map<Player, JTextField> bidFields;
        private Player highestBidder;
        private int highestBid;

        public AuctionDialog(JFrame parent, List<Player> players, Space currentSpace) {
            super(parent, "Property Auction", true);
            this.players = players;
            this.currentSpace = currentSpace;
            this.bidFields = new HashMap<>();
            this.highestBid = 0;
            this.highestBidder = null;

            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            // Title panel
            JPanel titlePanel = new JPanel();
            titlePanel.add(new JLabel("Auction for " + currentSpace.getName()));
            add(titlePanel, BorderLayout.NORTH);

            // Bidding panel
            JPanel biddingPanel = new JPanel(new GridLayout(0, 3, 5, 5));
            biddingPanel.setBorder(BorderFactory.createTitledBorder("Players"));

            biddingPanel.add(new JLabel("Player"));
            biddingPanel.add(new JLabel("Bid Amount"));
            biddingPanel.add(new JLabel("Actions"));

            for (Player player : players) {
                // Player name
                biddingPanel.add(new JLabel(player.getName()));

                // Bid field
                JTextField bidField = new JTextField();
                bidField.setEditable(true);
                bidFields.put(player, bidField);
                biddingPanel.add(bidField);

                // Bid button
                JButton bidButton = new JButton("Bid");
                bidButton.addActionListener(e -> placeBid(player));
                biddingPanel.add(bidButton);
            }

            add(new JScrollPane(biddingPanel), BorderLayout.CENTER);

            // Auction control panel
            JPanel controlPanel = new JPanel();
            JButton finishAuctionButton = new JButton("Finish Auction");
            finishAuctionButton.addActionListener(e -> finishAuction());
            controlPanel.add(finishAuctionButton);

            add(controlPanel, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(getParent());
        }

        private void placeBid(Player player) {
            JTextField bidField = bidFields.get(player);
            try {
                int bidAmount = Integer.parseInt(bidField.getText());

                // Validate bid
                if (bidAmount <= highestBid) {
                    JOptionPane.showMessageDialog(this,
                            "Bid must be higher than the current highest bid of $" + highestBid,
                            "Invalid Bid",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (bidAmount > player.getMoney()) {
                    JOptionPane.showMessageDialog(this,
                            "You cannot bid more than your available money",
                            "Invalid Bid",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update highest bid
                highestBid = bidAmount;
                highestBidder = player;

                // Highlight highest bid
                bidFields.forEach((p, field) -> {
                    field.setBackground(p == highestBidder ? Color.YELLOW : Color.WHITE);
                });

                logMessage(player.getName() + " bids $" + bidAmount);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number",
                        "Invalid Bid",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void finishAuction() {
            if (highestBidder != null) {
                // Sell property to highest bidder
                if (currentSpace instanceof Property) {
                    Property property = (Property) currentSpace;
                    property.setOwner(highestBidder);
                    highestBidder.subtractMoney(highestBid);
                    highestBidder.getProperties().add(property);

                    logMessage(highestBidder.getName() + " won the auction for " +
                            property.getName() + " at $" + highestBid);
                } else if (currentSpace instanceof RailroadSpace) {
                    RailroadSpace railroad = (RailroadSpace) currentSpace;
                    railroad.setOwner(highestBidder);
                    highestBidder.subtractMoney(highestBid);

                    logMessage(highestBidder.getName() + " won the auction for " +
                            railroad.getName() + " at $" + highestBid);
                } else if (currentSpace instanceof UtilitySpace) {
                    UtilitySpace utility = (UtilitySpace) currentSpace;
                    utility.setOwner(highestBidder);
                    highestBidder.subtractMoney(highestBid);

                    logMessage(highestBidder.getName() + " won the auction for " +
                            utility.getName() + " at $" + highestBid);
                }

                // Update UI
                updatePlayerInfo();
                updateActionButtons();
                boardPanel.repaint();
            } else {
                logMessage("No one bid on the property.");
            }

            dispose(); // Close the auction dialog
        }

        private void logMessage(String message) {
            // In this context, append to game log
            gameLog.append(message + "\n");
            gameLog.setCaretPosition(gameLog.getDocument().getLength());
        }
    }

    // Add these methods that were likely missing from your original implementation

    /**
     * Prompts for the number of players
     */
    private int promptNumberOfPlayers() {
        String[] options = {"2", "3", "4"};
        int selection = JOptionPane.showOptionDialog(
                this,
                "Select number of players:",
                "Player Setup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Return 2-4 based on selection (default to 2 if dialog closed)
        return selection >= 0 ? Integer.parseInt(options[selection]) : 2;
    }

    /**
     * Prompts for a player's name
     */
    private String promptPlayerName(int playerNumber) {
        String name = JOptionPane.showInputDialog(
                this,
                "Enter name for Player " + playerNumber + ":",
                "Player " + playerNumber
        );

        // Use default name if none provided
        return (name != null && !name.trim().isEmpty()) ? name : "Player " + playerNumber;
    }
    /**
     * Updates the player information panel
     */
    private void updatePlayerInfo() {
        playerInfoPanel.removeAll();

        for (Player player : players) {
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

            // Highlight current player
            if (player == gameState.getCurrentPlayer()) {
                playerPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.RED, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

                // Add "Current Player" label
                JLabel currentLabel = new JLabel("→ CURRENT PLAYER");
                currentLabel.setForeground(Color.RED);
                currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD));
                playerPanel.add(currentLabel);
            } else {
                playerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }

            // Player name and token
            JLabel nameLabel = new JLabel(player.getName() + " (" + player.getToken() + ")");
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));

            // Money
            JLabel moneyLabel = new JLabel("Money: $" + player.getMoney());

            // Position
            Space currentSpace = board.getspace(player.getPosition());
            JLabel positionLabel = new JLabel("Position: " + currentSpace.getName());

            // Properties
            JLabel propertiesLabel = new JLabel("Properties: " + player.getProperties().size());

            // Add labels to panel
            playerPanel.add(nameLabel);
            playerPanel.add(moneyLabel);
            playerPanel.add(positionLabel);
            playerPanel.add(propertiesLabel);

            // Add jail status if applicable
            if (gameState.isPlayerInJail(player)) {
                JLabel jailLabel = new JLabel("IN JAIL (" + player.getTurnsInJail() + " turns)");
                jailLabel.setForeground(Color.RED);
                playerPanel.add(jailLabel);
            }

            // Add "Get Out of Jail Free" card status if applicable
            if (player.hasGetOutOfJailFreeCard()) {
                JLabel cardLabel = new JLabel("Has Get Out of Jail Free card");
                cardLabel.setForeground(Color.BLUE);
                playerPanel.add(cardLabel);
            }

            playerInfoPanel.add(playerPanel);
            playerInfoPanel.add(Box.createVerticalStrut(10));
        }

        playerInfoPanel.revalidate();
        playerInfoPanel.repaint();
    }

    /**
     * Updates the action buttons based on current game state and turn phase
     */
    private void updateActionButtons() {
        actionPanel.removeAll();

        Player currentPlayer = gameState.getCurrentPlayer();
        int position = currentPlayer.getPosition();
        Space currentSpace = board.getspace(position);

        // Check if player is in jail
        if (gameState.isPlayerInJail(currentPlayer)) {
            // Show jail options
            actionPanel.add(new JLabel("Jail Options:"));

            // Only show pay option if player has enough money
            if (currentPlayer.getMoney() >= 50) {
                actionPanel.add(payJailFeeButton);
            }

            // Only show card option if player has a card
            if (currentPlayer.hasGetOutOfJailFreeCard()) {
                actionPanel.add(useJailCardButton);
            }

            actionPanel.add(rollForJailButton);
        } else {
            // Normal turn options

            // If dice haven't been rolled yet, only show roll button
            if (lastDiceRoll[0] == 0 && lastDiceRoll[1] == 0) {
                actionPanel.add(rollDiceButton);
            } else {
                // After dice roll, show appropriate action buttons

                // Buy property button - only if on unowned property
                if (currentSpace instanceof Property) {
                    Property property = (Property) currentSpace;
                    if (!property.isOwned() && currentPlayer.getMoney() >= property.getPrice()) {
                        actionPanel.add(buyPropertyButton);
                        actionPanel.add(auctionPropertyButton);
                    }
                } else if (currentSpace instanceof RailroadSpace) {
                    RailroadSpace railroad = (RailroadSpace) currentSpace;
                    if (!railroad.isOwned() && currentPlayer.getMoney() >= railroad.getPrice()) {
                        actionPanel.add(buyPropertyButton);
                        actionPanel.add(auctionPropertyButton);
                    }
                } else if (currentSpace instanceof UtilitySpace) {
                    UtilitySpace utility = (UtilitySpace) currentSpace;
                    if (!utility.isOwned() && currentPlayer.getMoney() >= utility.getPrice()) {
                        actionPanel.add(buyPropertyButton);
                        actionPanel.add(auctionPropertyButton);
                    }
                }

                // Always show these buttons after roll
                actionPanel.add(buildHouseButton);
                actionPanel.add(mortgageButton);
                actionPanel.add(unmortgageButton);
                actionPanel.add(endTurnButton);
            }
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    /**
     * Updates the dice display
     */
    private void updateDiceDisplay() {
        dicePanel.removeAll();

        if (lastDiceRoll[0] > 0 || lastDiceRoll[1] > 0) {
            // Create dice display
            JPanel die1Panel = createDiePanel(lastDiceRoll[0]);
            JPanel die2Panel = createDiePanel(lastDiceRoll[1]);

            // Add total
            JLabel totalLabel = new JLabel("Total: " + (lastDiceRoll[0] + lastDiceRoll[1]));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

            // Add doubles indicator if applicable
            if (lastDiceRoll[0] == lastDiceRoll[1]) {
                JLabel doublesLabel = new JLabel("DOUBLES!");
                doublesLabel.setForeground(Color.RED);
                doublesLabel.setFont(new Font("Arial", Font.BOLD, 14));
                dicePanel.add(doublesLabel);
            }

            // Layout
            JPanel diceContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            diceContainer.add(die1Panel);
            diceContainer.add(die2Panel);

            dicePanel.setLayout(new BorderLayout());
            dicePanel.add(diceContainer, BorderLayout.CENTER);
            dicePanel.add(totalLabel, BorderLayout.SOUTH);
        } else {
            // No dice have been rolled yet
            dicePanel.add(new JLabel("Roll dice to start your turn"));
        }

        dicePanel.revalidate();
        dicePanel.repaint();
    }

    /**
     * Creates a visual representation of a die
     */
    private JPanel createDiePanel(int value) {
        JPanel diePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw die face
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Draw dots based on value
                g2d.setColor(Color.BLACK);
                int dotSize = getWidth() / 5;
                int centerX = getWidth() / 2 - dotSize / 2;
                int centerY = getHeight() / 2 - dotSize / 2;

                switch (value) {
                    case 1:
                        // Center dot
                        g2d.fillOval(centerX, centerY, dotSize, dotSize);
                        break;
                    case 2:
                        // Top-left and bottom-right
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        break;
                    case 3:
                        // Same as 2 plus center
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(centerX, centerY, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        break;
                    case 4:
                        // All four corners
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        break;
                    case 5:
                        // Same as 4 plus center
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(centerX, centerY, dotSize, dotSize);
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        break;
                    case 6:
                        // Two rows of three dots
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(getWidth() / 2 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(getWidth() / 2 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        g2d.fillOval(3 * getWidth() / 4 - dotSize / 2, 3 * getHeight() / 4 - dotSize / 2, dotSize, dotSize);
                        break;
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 60);
            }
        };

        return diePanel;
    }

    /**
     * Creates all UI components
     */
    private void createUIComponents() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout());

        // Create game board panel
        boardPanel = new BoardPanel();

        // Create player info panel
        playerInfoPanel = createPlayerInfoPanel();

        // Create action panel with buttons
        actionPanel = createActionPanel();

        // Create dice display panel
        dicePanel = createDicePanel();

        // Create card display panel
        cardDisplayPanel = createCardDisplayPanel();

        // Create game log
        gameLog = new JTextArea(10, 40);
        gameLog.setEditable(false);
        logScrollPane = new JScrollPane(gameLog);
        logScrollPane.setBorder(new TitledBorder("Game Log"));
    }

    /**
     * Lays out the UI components
     */
    private void layoutUIComponents() {
        // Set up the right side panel containing player info and actions
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(playerInfoPanel, BorderLayout.NORTH);

        // Add action panel and dice panel to center right
        JPanel centerRightPanel = new JPanel(new BorderLayout());
        centerRightPanel.add(actionPanel, BorderLayout.NORTH);
        centerRightPanel.add(dicePanel, BorderLayout.CENTER);
        centerRightPanel.add(cardDisplayPanel, BorderLayout.SOUTH);
        rightPanel.add(centerRightPanel, BorderLayout.CENTER);

        // Add board and right panel to main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Add game log to bottom
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    /**
     * Custom panel for drawing the Monopoly board
     */
    private class BoardPanel extends JPanel {
        private static final int BOARD_SIZE = 700;
        private static final int SQUARE_SIZE = BOARD_SIZE / 10;

        // Color palette for properties
        private static final Color[] PROPERTY_COLORS = {
                new Color(139, 69, 19),    // Brown
                new Color(173, 216, 230),  // Light Blue
                new Color(255, 192, 203),  // Pink
                new Color(255, 165, 0),    // Orange
                new Color(255, 0, 0),      // Red
                new Color(255, 255, 0),    // Yellow
                new Color(0, 128, 0),      // Green
                new Color(0, 0, 139)       // Dark Blue
        };

        // Special space colors
        private static final Color[] SPECIAL_COLORS = {
                new Color(255, 182, 66),   // Chance - Orange
                new Color(102, 0, 153),    // Community Chest - Purple
                Color.BLACK,               // Tax - Black
                new Color(0, 102, 153)     // Railroads/Utilities - Blue
        };

        public BoardPanel() {
            setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setBackground(new Color(212, 221, 178)); // Sage green background
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw white grid
            g2d.setColor(Color.WHITE);
            for (int i = 0; i <= 10; i++) {
                g2d.drawLine(i * SQUARE_SIZE, 0, i * SQUARE_SIZE, BOARD_SIZE);
                g2d.drawLine(0, i * SQUARE_SIZE, BOARD_SIZE, i * SQUARE_SIZE);
            }

            // Draw corner spaces
            drawCornerSpaces(g2d);

            // Draw property spaces
            drawBottomRow(g2d);
            drawTopRow(g2d);
            drawLeftColumn(g2d);
            drawRightColumn(g2d);

            // Draw center spaces
            drawCenterSpaces(g2d);
        }

        private void drawCornerSpaces(Graphics2D g2d) {
            Color[] cornerColors = {
                    new Color(255, 182, 66),   // GO - Orange
                    new Color(153, 102, 51),   // Jail - Brown
                    Color.BLACK,               // Free Parking - Black
                    new Color(102, 0, 0)       // Go To Jail - Dark Red
            };

            String[] cornerLabels = {"GO", "JAIL", "FREE\nPARKING", "GO\nTO\nJAIL"};

            int[][] cornerPositions = {
                    {0, 0},
                    {BOARD_SIZE - SQUARE_SIZE, 0},
                    {BOARD_SIZE - SQUARE_SIZE, BOARD_SIZE - SQUARE_SIZE},
                    {0, BOARD_SIZE - SQUARE_SIZE}
            };

            for (int i = 0; i < 4; i++) {
                g2d.setColor(cornerColors[i]);
                g2d.fillRect(cornerPositions[i][0], cornerPositions[i][1], SQUARE_SIZE, SQUARE_SIZE);

                // Draw labels
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                drawCenteredMultilineText(g2d, cornerLabels[i],
                        cornerPositions[i][0], cornerPositions[i][1],
                        SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        private void drawBottomRow(Graphics2D g2d) {
            String[] properties = {
                    "Community Chest", "Baltic Avenue", "Income Tax",
                    "Mediterranean Avenue", "Reading Railroad",
                    "Oriental Avenue", "Chance", "Vermont Avenue",
                    "Connecticut Avenue"
            };

            int[] colorIndices = {-1, 0, -1, 0, -1, 1, -1, 1, 1};

            for (int i = 0; i < properties.length; i++) {
                int x = BOARD_SIZE - SQUARE_SIZE - (i + 1) * SQUARE_SIZE;
                int y = BOARD_SIZE - SQUARE_SIZE;

                // Color strip
                if (colorIndices[i] != -1) {
                    g2d.setColor(PROPERTY_COLORS[colorIndices[i]]);
                    g2d.fillRect(x, y, SQUARE_SIZE, 15);
                } else {
                    g2d.setColor(SPECIAL_COLORS[i]);
                    g2d.fillRect(x, y, SQUARE_SIZE, 15);
                }

                // Label
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                drawVerticalText(g2d, properties[i], x + 5, y + SQUARE_SIZE - 10);
            }
        }

        private void drawTopRow(Graphics2D g2d) {
            String[] properties = {
                    "Kentucky Avenue", "Indiana Avenue", "Illinois Avenue",
                    "St. Charles Place", "Electric Company", "Water Works"
            };

            int[] colorIndices = {4, 4, 4, 2, -1, -1};

            for (int i = 0; i < properties.length; i++) {
                int x = SQUARE_SIZE + i * SQUARE_SIZE;
                int y = 0;

                // Color strip
                if (colorIndices[i] != -1) {
                    g2d.setColor(PROPERTY_COLORS[colorIndices[i]]);
                    g2d.fillRect(x, y, SQUARE_SIZE, 15);
                } else {
                    g2d.setColor(SPECIAL_COLORS[4 + i]);
                    g2d.fillRect(x, y, SQUARE_SIZE, 15);
                }

                // Label
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                drawVerticalText(g2d, properties[i], x + 5, y + 15);
            }
        }

        private void drawLeftColumn(Graphics2D g2d) {
            String[] properties = {
                    "St. James Place", "Tennessee Avenue", "New York Avenue",
                    "Pennsylvania Avenue", "North Carolina Avenue", "Pacific Avenue",
                    "Marvin Gardens", "Ventnor Avenue", "Atlantic Avenue"
            };

            int[] colorIndices = {3, 3, 3, 6, 6, 6, 5, 5, 5};

            for (int i = 0; i < properties.length; i++) {
                int x = 0;
                int y = SQUARE_SIZE + i * SQUARE_SIZE;

                // Color strip
                g2d.setColor(PROPERTY_COLORS[colorIndices[i]]);
                g2d.fillRect(x, y, 15, SQUARE_SIZE);

                // Label
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                drawRotatedText(g2d, properties[i], x + 20, y + SQUARE_SIZE/2);
            }
        }

        private void drawRightColumn(Graphics2D g2d) {
            String[] properties = {
                    "Park Place", "Boardwalk", "B. & O. Railroad",
                    "Short Line", "Pennsylvania Railroad", "Reading Railroad"
            };

            int[] colorIndices = {7, 7, -1, -1, -1, -1};

            for (int i = 0; i < properties.length; i++) {
                int x = BOARD_SIZE - SQUARE_SIZE;
                int y = BOARD_SIZE - SQUARE_SIZE - (i + 1) * SQUARE_SIZE;

                // Color strip
                if (colorIndices[i] != -1) {
                    g2d.setColor(PROPERTY_COLORS[colorIndices[i]]);
                    g2d.fillRect(x, y, 15, SQUARE_SIZE);
                } else {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y, 15, SQUARE_SIZE);
                }

                // Label
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                drawRotatedText(g2d, properties[i], x + 20, y + SQUARE_SIZE/2);
            }
        }

        private void drawCenterSpaces(Graphics2D g2d) {
            // Two diamond-shaped center spaces
            Color[] centerColors = {
                    new Color(255, 182, 66),  // Chance orange
                    new Color(102, 0, 153)    // Community Chest purple
            };

            int[][] centerPositions = {
                    {SQUARE_SIZE * 3, SQUARE_SIZE * 3},
                    {SQUARE_SIZE * 6, SQUARE_SIZE * 6}
            };

            for (int i = 0; i < 2; i++) {
                g2d.setColor(centerColors[i]);

                // Create diamond shape
                int[] xPoints = {
                        centerPositions[i][0] + SQUARE_SIZE/2,
                        centerPositions[i][0] + SQUARE_SIZE,
                        centerPositions[i][0] + SQUARE_SIZE/2,
                        centerPositions[i][0]
                };
                int[] yPoints = {
                        centerPositions[i][1],
                        centerPositions[i][1] + SQUARE_SIZE/2,
                        centerPositions[i][1] + SQUARE_SIZE,
                        centerPositions[i][1] + SQUARE_SIZE/2
                };

                // Draw diamond with dashed border
                g2d.fillPolygon(xPoints, yPoints, 4);

                // Dashed border
                Stroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[]{5f}, 0f);
                g2d.setStroke(dashed);
                g2d.setColor(Color.WHITE);
                g2d.drawPolygon(xPoints, yPoints, 4);
            }
        }

        private void drawCenteredMultilineText(Graphics2D g2d, String text,
                                               int x, int y, int width, int height) {
            String[] lines = text.split("\n");
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();

            int totalTextHeight = lines.length * lineHeight;
            int startY = y + (height - totalTextHeight) / 2 + fm.getAscent();

            for (int i = 0; i < lines.length; i++) {
                int textWidth = fm.stringWidth(lines[i]);
                int startX = x + (width - textWidth) / 2;
                g2d.drawString(lines[i], startX, startY + i * lineHeight);
            }
        }

        private void drawRotatedText(Graphics2D g2d, String text, int x, int y) {
            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.rotate(-Math.PI/2, x, y);
            g2dRotated.drawString(text, x - 50, y);
            g2dRotated.dispose();
        }

        private void drawVerticalText(Graphics2D g2d, String text, int x, int y) {
            g2d.drawString(text, x, y);
        }
    }

    /**
     * Updates the card display
     */
    private void updateCardDisplay() {
        cardDisplayPanel.removeAll();

        if (lastDrawnCard != null && !lastDrawnCard.isEmpty()) {
            // Show card type
            JLabel typeLabel = new JLabel(lastCardType + " Card:");
            typeLabel.setFont(new Font("Arial", Font.BOLD, 14));

            // Show card text
            JTextArea cardText = new JTextArea(lastDrawnCard);
            cardText.setEditable(false);
            cardText.setLineWrap(true);
            cardText.setWrapStyleWord(true);
            cardText.setBackground(cardDisplayPanel.getBackground());
            cardText.setFont(new Font("Arial", Font.PLAIN, 12));

            // Add to panel
            cardDisplayPanel.setLayout(new BorderLayout());
            cardDisplayPanel.add(typeLabel, BorderLayout.NORTH);
            cardDisplayPanel.add(cardText, BorderLayout.CENTER);
        } else {
            // No card has been drawn yet
            cardDisplayPanel.add(new JLabel("No card drawn yet"));
        }

        cardDisplayPanel.revalidate();
        cardDisplayPanel.repaint();
    }

    /**
     * Creates the player information panel
     */
    private JPanel createPlayerInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Players"));
        panel.setPreferredSize(new Dimension(300, 250));

        // Will be populated by updatePlayerInfo()
        return panel;
    }

    /**
     * Creates the action panel with buttons
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(new TitledBorder("Actions"));
        panel.setPreferredSize(new Dimension(300, 200));

        // Create buttons
        rollDiceButton = new JButton("Roll Dice");
        buyPropertyButton = new JButton("Buy Property");
        auctionPropertyButton = new JButton("Auction Property");
        buildHouseButton = new JButton("Build House");
        mortgageButton = new JButton("Mortgage Property");
        unmortgageButton = new JButton("Unmortgage Property");
        endTurnButton = new JButton("End Turn");

        // Jail buttons
        payJailFeeButton = new JButton("Pay $50 to Get Out of Jail");
        useJailCardButton = new JButton("Use Get Out of Jail Free Card");
        rollForJailButton = new JButton("Roll for Doubles");

        // Add action listeners
        rollDiceButton.addActionListener(e -> handleRollDice());
        buyPropertyButton.addActionListener(e -> handleBuyProperty());
        auctionPropertyButton.addActionListener(e -> handleAuctionProperty());
        buildHouseButton.addActionListener(e -> handleBuildHouse());
        mortgageButton.addActionListener(e -> handleMortgage());
        unmortgageButton.addActionListener(e -> handleUnmortgage());
        endTurnButton.addActionListener(e -> handleEndTurn());

        // Jail button actions
        payJailFeeButton.addActionListener(e -> handlePayJailFee());
        useJailCardButton.addActionListener(e -> handleUseJailCard());
        rollForJailButton.addActionListener(e -> handleRollForJail());

        // Return panel without adding buttons initially
        // Buttons will be added dynamically based on game state
        return panel;
    }

    /**
     * Creates the dice display panel
     */
    private JPanel createDicePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Dice"));
        panel.setPreferredSize(new Dimension(300, 100));

        // Will be populated in updateDiceDisplay method
        return panel;
    }

    /**
     * Creates the card display panel for Chance and Community Chest
     */
    private JPanel createCardDisplayPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Last Card Drawn"));
        panel.setPreferredSize(new Dimension(300, 150));

        // Will be populated in updateCardDisplay method
        return panel;
    }

    /**
     * Prompts for a player's token
     */
    private String promptPlayerToken(Player player) {
        String availableTokens = Tokens.getavailabletokens();
        String[] tokenArray = availableTokens.replace("[", "").replace("]", "").split(", ");

        if (tokenArray.length > 0) {
            String token = (String) JOptionPane.showInputDialog(
                    this,
                    player.getName() + ", choose your token:",
                    "Token Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tokenArray,
                    tokenArray[0]
            );

            // Return selected token or first available if canceled
            return (token != null) ? token : tokenArray[0];
        }

        return "No tokens available";
    }

    /**
     * Main method to start the Monopoly game
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread-safe GUI creation
        SwingUtilities.invokeLater(() -> {
            new GUI(); // Create and show the GUI
        });
    }
}


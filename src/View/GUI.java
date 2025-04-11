/**
 * Monopoly Game GUI
 * This class is responsible for the visual representation of the Monopoly game.
 */
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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final int[] lastDiceRoll = {0, 0};
    private String lastCardType = "";

    // Constants
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final int BOARD_SIZE = 600;
    private static final int SPACE_SIZE = BOARD_SIZE / 11;

    /**
     * Constructor that initializes the GUI
     * Author: Marena
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

        // Make sure the frame is visible
        setVisible(true);

        // Initial log message
        logMessage("Welcome to Monopoly! Game started with " + players.size() + " players.");
        logMessage("Current player: " + gameState.getCurrentPlayer().getName());
    }

    /**
     * Sets up the game components
     * Author: Marena
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
     * Author: Marena
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
     * Prompts for the number of players
     * Author: Marena
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
     * Author: Marena
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
     * Prompts for a player's token
     * Author: Marena
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
     * Updates the entire UI
     * Author: Marena
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
     * Author: Marena
     */
    private void logMessage(String message) {
        gameLog.append(message + "\n");
        // Scroll to the bottom of the log
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    /**
     * Author: Marena
     * Creates all UI components
     */
    private void createUIComponents() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout());

        // Create game board panel with fixed size
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setMinimumSize(new Dimension(BOARD_SIZE, BOARD_SIZE));

        // Create player info panel
        playerInfoPanel = createPlayerInfoPanel();

        // Create action panel with buttons
        actionPanel = createActionPanel();

        // Create dice display panel
        dicePanel = createSimpleDiePanel(0);

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

        // Create a panel to hold the board with some padding
        JPanel boardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        boardContainer.add(boardPanel);

        // Add components to main panel
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    /**
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
     * Creates a simpler visual representation of a die
     */
    private JPanel createSimpleDiePanel(int value) {
        JPanel diePanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 60);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw white square with black border
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                // Draw value as text in center
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                String valueText = String.valueOf(value);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(valueText);
                int textHeight = fm.getHeight();
                g2d.drawString(valueText,
                        (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight) / 2 - fm.getDescent());
            }
        };

        return diePanel;
    }

    /**
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
     * Updates the dice display with a simpler approach
     */
    private void updateDiceDisplay() {
        dicePanel.removeAll();
        dicePanel.setLayout(new BorderLayout());

        if (lastDiceRoll[0] > 0 || lastDiceRoll[1] > 0) {
            // Create main container
            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

            // Add title
            JLabel titleLabel = new JLabel("Dice Roll", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(titleLabel);
            container.add(Box.createVerticalStrut(10));

            // Create dice container
            JPanel diceContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

            // Create dice panels
            JPanel die1 = createSimpleDiePanel(lastDiceRoll[0]);
            JPanel die2 = createSimpleDiePanel(lastDiceRoll[1]);

            // Add dice to container
            diceContainer.add(die1);
            diceContainer.add(die2);

            // Center the dice container
            diceContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(diceContainer);
            container.add(Box.createVerticalStrut(10));

            // Add result label
            int total = lastDiceRoll[0] + lastDiceRoll[1];
            JLabel resultLabel = new JLabel("Die 1: " + lastDiceRoll[0] + " + Die 2: " + lastDiceRoll[1] + " = Total: " + total, JLabel.CENTER);
            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(resultLabel);

            // Add doubles indicator if applicable
            if (lastDiceRoll[0] == lastDiceRoll[1]) {
                JLabel doublesLabel = new JLabel("DOUBLES!", JLabel.CENTER);
                doublesLabel.setForeground(Color.RED);
                doublesLabel.setFont(new Font("Arial", Font.BOLD, 14));
                doublesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                container.add(doublesLabel);
            }

            // Add to panel
            dicePanel.add(container, BorderLayout.CENTER);
        } else {
            // No dice rolled yet
            JLabel noRollLabel = new JLabel("Roll dice to start your turn", JLabel.CENTER);
            dicePanel.add(noRollLabel, BorderLayout.CENTER);
        }

        dicePanel.revalidate();
        dicePanel.repaint();
    }

    /**
     * Author: Marena
     * Creates a visual representation of a die with the specified value
     */
    private JPanel createDiePanel(int value) {
        JPanel diePanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 60);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw die face (white rounded square with black border)
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);

                // Draw dots based on value
                g2d.setColor(Color.BLACK);
                int dotSize = getWidth() / 6;  // Slightly smaller dots

                // Calculate positions
                int margin = getWidth() / 6;
                int center = getWidth() / 2 - dotSize / 2;
                int left = margin - dotSize / 2;
                int right = getWidth() - margin - dotSize / 2;
                int top = margin - dotSize / 2;
                int bottom = getHeight() - margin - dotSize / 2;

                switch (value) {
                    case 1:
                        // Center dot
                        g2d.fillOval(center, center, dotSize, dotSize);
                        break;
                    case 2:
                        // Top-left and bottom-right
                        g2d.fillOval(left, top, dotSize, dotSize);
                        g2d.fillOval(right, bottom, dotSize, dotSize);
                        break;
                    case 3:
                        // Top-left, center, and bottom-right
                        g2d.fillOval(left, top, dotSize, dotSize);
                        g2d.fillOval(center, center, dotSize, dotSize);
                        g2d.fillOval(right, bottom, dotSize, dotSize);
                        break;
                    case 4:
                        // Four corners
                        g2d.fillOval(left, top, dotSize, dotSize);
                        g2d.fillOval(right, top, dotSize, dotSize);
                        g2d.fillOval(left, bottom, dotSize, dotSize);
                        g2d.fillOval(right, bottom, dotSize, dotSize);
                        break;
                    case 5:
                        // Four corners plus center
                        g2d.fillOval(left, top, dotSize, dotSize);
                        g2d.fillOval(right, top, dotSize, dotSize);
                        g2d.fillOval(center, center, dotSize, dotSize);
                        g2d.fillOval(left, bottom, dotSize, dotSize);
                        g2d.fillOval(right, bottom, dotSize, dotSize);
                        break;
                    case 6:
                        // Six dots (three on each side)
                        g2d.fillOval(left, top, dotSize, dotSize);
                        g2d.fillOval(left, center, dotSize, dotSize);
                        g2d.fillOval(left, bottom, dotSize, dotSize);
                        g2d.fillOval(right, top, dotSize, dotSize);
                        g2d.fillOval(right, center, dotSize, dotSize);
                        g2d.fillOval(right, bottom, dotSize, dotSize);
                        break;
                }
            }
        };

        // Set background too transparent to better show the die face
        diePanel.setOpaque(false);

        // Add a subtle border to the panel itself
        diePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return diePanel;
    }

    /**
     * Author: Marena
     * Updates the card display
     */
    private void updateCardDisplay() {
        cardDisplayPanel.removeAll();

        String lastDrawnCard = "";
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
     * Author: Marena
     * Handles rolling dice without animation
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
     * Author: Marena
     * Shows a die roll animation effect
     */
    private void showDiceRollAnimation(final int finalDie1, final int finalDie2) {
        // Create timer for animation (rolls dice quickly several times)
        final int animationFrames = 10; // Number of animation frames
        final int delay = 50; // Milliseconds between frames

        final Timer timer = new Timer(delay, null);
        final int[] frameCount = {0};

        timer.addActionListener(e -> {
            if (frameCount[0] < animationFrames) {
                // Show random dice during animation
                lastDiceRoll[0] = (int) (Math.random() * 6) + 1;
                lastDiceRoll[1] = (int) (Math.random() * 6) + 1;
                updateDiceDisplay();
                frameCount[0]++;
            } else {
                // Show final dice values
                lastDiceRoll[0] = finalDie1;
                lastDiceRoll[1] = finalDie2;
                updateDiceDisplay();
                timer.stop();
            }
        });

        timer.start();
    }

    /**
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
     * Author: Marena
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
        boardPanel.repaint();
    }

    /**
     * Author: Marena
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
                boardPanel.repaint();
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
     * Author: Marena
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
                boardPanel.repaint();
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
     * Author: Marena
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
     * Author: Marena
     * Inner class for the auction dialog
     */
    private class AuctionDialog extends JDialog {
        private final List<Player> players;
        private final Space propertySpace;
        private final Map<Player, JTextField> bidFields;
        private Player highestBidder;
        private int highestBid;

        /**
         * Author: Marena
         * Constructs a new auction dialog
         */
        public AuctionDialog(JFrame parent, List<Player> players, Space propertySpace) {
            super(parent, "Property Auction", true);
            this.players = players;
            this.propertySpace = propertySpace;
            this.bidFields = new HashMap<>();
            this.highestBid = 0;
            this.highestBidder = null;

            initComponents();
            pack();
            setLocationRelativeTo(parent);
        }

        /**
         * Author: Marena
         * Initializes the auction dialog components
         */
        private void initComponents() {
            setLayout(new BorderLayout());

            // Title panel showing property being auctioned
            JPanel titlePanel = new JPanel();
            titlePanel.add(new JLabel("Auction for " + propertySpace.getName()));
            add(titlePanel, BorderLayout.NORTH);

            // Bidding panel with player bidding controls
            JPanel biddingPanel = new JPanel(new GridLayout(0, 3, 5, 5));
            biddingPanel.setBorder(BorderFactory.createTitledBorder("Players"));

            // Headers
            biddingPanel.add(new JLabel("Player"));
            biddingPanel.add(new JLabel("Bid Amount"));
            biddingPanel.add(new JLabel("Actions"));

            // Add bid controls for each player
            for (Player player : players) {
                // Skip current player if they declined to buy
                if (player == gameState.getCurrentPlayer() && propertySpace.equals(board.getspace(player.getPosition()))) {
                    continue;
                }

                // Player name
                biddingPanel.add(new JLabel(player.getName() + " ($" + player.getMoney() + ")"));

                // Bid field
                JTextField bidField = new JTextField("0");
                bidFields.put(player, bidField);
                biddingPanel.add(bidField);

                // Bid button
                JButton bidButton = new JButton("Bid");
                bidButton.addActionListener(e -> placeBid(player));
                biddingPanel.add(bidButton);
            }

            add(new JScrollPane(biddingPanel), BorderLayout.CENTER);

            // Control panel with finish button
            JPanel controlPanel = new JPanel();
            JButton finishButton = new JButton("Finish Auction");
            finishButton.addActionListener(e -> finishAuction());
            controlPanel.add(finishButton);

            add(controlPanel, BorderLayout.SOUTH);
        }

        /**
         * Author: Marena
         * Places a bid for a player
         */
        private void placeBid(Player player) {
            try {
                JTextField bidField = bidFields.get(player);
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
                            "You cannot bid more than your available money: $" + player.getMoney(),
                            "Invalid Bid",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update highest bid
                highestBid = bidAmount;
                highestBidder = player;

                // Highlight highest bid
                for (Map.Entry<Player, JTextField> entry : bidFields.entrySet()) {
                    entry.getValue().setBackground(entry.getKey() == highestBidder ? Color.YELLOW : Color.WHITE);
                }

                JOptionPane.showMessageDialog(this,
                        player.getName() + " has placed the highest bid of $" + bidAmount,
                        "New Highest Bid",
                        JOptionPane.INFORMATION_MESSAGE);

                logMessage(player.getName() + " bids $" + bidAmount + " for " + propertySpace.getName());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for your bid",
                        "Invalid Bid",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Author: Marena
         * Finishes the auction and sells the property to the highest bidder
         */
        private void finishAuction() {
            if (highestBidder == null) {
                JOptionPane.showMessageDialog(this,
                        "No bids were placed. The property remains with the bank.",
                        "Auction Ended",
                        JOptionPane.INFORMATION_MESSAGE);
                logMessage("No bids were placed for " + propertySpace.getName() + ". It remains with the bank.");
                dispose();
                return;
            }

            try {
                // Handle different property types
                if (propertySpace instanceof Property) {
                    Property property = (Property) propertySpace;
                    highestBidder.subtractMoney(highestBid);
                    property.setOwner(highestBidder);
                    highestBidder.getProperties().add(property);
                    logMessage(highestBidder.getName() + " won the auction for " + property.getName() +
                            " with a bid of $" + highestBid);
                } else if (propertySpace instanceof RailroadSpace) {
                    RailroadSpace railroad = (RailroadSpace) propertySpace;
                    highestBidder.subtractMoney(highestBid);
                    railroad.setOwner(highestBidder);
                    logMessage(highestBidder.getName() + " won the auction for " + railroad.getName() +
                            " with a bid of $" + highestBid);
                } else if (propertySpace instanceof UtilitySpace) {
                    UtilitySpace utility = (UtilitySpace) propertySpace;
                    highestBidder.subtractMoney(highestBid);
                    utility.setOwner(highestBidder);
                    logMessage(highestBidder.getName() + " won the auction for " + utility.getName() +
                            " with a bid of $" + highestBid);
                }

                JOptionPane.showMessageDialog(this,
                        highestBidder.getName() + " won the auction for " + propertySpace.getName() +
                                " with a bid of $" + highestBid,
                        "Auction Completed",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error completing auction: " + e.getMessage(),
                        "Auction Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Author: Marena
     * Edited by: Aiden Clare
     * BoardPanel class to render the Monopoly game board
     */
    private class BoardPanel extends JPanel {
        // Color palette for properties
        private final Color[] PROPERTY_COLORS = {
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
        private final Color[] SPECIAL_COLORS = {
                new Color(255, 182, 66),   // Chance - Orange
                new Color(102, 0, 153),    // Community Chest - Purple
                Color.LIGHT_GRAY,          // Tax spaces
                new Color(0, 102, 153)     // Railroads/Utilities - Blue
        };

       // Image fields
        private Image houseImage;
        private Image hotelImage;

        /**
         * Author: Marena
         * Edited by: Aiden Clare
         * Constructor for BoardPanel
         * Sets up the board size, background color, and loads images
         */

        // In the BoardPanel constructor:
        public BoardPanel() {
            setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setMinimumSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setBackground(new Color(217, 238, 217));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Load house and hotel images
            try {
                houseImage = ImageIO.read(new File("path/to/houses.jpg")).getScaledInstance(15, 15, Image.SCALE_SMOOTH);
                hotelImage = ImageIO.read(new File("path/to/hotel.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback to drawing if images can't be loaded
                houseImage = null;
                hotelImage = null;
            }
        }

        /**
         * Author: Marena
         * Paints the board and its components
         * @param g the <code>Graphics</code> object to protect
         */

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("Painting board: " + getWidth() + "x" + getHeight());

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw board outline
            drawBoardOutline(g2d);

            // Draw spaces
            drawCornerSpaces(g2d);
            drawSideSpaces(g2d);

            // Draw central logo
            drawCentralLogo(g2d);

            // Draw player tokens
            drawPlayerTokens(g2d);
        }

        /**
         * Author: Marena
         * Draws the board outline
         */
        private void drawBoardOutline(Graphics2D g2d) {
            // Draw outer border
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // Draw inner grid lines to outline the spaces
            g2d.setStroke(new BasicStroke(1));

            // Draw horizontal lines for the top and bottom rows
            for (int i = 1; i < 11; i++) {
                int x = i * SPACE_SIZE;
                // Top row
                g2d.drawLine(x, 0, x, SPACE_SIZE);
                // Bottom row
                g2d.drawLine(x, BOARD_SIZE - SPACE_SIZE, x, BOARD_SIZE);
            }

            // Draw vertical lines for the left and right columns
            for (int i = 1; i < 11; i++) {
                int y = i * SPACE_SIZE;
                // Left column
                g2d.drawLine(0, y, SPACE_SIZE, y);
                // Right column
                g2d.drawLine(BOARD_SIZE - SPACE_SIZE, y, BOARD_SIZE, y);
            }
        }


        /**
         * Author: Marena
         * Draws the corner spaces (GO, Jail, Free Parking, Go To Jail)
         */
        private void drawCornerSpaces(Graphics2D g2d) {
            // GO (Bottom right)
            g2d.setColor(new Color(255, 240, 245)); // Light pink background
            g2d.fillRect(BOARD_SIZE - SPACE_SIZE, BOARD_SIZE - SPACE_SIZE, SPACE_SIZE, SPACE_SIZE);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            drawRotatedText(g2d, "GO", BOARD_SIZE - SPACE_SIZE/2, BOARD_SIZE - SPACE_SIZE/2, 45);

            // Just Visiting / Jail (Bottom left)
            g2d.setColor(new Color(235, 235, 235)); // Light gray
            g2d.fillRect(0, BOARD_SIZE - SPACE_SIZE, SPACE_SIZE, SPACE_SIZE);
            // Draw jail cell
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(5, BOARD_SIZE - SPACE_SIZE + 5, SPACE_SIZE - 10, SPACE_SIZE/2 - 5);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(5, BOARD_SIZE - SPACE_SIZE + 5, SPACE_SIZE - 10, SPACE_SIZE/2 - 5);
            // Add text
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("JAIL", 10, BOARD_SIZE - SPACE_SIZE/2);
            g2d.drawString("Just Visiting", 5, BOARD_SIZE - 10);

            // Free Parking (Top left)
            g2d.setColor(new Color(235, 235, 235)); // Light gray
            g2d.fillRect(0, 0, SPACE_SIZE, SPACE_SIZE);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("FREE", 10, 20);
            g2d.drawString("PARKING", 5, 40);

            // Go To Jail (Top right)
            g2d.setColor(new Color(235, 235, 235)); // Light gray
            g2d.fillRect(BOARD_SIZE - SPACE_SIZE, 0, SPACE_SIZE, SPACE_SIZE);
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("GO TO", BOARD_SIZE - SPACE_SIZE + 10, 20);
            g2d.drawString("JAIL", BOARD_SIZE - SPACE_SIZE + 15, 40);
        }

        /**
         * Author: Aiden Clare
         * Draws the houses and hotels on properties
         * @param g2d
         * @param property
         * @param x
         * @param y
         * @param spaceWidth
         * @param spaceHeight
         */
        private void drawHouses(Graphics2D g2d, Property property, int x, int y, int spaceWidth, int spaceHeight) {
            if (property.getHouses() == 0) return;

            int houseCount = property.getHouses();
            boolean isHotel = houseCount == 5; // Assuming 5 houses = 1 hotel

            if (isHotel) {
                // Draw hotel
                if (hotelImage != null) {
                    g2d.drawImage(hotelImage, x + spaceWidth/2 - 10, y + spaceHeight - 30, null);
                } else {
                    // Fallback drawing
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x + spaceWidth/2 - 10, y + spaceHeight - 30, 20, 20);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("H", x + spaceWidth/2 - 5, y + spaceHeight - 15);
                }
            } else {
                // Draw houses
                int houseSize = 15;
                int startX = x + (spaceWidth - (houseCount * houseSize)) / 2;

                for (int i = 0; i < houseCount; i++) {
                    if (houseImage != null) {
                        g2d.drawImage(houseImage, startX + i * houseSize, y + spaceHeight - 30, null);
                    } else {
                        // Fallback drawing
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(startX + i * houseSize, y + spaceHeight - 30, houseSize-2, houseSize-2);
                    }
                }
            }
        }
        /**
         * Author: Marena
         * Edited by: Aiden Clare
         * Draws the side spaces (properties, utilities, railroads, etc.) with improved text spacing
         */
        private void drawSideSpaces(Graphics2D g2d) {
            // Set a thicker stroke for better visibility of space borders
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(1.5f));


            // Create a smaller font for property names to avoid text overlap
            Font smallFont = new Font("Arial", Font.PLAIN, 6);
            Font ownerFont = new Font("Arial", Font.BOLD, 6);

            // Bottom row (left to right, excluding corners)
            for (int i = 1; i < 10; i++) {
                int x = SPACE_SIZE * i;
                int y = BOARD_SIZE - SPACE_SIZE;

                // Get the corresponding space from the board
                Space space = board.getspace((i + 30) % 40); // Bottom row spaces

                // Fill space background with a border
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, SPACE_SIZE, SPACE_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, SPACE_SIZE, SPACE_SIZE);

                // Draw property band if it's a property
                if (space instanceof Property) {
                    Property property = (Property) space;
                    int colorIndex = getColorIndex(property.getColorGroup());
                    g2d.setColor(PROPERTY_COLORS[colorIndex]);
                    g2d.fillRect(x, y, SPACE_SIZE, 10); // Smaller colored band
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, SPACE_SIZE, 10); // Add border to color band
                } else if (space instanceof RailroadSpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, SPACE_SIZE, 10);
                } else if (space instanceof UtilitySpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, SPACE_SIZE, 10);
                } else {
                    // Special spaces
                    g2d.setColor(SPECIAL_COLORS[i % SPECIAL_COLORS.length]);
                    g2d.fillRect(x, y, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, SPACE_SIZE, 10);
                }

                // Draw space name with better wrapping
                g2d.setColor(Color.BLACK);
                g2d.setFont(smallFont);
                drawWrappedText(g2d, space.getName(), x + 3, y + 20, SPACE_SIZE - 6);

                // If property is owned, show owner
                if ((space instanceof Property && ((Property)space).isOwned()) ||
                        (space instanceof RailroadSpace && ((RailroadSpace)space).isOwned()) ||
                        (space instanceof UtilitySpace && ((UtilitySpace)space).isOwned())) {

                    Player owner = space.getOwner();
                    if (owner != null) {
                        g2d.setColor(Color.RED);
                        g2d.setFont(ownerFont);
                        drawCenteredString(g2d, "Owner: " + owner.getName(), x, y + SPACE_SIZE - 5, SPACE_SIZE);

                        if (space instanceof Property) {
                            Property property = (Property) space;
                            if (property.isOwned()) {
                                drawHouses(g2d, property, x, y - 15, SPACE_SIZE, SPACE_SIZE);
                            }
                        }
                    }
                }
            }

            // Left column (bottom to top, excluding corners)
            for (int i = 1; i < 10; i++) {
                int x = 0;
                int y = BOARD_SIZE - SPACE_SIZE * (i + 1);

                // Get the corresponding space from the board
                Space space = board.getspace(20 + i); // Left column spaces

                // Fill space background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, SPACE_SIZE, SPACE_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, SPACE_SIZE, SPACE_SIZE);

                // Draw property band if it's a property
                if (space instanceof Property) {
                    Property property = (Property) space;
                    int colorIndex = getColorIndex(property.getColorGroup());
                    g2d.setColor(PROPERTY_COLORS[colorIndex]);
                    g2d.fillRect(x, y, 10, SPACE_SIZE); // Smaller colored band
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, 10, SPACE_SIZE);
                } else if (space instanceof RailroadSpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, 10, SPACE_SIZE);
                } else if (space instanceof UtilitySpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, 10, SPACE_SIZE);
                } else {
                    // Special spaces
                    g2d.setColor(SPECIAL_COLORS[i % SPECIAL_COLORS.length]);
                    g2d.fillRect(x, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, 10, SPACE_SIZE);
                }

                // Draw space name - SIMPLIFIED VERSION
                g2d.setColor(Color.BLACK);
                g2d.setFont(smallFont);

                // Save original transform
                AffineTransform originalTransform = g2d.getTransform();

                // Draw in center of space, rotated 90 degrees
                int centerX = x + SPACE_SIZE/2;
                int centerY = y + SPACE_SIZE/2;

                // Rotate text
                g2d.rotate(Math.PI/2, centerX, centerY);

                // Draw centered text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(space.getName());
                g2d.drawString(space.getName(), centerX - textWidth/2, centerY + 3);

                // Restore transform
                g2d.setTransform(originalTransform);

                // If property is owned, show owner
                if ((space instanceof Property && ((Property)space).isOwned()) ||
                        (space instanceof RailroadSpace && ((RailroadSpace)space).isOwned()) ||
                        (space instanceof UtilitySpace && ((UtilitySpace)space).isOwned())) {

                    Player owner = space.getOwner();
                    if (owner != null) {
                        g2d.setColor(Color.RED);
                        g2d.setFont(ownerFont);

                        // Draw owner name with rotation
                        g2d.rotate(Math.PI/2, x + SPACE_SIZE - 8, y + SPACE_SIZE/2);
                        g2d.drawString("Owner: " + owner.getName(), x + SPACE_SIZE - 8, y + SPACE_SIZE/2);
                        g2d.rotate(-Math.PI/2, x + SPACE_SIZE - 8, y + SPACE_SIZE/2);

                        if (space instanceof Property) {
                            Property property = (Property) space;
                            if (property.isOwned()) {
                                AffineTransform original = g2d.getTransform();
                                g2d.rotate(Math.PI/2, x + SPACE_SIZE/2, y + SPACE_SIZE/2);
                                drawHouses(g2d, property, x, y, SPACE_SIZE, SPACE_SIZE);
                                g2d.setTransform(original);
                            }
                        }
                    }
                }
            }

            // Top row (right to left, excluding corners)
            for (int i = 1; i < 10; i++) {
                int x = BOARD_SIZE - SPACE_SIZE * (i + 1);
                int y = 0;

                // Get the corresponding space from the board
                Space space = board.getspace(20 - i); // Top row spaces

                // Fill space background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, SPACE_SIZE, SPACE_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, SPACE_SIZE, SPACE_SIZE);

                // Draw property band if it's a property
                if (space instanceof Property) {
                    Property property = (Property) space;
                    int colorIndex = getColorIndex(property.getColorGroup());
                    g2d.setColor(PROPERTY_COLORS[colorIndex]);
                    g2d.fillRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10); // Colored band at bottom
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                } else if (space instanceof RailroadSpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                } else if (space instanceof UtilitySpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                } else {
                    // Special spaces
                    g2d.setColor(SPECIAL_COLORS[i % SPECIAL_COLORS.length]);
                    g2d.fillRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y + SPACE_SIZE - 10, SPACE_SIZE, 10);
                }

                // Draw space name upside down
                g2d.setColor(Color.BLACK);
                g2d.setFont(smallFont);
                drawTopRowText(g2d, space.getName(), x + SPACE_SIZE/2, y + 20);

                // If property is owned, show owner
                if ((space instanceof Property && ((Property)space).isOwned()) ||
                        (space instanceof RailroadSpace && ((RailroadSpace)space).isOwned()) ||
                        (space instanceof UtilitySpace && ((UtilitySpace)space).isOwned())) {

                    Player owner = space.getOwner();
                    if (owner != null) {
                        g2d.setColor(Color.RED);
                        g2d.setFont(ownerFont);
                        g2d.rotate(Math.PI, x + SPACE_SIZE/2, y + 10);
                        drawCenteredString(g2d, "Owner: " + owner.getName(), x, y + 10, SPACE_SIZE);
                        g2d.rotate(-Math.PI, x + SPACE_SIZE/2, y + 10);
                        if (space instanceof Property) {
                            Property property = (Property) space;
                            if (property.isOwned()) {
                                AffineTransform original = g2d.getTransform();
                                g2d.rotate(Math.PI, x + SPACE_SIZE/2, y + SPACE_SIZE/2);
                                drawHouses(g2d, property, x, y, SPACE_SIZE, SPACE_SIZE);
                                g2d.setTransform(original);
                            }
                        }
                    }
                }
            }

            // Right column (top to bottom, excluding corners)
            for (int i = 1; i < 10; i++) {
                int x = BOARD_SIZE - SPACE_SIZE;
                int y = SPACE_SIZE * i;

                // Get the corresponding space from the board
                Space space = board.getspace(i); // Right column spaces

                // Fill space background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, SPACE_SIZE, SPACE_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, SPACE_SIZE, SPACE_SIZE);

                // Draw property band if it's a property
                if (space instanceof Property) {
                    Property property = (Property) space;
                    int colorIndex = getColorIndex(property.getColorGroup());
                    g2d.setColor(PROPERTY_COLORS[colorIndex]);
                    g2d.fillRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE); // Colored band on right
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                } else if (space instanceof RailroadSpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                } else if (space instanceof UtilitySpace) {
                    g2d.setColor(SPECIAL_COLORS[3]);
                    g2d.fillRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                } else {
                    // Special spaces
                    g2d.setColor(SPECIAL_COLORS[i % SPECIAL_COLORS.length]);
                    g2d.fillRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + SPACE_SIZE - 10, y, 10, SPACE_SIZE);
                }

                // Draw space name - SIMPLIFIED VERSION
                g2d.setColor(Color.BLACK);
                g2d.setFont(smallFont);

                // Save original transform
                AffineTransform originalTransform = g2d.getTransform();

                // Draw in center of space, rotated -90 degrees
                int centerX = x + SPACE_SIZE/2;
                int centerY = y + SPACE_SIZE/2;

                // Rotate text
                g2d.rotate(-Math.PI/2, centerX, centerY);

                // Draw centered text
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(space.getName());
                g2d.drawString(space.getName(), centerX - textWidth/2, centerY + 3);

                // Restore transform
                g2d.setTransform(originalTransform);

                // If property is owned, show owner
                if ((space instanceof Property && ((Property)space).isOwned()) ||
                        (space instanceof RailroadSpace && ((RailroadSpace)space).isOwned()) ||
                        (space instanceof UtilitySpace && ((UtilitySpace)space).isOwned())) {

                    Player owner = space.getOwner();
                    if (owner != null) {
                        g2d.setColor(Color.RED);
                        g2d.setFont(ownerFont);

                        // Draw owner name with rotation
                        g2d.rotate(-Math.PI/2, x + 8, y + SPACE_SIZE/2);
                        String ownerText = "Owner: " + owner.getName();
                        int ownerWidth = g2d.getFontMetrics().stringWidth(ownerText);
                        g2d.drawString(ownerText, x + 8 - ownerWidth/2, y + SPACE_SIZE/2);
                        g2d.rotate(Math.PI/2, x + 8, y + SPACE_SIZE/2);
                        if (space instanceof Property) {
                            Property property = (Property) space;
                            if (property.isOwned()) {
                                AffineTransform original = g2d.getTransform();
                                g2d.rotate(-Math.PI/2, x + SPACE_SIZE/2, y + SPACE_SIZE/2);
                                drawHouses(g2d, property, x, y, SPACE_SIZE, SPACE_SIZE);
                                g2d.setTransform(original);
                            }
                        }
                    }
                }
            }

            // Restore original stroke
            g2d.setStroke(originalStroke);
        }


        /**
         * Author: Marena
         * Improved method to draw wrapped text for property names
         */
        private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int width) {
            FontMetrics fm = g2d.getFontMetrics();
            String[] words = text.split(" ");

            int maxLines = 3; // Limit number of lines to avoid overflow
            int lineHeight = fm.getHeight();
            int currentY = y;
            int lineCount = 0;

            StringBuilder currentLine = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (lineCount >= maxLines) break;

                String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;

                if (fm.stringWidth(testLine) <= width) {
                    // Word fits on current line
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                } else {
                    // Word doesn't fit, start a new line
                    if (currentLine.length() > 0) {
                        // Draw the current line centered
                        drawCenteredString(g2d, currentLine.toString(), x, currentY, width);
                        currentY += lineHeight;
                        lineCount++;

                        if (lineCount >= maxLines) {
                            // If this is the last line and we have more words, add ellipsis
                            if (words.length > i + 1) {
                                drawCenteredString(g2d, word + "...", x, currentY, width);
                            } else {
                                drawCenteredString(g2d, word, x, currentY, width);
                            }
                            break;
                        }

                        currentLine = new StringBuilder(word);
                    } else {
                        // First word is too long, truncate it
                        String truncated = word;
                        while (truncated.length() > 3 && fm.stringWidth(truncated + "...") > width) {
                            truncated = truncated.substring(0, truncated.length() - 1);
                        }
                        if (truncated.length() < word.length()) {
                            truncated += "...";
                        }
                        drawCenteredString(g2d, truncated, x, currentY, width);
                        currentY += lineHeight;
                        lineCount++;
                        currentLine = new StringBuilder();
                    }
                }
            }

            // Draw any remaining text
            if (currentLine.length() > 0 && lineCount < maxLines) {
                drawCenteredString(g2d, currentLine.toString(), x, currentY, width);
            }
        }



        /**
         * Author: Marena
         * Helper method to draw text for top row spaces with better visibility - simplified version
         */
        private void drawTopRowText(Graphics2D g2d, String text, int x, int y) {
            // Save original transform
            AffineTransform originalTransform = g2d.getTransform();

            // Rotate 180 degrees for top row
            g2d.rotate(Math.PI, x, y);

            // Get font metrics for centering
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);

            // Draw the text centered
            g2d.drawString(text, x - textWidth/2, y);

            // Restore original transform
            g2d.setTransform(originalTransform);
        }




        /**
         * Author: Marena
         * Draws the central Monopoly logo
         */
        private void drawCentralLogo(Graphics2D g2d) {
            // Save the current transform
            AffineTransform originalTransform = g2d.getTransform();

            // Center coordinates
            int centerX = BOARD_SIZE / 2;
            int centerY = BOARD_SIZE / 2;

            // Draw MONOPOLY text
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.setColor(Color.RED);

            // Rotate the text for a diagonal orientation
            g2d.rotate(Math.PI / 4, centerX, centerY);

            FontMetrics fm = g2d.getFontMetrics();
            String logo = "MONOPOLY";
            int textWidth = fm.stringWidth(logo);

            // Draw the logo text centered
            g2d.drawString(logo, centerX - textWidth / 2, centerY + fm.getAscent() / 2 - 10);

            // Restore original transform for the cards
            g2d.setTransform(originalTransform);

            // Draw diamond-shaped Chance card area
            drawDiamondCard(g2d, "CHANCE", centerX - 100, centerY - 100,
                    new Color(255, 182, 66, 200), Color.BLACK);

            // Draw diamond-shaped Community Chest card area
            drawDiamondCard(g2d, "COMMUNITY CHEST", centerX + 40, centerY + 40,
                    new Color(102, 0, 153, 200), Color.WHITE);
        }

        /**
         * Author: Marena
         * Helper method to draw a diamond-shaped card with text
         */
        private void drawDiamondCard(Graphics2D g2d, String text, int x, int y, Color fillColor, Color textColor) {
            int cardSize = 100;

            // Create diamond shape
            int[] xPoints = {
                    x,
                    x + cardSize/2,
                    x + cardSize,
                    x + cardSize/2
            };
            int[] yPoints = {
                    y + cardSize/2,
                    y,
                    y + cardSize/2,
                    y + cardSize
            };

            // Draw diamond
            g2d.setColor(fillColor);
            g2d.fillPolygon(xPoints, yPoints, 4);

            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawPolygon(xPoints, yPoints, 4);

            // Draw text
            g2d.setColor(textColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            // Split text if it's Community Chest
            if (text.contains(" ")) {
                String[] parts = text.split(" ");
                FontMetrics fm = g2d.getFontMetrics();

                for (int i = 0; i < parts.length; i++) {
                    int textWidth = fm.stringWidth(parts[i]);
                    g2d.drawString(parts[i], x + cardSize/2 - textWidth/2,
                            y + cardSize/2 + i*15);
                }
            } else {
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                g2d.drawString(text, x + cardSize/2 - textWidth/2, y + cardSize/2);
            }
        }

        /**
         * Author: Marena
         * Draws player tokens at their current positions on the board
         */
        private void drawPlayerTokens(Graphics2D g2d) {
            // Draw each player's token at their current position
            for (Player player : players) {
                int position = player.getPosition();
                Point tokenPos = getTokenPosition(position, player);

                // Draw different tokens based on player color and token type
                drawToken(g2d, player, tokenPos.x, tokenPos.y);
            }
        }

        /**
         * Author: Marena
         * Gets the x,y coordinates for drawing a token at a given board position
         */
        private Point getTokenPosition(int position, Player player) {
            int x, y;

            // Determine the base position for the space
            if (position == 0) {
                // GO (bottom right)
                x = BOARD_SIZE - SPACE_SIZE / 2;
                y = BOARD_SIZE - SPACE_SIZE / 2;
            } else if (position < 10) {
                // Bottom row (right side)
                x = BOARD_SIZE - SPACE_SIZE - (position * SPACE_SIZE) + SPACE_SIZE / 2;
                y = BOARD_SIZE - SPACE_SIZE / 2;
            } else if (position == 10) {
                // JAIL (bottom left)
                x = SPACE_SIZE / 2;
                y = BOARD_SIZE - SPACE_SIZE / 2;
            } else if (position < 20) {
                // Left column (going up)
                x = SPACE_SIZE / 2;
                y = BOARD_SIZE - SPACE_SIZE - ((position - 10) * SPACE_SIZE) + SPACE_SIZE / 2;
            } else if (position == 20) {
                // FREE PARKING (top left)
                x = SPACE_SIZE / 2;
                y = SPACE_SIZE / 2;
            } else if (position < 30) {
                // Top row (going right)
                x = SPACE_SIZE + ((position - 20) * SPACE_SIZE) - SPACE_SIZE / 2;
                y = SPACE_SIZE / 2;
            } else if (position == 30) {
                // GO TO JAIL (top right)
                x = BOARD_SIZE - SPACE_SIZE / 2;
                y = SPACE_SIZE / 2;
            } else {
                // Right column (going down)
                x = BOARD_SIZE - SPACE_SIZE / 2;
                y = SPACE_SIZE + ((position - 30) * SPACE_SIZE) - SPACE_SIZE / 2;
            }

            // Add a small offset for each player to avoid tokens overlapping
            // We'll get the player index and offset based on that
            int playerIndex = players.indexOf(player);
            x += (playerIndex - 1) * 7;
            y += (playerIndex - 1) * 7;

            return new Point(x, y);
        }

        /**
         * Author: Marena
         * Draws a token for a player at the specified coordinates
         */
        private void drawToken(Graphics2D g2d, Player player, int x, int y) {
            String tokenName = player.getToken();

            // Choose color based on player index
            int playerIndex = players.indexOf(player);
            Color[] playerColors = {
                    Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
            };
            Color tokenColor = playerColors[playerIndex % playerColors.length];

            // Draw the token based on its name
            g2d.setColor(tokenColor);
            if (tokenName.equals("Top Hat")) {
                g2d.fillOval(x - 8, y - 8, 16, 10);  // Brim
                g2d.fillRect(x - 5, y - 15, 10, 10); // Hat
            } else if (tokenName.equals("Thimble")) {
                g2d.fillOval(x - 8, y - 8, 16, 16);  // Base
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - 4, y - 4, 8, 8);    // Inside
            } else if (tokenName.equals("Iron")) {
                g2d.fillRect(x - 8, y - 5, 16, 10);  // Base
                g2d.fillRect(x - 2, y - 12, 4, 8);   // Handle
            } else if (tokenName.equals("Boot")) {
                g2d.fillRect(x - 8, y - 4, 12, 8);   // Foot
                g2d.fillRect(x - 4, y - 12, 8, 8);   // Ankle
            } else if (tokenName.equals("Battleship")) {
                g2d.fillRect(x - 10, y - 3, 20, 6);  // Hull
                g2d.fillRect(x - 3, y - 10, 6, 10);  // Tower
            } else if (tokenName.equals("Cannon")) {
                g2d.fillRect(x - 10, y - 3, 20, 6);  // Base
                g2d.fillRect(x - 3, y - 8, 15, 4);   // Barrel
            } else if (tokenName.equals("Race Car")) {
                g2d.fillRect(x - 10, y - 3, 20, 6);  // Body
                g2d.fillOval(x - 8, y + 3, 6, 6);    // Rear wheel
                g2d.fillOval(x + 2, y + 3, 6, 6);    // Front wheel
            } else if (tokenName.equals("Scottie Dog")) {
                g2d.fillOval(x - 8, y - 5, 16, 10);  // Body
                g2d.fillOval(x + 4, y - 10, 8, 6);   // Head
                g2d.fillRect(x + 8, y - 2, 4, 6);    // Tail
            } else if (tokenName.equals("Wheelbarrow")) {
                g2d.fillRect(x - 10, y - 3, 16, 6);  // Tray
                g2d.fillOval(x + 4, y + 3, 6, 6);    // Wheel
                g2d.fillRect(x - 10, y - 8, 4, 8);   // Handle
            } else {
                // Generic token if none of the above
                g2d.fillOval(x - 8, y - 8, 16, 16);
            }

            // Draw a small name label below the token if player is current player
            if (player == gameState.getCurrentPlayer()) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 8));
                g2d.drawString(player.getName(), x - 10, y + 20);
            }
        }

        /**
         * Author: Marena
         * Helper method to get the color index for a property color group
         */
        private int getColorIndex(String colorGroup) {
            if (colorGroup == null) return 0;

            switch (colorGroup.toLowerCase()) {
                case "brown": return 0;
                case "light blue": return 1;
                case "pink": return 2;
                case "orange": return 3;
                case "red": return 4;
                case "yellow": return 5;
                case "green": return 6;
                case "dark blue": return 7;
                default: return 0;
            }
        }

        /**
         * Author: Marena
         * Draws centered text
         */
        private void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width) {
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, x + (width - textWidth) / 2, y);
        }

        /**
         * Author: Marena
         * Draws rotated text
         */
        private void drawRotatedText(Graphics2D g2d, String text, int x, int y, double angleDegrees) {
            AffineTransform originalTransform = g2d.getTransform();

            double angleRadians = Math.toRadians(angleDegrees);
            g2d.rotate(angleRadians, x, y);
            g2d.drawString(text, x, y);

            g2d.setTransform(originalTransform);
        }

        /**
         * Author: Marena
         * Draws rotated and centered text
         */
        private void drawRotatedCenteredString(Graphics2D g2d, String text, int x, int y, int width, double angleDegrees) {
            AffineTransform originalTransform = g2d.getTransform();

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            double angleRadians = Math.toRadians(angleDegrees);

            g2d.rotate(angleRadians, x, y);

            if (angleDegrees == 90 || angleDegrees == 270) {
                // For vertical text
                g2d.drawString(text, x - fm.getAscent() / 2, y + textWidth / 2);
            } else {
                // For horizontal or diagonal text
                g2d.drawString(text, x - textWidth / 2, y);
            }

            g2d.setTransform(originalTransform);
        }
    }

    /**
     * Author: Marena
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Launch the GUI on the event dispatch thread
        SwingUtilities.invokeLater(GUI::new);
    }
}


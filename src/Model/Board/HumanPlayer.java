package Model.Board;

/**
 * Represents a human player in the Monopoly game.
 * Extends the Player class and adds human-specific functionality
 * such as prompting for decisions.
 */
public class HumanPlayer extends Player {

    /**
     * Constructs a new human player with the given name.
     *
     * @param name The player's name
     */
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * Prompts the human player to make a decision about buying a property.
     *
     * @param propertyName The name of the property
     * @param price The price of the property
     * @return true if the player wants to buy, false otherwise
     */
    public boolean promptBuyProperty(String propertyName, int price) {
        System.out.println(getName() + ", would you like to buy " + propertyName + " for $" + price + "? (y/n)");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.startsWith("y");
    }

    /**
     * Prompts the human player to make a decision about using a Get Out of Jail Free card.
     *
     * @return true if the player wants to use the card, false otherwise
     */
    public boolean promptUseGetOutOfJailCard() {
        System.out.println(getName() + ", would you like to use your Get Out of Jail Free card? (y/n)");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.startsWith("y");
    }

    /**
     * Prompts the human player to choose from jail options.
     *
     * @return int representing the chosen option (1 = pay, 2 = use card, 3 = roll)
     */
    public int promptJailOptions() {
        System.out.println(getName() + ", choose your jail option:");
        System.out.println("1. Pay $50 to get out");
        System.out.println("2. Use Get Out of Jail Free card (if available)");
        System.out.println("3. Try to roll doubles");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= 3) {
                return choice;
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }

        System.out.println("Invalid choice. Defaulting to option 3 (roll for doubles).");
        return 3;
    }
}
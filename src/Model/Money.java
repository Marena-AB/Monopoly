
package Model;
/**
 * Represents the currency system in the Monopoly game.
 * Manages denominations and provides utility methods for handling money.
 */

public class Money {
    // Standard Monopoly denominations
    public static final int ONE = 1;
    public static final int FIVE = 5;
    public static final int TEN = 10;
    public static final int TWENTY = 20;
    public static final int FIFTY = 50;
    public static final int ONE_HUNDRED = 100;
    public static final int FIVE_HUNDRED = 500;

    // Default starting amount for players
    public static final int STARTING_AMOUNT = 1500;

    // Common fees and payments
    public static final int PASS_GO_AMOUNT = 200;
    public static final int LUXURY_TAX = 100;
    public static final int INCOME_TAX = 200;
    public static final int JAIL_FEE = 50;

    /**
     * Author: Marena
     * Calculates the optimal distribution of bills for a given amount.
     *
     * @param amount The amount to distribute
     * @return An array with counts of each denomination [1, 5, 10, 20, 50, 100, 500]
     */
    public static int[] calculateBillDistribution(int amount) {
        int[] distribution = new int[7];
        int remaining = amount;

        // Start with largest denomination
        distribution[6] = remaining / FIVE_HUNDRED;
        remaining %= FIVE_HUNDRED;

        distribution[5] = remaining / ONE_HUNDRED;
        remaining %= ONE_HUNDRED;

        distribution[4] = remaining / FIFTY;
        remaining %= FIFTY;

        distribution[3] = remaining / TWENTY;
        remaining %= TWENTY;

        distribution[2] = remaining / TEN;
        remaining %= TEN;

        distribution[1] = remaining / FIVE;
        remaining %= FIVE;

        distribution[0] = remaining; // Remaining ones

        return distribution;
    }

    /**
     * Author: Marena
     * Formats the bill distribution into a readable string.
     *
     * @param distribution An array with counts of each denomination
     * @return A formatted string representation
     */
    public static String formatBillDistribution(int[] distribution) {
        StringBuilder sb = new StringBuilder();

        if (distribution[6] > 0) {
            sb.append(distribution[6]).append(" x $500, ");
        }
        if (distribution[5] > 0) {
            sb.append(distribution[5]).append(" x $100, ");
        }
        if (distribution[4] > 0) {
            sb.append(distribution[4]).append(" x $50, ");
        }
        if (distribution[3] > 0) {
            sb.append(distribution[3]).append(" x $20, ");
        }
        if (distribution[2] > 0) {
            sb.append(distribution[2]).append(" x $10, ");
        }
        if (distribution[1] > 0) {
            sb.append(distribution[1]).append(" x $5, ");
        }
        if (distribution[0] > 0) {
            sb.append(distribution[0]).append(" x $1");
        }

        // Remove trailing comma if needed
        String result = sb.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }

    /**
     * Author: Marena
     * Gets the standard starting money for a player.
     *
     * @return The starting money amount
     */
    public static int getStartingMoney() {
        return STARTING_AMOUNT;
    }

    /**
     * Author: Marena
     * Prints the details of a money transaction.
     *
     * @param amount The amount of money transferred
     * @param fromName The name of the payer (or "Model.Board.Bank")
     * @param toName The name of the receiver
     * @param reason The reason for the transaction
     */
    public static void printTransaction(int amount, String fromName, String toName, String reason) {
        System.out.println(fromName + " pays $" + amount + " to " + toName +
                (reason != null ? " for " + reason : ""));
    }

    /**
     * Author: Marena
     * Calculates the total value of a bill distribution.
     *
     * @param distribution An array with counts of each denomination
     * @return The total monetary value
     */
    public static int calculateTotal(int[] distribution) {
        return distribution[0] * ONE +
                distribution[1] * FIVE +
                distribution[2] * TEN +
                distribution[3] * TWENTY +
                distribution[4] * FIFTY +
                distribution[5] * ONE_HUNDRED +
                distribution[6] * FIVE_HUNDRED;
    }
}

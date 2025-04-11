package Model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Money class.
 * Tests all methods and constants to ensure complete coverage.
 */
public class MoneyTest {

    @Test
    public void testConstants() {
        // Test that all denomination constants are set correctly
        assertEquals(1, Money.ONE);
        assertEquals(5, Money.FIVE);
        assertEquals(10, Money.TEN);
        assertEquals(20, Money.TWENTY);
        assertEquals(50, Money.FIFTY);
        assertEquals(100, Money.ONE_HUNDRED);
        assertEquals(500, Money.FIVE_HUNDRED);

        // Test game-related constants
        assertEquals(1500, Money.STARTING_AMOUNT);
        assertEquals(200, Money.PASS_GO_AMOUNT);
        assertEquals(100, Money.LUXURY_TAX);
        assertEquals(200, Money.INCOME_TAX);
        assertEquals(50, Money.JAIL_FEE);
    }

    @Test
    public void testCalculateBillDistribution_ZeroAmount() {
        // Test with zero amount
        int[] distribution = Money.calculateBillDistribution(0);

        // Should be array of zeros
        assertEquals(7, distribution.length);
        for (int count : distribution) {
            assertEquals(0, count);
        }
    }

    @Test
    public void testCalculateBillDistribution_SingleDenomination() {
        // Test with amounts that use only one denomination

        // Just $1 bills
        int[] distribution = Money.calculateBillDistribution(4);
        assertEquals(4, distribution[0]); // Four $1 bills
        assertEquals(0, distribution[1]); // No $5 bills

        // Just $5 bills
        distribution = Money.calculateBillDistribution(15);
        assertEquals(0, distribution[0]); // No $1 bills
        assertEquals(3, distribution[1]); // Three $5 bills

        // Just $500 bills
        distribution = Money.calculateBillDistribution(1500);
        assertEquals(3, distribution[6]); // Three $500 bills
        for (int i = 0; i < 6; i++) {
            assertEquals(0, distribution[i]); // No other bills
        }
    }

    @Test
    public void testCalculateBillDistribution_MixedDenominations() {
        // Test with 1984 (a mix of all denominations)
        int[] distribution = Money.calculateBillDistribution(1984);

        // Expected: 3x$500 + 4x$100 + 1x$50 + 1x$20 + 1x$10 + 0x$5 + 4x$1
        assertEquals(4, distribution[0]); // $1 bills
        assertEquals(0, distribution[1]); // $5 bills
        assertEquals(1, distribution[2]); // $10 bills
        assertEquals(1, distribution[3]); // $20 bills
        assertEquals(1, distribution[4]); // $50 bills
        assertEquals(4, distribution[5]); // $100 bills
        assertEquals(3, distribution[6]); // $500 bills

        // Test with 278 (a different mix)
        distribution = Money.calculateBillDistribution(278);

        // Expected: 0x$500 + 2x$100 + 1x$50 + 1x$20 + 0x$10 + 1x$5 + 3x$1
        assertEquals(3, distribution[0]); // $1 bills
        assertEquals(1, distribution[1]); // $5 bills
        assertEquals(0, distribution[2]); // $10 bills
        assertEquals(1, distribution[3]); // $20 bills
        assertEquals(1, distribution[4]); // $50 bills
        assertEquals(2, distribution[5]); // $100 bills
        assertEquals(0, distribution[6]); // $500 bills
    }

    @Test
    public void testFormatBillDistribution_AllZeros() {
        // Test with all zeros
        int[] distribution = new int[7];
        String formatted = Money.formatBillDistribution(distribution);

        // Should be empty string (or some default message)
        assertEquals("", formatted);
    }

    @Test
    public void testFormatBillDistribution_SingleDenomination() {
        // Test with only one denomination
        int[] distribution = new int[7];
        distribution[6] = 3; // Three $500 bills

        String formatted = Money.formatBillDistribution(distribution);
        assertEquals("3 x $500", formatted);

        // Test with different denomination
        distribution = new int[7];
        distribution[2] = 5; // Five $10 bills

        formatted = Money.formatBillDistribution(distribution);
        assertEquals("5 x $10", formatted);
    }

    @Test
    public void testFormatBillDistribution_MultipleDenominations() {
        // Test with multiple denominations
        int[] distribution = {2, 1, 3, 0, 1, 2, 1}; // Mix of all denominations

        String formatted = Money.formatBillDistribution(distribution);
        assertEquals("1 x $500, 2 x $100, 1 x $50, 3 x $10, 1 x $5, 2 x $1", formatted);

        // Test with some zero counts (should be skipped)
        distribution = new int[]{0, 0, 2, 1, 0, 3, 0};

        formatted = Money.formatBillDistribution(distribution);
        assertEquals("3 x $100, 1 x $20, 2 x $10", formatted);
    }

    @Test
    public void testGetStartingMoney() {
        assertEquals(Money.STARTING_AMOUNT, Money.getStartingMoney());
    }

    @Test
    public void testPrintTransaction() {
        // Capture System.out for testing
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            // Test with reason
            Money.printTransaction(200, "Player 1", "Bank", "Passing GO");
            assertTrue(outContent.toString().contains("Player 1 pays $200 to Bank for Passing GO"));

            // Reset output
            outContent.reset();

            // Test without reason
            Money.printTransaction(150, "Player 2", "Player 1", null);
            assertTrue(outContent.toString().contains("Player 2 pays $150 to Player 1"));
            assertFalse(outContent.toString().contains("for"));
        } finally {
            // Restore System.out
            System.setOut(originalOut);
        }
    }

    @Test
    public void testCalculateTotal() {
        // Test with zero
        int[] distribution = new int[7];
        assertEquals(0, Money.calculateTotal(distribution));

        // Test with single denomination
        distribution = new int[7];
        distribution[3] = 4; // Four $20 bills
        assertEquals(4 * 20, Money.calculateTotal(distribution));

        // Test with multiple denominations
        distribution = new int[]{3, 2, 1, 2, 1, 3, 2};
        int expected = 3*1 + 2*5 + 1*10 + 2*20 + 1*50 + 3*100 + 2*500;
        assertEquals(expected, Money.calculateTotal(distribution));
    }
}
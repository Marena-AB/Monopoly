package Model.Board;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Tokens class
 */
public class TokensTest {

    private Player player;

    @Before
    public void setUp() {
        // Initialize available tokens before each test
        Tokens.initializeTokens();
        player = new Player("Test Player");
    }

    @After
    public void tearDown() {
        // Reset tokens after each test to avoid test interference
        Tokens.initializeTokens();
    }

    @Test
    public void testInitializeTokens() {
        // Test that initializing tokens makes all tokens available
        // First assign some tokens to make them unavailable
        Tokens.assignToken("Top Hat");
        Tokens.assignToken("Thimble");

        // Then reinitialize
        Tokens.initializeTokens();

        // All tokens should be available again
        assertTrue(Tokens.isTokenAvailable("Top Hat"));
        assertTrue(Tokens.isTokenAvailable("Thimble"));
        assertTrue(Tokens.isTokenAvailable("Iron"));
        assertTrue(Tokens.isTokenAvailable("Boot"));
        assertTrue(Tokens.isTokenAvailable("Battleship"));
    }

    @Test
    public void testIsTokenAvailable() {
        // Test checking if tokens are available
        assertTrue(Tokens.isTokenAvailable("Top Hat"));
        assertTrue(Tokens.isTokenAvailable("Thimble"));

        // Assign a token and verify it's no longer available
        Tokens.assignToken("Top Hat");
        assertFalse(Tokens.isTokenAvailable("Top Hat"));
        assertTrue(Tokens.isTokenAvailable("Thimble")); // Other tokens should still be available
    }

    @Test
    public void testAssignToken() {
        // Test assigning tokens
        boolean result = Tokens.assignToken("Top Hat");
        assertTrue("Should successfully assign available token", result);
        assertFalse("Token should no longer be available", Tokens.isTokenAvailable("Top Hat"));

        // Test assigning an already assigned token
        result = Tokens.assignToken("Top Hat");
        assertFalse("Should fail to assign unavailable token", result);
    }

    @Test
    public void testAssignTokenNoArgs() {
        // Test the parameterless assignToken method
        char[] token = Tokens.assignToken();
        assertNotNull("Should assign a token", token);

        // Assign all remaining tokens to test empty case
        for (int i = 0; i < Tokens.TOKENS.length - 1; i++) {
            Tokens.assignToken();
        }

        // Test when no tokens are available
        char[] noToken = Tokens.assignToken();
        assertNull("Should return null when no tokens left", noToken);
    }

    @Test
    public void testGetAvailableTokens() {
        // Test getting available tokens string
        String availableTokens = Tokens.getavailabletokens();

        // Should contain all tokens initially
        for (String token : Tokens.TOKENS) {
            assertTrue("Available tokens should include " + token, availableTokens.contains(token));
        }

        // Assign some tokens
        Tokens.assignToken("Top Hat");
        Tokens.assignToken("Thimble");

        // Get updated available tokens
        availableTokens = Tokens.getavailabletokens();

        // Should not contain assigned tokens
        assertFalse("Available tokens should not include Top Hat", availableTokens.contains("Top Hat"));
        assertFalse("Available tokens should not include Thimble", availableTokens.contains("Thimble"));

        // Should still contain other tokens
        assertTrue("Available tokens should include Iron", availableTokens.contains("Iron"));
    }

    @Test
    public void testChooseToken() {
        // Test the static method to choose a token for a player
        boolean result = Tokens.chooseToken(player, "Race Car");

        // In the implementation, this method returns false (appears to be a stub)
        // Just verify the method exists and returns expected value
        assertFalse(result);
    }

    @Test
    public void testMoveToken() {
        // Test the static method to move a token
        // This method doesn't return anything and appears to be a stub
        // Just verify the method exists and doesn't throw exceptions
        Tokens.moveToken(player, 5);
    }

    @Test
    public void testTokenOwnership() {
        // Test creating a token instance and setting ownership
        Tokens token = new Tokens();
        assertNull("New token should have no owner", token.getOwner());
        assertEquals("New token should be at position 0", 0, token.getBoardPosition());

        // Set token owner and position
        token.setOwner("Test Player");
        token.setBoardPosition(5);

        // Verify changes
        assertEquals("Token owner should be set", "Test Player", token.getOwner());
        assertEquals("Token position should be set", 5, token.getBoardPosition());
    }

    @Test
    public void testTokenToString() {
        // Test token string representation
        Tokens token = new Tokens();
        token.setOwner("Test Player");
        token.setBoardPosition(5);

        String tokenString = token.toString();
        assertTrue("Token string should contain owner", tokenString.contains("Test Player"));
        assertTrue("Token string should contain position", tokenString.contains("5"));
    }

    @Test
    public void testConstantValues() {
        // Test that TOKENS constant contains expected values
        boolean containsTopHat = false;
        boolean containsRaceCar = false;
        boolean containsScottieDog = false;

        for (String token : Tokens.TOKENS) {
            if (token.equals("Top Hat")) containsTopHat = true;
            if (token.equals("Race Car")) containsRaceCar = true;
            if (token.equals("Scottie Dog")) containsScottieDog = true;
        }

        assertTrue("TOKENS should contain Top Hat", containsTopHat);
        assertTrue("TOKENS should contain Race Car", containsRaceCar);
        assertTrue("TOKENS should contain Scottie Dog", containsScottieDog);
    }

    @Test
    public void testNoAvailableTokens() {
        // Assign all tokens
        for (String token : Tokens.TOKENS) {
            Tokens.assignToken(token);
        }

        // Check what happens when no tokens are available
        String noTokens = Tokens.getavailabletokens();
        assertEquals("Should return message when no tokens available", "No tokens available", noTokens);
    }
}
package Model.Spaces;

import Model.Board.Player;
import Model.GameState;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Test class for the FreeParkingSpace class
 */
public class FreeParkingSpaceTest {

    private FreeParkingSpace freeParkingSpace;
    private Player player;
    private GameState gameState;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() {
        // Create free parking space and test player
        freeParkingSpace = new FreeParkingSpace();
        player = new Player("Test Player");

        // Set up output capture
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Mock game state
        gameState = new GameState(java.util.Arrays.asList(player), new Model.Board.Gameboard());
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testConstructor() {
        assertEquals("Free Parking", freeParkingSpace.getName());
        assertEquals(20, freeParkingSpace.getPosition());
        assertEquals("Free Parking", freeParkingSpace.getType());
        assertEquals(0, freeParkingSpace.getMoneyPool());
    }

    @Test
    public void testOnLandStandardRules() {
        // Test landing on Free Parking with standard rules (no money collection)
        freeParkingSpace.onLand(player, gameState);

        assertTrue(outContent.toString().contains("Test Player landed on Free Parking"));

        // Money should not change with standard rules
        assertEquals(1500, player.getMoney());
    }

    @Test
    public void testOnLandWithHouseRules() {
        // Add money to the pool
        freeParkingSpace.addMoneyToPool(500);
        assertEquals(500, freeParkingSpace.getMoneyPool());

        // Test landing on Free Parking with house rules (collect money)
        freeParkingSpace.onLand(player, gameState, true);

        assertTrue(outContent.toString().contains("Test Player landed on Free Parking"));
        assertTrue(outContent.toString().contains("Test Player collects $500 from Free Parking"));

        // Player should have collected the money
        assertEquals(2000, player.getMoney());

        // Money pool should be reset
        assertEquals(0, freeParkingSpace.getMoneyPool());
    }

    @Test
    public void testOnLandWithHouseRulesNoMoney() {
        // Test landing on Free Parking with house rules but no money in pool
        freeParkingSpace.onLand(player, gameState, true);

        assertTrue(outContent.toString().contains("Test Player landed on Free Parking"));

        // No money message as pool is empty
        assertFalse(outContent.toString().contains("collects"));

        // Money should not change
        assertEquals(1500, player.getMoney());
    }

    @Test
    public void testAddMoneyToPool() {
        // Initially pool is empty
        assertEquals(0, freeParkingSpace.getMoneyPool());

        // Add money to the pool
        freeParkingSpace.addMoneyToPool(200);
        assertEquals(200, freeParkingSpace.getMoneyPool());

        // Add more money
        freeParkingSpace.addMoneyToPool(150);
        assertEquals(350, freeParkingSpace.getMoneyPool());
    }

    @Test
    public void testGetMoneyPool() {
        assertEquals(0, freeParkingSpace.getMoneyPool());

        freeParkingSpace.addMoneyToPool(300);
        assertEquals(300, freeParkingSpace.getMoneyPool());
    }

    @Test
    public void testPlayerOnSpecialSpace() {
        freeParkingSpace.playerOnSpecialSpace();
        assertTrue(outContent.toString().contains("Player landed on Free Parking"));
    }

    @Test
    public void testInheritance() {
        assertTrue(freeParkingSpace instanceof SpecialSpace);
        assertTrue(freeParkingSpace instanceof Space);
    }
}
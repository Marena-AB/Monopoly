package Model.Property;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Test class for the PropertyManager class.
 * Tests all methods to ensure complete coverage.
 */
public class PropertyManagerTest {

    private PropertyManager propertyManager;
    private Property property1;
    private Property property2;
    private Property property3;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));

        // Create a new property manager
        propertyManager = new PropertyManager();

        // Create test properties
        property1 = new Property("Mediterranean Avenue", 1, 60, "Brown");
        property2 = new Property("Baltic Avenue", 3, 60, "Brown");
        property3 = new Property("Boardwalk", 39, 400, "Dark Blue");
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testConstructor() {
        // Verify property manager is initialized with an empty list
        assertNotNull(propertyManager);

        // No direct way to check if the list is empty, but we can check that
        // getting a non-existent property returns null
        assertNull(propertyManager.getProperty(1));
    }

    @Test
    public void testAddProperty() {
        // Add a property and verify it can be retrieved
        propertyManager.addProperty(property1);
        assertEquals(property1, propertyManager.getProperty(property1.getPosition()));

        // Add a second property
        propertyManager.addProperty(property2);
        assertEquals(property2, propertyManager.getProperty(property2.getPosition()));

        // Verify first property is still accessible
        assertEquals(property1, propertyManager.getProperty(property1.getPosition()));
    }

    @Test
    public void testRemoveProperty() {
        // Add properties
        propertyManager.addProperty(property1);
        propertyManager.addProperty(property2);

        // Verify properties exist
        assertNotNull(propertyManager.getProperty(property1.getPosition()));
        assertNotNull(propertyManager.getProperty(property2.getPosition()));

        // Remove a property
        propertyManager.removeProperty(property1.getPosition());

        // Verify property was removed
        assertNull(propertyManager.getProperty(property1.getPosition()));

        // Verify other property still exists
        assertNotNull(propertyManager.getProperty(property2.getPosition()));
    }

    @Test
    public void testRemoveNonExistentProperty() {
        // Add one property
        propertyManager.addProperty(property1);

        // Try to remove a property that doesn't exist
        propertyManager.removeProperty(999);

        // Verify the existing property wasn't affected
        assertNotNull(propertyManager.getProperty(property1.getPosition()));
    }

    @Test
    public void testGetProperty() {
        // Initially no properties exist
        assertNull(propertyManager.getProperty(1));

        // Add a property
        propertyManager.addProperty(property1);

        // Verify we can get it by position
        assertEquals(property1, propertyManager.getProperty(property1.getPosition()));

        // Try to get a non-existent property
        assertNull(propertyManager.getProperty(999));
    }

    @Test
    public void testGetPropertyWithMultipleProperties() {
        // Add multiple properties
        propertyManager.addProperty(property1);
        propertyManager.addProperty(property2);
        propertyManager.addProperty(property3);

        // Verify we can get each one by position
        assertEquals(property1, propertyManager.getProperty(property1.getPosition()));
        assertEquals(property2, propertyManager.getProperty(property2.getPosition()));
        assertEquals(property3, propertyManager.getProperty(property3.getPosition()));
    }

    @Test
    public void testDisplayProperties() {
        // Add properties
        propertyManager.addProperty(property1);
        propertyManager.addProperty(property2);

        // Display properties
        propertyManager.displayProperties();

        // Verify output contains property details
        String output = outContent.toString();
        assertTrue(output.contains(property1.toString()));
        assertTrue(output.contains(property2.toString()));
    }

    @Test
    public void testDisplayPropertiesEmpty() {
        // Display with no properties
        propertyManager.displayProperties();

        // Output should be empty (no properties to display)
        assertEquals("", outContent.toString().trim());
    }

    @Test
    public void testPropertyPositionUniqueness() {
        // Add a property at position 1
        propertyManager.addProperty(property1);

        // Create a different property at the same position
        Property duplicatePosition = new Property("Duplicate", 1, 100, "Red");

        // Add the duplicate position property
        propertyManager.addProperty(duplicatePosition);

        // Get the property at position 1
        Property retrievedProperty = propertyManager.getProperty(1);

        // Should return the most recently added property
        assertEquals(duplicatePosition, retrievedProperty);
        assertNotEquals(property1, retrievedProperty);
    }

    @Test
    public void testRemoveAndReadd() {
        // Add a property
        propertyManager.addProperty(property1);

        // Verify it exists
        assertNotNull(propertyManager.getProperty(property1.getPosition()));

        // Remove it
        propertyManager.removeProperty(property1.getPosition());

        // Verify it's gone
        assertNull(propertyManager.getProperty(property1.getPosition()));

        // Add it again
        propertyManager.addProperty(property1);

        // Verify it exists again
        assertNotNull(propertyManager.getProperty(property1.getPosition()));
        assertEquals(property1, propertyManager.getProperty(property1.getPosition()));
    }

    @Test
    public void testStressWithManyProperties() {
        // Add many properties (stress test)
        for (int i = 0; i < 100; i++) {
            Property testProperty = new Property("Test " + i, i, 100, "Test");
            propertyManager.addProperty(testProperty);
        }

        // Verify we can retrieve all of them
        for (int i = 0; i < 100; i++) {
            Property retrievedProperty = propertyManager.getProperty(i);
            assertNotNull(retrievedProperty);
            assertEquals("Test " + i, retrievedProperty.getName());
            assertEquals(i, retrievedProperty.getPosition());
        }

        // Remove half of them
        for (int i = 0; i < 100; i += 2) {
            propertyManager.removeProperty(i);
        }

        // Verify the removed ones are gone and the others still exist
        for (int i = 0; i < 100; i++) {
            Property retrievedProperty = propertyManager.getProperty(i);
            if (i % 2 == 0) {
                assertNull(retrievedProperty);
            } else {
                assertNotNull(retrievedProperty);
                assertEquals("Test " + i, retrievedProperty.getName());
            }
        }
    }
}
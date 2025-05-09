package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class MapTest {

    /**
     * Tests that a Map object is created with the correct values for its name, width, and height.
     */
    @Test
    public void testMapCreationStoresCorrectValues() {
        Map map = new Map("MetroMap", 10, 15);

        assertEquals("MetroMap", map.getMapName());
        assertEquals(10, map.getWidth());
        assertEquals(15, map.getHeight());
    }

    /**
     * Tests that the stations list in a Map object is initialized and starts as an empty list.
     */
    @Test
    public void testStationsListIsInitialized() {
        Map map = new Map("CityMap", 5, 5);

        assertNotNull(map.getStations());
        assertTrue(map.getStations().isEmpty());
    }
    /**
     * Tests that the toString method of the Map class returns the map's name.
     */
    @Test
    public void testToStringReturnsMapName() {
        Map map = new Map("WorldMap", 20, 30);

        assertEquals("WorldMap", map.toString());
    }
    /**
     * Tests that duplicate map names can be detected externally using a Set.
     * Ensures that adding a map with a duplicate name to the Set fails.
     */
    @Test
    public void testDuplicateMapNameDetectionExternally() {
        Set<String> existingNames = new HashSet<>();
        Map map1 = new Map("TestMap", 10, 10);
        assertTrue(existingNames.add(map1.getMapName()));

        Map map2 = new Map("TestMap", 20, 20);
        boolean added = existingNames.add(map2.getMapName());

        assertFalse(added, "Duplicate map name should not be allowed externally");
    }
    /**
     * Tests that a Map object can be created with negative width and height values.
     * This test will fail if future validations are added to the constructor
     * to prevent negative dimensions.
     */
    @Test
    public void testNegativeWidthOrHeightIsAllowed() {
        Map map = new Map("InvalidMap", -5, -10);

        assertEquals(-5, map.getWidth());
        assertEquals(-10, map.getHeight());
    }
}
